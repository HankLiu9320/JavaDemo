package com.demo.thread;

public class ThreadRunnableDemo {
   public static void main(String[] args) {
      Thread t = new Thread("My-Thread1") {
         public void run() {
            System.err.println("Run by " + currentThread().getName());
         }
      };
      t.start();

      Thread t1 = new Thread(new Runnable() {
         @Override
         public void run() {
            System.err.println("Run by " + Thread.currentThread().getName());
         }
      }, "My-Runnable2");
      t1.start();

      Thread t2 = new Thread(new Runnable() {
         @Override
         public void run() {
            System.err.println("T2 runnable- Run by " + Thread.currentThread().getName());
         }
      }, "My-Thread3") {
         public void run() {
            System.err.println("T2 thread- Run by " + currentThread().getName());
         }
      };
      t2.start();
   }
}
