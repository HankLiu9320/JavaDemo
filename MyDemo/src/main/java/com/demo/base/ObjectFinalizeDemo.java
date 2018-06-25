package com.demo.base;

/**
 * http://blog.csdn.net/kjfcpua/article/details/8495199
 * @author Administrator
 *
 */
public class ObjectFinalizeDemo {
   public static class A {
      B b;

      public void finalize() {
         System.out.println("method A.finalize at " + System.nanoTime());
      }
   }

   public static class B {
      public void finalize() {
         System.out.println("method B.finalize at " + System.nanoTime());
      }
   }

   public static void main(String[] args) {
      A a = new ObjectFinalizeDemo.A();
      a.b = new ObjectFinalizeDemo.B();
      a = null;
      System.gc();
   }
}
