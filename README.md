# JUC、JMM核心知识点笔记

尚硅谷周阳老师课程——[互联网大厂高频重点面试题第2季](https://www.bilibili.com/video/av48961087/)笔记。

#JUC部分

[JUC](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/docs/JUC.md)

- JMM
- volatile关键字
  - 可见性
  - 原子性
  - 有序性
  - 哪些地方用到过volatile？
    - 单例模式的安全问题
- CAS
  - CAS底层原理
  - CAS缺点
- ABA问题
  - AtomicReference
  - AtomicStampedReference和ABA问题的解决
- 集合类不安全问题
  - List
    - CopyOnWriteArrayList
  - Set
    - HashSet和HashMap
  - Map
- Java锁
  - 公平锁/非公平锁
  - 可重入锁/递归锁
    - 锁的配对
  - 自旋锁
  - 读写锁/独占/共享锁
  - Synchronized和Lock的区别
- CountDownLatch/CyclicBarrier/Semaphore
  - CountDownLatch
    - 枚举类的使用
  - CyclicBarrier
  - Semaphore
- 阻塞队列
  - SynchronousQueue
- Callable接口
- 阻塞队列的应用——生产者消费者
  - 传统模式
  - 阻塞队列模式
- 阻塞队列的应用——线程池
  - 线程池基本概念
  - 线程池三种常用创建方式
  - 线程池创建的七个参数
  - 线程池底层原理
  - 线程池的拒绝策略
  - 实际生产使用哪一个线程池？
    - 自定义线程池参数选择
- 死锁编码和定位
