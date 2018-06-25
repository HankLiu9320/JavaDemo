package com.demo.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LinkedHashMasp
 * http://uule.iteye.com/blog/1522291
 * 描述：
 * 是HashMap的一个子类，保存了记录的插入顺序，在用Iterator遍历LinkedHashMap时，先得到的记录肯定是先插入的.也可以在构造时用带参数
 * ，按照应用次数排序。在遍历的时候会比HashMap慢，不过有种情况例外，当HashMap容量很大，实际数据较少时，遍历起来可能会比
 * LinkedHashMap慢，因为LinkedHashMap的遍历速度只和实际数据有关，和容量无关，而HashMap的遍历速度和他的容量有关。
 * 
 * 
 */
public class LinkedHashMapDemoRead {
   public static void testConstruction() {
      /**
       * LinkedHashMap继成hashMap，调用hashmap的构造。在构造的时候使用重写的init方法，初始化链表entry
       */
      Map<Integer, String> map = new LinkedHashMap<Integer, String>();
      /**
       * 调用hashMap的构造，将容量大小传给hashmap
       */
      map = new LinkedHashMap<Integer, String>(16);
      /**
       * 调用hashMap的构造，将容量大小,加载因子传给hashmap
       */
      map = new LinkedHashMap<Integer, String>(16, 0.75f);
      /**
       * 调用hashMap的构造，将容量大小,加载因子传给hashmap,accessOrder传给linkedHashmap
       */
      map = new LinkedHashMap<Integer, String>(16, 0.75f, false);
      /**
       * 调用hashMap的构造，将传入的map传给hashmap进行构造
       */
      map = new LinkedHashMap<Integer, String>(new HashMap<Integer, String>());
   }

   public static void test() {
      LinkedHashMap<Integer, String> map = new LinkedHashMap<Integer, String>();
      
      /**
       * LinkedHashMap是继成HashMap的
       * 重新的方法：
       * init
       * transfer
       * addEntry
       * createEntry
       */
      
      /**
       * 1)调用HashMap的put方法
       * 2)调用LinkedHashMap的addEntry,添加了removeEldestEntry移除策略，可以重写此方法，修改移除策略
       * 3)调用LinkedHashMap的createEntry，逻辑与HashMap的createEntry差不多，只是在最后维护了entry列表，
       *   调用了entry.addBefore(header),把新元素加到entry链表的头
       */
      map.put(100, "abc");

      /**
       * LinkedHashMap重写了
       * newKeyIterator->LinkedHashMap.KeyIterator
       * newValueIterator->LinkedHashMap.ValueIterator
       * newEntryIterator->LinkedHashMap.EntryIterator
       */
      map.keySet();
      map.values();
      map.entrySet();

      /**
       * 重写了HashMap的get,调用HashMap的getEntry - recordAccess(重写)
       * 根据链表中元素的顺序可以分为：按插入顺序的链表，和按访问顺序(调用get方法)的链表。
       * 
       * 默认是按插入顺序排序，如果指定按访问顺序排序（accessOrder=true），那么调用get方法后，
       * 会将这次访问的元素在当前位置删除，并添加套链表头部，不断访问可以形成按访问顺序排序的链表。第一次访问元素在链表的最后  
       * 可以重写removeEldestEntry方法返回true值指定插入元素时移除最老的元素。 
       */
      map.get(100);
      
      /**
       * LinkedHashMap的removeEldestEntry可以在子类中重写
       * 如果用此映射构建LRU缓存，则非常方便，它允许映射通过删除旧条目来减少内存损耗。
       * 重写此方法，维持此映射只保存100个条目的稳定状态，在每次添加新条目时删除最旧的条目。
       * private static final int MAX_ENTRIES = 100;  
       * protected boolean removeEldestEntry(Map.Entry eldest) {  
       *    return size() > MAX_ENTRIES;
       * }  
       */
      //map.removeEldestEntry();
      
      map.clear();
      map.containsValue("abc");
   }

   public static void main(String[] args) {
      Map<Integer, String> map = new LinkedHashMap<Integer, String>();
      map.put(1, "apple");
      map.put(2, "pear");
      map.put(3, "banana");

      System.err.println(map.keySet().getClass());
      System.err.println(map.keySet().iterator());
      
      for(Iterator it = map.keySet().iterator(); it.hasNext();) {
         Object key = it.next();
         System.out.println(key + "=" + map.get(key));
      }
      
      for(Integer i : map.keySet()) {
         System.err.println(i + "," + map.get(i));
      }
   }
}
