package com.demo.util;

import java.util.WeakHashMap;

/**
 * 描述： 以弱键 实现的基于哈希表的 Map。在 WeakHashMap
 * 中，当某个键不再正常使用时，将自动移除其条目。更精确地说，对于一个给定的键，其映射的存在并不阻止垃圾回收器对该键的丢弃，这就使该键成为可终止的，被终止，
 * 然后被回收。丢弃某个键时，其条目从映射中有效地移除，因此，该类的行为与其他的 Map 实现有所不同。
 * 
 * null 值和 null 键都被支持。该类具有与 HashMap 类相似的性能特征,并具有相同的效能参数初始容量 和加载因子。
 * 
 * 像大多数集合类一样，该类是不同步的。可以使用 Collections.synchronizedMap 方法来构造同步的 WeakHashMap。
 * 
 * 该类主要与这样的键对象一起使用，其 equals 方法使用 == 运算符来测试对象标识。一旦这种键被丢弃，就永远无法再创建了，所以，过段时间后在
 * WeakHashMap 中查找此键是不可能的，不必对其项已移除而感到惊讶。该类十分适合与 equals
 * 方法不是基于对象标识的键对象一起使用，比如，String 实例。然而，对于这种可重新创建的键对象，键若丢弃，就自动移除 WeakHashMap
 * 条目，这种表现令人疑惑。
 * 
 * WeakHashMap 类的行为部分取决于垃圾回收器的动作，所以，几个常见的（虽然不是必需的）Map
 * 常量不支持此类。因为垃圾回收器在任何时候都可能丢弃键，WeakHashMap 就像是一个被悄悄移除条目的未知线程。特别地，即使对 WeakHashMap
 * 实例进行同步，并且没有调用任何赋值方法，在一段时间后 size 方法也可能返回较小的值，对于 isEmpty 方法，返回 false，然后返回
 * true，对于给定的键，containsKey 方法返回 true 然后返回 false，对于给定的键，get 方法返回一个值，但接着返回
 * null，对于以前出现在映射中的键，put 方法返回 null，而 remove 方法返回
 * false，对于键集、值集、项集进行的检查，生成的元素数量越来越少。
 * 
 * WeakHashMap
 * 中的每个键对象间接地存储为一个弱引用的指示对象。因此，不管是在映射内还是在映射之外，只有在垃圾回收器清除某个键的弱引用之后，该键才会自动移除。
 * 
 * 实现注意事项：WeakHashMap
 * 中的值对象由普通的强引用保持。因此应该小心谨慎，确保值对象不会直接或间接地强引用其自身的键，因为这会阻止键的丢弃。注意，值对象可以通过
 * WeakHashMap
 * 本身间接引用其对应的键；这就是说，某个值对象可能强引用某个其他的键对象，而与该键对象相关联的值对象转而强引用第一个值对象的键。处理此问题的一种方法是，
 * 在插入前将值自身包装在 WeakReferences 中，如：m.put(key, new WeakReference(value))，然后，分别用
 * get 进行解包。
 * 
 * 该类所有“collection 视图方法”返回的迭代器均是快速失败的：在迭代器创建之后，如果从结构上对映射进行修改，除非通过迭代器自身的 remove 或
 * add 方法,其他任何时间任何方式的修改，迭代器都将抛出
 * ConcurrentModificationException。因此，面对并发的修改，迭代器很快就完全失败，
 * 而不是冒着在将来不确定的时间任意发生不确定行为的风险。
 * 
 * 注意，迭代器的快速失败行为不能得到保证，一般来说，存在不同步的并发修改时，不可能作出任何坚决的保证。快速失败迭代器尽最大努力抛出
 * ConcurrentModificationException。因此，编写依赖于此异常程序的方式是错误的，正确做法是：迭代器的快速失败行为应该仅用于检测
 * bug。
 * 
 * http://blog.csdn.net/u012129558/article/details/51980883
 */
public class WeakHashMapDemoRead {
   public void testConstruction() {
      /**
       * 用默认初始容量 (16) 和默认加载因子 (0.75) 构造一个新的空 WeakHashMap。
       */
      WeakHashMap<String, Integer> wMap = new WeakHashMap<String, Integer>();

      /**
       * 初始化容量initialCapacity = 16
       */
      wMap = new WeakHashMap<String, Integer>(16);
      /**
       * 初始化容量initialCapacity = 16,loadFactor = 0.75f 1)
       * 不管传入的initialCapacity为多少，实际容量大小都是恰好大于等于传入值得2的n次方 2)
       * 使用计算好的实际大小创建一个entry的数组 3) 加载因子为实际大小*loadFactor
       */
      wMap = new WeakHashMap<String, Integer>(16, 0.75f);

   }

   public void test() {
      WeakHashMap<String, Integer> wMap = new WeakHashMap<String, Integer>();

      /**
       * 1)根据key计算出hash值 2)通过getTable拿到table的entry数组，expungeStaleEntries清楚弱引用
       * 3)遍历entry数组，有则替换，没有添加新entry。如果size大于阈值，则扩容为之前table的两倍
       */
      wMap.put("abc", 2);

      /**
       * 与map的get类似，只是使用getTable消除弱引用
       */
      wMap.get("abc");

      /**
       * 与map同理
       */
      wMap.keySet();
      wMap.values();
      wMap.entrySet();

      wMap.containsKey("abc");
      wMap.containsValue(2);
      wMap.isEmpty();
      wMap.remove("abc");
   }

   public static void main(String[] args) {
      WeakHashMap<Object, Object> wMap = new WeakHashMap<Object, Object>();
      Object a = new Object();
      wMap.put(a, "abc");
      System.err.println(wMap.size());
      a = null;
      System.gc();
      System.err.println(wMap.size());
   }
}
