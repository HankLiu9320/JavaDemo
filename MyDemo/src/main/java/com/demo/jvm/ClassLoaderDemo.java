package com.demo.jvm;

public class ClassLoaderDemo {
   public static void main(String[] args) {
//      1. Bootstrap CLassloder 
//      2. Extention ClassLoader 
//      3. AppClassLoader
      ClassLoader cl = ClassLoadOrderDemo.class.getClassLoader();
      System.out.println("ClassLoader is:" + cl.toString());
      System.out.println("ClassLoader is:" + cl.getParent());
   }
}
