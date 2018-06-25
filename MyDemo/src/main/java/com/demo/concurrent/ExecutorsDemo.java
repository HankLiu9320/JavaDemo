package com.demo.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

//http://www.cnblogs.com/lxl57610/p/5845831.html
public class ExecutorsDemo {
   public static void main(String[] args) throws InterruptedException, ExecutionException {
      // 创建一个可根据需要创建新线程的线程池，但是在以前构造的线程可用时将重用它们。
      ExecutorService cachedService = Executors.newCachedThreadPool();

      for(int i = 0; i < 5; i++) {
         final int index = i;

         cachedService.execute(new Runnable() {
            @Override
            public void run() {
               System.out.println(Thread.currentThread().getName() + ", index=" + index);
            }
         });

         try {
            Thread.sleep(1000);
         }
         catch(InterruptedException e) {
            e.printStackTrace();
         }
      }

      // 1. execute 方式添加执行的线程，不返回结果
      for(int i = 0; i < 5; i++) {
         cachedService.execute(new Runnable() {
            @Override
            public void run() {
               try {
                  TimeUnit.MILLISECONDS.sleep(1000);
                  System.err.println("cached service run");
               }
               catch(InterruptedException e) {
                  e.printStackTrace();
               }
            }
         });
      }

      // 2. submit 通过submit执行线程，返回结果Future
      Future<?> rs = cachedService.submit(new Runnable() {
         @Override
         public void run() {
            // TODO Auto-generated method stub
            System.err.println("run submit cached service");
            try {
               TimeUnit.MILLISECONDS.sleep(5000);
            }
            catch(InterruptedException e) {
               e.printStackTrace();
            }
         }
      });

      cachedService.shutdown();
      // List<Runnable> shutRs = cachedService.shutdownNow();
      // System.err.println(shutRs);
      System.err.println(rs.isDone() + "," + rs.isCancelled());
      rs.cancel(false);
      System.err.println(rs.isDone() + "," + rs.isCancelled());

      // 创建一个可重用固定线程数的线程池，以共享的无界队列方式来运行这些线程。
      ExecutorService eservice = Executors.newFixedThreadPool(3);

      // 创建一个线程池，它可安排在给定延迟后运行命令或者定期地执行。
      ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(10);
      ScheduledFuture a = scheduledService.schedule(new Callable() {
         public Object call() throws Exception {
            System.out.println("Executed!");
            return "Called!";
         }
      }, 5, TimeUnit.SECONDS);
      scheduledService.shutdown();
      System.err.println("result:" + a.get());

      // 创建一个使用单个 worker 线程的 Executor，以无界队列方式来运行该线程。
      ExecutorService singleService = Executors.newSingleThreadExecutor();

      // 创建一个单线程执行程序，它可安排在给定延迟后运行命令或者定期地执行。
      ScheduledExecutorService seservice = Executors.newSingleThreadScheduledExecutor();
   }
}
