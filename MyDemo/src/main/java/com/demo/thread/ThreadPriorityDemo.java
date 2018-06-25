package com.demo.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** 优先级的测试类,一般情况下不需要用到优先级 */
public class ThreadPriorityDemo implements Runnable {
   private int countDown = 5;
   private volatile double d; // 直接在主存操作这个变量
   private int priority;

   public ThreadPriorityDemo(int priority) {
      this.priority = priority;
   }

   public String toString() {
      return Thread.currentThread() + " : " + countDown;
   }

   public void run() {
      Thread.currentThread().setPriority(priority);// 设置优先级，需要在run开头部分设置

      while(true) {
         for(int i = 1; i < 100000; i++) {
            d += (Math.PI + Math.E) / (double) i;
            if(i % 1000 == 0) {
               Thread.yield();
            }
         }

         System.out.println(this);

         if(--countDown == 0) {
            return;
         }
      }
   }

   public static void main(String[] args) {
      ExecutorService service = Executors.newCachedThreadPool();
      
      for(int i = 0; i < 5; i++) {
         service.execute(new ThreadPriorityDemo(Thread.MIN_PRIORITY));
      }

      //设置最后一个线程优先级最高
      service.execute(new ThreadPriorityDemo(Thread.MAX_PRIORITY));
      service.shutdown();
   }
}