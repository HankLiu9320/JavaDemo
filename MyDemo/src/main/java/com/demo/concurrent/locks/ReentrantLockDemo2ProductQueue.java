package com.demo.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import sun.util.TimeZoneNameUtility;

/**
 * 测试await
 * 问题1：await怎么解锁的，为什么await后可以在take方法中继续lock
 * @author Administrator
 *
 * @param <T>
 */
public class ReentrantLockDemo2ProductQueue<T> {
   private final T[] items;
   private final Lock lock = new ReentrantLock();
   private Condition notFull = lock.newCondition();
   private Condition notEmpty = lock.newCondition();

   //
   private int head, tail, count;

   public ReentrantLockDemo2ProductQueue(int maxSize) {
      items = (T[]) new Object[maxSize];
   }

   public ReentrantLockDemo2ProductQueue() {
      this(10);
   }

   public void put(T t) throws InterruptedException {
      lock.lock();
System.err.println(Thread.currentThread().getName() + " get Lock!");

      try {
         while(count == getCapacity()) {
System.err.println(Thread.currentThread().getName() + " await!");
            notFull.await();
         }

         items[tail] = t;

         if(++tail == getCapacity()) {
            tail = 0;
         }

         ++count;
         notEmpty.signalAll();
      }
      finally {
         lock.unlock();
System.err.println("put finish:" + t);
      }
   }

   public T take() throws InterruptedException {
      lock.lock();
System.err.println(Thread.currentThread().getName() + " get Lock!");
      try {
         while(count == 0) {
System.err.println(Thread.currentThread().getName() + " await");
            notEmpty.await();
         }

         T ret = items[head];
         items[head] = null;// GC
         //
         if(++head == getCapacity()) {
            head = 0;
         }
         --count;
         notFull.signalAll();
         return ret;
      }
      finally {
         lock.unlock();
System.err.println("take finish");
      }
   }

   public int getCapacity() {
      return items.length;
   }

   public int size() {
      lock.lock();
      try {
         return count;
      }
      finally {
         lock.unlock();
      }
   }

   public static void main(String[] args) {
      final ReentrantLockDemo2ProductQueue<Long> t = new ReentrantLockDemo2ProductQueue<Long>();

      Thread a = new Thread(new Runnable() {
         @Override
         public void run() {
            while(true) {
               try {
                  Thread.sleep(200);
                  t.put(System.currentTimeMillis());
               }
               catch(InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
            }
         }
      }, "put thread");
      
      Thread b = new Thread(new Runnable() {
         @Override
         public void run() {
            while(true) {
               try {
                  Thread.sleep(100);
                  long rs = t.take();
                  System.err.println("rs:" + rs);
               }
               catch(InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
            }
         }
      }, "take thread");
      
      a.start();
      b.start();
   }
}