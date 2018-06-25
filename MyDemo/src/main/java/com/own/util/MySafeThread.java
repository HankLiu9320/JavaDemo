package com.own.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MySafeThread {
   private static AtomicInteger count = new AtomicInteger(0);// 线程安全的计数变量

   public static void main(String[] args) {
      ExecutorService e = Executors.newFixedThreadPool(5);

      for(int i = 0; i < 100; i++) {
         e.submit(new Runnable() {
            @Override
            public void run() {
               for(int i = 0; i < 100000; i++) {
                  count.incrementAndGet();
               }
            }
         });
      }

      e.shutdown();

      try {
         e.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
      }
      catch(InterruptedException e1) {
         e1.printStackTrace();
      }

      System.err.println(count.get());
   }
}