package com.demo.jvm;

public class StackDemo {
   //-Xss1M
   private int count = 0;
   
   public void recursion() {
      count++;
      recursion();
   }
   
   public static void main(String[] args) {
      StackDemo demo = new StackDemo();
      try {
         demo.recursion();
      }
      catch(Exception e) {
         System.err.println(demo.count);
         e.printStackTrace();
      }
   }
}
