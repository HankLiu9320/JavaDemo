package com.demo.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 描述： 
 * 此类实现 Set 接口，由哈希表（实际上是一个 HashMap 实例）支持。它不保证集合的迭代顺序；特别是它不保证该顺序恒久不变。此类允许使用
 * null 元素。
 * 
 * 此类为基本操作提供了稳定性能，这些基本操作包括 add、remove、contains 和
 * size，假定哈希函数将这些元素正确地分布在桶中。对此集合进行迭代所需的时间与 HashSet 实例的大小（元素的数量）和底层 HashMap
 * 实例（桶的数量）的“容量”的和成比例。因此，如果迭代性能很重要，则不要将初始容量设置得太高（或将加载因子设置得太低）。
 * 
 * 注意，此实现不是同步的。 如果多个线程同时访问一个集合，而其中至少一个线程修改了该集合，那么它必须
 * 保持外部同步。这通常是通过对自然封装该集合的对象执行同步操作来完成的。如果不存在这样的对象，则应该使用
 * Collections.synchronizedSet 方法来“包装”集合。最好在创建时完成这一操作，以防止对 HashSet 实例进行意外的不同步访问：
 * 
 * Set s = Collections.synchronizedSet(new HashSet(...)); 此类的 iterator
 * 方法返回的迭代器是快速失败 的：在创建迭代器之后，如果对集合进行修改，除非通过迭代器自身的 remove
 * 方法，否则在任何时间以任何方式对其进行修改，Iterator 都将抛出
 * ConcurrentModificationException。因此，面对并发的修改，迭代器很快就会完全失败，
 * 而不冒将来在某个不确定时间发生任意不确定行为的风险。
 * 
 * 注意，迭代器的快速失败行为无法得到保证，因为一般来说，不可能对是否出现不同步并发修改做出任何硬性保证。快速失败迭代器在尽最大努力抛出
 * ConcurrentModificationException。因此，为提高这类迭代器的正确性而编写一个依赖于此异常的程序是错误做法：
 * 迭代器的快速失败行为应该仅用于检测程序错误
 * 
 * 
 * HashSet HashSet有以下特点  不能保证元素的排列顺序，顺序有可能发生变化  不是同步的 
 * 集合元素可以是null,但只能放入一个null
 * 当向HashSet结合中存入一个元素时，HashSet会调用该对象的hashCode()方法来得到该对象的hashCode值，然后根据
 * hashCode值来决定该对象在HashSet中存储位置。
 * 简单的说，HashSet集合判断两个元素相等的标准是两个对象通过equals方法比较相等，并且两个对象的hashCode()方法返回值相 等
 * 注意，如果要把一个对象放入HashSet中，重写该对象对应类的equals方法，也应该重写其hashCode()方法。其规则是如果两个对
 * 象通过equals方法比较返回true时，其hashCode也应该相同。另外，对象中用作equals比较标准的属性，都应该用来计算 hashCode的值。
 *
 */
public class HashSetDemoRead {
   public static void testConstruction() {
      /**
       * new 了一个hashMap实现set
       */
      Set<Integer> set = new HashSet<Integer>();
      /**
       * map = new HashMap<>(Math.max((int) (c.size()/.75f) + 1, 16));
       */
      set = new HashSet<Integer>(new ArrayList<Integer>());

      /**
       * map = new HashMap<>(initialCapacity, loadFactor);
       */
      set = new HashSet<Integer>(16, 0.75f);
   }

   public static void test() {
      HashSet<Integer> set = new HashSet<Integer>();
      
      /**
       * 调用的是map.put，key是传入的值，value是set内部的一个persent对象
       */
      set.add(100);
      
      /**
       * 均调用了map的相应方法
       */
      set.remove(100);
      set.clear();
      set.contains(100);
      set.isEmpty();
      set.size();
      
      /**
       * map.keySet.iterator();
       */
      set.iterator();
   }

   public static void main(String[] args) {
      Set<Integer> set = new HashSet<Integer>();
      set.add(3);
      set.add(222);
      set.add(11);
      set.add(22);

      // set 的默认顺序？
      for(Integer i : set) {
         System.err.println(i);
      }
   }
}
