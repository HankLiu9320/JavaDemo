package com.demo.java.util;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

public class VectorTest {
   public static void main(String[] args) {
      List vector = new Vector<Integer>();

      for(int i = 0; i < 10; i++) {
         vector.add(i);
      }
      
      
      System.err.println(vector.get(2));
      
      
      Stack stack = new Stack();

      for(int i = 0; i < 10; i++) {
         stack.push(i);
      }

      System.err.println(stack.get(2));
      System.err.println(stack.pop() + "," + stack.size());
      System.err.println(stack.pop() + "," + stack.size());
   }
}
