package com.demo.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 捕获现成中的异常
 * 
 * 1.如果给指定的Thread设置了uncaughtExceptionHandler，则使用设置的。否则使用全局默认设置的异常捕获（setDefaultUncaughtExceptionHandler）。
 * @author Liujianjia
 */
public class ThreadExceptionDemo {
   public static void main(String[] args) {
      Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
         @Override
         public void uncaughtException(Thread t, Throwable e) {
            System.err.println("默认异常捕获 ：" + t + "," + e);
         }
      });

      //first test 
      ExecutorService exe = Executors.newCachedThreadPool(new HandlerThreadFactory());
      exe.execute(new ExceptionThread());
      exe.shutdown();
      
      //second test
      Thread t = new Thread() {
         public void run() {
            throw new RuntimeException("Throw exception");
         }
      };
      t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
         @Override
         public void uncaughtException(Thread t, Throwable e) {
            System.err.println("捕获到的异常2：" + t + "," + e);
         }
      });
      t.start();
      
      //third test
      new Thread(new Runnable() {
         @Override
         public void run() {
            throw new RuntimeException("默认异常抛出");
         }
      }).start();;
   }
}

class HandlerThreadFactory implements ThreadFactory {
   @Override
   public Thread newThread(Runnable r) {
      Thread t = new Thread(r);
      System.err.println("created new thead:" + t);
      t.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
      return t;
   }
}

class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
   @Override
   public void uncaughtException(Thread t, Throwable e) {
      System.err.println("捕获到的异常" + t + "," + e);
   }
}

class ExceptionThread implements Runnable {
   @Override
   public void run() {
      Thread t = Thread.currentThread();
      System.err.println("t:" + t);
      System.err.println(t.getUncaughtExceptionHandler());
      throw new RuntimeException("-Exception thread throw the exception-");
   }
}
