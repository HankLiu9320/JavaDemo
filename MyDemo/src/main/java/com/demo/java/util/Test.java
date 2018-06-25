package com.demo.java.util;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class Test {
    public static void main(String[] args) {
      System.err.println("test2");
      double d = Double.parseDouble("10");
      System.err.println(d);
   }

   public static void main1(String[] args) throws IOException {
      String a = "你好";
      char[] as = a.toCharArray();

      for(char b : as) {
         System.err.println(Byte.valueOf(b + ""));
      }

      PipedInputStream in = new PipedInputStream();
      PipedOutputStream out = new PipedOutputStream();
      in.connect(out);
      
   }

   static String a, a1;

   class MyGenericClass<T, V> {

      T obj1 = null;

      V obj2 = null;

      void setValue(T obj) {

      }

   }
}
