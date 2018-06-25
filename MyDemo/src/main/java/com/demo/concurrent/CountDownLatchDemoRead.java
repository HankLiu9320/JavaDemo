package com.demo.concurrent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * 一个同步辅助类，在完成一组正在其他线程中执行的操作之前，它允许一个或多个线程一直等待。
 * 
 * 用给定的计数 初始化 CountDownLatch。由于调用了 countDown() 方法，所以在当前计数到达零之前，await
 * 方法会一直受阻塞。之后，会释放所有等待的线程，await 的所有后续调用都将立即返回。这种现象只出现一次——计数无法被重置。如果需要重置计数，请考虑使用
 * CyclicBarrier。
 * 
 * CountDownLatch 是一个通用同步工具，它有很多用途。将计数 1 初始化的 CountDownLatch
 * 用作一个简单的开/关锁存器，或入口：在通过调用 countDown() 的线程打开入口前，所有调用 await 的线程都一直在入口处等待。用 N 初始化的
 * CountDownLatch 可以使一个线程在 N 个线程完成某项操作之前一直等待，或者使其在某项操作完成 N 次之前一直等待。
 * 
 * CountDownLatch 的一个有用特性是，它不要求调用 countDown
 * 方法的线程等到计数到达零时才继续，而在所有线程都能通过之前，它只是阻止任何线程继续通过一个 await。
 * 
 * java.util.concurrent.CountDownLatch 是一个并发构造，它允许一个或多个线程等待一系列指定操作的完成。
 * CountDownLatch 以一个给定的数量初始化。countDown() 每被调用一次，这一数量就减一。通过调用 await()
 * 方法之一，线程可以阻塞等待这一数量到达零。
 */
public class CountDownLatchDemoRead {
   private void testConstruction() {
      /**
       * 创建一个Sync并设置State为3
       */
      CountDownLatch latch = new CountDownLatch(3);
   }

   private void test() throws InterruptedException {
      CountDownLatch latch = new CountDownLatch(3);
      
      /**
       * sync->releaseShared(1)
       */
      latch.countDown();
      
      /**
       * acquireSharedInterruptibly
       * tryAcquireShared ->return (getState() == 0) ? 1 : -1;
       */
      latch.await();

      /**
       * sync getState
       */
      latch.getCount();
   }

   final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

   public static void main(String[] args) throws InterruptedException {
      final CountDownLatch latch = new CountDownLatch(2);// 两个工人的协作
      Worker worker1 = new Worker("zhang san", 5000, latch);
      Worker worker2 = new Worker("li si", 8000, latch);
      worker1.start();//
      worker2.start();//

      // run way 1
      new Thread(new Runnable() {
         @Override
         public void run() {
            try {
               latch.await();
               System.out.println("all work done at " + sdf.format(new Date()));
            }
            catch(InterruptedException e) {
               e.printStackTrace();
            } // 等待所有工人完成工作
         }
      }).start();

      // run way 2
      latch.await();
      System.err.println("finish works");
   }

   static class Worker extends Thread {
      String workerName;
      int workTime;
      CountDownLatch latch;

      public Worker(String workerName, int workTime, CountDownLatch latch) {
         this.workerName = workerName;
         this.workTime = workTime;
         this.latch = latch;
      }

      public void run() {
         System.out.println("Worker " + workerName + " do work begin at " + sdf.format(new Date()));
         doWork();// 工作了
         System.out.println("Worker " + workerName + " do work complete at " + sdf.format(new Date()));
         latch.countDown();// 工人完成工作，计数器减一
         System.err.println("count:" + latch.getCount());
      }

      private void doWork() {
         try {
            Thread.sleep(workTime);
         }
         catch(InterruptedException e) {
            e.printStackTrace();
         }
      }
   }
}
