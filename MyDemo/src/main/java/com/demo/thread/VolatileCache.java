package com.demo.thread;

import java.util.concurrent.CountDownLatch;

public class VolatileCache {
   private static final int threadQuantity = 1000;
   public static CountDownLatch startLatch = new CountDownLatch(threadQuantity);
   public static CountDownLatch endLatch = new CountDownLatch(threadQuantity);
   public volatile static int count = 0;

   public static void inc() {
      count++;
   }

   public static void main(String[] args) {
      // 同时启动1000个线程，去进行i++计算，看看实际结果
      for(int i = 0; i < threadQuantity; i++) {
         new Thread(new Runnable() {
            public void run() {
               String name = Thread.currentThread().getName();
               try {
                  startLatch.await();
               }
               catch(InterruptedException e) {
                  e.printStackTrace();
               }
               int got = VolatileCache.count;
               VolatileCache.count = got + 1;
               System.out.println(got + ",->," + VolatileCache.count + "," + name);
               endLatch.countDown();
            }
         }, "Thread-" + i).start();
         startLatch.countDown();
      }
      try {
         System.out.println("Waiting Work Done");
         endLatch.await();
         System.out.println("运行结果:Counter.count=" + VolatileCache.count);
      }
      catch(InterruptedException e) {
         e.printStackTrace();
      }
   }
}