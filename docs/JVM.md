# JVM核心知识点
- [Java8  JVM内存结构](#java8--jvm内存结构)
- [GC Roots](#gc-roots)
  - [如果判断一个对象可以被回收？](#如果判断一个对象可以被回收？)
    - [引用计数算法](#引用计数算法)
    - [可达性分析算法](#可达性分析算法)
  - [哪些对象可以作为GC Roots？](#哪些对象可以作为gc-roots？)
- [JVM参数](#jvm参数)
  - [JVM 三种类型参数](#jvm-三种类型参数)
    - [标配参数](#标配参数)
    - [X参数](#x参数)
    - [XX参数](#xx参数)
  - [JVM XX参数](#jvm-xx参数)
    - [布尔类型](#布尔类型)
    - [KV键值类型](#kv键值类型)
  - [JVM Xms/Xmx参数](#jvm-xmsxmx参数)
  - [JVM 查看参数](#jvm-查看参数)
    - [查看某个参数](#查看某个参数)
    - [查看所有参数](#查看所有参数)
    - [查看修改后的参数](#查看修改后的参数)
    - [查看常见参数](#查看常见参数)
  - [JVM 常用参数](#jvm-常用参数)
    - [-Xmx/-Xms](#-xmx-xms)
    - [-Xss](#-xss)
    - [-Xmn](#-xmn)
    - [-XX:MetaspaceSize](#-xxmetaspacesize)
    - [-XX:+PrintGCDetails](#-xxprintgcdetails)
    - [-XX:SurvivorRatio](#-xxsurvivorratio)
    - [-XX:NewRatio](#-xxnewratio)
    - [-XX:MaxTenuringThreshold](#-xxmaxtenuringthreshold)
- [四大引用](#四大引用)
  - [强引用](#强引用)
  - [软引用](#软引用)
  - [弱引用](#弱引用)
    - [WeakHashMap](#weakhashmap)
  - [虚引用](#虚引用)
  - [引用队列](#引用队列)
- [OutOfMemoryError](#outofmemoryerror)
  - [StackOverflowError](#stackoverflowerror)
  - [OOM—Java head space](#oomjava-head-space)
  - [OOM—GC overhead limit exceeded](#oomgc-overhead-limit-exceeded)
  - [OOM—GC Direct buffer memory](#oomgc-direct-buffer-memory)
  - [OOM—unable to create new native thread](#oomunable-to-create-new-native-thread)
  - [OOM—Metaspace](#oommetaspace)
- [JVM垃圾收集器](#jvm垃圾收集器)
  - [四大垃圾收集算法](#四大垃圾收集算法)
    - [标记整理](#标记整理)
    - [标记清除](#标记清除)
    - [复制算法](#复制算法)
    - [分代收集算法](#分代收集算法)
  - [四种垃圾收集器](#四种垃圾收集器)
    - [串行收集器Serial](#串行收集器serial)
    - [并行收集器Parrallel](#并行收集器parrallel)
    - [并发收集器CMS](#并发收集器cms)
    - [G1收集器](#g1收集器)
  - [默认垃圾收集器](#默认垃圾收集器)
    - [默认收集器有哪些？](#默认收集器有哪些？)
    - [查看默认垃圾修改器](#查看默认垃圾修改器)
  - [七大垃圾收集器](#七大垃圾收集器)
    - [体系结构](#体系结构)
    - [Serial收集器](#serial收集器)
    - [ParNew收集器](#parnew收集器)
    - [Parallel Scavenge收集器](#parallel-scavenge收集器)
    - [SerialOld收集器](#serialold收集器)
    - [ParallelOld收集器](#parallelold收集器)
    - [CMS收集器](#cms收集器)
      - [过程](#过程)
      - [优缺点](#优缺点)
    - [G1收集器](#g1收集器-1)
      - [特点](#特点)
      - [过程](#过程-1)
- [附—Linux相关指令](#附linux相关指令)
  - [top](#top)
  - [vmstat](#vmstat)
  - [pidstat](#pidstat)
  - [free](#free)
  - [df](#df)
  - [iostat](#iostat)
  - [ifstat](#ifstat)
- [CPU占用过高原因定位](#cpu占用过高原因定位)
- [JVM性能调优和监控工具](#jvm性能调优和监控工具)
  - [jps](#jps)
  - [jstack](#jstack)
  - [jinfo/jstat](#jinfojstat)
  - [jmap](#jmap)

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

## StackOverflowError

栈满会抛出该错误。无限递归就会导致StackOverflowError，是`java.lang.Throwable`→`java.lang.Error`→`java.lang.VirtualMachineError`下的错误。详见[StackOverflowErrorDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/jvm/StackOverflowErrorDemo.java)。

## OOM—Java head space

栈满会抛出该错误。详见[JavaHeapSpaceDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/jvm/JavaHeapSpaceDemo.java)。

## OOM—GC overhead limit exceeded

这个错误是指：GC的时候会有“Stop the World"，STW越小越好，正常情况是GC只会占到很少一部分时间。但是如果用超过98%的时间来做GC，而且收效甚微，就会被JVM叫停。下例中，执行了多次`Full GC`，但是内存回收很少，最后抛出了`OOM:GC overhead limit exceeded`错误。详见[GCOverheadDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/jvm/GCOverheadDemo.java)。

```java
[GC (Allocation Failure) [PSYoungGen: 2048K->496K(2560K)] 2048K->960K(9728K), 0.0036555 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[GC (Allocation Failure) [PSYoungGen: 2544K->489K(2560K)] 3008K->2689K(9728K), 0.0060306 secs] [Times: user=0.08 sys=0.00, real=0.01 secs] 
[GC (Allocation Failure) [PSYoungGen: 2537K->512K(2560K)] 4737K->4565K(9728K), 0.0050620 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[GC (Allocation Failure) [PSYoungGen: 2560K->496K(2560K)] 6613K->6638K(9728K), 0.0064025 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 

[Full GC (Ergonomics) [PSYoungGen: 2048K->860K(2560K)] [ParOldGen: 6264K->7008K(7168K)] 8312K->7869K(9728K), [Metaspace: 3223K->3223K(1056768K)], 0.1674947 secs] [Times: user=0.63 sys=0.00, real=0.17 secs] 
[Full GC (Ergonomics) [PSYoungGen: 2048K->2006K(2560K)] [ParOldGen: 7008K->7008K(7168K)] 9056K->9015K(9728K), [Metaspace: 3224K->3224K(1056768K)], 0.1048666 secs] [Times: user=0.45 sys=0.00, real=0.10 secs] 
[Full GC (Ergonomics) [PSYoungGen: 2047K->2047K(2560K)] [ParOldGen: 7082K->7082K(7168K)] 9130K->9130K(9728K), [Metaspace: 3313K->3313K(1056768K)], 0.0742516 secs] [Times: user=0.28 sys=0.00, real=0.07 secs] 

·······

[Full GC (Ergonomics) [PSYoungGen: 2047K->2047K(2560K)] [ParOldGen: 7084K->7084K(7168K)] 9132K->9132K(9728K), [Metaspace: 3313K->3313K(1056768K)], 0.0738461 secs] [Times: user=0.36 sys=0.02, real=0.07 secs] 

Exception in thread "main" [Full GC (Ergonomics) [PSYoungGen: 2047K->0K(2560K)] [ParOldGen: 7119K->647K(7168K)] 9167K->647K(9728K), [Metaspace: 3360K->3360K(1056768K)], 0.0129597 secs] [Times: user=0.11 sys=0.00, real=0.01 secs] 
java.lang.OutOfMemoryError: GC overhead limit exceeded
	at java.lang.Integer.toString(Integer.java:401)
	at java.lang.String.valueOf(String.java:3099)
	at jvm.GCOverheadDemo.main(GCOverheadDemo.java:12)
```

## OOM—GC Direct buffer memory

在写`NIO`程序的时候，会用到`ByteBuffer`来读取和存入数据。与Java堆的数据不一样，`ByteBuffer`使用`native`方法，直接在**堆外分配内存**。当堆外内存（也即本地物理内存）不够时，就会抛出这个异常。详见[DirectBufferMemoryDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/jvm/DirectBufferMemoryDemo.java)。

## OOM—unable to create new native thread

在高并发应用场景时，如果创建超过了系统默认的最大线程数，就会抛出该异常。Linux单个进程默认不能超过1024个线程。**解决方法**要么降低程序线程数，要么修改系统最大线程数`vim /etc/security/limits.d/90-nproc.conf`。详见[UnableCreateNewThreadDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/jvm/UnableCreateNewThreadDemo.java)

## OOM—Metaspace

元空间满了就会抛出这个异常。

# JVM垃圾收集器

## 四大垃圾收集算法

### 标记整理

![](https://raw.githubusercontent.com/MaJesTySA/JVM-JUC-Core/master/imgs/GCbq.png)

![](https://raw.githubusercontent.com/MaJesTySA/JVM-JUC-Core/master/imgs/GCbq2.png)

### 标记清除

![](https://raw.githubusercontent.com/MaJesTySA/JVM-JUC-Core/master/imgs/GCbq.png)

![](https://raw.githubusercontent.com/MaJesTySA/JVM-JUC-Core/master/imgs/GCbz.png)

### 复制算法

![](https://raw.githubusercontent.com/MaJesTySA/JVM-JUC-Core/master/imgs/GCfz.png)

![](https://raw.githubusercontent.com/MaJesTySA/JVM-JUC-Core/master/imgs/GCfz2.png)

### 分代收集算法

准确来讲，跟前面三种算法有所区别。分代收集算法就是根据对象的年代，采用上述三种算法来收集。

1. 对于新生代：每次GC都有大量对象死去，存活的很少，常采用复制算法，只需要拷贝很少的对象。
2. 对于老年代：常采用标整或者标清算法。

## 四种垃圾收集器

Java 8可以将垃圾收集器分为四类。

### 串行收集器Serial

为单线程环境设计且**只使用一个线程**进行GC，会暂停所有用户线程，不适用于服务器。就像去餐厅吃饭，只有一个清洁工在打扫。

### 并行收集器Parrallel

使用**多个线程**并行地进行GC，会暂停所有用户线程，适用于科学计算、大数据后台，交互性不敏感的场合。多个清洁工同时在打扫。

### 并发收集器CMS

用户线程和GC线程同时执行（不一定是并行，交替执行），GC时不需要停顿用户线程，互联网公司多用，适用对响应时间有要求的场合。清洁工打扫的时候，也可以就餐。

### G1收集器

对内存的划分与前面3种很大不同，将堆内存分割成不同的区域，然后并发地进行垃圾回收。

## 默认垃圾收集器

### 默认收集器有哪些？

有`Serial`、`Parallel`、`ConcMarkSweep`（CMS）、`ParNew`、`ParallelOld`、`G1`。还有一个`SerialOld`，快被淘汰了。

### 查看默认垃圾修改器

使用`java -XX:+PrintCommandLineFlags`即可看到，Java 8默认使用`-XX:+UseParallelGC`。

```java
-XX:InitialHeapSize=132375936 -XX:MaxHeapSize=2118014976 -XX:+PrintCommandLineFlags -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:-UseLargePagesIndividualAllocation -XX:+UseParallelGC
```

## 七大垃圾收集器

### 体系结构

`Serial`、`Parallel Scavenge`、`ParNew`用户回收新生代；`SerialOld`、`ParallelOld`、`CMS`用于回收老年代。而`G1`收集器，既可以回收新生代，也可以回收老年代。

![](https://raw.githubusercontent.com/MaJesTySA/JVM-JUC-Core/master/imgs/GCqi.png)

连线表示可以搭配使用，红叉表示不推荐一同使用，比如新生代用`Serial`，老年代用`CMS`。

![](https://raw.githubusercontent.com/MaJesTySA/JVM-JUC-Core/master/imgs/GCqi2.png)



### Serial收集器

年代最久远，是`Client VM`模式下的默认新生代收集器，使用**复制算法**。**优点**：单个线程收集，没有线程切换开销，拥有最高的单线程GC效率。**缺点**：收集的时候会暂停用户线程。

使用`-XX:+UseSerialGC`可以显式开启，开启后默认使用`Serial`+`SerialOld`的组合。

![](https://raw.githubusercontent.com/MaJesTySA/JVM-JUC-Core/master/imgs/serial.jpeg)

### ParNew收集器

也就是`Serial`的多线程版本，GC的时候不再是一个线程，而是多个，是`Server VM`模式下的默认新生代收集器，采用**复制算法**。

使用`-XX:+UseParNewGC`可以显式开启，开启后默认使用`ParNew`+`SerialOld`的组合。但是由于`SerialOld`已经过时，所以建议配合`CMS`使用。

![](https://raw.githubusercontent.com/MaJesTySA/JVM-JUC-Core/master/imgs/parnew.jpeg)

### Parallel Scavenge收集器

`ParNew`收集器仅在新生代使用多线程收集，老年代默认是`SerialOld`，所以是单线程收集。而`Parallel Scavenge`在新、老两代都采用多线程收集。`Parallel Scavenge`还有一个特点就是**吞吐量优先收集器**，可以通过自适应调节，保证最大吞吐量。采用**复制算法**。

使用`-XX:+UseParallelGC`可以开启， 同时也会使用`ParallelOld`收集老年代。其它参数，比如`-XX:ParallelGCThreads=N`可以选择N个线程进行GC，`-XX:+UseAdaptiveSizePolicy`使用自适应调节策略。

### SerialOld收集器

`Serial`的老年代版本，采用**标整算法**。JDK1.5之前跟`Parallel Scavenge`配合使用，现在已经不了，作为`CMS`的后备收集器。

### ParallelOld收集器

`Parallel`的老年代版本，JDK1.6之前，新生代用`Parallel`而老年代用`SerialOld`，只能保证新生代的吞吐量。JDK1.8后，老年代改用`ParallelOld`。

使用`-XX:+UseParallelOldGC`可以开启， 同时也会使用`Parallel`收集新生代。

### CMS收集器

并发标记清除收集器，是一种以获得**最短GC停顿为**目标的收集器。适用在互联网或者B/S系统的服务器上，这类应用尤其重视服务器的**响应速度**，希望停顿时间最短。是`G1`收集器出来之前的首选收集器。使用**标清算法**。在GC的时候，会与用户线程并发执行，不会停顿用户线程。但是在**标记**的时候，仍然会**STW**。

使用`-XX:+UseConcMarkSweepGC`开启。开启过后，新生代默认使用`ParNew`，同时老年代使用`SerialOld`作为备用。

![](https://raw.githubusercontent.com/MaJesTySA/JVM-JUC-Core/master/imgs/cms.jpeg)

#### 过程

1. **初始标记**：只是标记一下GC Roots能直接关联的对象，速度很快，需要**STW**。
2. **并发标记**：主要标记过程，标记全部对象，和用户线程一起工作，不需要STW。
3. **重新标记**：修正在并发标记阶段出现的变动，需要**STW**。
4. **并发清除**：和用户线程一起，清除垃圾，不需要STW。

#### 优缺点

**优点**：停顿时间少，响应速度快，用户体验好。

**缺点**：

1. 对CPU资源非常敏感：由于需要并发工作，多少会占用系统线程资源。
2. 无法处理浮动垃圾：由于标记垃圾的时候，用户进程仍然在运行，无法有效处理新产生的垃圾。
3. 产生内存碎片：由于使用**标清算法**，会产生内存碎片。

### G1收集器

`G1`收集器与之前垃圾收集器的一个显著区别就是——之前收集器都有三个区域，新、老两代和元空间。而G1收集器只有G1区和元空间。而G1区，不像之前的收集器，分为新、老两代，而是一个一个Region，每个Region既可能包含新生代，也可能包含老年代。

`G1`收集器既可以提高吞吐量，又可以减少GC时间。最重要的是**STW可控**，增加了预测机制，让用户指定停顿时间。

使用`-XX:+UseG1GC`开启，还有`-XX:G1HeapRegionSize=n`、`-XX:MaxGCPauseMillis=n`等参数可调。

#### 特点

1. **并行和并发**：充分利用多核、多线程CPU，尽量缩短STW。
2. **分代收集**：虽然还保留着新、老两代的概念，但物理上不再隔离，而是融合在Region中。
3. **空间整合**：`G1`整体上看是**标整**算法，在局部看又是**复制算法**，不会产生内存碎片。
4. **可预测停顿**：用户可以指定一个GC停顿时间，`G1`收集器会尽量满足。

#### 过程

与`CMS`类似。

1. 初始标记。
2. 并发标记。
3. 最终标记。
4. 筛选回收。

# 附—Linux相关指令

## top

主要查看`%CPU`、`%MEM`，还有`load average`。`load average`后面的三个数字，表示系统1分钟、5分钟、15分钟的平均负载值。如果三者平均值高于0.6，则复杂比较高了。当然，用`uptime`也可以查看。

## vmstat

查看进程、内存、I/O等多个系统运行状态。2表示每两秒采样一次，3表示一共采样3次。`procs`的`r`表示运行和等待CPU时间片的进程数，原则上1核CPU不要超过2。`b`是等待资源的进程数，比如磁盘I/O、网络I/O等。

```shell
[root@ ~]# vmstat -n 2 3
procs -----------memory---------- ---swap-- -----io---- -system-- ------cpu-----
 r  b   swpd   free   buff  cache   si   so    bi    bo   in   cs us sy id wa st
 2  0      0 173188 239748 1362628    0    0     0     3   17    8  0  0 99  0  0
 0  0      0 172800 239748 1362636    0    0     0     0  194  485  1  1 99  0  0
 1  0      0 172800 239748 1362640    0    0     0     0  192  421  1  1 99  0  0
```

## pidstat

查看某个进程的运行信息。

## free

查看内存信息。

## df

查看磁盘信息。

## iostat

查看磁盘I/O信息。比如有时候MySQL在查表的时候，会占用大量磁盘I/O，体现在该指令的`%util`字段很大。对于死循环的程序，CPU占用固然很高，但是磁盘I/O不高。

## ifstat

查看网络I/O信息，需要安装。

# CPU占用过高原因定位

先用`top`找到CPU占用最高的进程，然后用`ps -mp pid -o THREAD,tid,time`，得到该**进程**里面占用最高的**线程**。这个线程是10进制的，将其转成16进制，然后用`jstack pid | grep tid`可以定位到具体哪一行导致了占用过高。

# JVM性能调优和监控工具

## jps

Java版的`ps -ef`查看所有JVM进程。

## jstack

查看JVM中运行线程的状态，比较重要。可以定位CPU占用过高位置，定位死锁位置。

## jinfo/jstat

`jinfo`查看JVM的运行环境参数，比如默认的JVM参数等。`jstat`统计信息监视工具。

## jmap

JVM内存映像工具。
