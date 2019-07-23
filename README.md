# JUC、JMM核心知识点笔记

尚硅谷周阳老师课程——[互联网大厂高频重点面试题第2季](https://www.bilibili.com/video/av48961087/)笔记

# JMM

JMM是指Java**内存模型**，不是Java**内存布局**，不是所谓的栈、堆、方法区。

每个Java线程都有自己的**工作内存**。操作数据，首先从主内存中读，得到一份拷贝，操作完毕后再写回到主内存。

![](https://raw.githubusercontent.com/MaJesTySA/JVM-JUC-Core/master/imgs/JMM.png)

JMM可能带来**可见性**、**原子性**和**有序性**问题。所谓可见性，就是某个线程对主内存内容的更改，应该立刻通知到其它线程。原子性是指一个操作是不可分割的，不能执行到一半，就不执行了。所谓有序性，就是指令是有序的，不会被重排。

# volatile关键字

volatile关键字是Java提供的一种**轻量级**同步机制。它能够保证**可见性**和**有序性**，但是不能保证**原子性**。

## 可见性

[可见性测试](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/thread/VolatileDemo.java)

```java
class MyData{
    int number=0;
    //volatile int number=0;

    AtomicInteger atomicInteger=new AtomicInteger();
    public void setTo60(){
        this.number=60;
    }

    //此时number前面已经加了volatile，但是不保证原子性
    public void addPlusPlus(){
        number++;
    }

    public void addAtomic(){
        atomicInteger.getAndIncrement();
    }
}

//volatile可以保证可见性，及时通知其它线程主物理内存的值已被修改
private static void volatileVisibilityDemo() {
	System.out.println("可见性测试");
	MyData myData=new MyData();//资源类
	//启动一个线程操作共享数据
	new Thread(()->{
		System.out.println(Thread.currentThread().getName()+"\t come in");
	try {TimeUnit.SECONDS.sleep(3);myData.setTo60();
         System.out.println(Thread.currentThread().getName()+"\t update number value: "+myData.number);}catch (InterruptedException e){e.printStackTrace();}
	},"AAA").start();
	while (myData.number==0){
 	//main线程持有共享数据的拷贝，一直为0
	}
	System.out.println(Thread.currentThread().getName()+"\t mission is over. main get number value: "+myData.number);
    }
```

MyData类是资源类，一开始number变量没有用volatile修饰，所以程序运行的结果是：

```java
可见性测试
AAA	 come in
AAA	 update number value: 60
```

虽然一个线程把number修改成了60，但是main线程持有的仍然是最开始的0，所以一直循环，程序不会结束。

如果对number添加了volatile修饰，运行结果是：

```java
AAA	 come in
AAA	 update number value: 60
main	 mission is over. main get number value: 60
```

可见某个线程对number的修改，会立刻反映到主内存上。

## 原子性

volatile并**不能保证操作的原子性**。这是因为，比如一条number++的操作，会形成3条指令。

```assembly
getfield    //读
iconst_1	//++常量1
iadd		//加操作
putfield	//写操作
```

假设有3个线程，分别执行number++，都先从主内存中拿到最开始的值，number=0，然后三个线程分别进行操作。假设线程0执行完毕，number=1，也立刻通知到了其它线程，但是此时线程1、2已经拿到了number=0，所以结果就是写覆盖。

解决的方式就是：①对addPlusPlus()方法加锁。②使用j.u.c.AtomicInteger类。

```java
private static void atomicDemo() {
	System.out.println("原子性测试");
	MyData myData=new MyData();
	for (int i = 1; i <= 20; i++) {
		new Thread(()->{
			for (int j = 0; j <1000 ; j++) {
				myData.addPlusPlus();
				myData.addAtomic();
            }
        },String.valueOf(i)).start();
    }
    while (Thread.activeCount()>2){
        Thread.yield();
    }
    System.out.println(Thread.currentThread().getName()+"\t int type finally number value: "+myData.number);
    System.out.println(Thread.currentThread().getName()+"\t AtomicInteger type finally number value: "+myData.atomicInteger);
    }
```

结果：可见，由于volatile不能保证原子性，出现了线程重复写的问题，最终结果比20000小。

```java
原子性测试
main	 int type finally number value: 17542
main	 AtomicInteger type finally number value: 20000
```

## 有序性

[有序性案例](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/thread/ResortSeqDemo.java)

volatile可以保证**有序性**，也就是防止**指令重排序**。所谓指令重排序，就是出于优化考虑，CPU执行指令的顺序跟程序员自己编写的顺序不一致。就好比一份试卷，题号是老师规定的，是程序员规定的，但是考生（CPU）可以先做选择，也可以先做填空。

```java
int x = 11; //语句1
int y = 12; //语句2
x = x + 5;  //语句3
y = x * x;  //语句4
```

以上例子，可能出现的执行顺序有1234、2134、1342，这三个都没有问题，最终结果都是x = 16，y=256。但是如果是4开头，就有问题了，y=0。这个时候就**不需要**指令重排序。

volatile底层是用CPU的**内存屏障（Memory Barrier）指令**来实现的，有两个作用，一个是保证特定操作的顺序性，二是保证变量的可见性。在指令之间插入一条Memory Barrier指令，告诉编译器和CPU，在Memory Barrier指令之间的指令不能被重排序。

# 单例模式的安全问题

常见的DCL（Double Check Lock）模式虽然加了同步，但是在多线程下依然会有线程安全问题。

```java
public class SingletonDemo {
    private static SingletonDemo singletonDemo=null;
    private SingletonDemo(){
        System.out.println(Thread.currentThread().getName()+"\t 我是构造方法");
    }
    //DCL模式 Double Check Lock 双端检索机制：在加锁前后都进行判断
    public static SingletonDemo getInstance(){
		if (singletonDemo==null){
            synchronized (SingletonDemo.class){
                if (singletonDemo==null){
                    singletonDemo=new SingletonDemo();
                }
            }
        }
        return singletonDemo;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                SingletonDemo.getInstance();
            },String.valueOf(i+1)).start();
        }
    }
}
```

这个漏洞比较tricky，很难捕捉，但是是存在的。`instance=new SingletonDemo();`可以大致分为三步

```java
memory = allocate(); //1.分配内存
instance(memory);	 //2.初始化对象
instance = memory;	 //3.设置引用地址
```

其中2、3没有数据依赖关系，**可能发生重排**。如果发生，此时内存已经分配，那么`instance=memory`不为null。如果此时线程挂起，`instance(memory)`还未执行，对象还未初始化。由于`instance!=null`，所以两次判断都跳过，最后返回的`instance`没有任何内容，还没初始化。

解决的方法就是对`singletondemo`对象添加上`volatile`关键字，禁止指令重排。

