package thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo {
    public static void main(String[] args) {
        Phone phone=new Phone();
        syncTest(phone);
        System.out.println();

        Thread t3=new Thread(phone);
        Thread t4=new Thread(phone);
        t3.start();
        t4.start();

    }

    private static void syncTest(Phone phone) {

        new Thread(()->{
            try{
                phone.sendSMS();
            }catch (Exception e){
                e.printStackTrace();
            }
        },"t1").start();

        new Thread(()->{
            try{
                phone.sendSMS();
            }catch (Exception e){
                e.printStackTrace();
            }
        },"t2").start();
    }
}
class Phone implements Runnable{
    //Synchronized TEST

    public synchronized void sendSMS(){
        System.out.println(Thread.currentThread().getId()+"\t"+"sendSMS()");
        sendEmail();
    }
    public synchronized void sendEmail(){
        System.out.println(Thread.currentThread().getId()+"\t"+"sendEmail()");
    }

    //Reentrant TEST

    Lock lock=new ReentrantLock();
    @Override
    public void run() {
        get();
    }
    public void get(){
        lock.lock();
        try{
            System.out.println(Thread.currentThread().getId()+"\t"+"get()");
            set();
        }finally {
            lock.unlock();
        }
    }
    public void set(){
        lock.lock();
        try{
            System.out.println(Thread.currentThread().getId()+"\t"+"set()");
        }finally {
            lock.unlock();
        }
    }
}
