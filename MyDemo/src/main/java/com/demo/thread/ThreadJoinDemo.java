package com.demo.thread;

/**
 * 即join()的作用是：“等待该线程终止”，这里需要理解的就是该线程是指的主线程等待子线程的终止。 
 * 也就是在子线程调用了join()方法后面的代码，只有等到子线程结束了才能执行。
 * @author Liujianjia
 */
public class ThreadJoinDemo {
   public static void main(String[] args) {
      String threadName = Thread.currentThread().getName();
      System.out.println(threadName + " start.");
      BThread bt = new BThread();
      AThread at = new AThread(bt);

      try {
         bt.start();
         Thread.sleep(2000);
         at.start();
         at.join(); // at线程结束后，才可继续向下执行
      }
      catch(Exception e) {
         System.out.println("Exception from main");
      }
      System.out.println(threadName + " end!");
   }
}

class BThread extends Thread {
   public BThread() {
      super("[BThread] Thread");
   };

   public void run() {
      String threadName = Thread.currentThread().getName();
      System.out.println(threadName + " start.");

      try {
         for(int i = 0; i < 5; i++) {
            System.out.println(threadName + " loop at " + i);
            Thread.sleep(1000);
         }

         System.out.println(threadName + " end.");
      }
      catch(Exception e) {
         System.out.println("Exception from " + threadName + ".run");
      }
   }
}

class AThread extends Thread {
   BThread bt;

   public AThread(BThread bt) {
      super("[AThread] Thread");
      this.bt = bt;
   }

   public void run() {
      String threadName = Thread.currentThread().getName();
      System.out.println(threadName + " start.");
      try {
         bt.join(); // 当bt线程结束后才可以继续向下执行
         System.out.println(threadName + " end.");
      }
      catch(Exception e) {
         System.out.println("Exception from " + threadName + ".run");
      }
   }
}