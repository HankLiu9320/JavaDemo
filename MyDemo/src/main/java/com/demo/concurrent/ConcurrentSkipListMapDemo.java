package com.demo.concurrent;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConcurrentSkipListMapDemo {
   public static void main(String[] args) {
      ConcurrentNavigableMap<String, String> concurrentSkipListMap = new ConcurrentSkipListMap<String, String>();
      concurrentSkipListMap.put("3", "Wednesday");
      concurrentSkipListMap.put("2", "Tuesday");
      concurrentSkipListMap.put("1", "Monday");
      concurrentSkipListMap.put("5", "Friday");
      concurrentSkipListMap.put("4", "Thursday");

      NavigableSet<String> navigableSet = concurrentSkipListMap.descendingKeySet();
      System.out.println("descendingKeySet: ");
      Iterator<String> itr = navigableSet.iterator();

      while(itr.hasNext()) {
         String s = itr.next();
         System.out.println(s);
      }

      System.out.println("ceilingEntry-2: " + concurrentSkipListMap.ceilingEntry("2"));
      System.out.println("firstEntry: " + concurrentSkipListMap.firstEntry());
      System.out.println("lastEntry: " + concurrentSkipListMap.lastEntry());
      System.out.println("pollFirstEntry: " + concurrentSkipListMap.pollFirstEntry());
      System.out.println("now firstEntry: " + concurrentSkipListMap.firstEntry());
      System.out.println("pollLastEntry: " + concurrentSkipListMap.pollLastEntry());
      System.out.println("now lastEntry: " + concurrentSkipListMap.lastEntry());
      System.out.println("Entry-2: " + concurrentSkipListMap.get("2"));
   }

}