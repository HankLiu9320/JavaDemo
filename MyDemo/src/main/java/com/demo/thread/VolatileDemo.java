package com.demo.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * inc
 * 4002ms, count:0,count2:10000000
 * 4023ms, count:0,count2:10000000
 * 4046ms, count:0,count2:10000000
 * 57643ms, count:0,count2:500000000
 * 
 * inc2
 * 4432ms, count:10000000,count2:0
 * 3847ms, count:10000000,count2:0
   3899ms, count:10000000,count2:0
   3889ms, count:10000000,count2:0
   
   11454ms, count:500000000,count2:0
   11439ms, count:500000000,count2:0
   
   4465ms, count:500000000,count2:0
 *
 *单线程：
 *3ms, count:500000000
 */
public class VolatileDemo {
   private  static long count = 0;
   private static AtomicLong count2 = new AtomicLong(0);

   private static void inc() {
      count2.incrementAndGet();
   }

   private static synchronized void inc2() {
      count++;
   }

   private static long getCount() {
      return count;
   }

   public static void main1(String[] args) throws InterruptedException {
      int count = 1000;
      final CountDownLatch d = new CountDownLatch(1);
      final CountDownLatch d1 = new CountDownLatch(count);

      long start = System.currentTimeMillis();

      for(int i = 0; i < count; i++) {
         Thread t1 = new Thread() {
            public void run() {
               try {
                  d.await();
               }
               catch(InterruptedException e) {
                  e.printStackTrace();
               }

               for(int j = 0; j < 500000; j++) {
//                  inc();
                  inc2();
               }
               d1.countDown();
            }
         };

         t1.start();
      }

      d.countDown();
      d1.await();
      long end = System.currentTimeMillis();
      System.err.println((end - start) + "ms, count:" + getCount() + ",count2:" + count2.get());
   }

   public static void main(String[] args) {
      long start = System.currentTimeMillis();
      int count = 0;

      for(int i = 0; i < 500000000; i++) {
         count++;
      }
      long end = System.currentTimeMillis();
      System.err.println((end - start) + "ms, count:" + count);
   }
}
