package com.demo.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock 一个可重入的互斥锁 Lock，它具有与使用 synchronized
 * 方法和语句所访问的隐式监视器锁相同的一些基本行为和语义，但功能更强大。 for
 * http://blog.csdn.net/eclipser1987/article/details/7301828
 * 
 * @author ljj
 */
public class ReentrantLockDemoRead {
   private ReentrantLock lock = new ReentrantLock();

   private void testConstruction() {
      ReentrantLock lock = new ReentrantLock();
      /**
       * 使用非公平锁
       * true ->FairSync
       * false ->NonfairSync
       */
      lock = new ReentrantLock(false);
   }

   private void test() {
      ReentrantLock lock = new ReentrantLock(true);
      /**
       * acquire(1); 通过同步队列，排队获得锁
       */
      lock.lock();
      
      lock = new ReentrantLock(false);
      
      /**
       * 1)直接通过CAS方式设置状态compareAndSetState，成功则获得锁
       * 2)失败则通过acquire获得，acquire->tryAcquire->nonfairTryAcquire
       *   nonfairTryAcquire中，如果state==0，则直接设置状态获取，如果当前线程已获得锁，则state累加，并获得锁
       */
      lock.lock();
      
      //lock的其他方法，在AQS类中
   }

   public ReentrantLock getLock() {
      return lock;
   }

   /**
    * 测试查询当前线程保持此锁的次数。
    */
   public void testHoldCount() {
      for(int i = 0; i < 5; i++) {
         new Thread() {
            public void run() {
               lock.lock();
               try {
                  Thread.sleep(2000);
               }
               catch(InterruptedException e) {
                  e.printStackTrace();
               }
               
               lock.lock();
               lock.lock();
               System.err.println("holdCount1:" + lock.getHoldCount() + "," + lock);
               System.err.println(lock.getQueueLength());
               lock.unlock();
               lock.unlock();
               lock.unlock();
            }
         }.start();
      }
   }

   /**
    * 测试tryLock方法
    * @throws InterruptedException 
    */
   public void testTryLock() throws InterruptedException {
      new Thread() {
         public void run() {
            while(true) {
               lock.lock();
               try {
                  Thread.sleep(10000);
               }
               catch(InterruptedException e) {
                  e.printStackTrace();
               }
               lock.unlock();
               break;
            }
         }
      }.start();

      Thread.sleep(2000);
      System.err.println("tryLock1:" + lock.tryLock());
      System.err.println("tryLock2:" + lock.tryLock(3000, TimeUnit.MILLISECONDS));
   }

   public void write() {
      lock.lock();
      try {
         long startTime = System.currentTimeMillis();
         System.out.println("开始往这个buff写入数据…");

         // 模拟要处理很长时间
         for(;;) {
            if(System.currentTimeMillis() - startTime > Integer.MAX_VALUE) {
               break;
            }
         }

         System.out.println("终于写完了");
      }
      finally {
         lock.unlock();
      }
   }

   public void read() throws InterruptedException {
      lock.lockInterruptibly();// 注意这里，可以响应中断

      try {
         System.out.println("从这个buff读数据");
      }
      finally {
         lock.unlock();
      }
   }

   public void testLockInterruptibly() {
      final ReentrantLockDemoRead obj = new ReentrantLockDemoRead();

      final Writer2 writer = new Writer2(obj);
      final Reader2 reader = new Reader2(obj);
      writer.start();
      reader.start();

      new Thread(new Runnable() {
         @Override
         public void run() {
            long start = System.currentTimeMillis();
            for(;;) {
               if(System.currentTimeMillis() - start > 5000) {
                  System.out.println("不等了，尝试中断");
                  reader.interrupt();
                  break;
               }
            }
         }
      }).start();
   }

   public static void main(String[] args) throws InterruptedException {
      final ReentrantLockDemoRead obj = new ReentrantLockDemoRead();
      //test holdcount function
      //obj.testHoldCount();
      
      //test trylock function
      //obj.testTryLock();
      
      //test lockInterruptibly function
      obj.testLockInterruptibly();
   }
}

class Reader2 extends Thread {
   private ReentrantLockDemoRead buff;

   public Reader2(ReentrantLockDemoRead buff) {
      this.buff = buff;
   }

   @Override
   public void run() {
      try {
         buff.read();// 可以收到中断的异常，从而有效退出
      }
      catch(InterruptedException e) {
         System.out.println("我不读了");
      }

      System.out.println("读结束");

   }
}

class Writer2 extends Thread {
   private ReentrantLockDemoRead buff;

   public Writer2(ReentrantLockDemoRead buff) {
      this.buff = buff;
   }

   @Override
   public void run() {
      buff.write();
   }
}
