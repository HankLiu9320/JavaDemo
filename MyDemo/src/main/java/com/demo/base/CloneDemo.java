package com.demo.base;

import java.io.Serializable;

public class CloneDemo implements Cloneable, Serializable {
   private static final long serialVersionUID = 1L;

   private String a;
   private transient int b;

   public CloneDemo(String a, int b) {
      this.a = a;
      this.b = b;
   }

   @Override
   public String toString() {
      return "a:" + this.a + ",b:" + this.b + "," + super.toString();
   }

   public static void main(String[] args) throws CloneNotSupportedException {
      CloneDemo c1 = new CloneDemo("aaaa", 2);
      System.err.println("c1=" + c1);
      CloneDemo c2 = (CloneDemo) c1.clone();
      System.err.println("c2=" + c2);
   }
}
