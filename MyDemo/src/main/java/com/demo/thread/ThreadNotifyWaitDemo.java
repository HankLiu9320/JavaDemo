package com.demo.thread;

import java.util.concurrent.TimeUnit;

public class ThreadNotifyWaitDemo {
   private final static Object lock = new Object();

   public static void main(String[] args) {
      Thread t1 = new Thread() {
         public void run() {
            try {
               synchronized(lock) {
                  lock.wait();
               }
               System.err.println("run t1");
            }
            catch(InterruptedException e) {
               e.printStackTrace();
            }
         }
      };

      Thread t2 = new Thread() {
         public void run() {
            try {
               synchronized(lock) {
                  lock.wait();
               }
               System.err.println("run t2");
            }
            catch(InterruptedException e) {
               e.printStackTrace();
            }
         }
      };

      Thread t3 = new Thread() {
         public void run() {
            try {
               TimeUnit.SECONDS.sleep(2);
               synchronized(lock) {
                  lock.notify(); // notify通知lock对象上wait状态的线程。应该先通知最先在lock上wait的线程
               }

               TimeUnit.SECONDS.sleep(2);
               synchronized(lock) {
                  lock.notify();
               }
               System.err.println("run t3");
            }
            catch(InterruptedException e) {
               e.printStackTrace();
            }
         }
      };

      t1.start();
      t2.start();
      t3.start();
   }
}
