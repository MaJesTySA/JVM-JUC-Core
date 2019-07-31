# Java8  JVM内存结构

基本结构与之前类似，只是Java8取消了之前的“永久代”，取而代之的是“元空间”——**Metaspace**，两者本质是一样的。“永久代”使用的是JVM的堆内存，而“元空间”是直接使用的本机物理内存。

![](https://raw.githubusercontent.com/MaJesTySA/JVM-JUC-Core/master/imgs/JVMMem.png)

# GC Roots

## 如果判断一个对象可以被回收？

### 引用计数算法

维护一个计数器，如果有对该对象的引用，计数器+1，反之-1。无法解决循环引用的问题。

### 可达性分析算法

从一组名为“GC Roots”的根节点对象出发，向下遍历。那些没有被遍历到、与GC Roots形成通路的对象，会被标记为“回收”。

## 哪些对象可以作为GC Roots？

1. 虚拟机栈（栈帧中的局部变量）中引用的对象。
2. 本地方法栈（native）中引用的对象。
3. 方法区中常量引用的对象。
4. 方法区中类静态属性引用的对象。

# JVM参数

## JVM 三种类型参数

### 标配参数

比如`-version`、`-help`、`-showversion`等，几乎不会改变。

### X参数

用得不多，比如`-Xint`，解释执行模式；`-Xcomp`，编译模式；`-Xmixed`，开启混合模式（默认）。

![](https://raw.githubusercontent.com/MaJesTySA/JVM-JUC-Core/master/imgs/InkedJVMXParam_LI.jpg)

### XX参数

重要，用于JVM调优。

## JVM XX参数

### 布尔类型

**公式**：`-XX:+某个属性`、`-XX:-某个属性`，开启或关闭某个功能。比如`-XX:+PrintGCDetails`，开启GC详细信息。

### KV键值类型

**公式**：`-XX:属性key=值value`。比如`-XX:Metaspace=128m`、`-XX:MaxTenuringThreshold=15`。

## JVM Xms/Xmx参数

`-Xms`和`-Xmx`十分常见，用于设置**初始堆大小**和**最大堆大小**。第一眼看上去，既不像X参数，也不像XX参数。实际上`-Xms`等价于`-XX:InitialHeapSize`，`-Xmx`等价于`-XX:MaxHeapSize`。所以`-Xms`和`-Xmx`属于XX参数。

## JVM 查看参数

### 查看某个参数

使用`jps -l`配合`jinfo -flag JVM参数 pid` 。先用`jsp -l`查看java进程，选择某个进程号。

```java
17888 org.jetbrains.jps.cmdline.Launcher
5360 org.jetbrains.idea.maven.server.RemoteMavenServer
18052 demo3.demo3
```

`jinfo -flag PrintGCDetails 18052`可以查看18052 Java进程的`PrintGCDetails`参数信息。

```java
-XX:-PrintGCDetails
```

### 查看**所有**参数

使用`jps -l`配合`jinfo -flags pid`可以查看所有参数。

也可以使用`java -XX:+PrintFlagsInitial`

```java
[Global flags]
     intx ActiveProcessorCount                      = -1            {product}
    uintx AdaptiveSizeDecrementScaleFactor          = 4             {product}
    uintx AdaptiveSizeMajorGCDecayTimeScale         = 10            {product}
    uintx AdaptiveSizePausePolicy                   = 0             {product}
······
    uintx YoungPLABSize                             = 4096          {product}
     bool ZeroTLAB                                  = false         {product}
     intx hashCode                                  = 5             {product}

```

### 查看**修改**后的参数

使用`java -XX:PrintFlagsFinal`可以查看修改后的参数，与上面类似。只是修改过后是`:=`而不是`=`。

### 查看**常见**参数

如果不想查看所有参数，可以用`-XX:+PrintCommandLineFlags`查看常用参数。

```java
-XX:InitialHeapSize=132375936 -XX:MaxHeapSize=2118014976 -XX:+PrintCommandLineFlags -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:-UseLargePagesIndividualAllocation -XX:+UseParallelGC
```

## JVM 常用参数

### -Xmx/-Xms

最大和初始堆大小。最大默认为物理内存的1/4，初始默认为物理内存的1/64。

### -Xss

等价于`-XX:ThresholdStackSize`。用于设置单个栈的大小，系统默认值是0，**不代表栈大小为0**。而是根据操作系统的不同，有不同的值。比如64位的Linux系统是1024K，而Windows系统依赖于虚拟内存。

### -Xmn

新生代大小，一般不调。

### -XX:MetaspaceSize

设置元空间大小。

### -XX:+PrintGCDetails

输出GC收集信息，包含`GC`和`Full GC`信息。

### -XX:SurvivorRatio

新生代中，`Eden`区和两个`Survivor`区的比例，默认是`8:1:1`。通过`-XX:SurvivorRatio=4`改成`4:1:1`

### -XX:NewRatio

老生代和新年代的比列，默认是2，即老年代占2，新生代占1。如果改成`-XX:NewRatio=4`，则老年代占4，新生代占1。

### -XX:MaxTenuringThreshold

新生代设置进入老年代的时间，默认是新生代逃过15次GC后，进入老年代。如果改成0，那么对象不会在新生代分配，直接进入老年代。

# 四大引用

以下Demo都需要设置`-Xmx`和`-Xms`，不然系统默认很大，很难演示。

## 强引用

使用`new`方法创造出来的对象，默认都是强引用。GC的时候，就算**内存不够**，抛出`OutOfMemoryError`也不会回收对象，**死了也不回收**。详见[StrongReferenceDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/jvm/StrongReferenceDemo.java)。

## 软引用

需要用`Object.Reference.SoftReference`来显示创建。**如果内存够**，GC的时候**不回收**。**内存不够**，**则回收**。常用于内存敏感的应用，比如高速缓存。详见[SoftReferenceDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/jvm/SoftReferenceDemo.java)。

## 弱引用

需要用`Object.Reference.WeakReference`来显示创建。**无论内存够不够，GC的时候都回收**，也可以用在高速缓存上。详见[WeakReferenceDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/jvm/WeakReferenceDemo.java)

### WeakHashMap

传统的`HashMap`就算`key==null`了，也不会回收键值对。但是如果是`WeakHashMap`，一旦内存不够用时，且`key==null`时，会回收这个键值对。详见[WeakHashMapDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/jvm/WeakHashMapDemo.java)。

## 虚引用

软应用和弱引用可以通过`get()`方法获得对象，但是虚引用不行。虚引形同虚设，在任何时候都可能被GC，不能单独使用，必须配合**引用队列（ReferenceQueue）**来使用。设置虚引用的**唯一目的**，就是在这个对象被回收时，收到一个**通知**以便进行后续操作，有点像`Spring`的后置通知。详见[PhantomReferenceDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/jvm/PhantomReferenceDemo.java)。

## 引用队列

弱引用、虚引用被回收后，会被放到引用队列里面，通过`poll`方法可以得到。关于引用队列和弱、虚引用的配合使用，见[ReferenceQueueDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/jvm/ReferenceQueueDemo.java)。

# OutOfMemoryError
