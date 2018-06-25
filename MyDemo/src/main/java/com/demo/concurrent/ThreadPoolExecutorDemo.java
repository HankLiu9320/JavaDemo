package com.demo.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorDemo {
   public static void main(String[] args) {
      RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardOldestPolicy();
      ThreadFactory threadFactory = new ThreadFactory() {
         @Override
         public Thread newThread(Runnable r) {
            System.err.println("call newThread---");
            return new Thread(r);
         }
      };

      BlockingQueue queue = new ArrayBlockingQueue<>(2);

      ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 4, 1000L, TimeUnit.MILLISECONDS, queue, threadFactory, handler);

      for(int i = 0; i < 7; i++) {
         /**
          * 1. 如果加入的线程数小于2（corePoolSize），则创建worker添加到hashset，并启动线程
          * 2. 如果加入的线程数大于2（corePoolSize），则将新入线程加到同步队列中
          * 3. 如果同步队列已满，但是未达到maxThreadSize，则继续执行第一步，添加到worker所在的hashset中。
          * 4. 如果继续添加后，导致workers的size>=maxnumPollsize，则会调用reject的handler
          * 
          * 启动过程：
          * 1. 在new worker的时候，将execute给入的runnable当做一个执行器给到worker中，worker本身是一个runnable，
          *    在线程启动的时候，其实启动的是worker本身这个runnable，execute传入的runnable只调用run方法，不当做线程启动。
          * 2. 如果workers中的线程某一个跑完了，在worder的run中是while循环，从队列中获得一个runnable，继续调用run方法执行。
          *    知道队列中没有为止。
          */
         pool.execute(new Thread("thread -" + i) {
            @Override
            public void run() {
               int i = 10;
               while(i > 0) {
                  try {
                     Thread.sleep(1000);
                     System.err.println(getName() + "pool runing--");
                  }
                  catch(InterruptedException e) {
                     e.printStackTrace();
                  }
                  
                  i--;
               }
            }
         });
      }
   }
}
