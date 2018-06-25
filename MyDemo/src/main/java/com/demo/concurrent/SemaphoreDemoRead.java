package com.demo.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Semaphore也是一个线程同步的辅助类，可以维护当前访问自身的线程个数，并提供了同步机制。
 * 使用Semaphore可以控制同时访问资源的线程个数，例如，实现一个文件允许的并发访问数
 * 
 * 一个计数信号量。从概念上讲，信号量维护了一个许可集。如有必要，在许可可用前会阻塞每一个 acquire()，然后再获取该许可。
 * 每个 release()添加一个许可，从而可能释放一个正在阻塞的获取者。但是，不使用实际的许可对象，Semaphore
 * 只对可用许可的号码进行计数，并采取相应的行动。拿到信号量的线程可以进入代码，否则就等待。
 * 通过acquire()和release()获取和释放访问许可。
 * 
 * https://my.oschina.net/cloudcoder/blog/362974
 * @author Liujianjia
 */
public class SemaphoreDemoRead {
   private void testConstruction() {
      /**
       * 创建一个非公平同步器->NonfairSync extends Sync
       * 并将AQS的state设置成3
       */
      Semaphore sp = new Semaphore(3);
      
      /**
       * fair=true,创建一个FairSync extends Sync
       * 同样将AQS的state设置成传入的3
       */
      sp = new Semaphore(3, true);
   }

   private void test() throws InterruptedException {
      Semaphore sp = new Semaphore(3);
      
      /**
       * 从此信号量获取一个许可，在提供一个许可前一直将线程阻塞，否则线程被中断。
       * 1)sync.acquireSharedInterruptibly(1)
       * 2)如果线程已被终端，则抛出InterruptedException异常
       * 3)tryAcquireShared(默认Semaphore使用的是NonfairSync，非公平模式在NonfairSync中重写，
       *   公平模式在FairSync中重写)获取锁，如果获取失败，调用AQS的doAcquireSharedInterruptibly获取锁，或者挂起
       */
      sp.acquire();
      
      /**
       * 从此信号量获取给定数目的许可，在提供这些许可前一直将线程阻塞，或者线程已被中断。
       */
      sp.acquire(2);
      
      /**
       * 同上，不可中断
       * 从此信号量中获取许可，在有可用的许可前将其阻塞。
       */
      sp.acquireUninterruptibly();
      sp.acquireUninterruptibly(3);
      
      /**
       * AQS的releaseShared
       */
      sp.release();
      sp.release(2);

      /**
       * 以下同AQS里逻辑雷同
       */
      sp.availablePermits();
      sp.drainPermits();
      sp.getQueueLength();
      sp.hasQueuedThreads();
      sp.isFair();
      
      sp.tryAcquire();
      sp.tryAcquire(1000, TimeUnit.MILLISECONDS);
      sp.tryAcquire(2, 1000, TimeUnit.MILLISECONDS);
   }

   public static void main(String[] args) {
      ExecutorService service = Executors.newCachedThreadPool();
      final Semaphore sp = new Semaphore(3);// 创建Semaphore信号量，初始化许可大小为3

      for(int i = 0; i < 10; i++) {
         try {
            Thread.sleep(100);
         }
         catch(InterruptedException e2) {
            e2.printStackTrace();
         }

         Runnable runnable = new Runnable() {
            public void run() {
               try {
                  System.err.println("请求获得许可.....");
                  sp.acquire();// 请求获得许可，如果有可获得的许可则继续往下执行，许可数减1。否则进入阻塞状态
               }
               catch(InterruptedException e1) {
                  e1.printStackTrace();
               }

               System.out.println("线程" + Thread.currentThread().getName() + "进入，当前已有" + (3 - sp.availablePermits()) + "个并发");

               try {
                  Thread.sleep((long) (Math.random() * 10000));
               }
               catch(InterruptedException e) {
                  e.printStackTrace();
               }

               System.out.println("线程" + Thread.currentThread().getName() + "即将离开");
               sp.release();// 释放许可，许可数加1
               // 下面代码有时候执行不准确，因为其没有和上面的代码合成原子单元
               System.out.println("线程" + Thread.currentThread().getName() + "已离开，当前已有" + (3 - sp.availablePermits()) + "个并发");
            }
         };

         service.execute(runnable);
      }
   }

}
