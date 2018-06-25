package com.demo.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadLocalDemo {
   private static ThreadLocal<Integer> locals = new ThreadLocal<Integer>();

   public static void main(String[] args) {
      ExecutorService exes = Executors.newCachedThreadPool();

      for(int i = 0; i < 1000; i++) {
         exes.execute(new Runnable() {
            @Override
            public void run() {
               int count = 0;
               
               for(int j = 0; j < 10; j++) {
                  count += j;
               }
               locals.set(count);
            }
         });
      }

      exes.shutdown();
   }
}
