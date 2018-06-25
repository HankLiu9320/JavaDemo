package com.demo.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicDemo {
   //现成不安全
   private static AtomicInteger count = new AtomicInteger(0);
   
   public static void main(String[] args) {
      ExecutorService exes = Executors.newCachedThreadPool();
      
      for(int i = 0; i < 1000; i++) {
         exes.execute(new Runnable() {
            @Override
            public void run() {
               for(int j = 0; j < 10; j++) {
                  count.addAndGet(1);
               }
            }
         });
      }

      exes.shutdown();
      System.err.println("count:" + count);
   }
}
