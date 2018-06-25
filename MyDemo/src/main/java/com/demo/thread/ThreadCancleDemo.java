package com.demo.thread;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ThreadCancleDemo {
   public static void main(String[] args) {
      // final Thread t = new Thread(new Runnable() {
      // @Override
      // public void run() {
      // while(true) {
      // try {
      // TimeUnit.MILLISECONDS.sleep(2000);
      // System.err.println(Thread.currentThread() + "|run");
      // }
      // catch(InterruptedException e) {
      // e.printStackTrace();
      // }
      // }
      // }
      // });
      //
      // t.start();
      //
      // new Thread() {
      // @Override
      // public void run() {
      // try {
      // Thread.sleep(5000);
      // }
      // catch(InterruptedException e) {
      // e.printStackTrace();
      // }
      // t.interrupt();
      // t.stop(); //淘汰
      // }
      // }.start();
      //

      ExecutorService exes = Executors.newCachedThreadPool();
      Future future = exes.submit(new Runnable() {
         @Override
         public void run() {
            while(true) {
               try {
                  TimeUnit.MILLISECONDS.sleep(2000);
                  System.err.println("exes runing:" + Thread.currentThread());
               }
               catch(InterruptedException e) {
                  e.printStackTrace();
                  return;
               }
            }
         }
      });

      try {
         Thread.sleep(5000);
         // 使用future cancel
         // future.cancel(true);

         // 使用executors的shutdownnow
         List<Runnable> runs = exes.shutdownNow();
         System.err.println(runs);
      }
      catch(InterruptedException e) {
         e.printStackTrace();
      }
   }
}
