package com.demo.util;

import java.util.Hashtable;
import java.util.Map;

/**
 * Hashtable与 HashMap类似,它继承自Dictionary类，不同的是:它不允许记录的键或者值为空;它支持线程的同步，
 * 即任一时刻只有一个线程能写Hashtable, 因此也导致了 Hashtable在写入时会比较慢。
 */
public class HashtableDemo {
   public static void main(String[] args) {
      Map<Integer, String> map = new Hashtable<Integer, String>();
      map.put(3, "a");
      map.put(1, "c");
      map.put(2, "b");

      for(Integer i : map.keySet()) {
         System.err.println(i + ":" + map.get(i));
      }
   }
}
