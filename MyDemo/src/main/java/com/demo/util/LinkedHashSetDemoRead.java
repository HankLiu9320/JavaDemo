package com.demo.util;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * LinkedHashSet
 * LinkedHashSet集合同样是根据元素的hashCode值来决定元素的存储位置，但是它同时使用链表维护元素的次序。这样使得元素看起
 * 来像是以插入顺序保存的，也就是说，当遍历该集合时候，LinkedHashSet将会以元素的添加顺序访问集合的元素。
 * LinkedHashSet在迭代访问Set中的全部元素时，性能比HashSet好，但是插入时性能稍微逊色于HashSet。
 *
 *
 * 具有可预知迭代顺序的 Set 接口的哈希表和链接列表实现。此实现与 HashSet
 * 的不同之外在于，后者维护着一个运行于所有条目的双重链接列表。此链接列表定义了迭代顺序，即按照将元素插入到集合中的顺序（插入顺序）进行迭代。注意，插入顺序不
 * 受在集合中重新插入的 元素的影响。（如果在 s.contains(e) 返回 true 后立即调用 s.add(e)，则元素 e 会被重新插入到集合 s
 * 中。）
 * 
 * 此实现可以让客户免遭未指定的、由 HashSet 提供的通常杂乱无章的排序工作，而又不致引起与 TreeSet
 * 关联的成本增加。使用它可以生成一个与原来顺序相同的集合副本，并且与原集合的实现无关：
 * 
 * void foo(Set m) { Set copy = new LinkedHashSet(m); ... }
 * 如果模块通过输入得到一个集合，复制这个集合，然后返回由此副本决定了顺序的结果，这种情况下这项技术特别有用。（客户通常期望内容返回的顺序与它们出现的顺序相同
 * 。） 此类提供所有可选的 Set 操作，并且允许 null 元素。与 HashSet 一样，它可以为基本操作（add、contains 和
 * remove）提供稳定的性能，假定哈希函数将元素正确地分布到存储段中。由于增加了维护链接列表的开支，其性能很可能会比 HashSet
 * 稍逊一筹，不过，这一点例外：LinkedHashSet 迭代所需时间与集合的大小 成正比，而与容量无关。HashSet
 * 迭代很可能支出较大，因为它所需迭代时间与其容量 成正比。
 * 
 * 链接的哈希集合有两个影响其性能的参数：初始容量 和加载因子。它们与 HashSet 中的定义极其相同。注意，为初始容量选择非常高的值对此类的影响比对
 * HashSet 要小，因为此类的迭代时间不受容量的影响。
 * 
 * 注意，此实现不是同步的。如果多个线程同时访问链接的哈希集合，而其中至少一个线程修改了该集合，则它必须
 * 保持外部同步。这一般通过对自然封装该集合的对象进行同步操作来完成。如果不存在这样的对象，则应该使用 Collections.synchronizedSet
 * 方法来“包装”该集合。最好在创建时完成这一操作，以防止意外的非同步访问：
 * 
 * Set s = Collections.synchronizedSet(new LinkedHashSet(...)); 此类的 iterator
 * 方法返回的迭代器是快速失败 的：在迭代器创建之后，如果对集合进行修改，除非通过迭代器自身的移除方法，其他任何时间任何方式的修改，迭代器都将抛出
 * ConcurrentModificationException。因此，面对并发的修改，迭代器很快就会完全失败，
 * 而不冒将来不确定的时间任意发生不确定行为的风险。
 * 
 * 注意，迭代器的快速失败行为不能得到保证，一般来说，存在不同步的并发修改时，不可能作出任何强有力的保证。快速失败迭代器尽最大努力抛出
 * ConcurrentModificationException。因此，编写依赖于此异常的程序的方式是错误的，正确做法是：
 * 迭代器的快速失败行为应该仅用于检测程序错误。
 * 
 */
public class LinkedHashSetDemoRead {
   public static void testConstrunction() {
      /**
       * 完全基于LinkedHashMap实现
       */
      LinkedHashSet set = new LinkedHashSet();

   }

   public static void test() {

   }

   public static void main(String[] args) {
      Set<Integer> set = new LinkedHashSet<Integer>();
      set.add(3);
      set.add(222);
      set.add(11);
      set.add(22);

      // LinkedHashSet
      for(Integer i : set) {
         System.err.println(i);
      }
   }
}
