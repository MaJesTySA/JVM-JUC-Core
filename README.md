# JUC、JMM核心知识点笔记

尚硅谷周阳老师课程——[互联网大厂高频重点面试题第2季](https://www.bilibili.com/video/av48961087/)笔记。

#JUC部分
[JUC](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/docs/JUC.md)
- JUC、JMM核心知识点笔记](#juc、jmm核心知识点笔记)
- JMM
- volatile关键字
  - 可见性
  - 原子性
  - 有序性
  - 哪些地方用到过volatile？
    - 单例模式的安全问题
- [CAS](#cas)
  - [CAS底层原理](#cas底层原理)
  - [CAS缺点](#cas缺点)
- [ABA问题](#aba问题)
  - [AtomicReference](#atomicreference)
  - [AtomicStampedReference和ABA问题的解决](#atomicstampedreference和aba问题的解决)
- [集合类不安全问题](#集合类不安全问题)
  - [List](#list)
    - [CopyOnWriteArrayList](#copyonwritearraylist)
  - [Set](#set)
    - [HashSet和HashMap](#hashset和hashmap)
  - [Map](#map)
- [Java锁](#java锁)
  - [公平锁/非公平锁](#公平锁非公平锁)
  - [可重入锁/递归锁](#可重入锁递归锁)
    - [锁的配对](#锁的配对)
  - [自旋锁](#自旋锁)
  - [读写锁/独占/共享锁](#读写锁独占共享锁)
  - [Synchronized和Lock的区别](#synchronized和lock的区别)
- [CountDownLatch/CyclicBarrier/Semaphore](#countdownlatchcyclicbarriersemaphore)
  - [CountDownLatch](#countdownlatch)
    - [枚举类的使用](#枚举类的使用)
  - [CyclicBarrier](#cyclicbarrier)
  - [Semaphore](#semaphore)
- [阻塞队列](#阻塞队列)
  - [SynchronousQueue](#synchronousqueue)
- [Callable接口](#callable接口)
- [阻塞队列的应用——生产者消费者](#阻塞队列的应用生产者消费者)
  - [传统模式](#传统模式)
  - [阻塞队列模式](#阻塞队列模式)
- [阻塞队列的应用——线程池](#阻塞队列的应用线程池)
  - [线程池基本概念](#线程池基本概念)
  - [线程池三种常用创建方式](#线程池三种常用创建方式)
  - [线程池创建的七个参数](#线程池创建的七个参数)
  - [线程池底层原理](#线程池底层原理)
  - [线程池的拒绝策略](#线程池的拒绝策略)
  - [实际生产使用哪一个线程池？](#实际生产使用哪一个线程池？)
    - [自定义线程池参数选择](#自定义线程池参数选择)
- [死锁编码和定位](
