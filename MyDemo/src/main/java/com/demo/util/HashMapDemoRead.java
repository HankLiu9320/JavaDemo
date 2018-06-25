package com.demo.util;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 描述： 基于哈希表的 Map 接口的实现。此实现提供所有可选的映射操作，并允许使用 null 值和 null 键。（除了不同步和允许使用 null 之外，
 * HashMap 类与 Hashtable 大致相同。）此类不保证映射的顺序，特别是它不保证该顺序恒久不变。
 * 此实现假定哈希函数将元素正确分布在各桶之间，可为基本操作（get 和 put）提供稳定的性能。迭代集合视图所需的时间与 HashMap
 * 实例的“容量”（桶的数量）及其大小（键-值映射关系数）的和成比例。所以，如果迭代性能很重要，则不要将初始容量设置得太高（或将加载因子设置得太低）。
 * 
 * HashMap 的实例有两个参数影响其性能：初始容量 和加载因子。容量 是哈希表中桶的数量，初始容量只是哈希表在创建时的容量。加载因子
 * 是哈希表在其容量自动增加之前可以达到多满的一种尺度。当哈希表中的条目数超出了加载因子与当前容量的乘积时，通过调用 rehash 方法将容量翻倍。
 * 
 * 通常，默认加载因子 (.75) 在时间和空间成本上寻求一种折衷。加载因子过高虽然减少了空间开销，但同时也增加了查询成本（在大多数 HashMap
 * 类的操作中，包括 get 和 put 操作，都反映了这一点）。在设置初始容量时应该考虑到映射中所需的条目数及其加载因子，以便最大限度地降低 rehash
 * 操作次数。如果初始容量大于最大条目数除以加载因子，则不会发生 rehash 操作。
 * 
 * 如果很多映射关系要存储在 HashMap 实例中，则相对于按需执行自动的 rehash
 * 操作以增大表的容量来说，使用足够大的初始容量创建它将使得映射关系能更有效地存储。
 * 
 * 注意，此实现不是同步的。如果多个线程同时访问此映射，而其中至少一个线程从结构上修改了该映射，则它必须
 * 保持外部同步。（结构上的修改是指添加或删除一个或多个映射关系的操作；仅改变与实例已经包含的键关联的值不是结构上的修改。）
 * 这一般通过对自然封装该映射的对象进行同步操作来完成。如果不存在这样的对象，则应该使用 Collections.synchronizedMap
 * 方法来“包装”该映射。最好在创建时完成这一操作，以防止对映射进行意外的不同步访问，如下所示：
 * 
 * Map m = Collections.synchronizedMap(new HashMap(...));
 * 由所有此类的“集合视图方法”所返回的迭代器都是快速失败 的：在迭代器创建之后，如果从结构上对映射进行修改，除非通过迭代器自身的 remove 或 add
 * 方法，其他任何时间任何方式的修改，迭代器都将抛出
 * ConcurrentModificationException。因此，面对并发的修改，迭代器很快就会完全失败，
 * 而不冒在将来不确定的时间任意发生不确定行为的风险。
 * 
 * 注意，迭代器的快速失败行为不能得到保证，一般来说，存在不同步的并发修改时，不可能作出任何坚决的保证。快速失败迭代器尽最大努力抛出
 * ConcurrentModificationException。因此，编写依赖于此异常程序的方式是错误的，正确做法是：
 * 迭代器的快速失败行为应该仅用于检测程序错误。
 *
 * 线程安全：线程不安全
 * 性能：1)循环map的时候，尽量使用entrySet来遍历，这样可以同时拿到key，value，使用keySet时，消耗了和entrySet一样的性能，
 *        但是只能拿到keys，在通过map.get(key),性能消耗严重
 *      2)modCount作用是迭代器的快速失败，通过modCount可以判断是否多个线程对iterator写操作，
 *        modCount是在put，remove，clear时做modCount++操作，多个线程同时get操作不会出现问题
 */
public class HashMapDemoRead {
   /**
    * 构造方法
    */
   private void testConstruction() {
      // initialCapacity=16，loadFactor=0.75
      Map<String, String> map = new HashMap<String, String>();
      // initialCapacity=10, loadFactor=0.75
      map = new HashMap<String, String>(10);
      // initialCapacity=10, loadFactor=0.70
      map = new HashMap<String, String>(10, 0.70f);
   }

   private void test() {
      HashMap<String, String> map = new HashMap<String, String>();

      /**
       * HashMap的put 1)线判断Entry<K,V>[]
       * table是否为空(Entry中包含key,value,next,hash)，等于空则扩容table（inflateTable(
       * threshold)）
       * 2)inflateTable初始化table数组大小，保持数组大小为2的n次方，如果传入16的大小为16，如果传入17则大小为32，
       * 并设置threshold扩容 阈值为数组的百分之loadFactor.和设置hash种子（hashSeed）的值
       * 
       * 3)如果put进去的key为空，则使用putForNullKey，先对table数组循环判断是否存在空，存在则替换，否则
       * 调用addEntry，如果map的size>=treshold（扩容阈值），则进行resize操作，扩容table数组，
       * 并调用transfer将原有的table数据散列到扩容的table中。
       * 并重置treshold阈值。如果size小于treshold，未达到扩容条件，则调用createEntry，
       * 设置新的数据到桶中的entry链表头
       * 
       * 4)如果put进去的key不为空，则通过key的hash值找到table的index，并遍历table[index]的链表，
       * 通过比较hash值和key，判断是否有相同值，有则替换。
       * 没有则调用addEntry，如果map的size>=treshold（扩容阈值），则进行resize操作，扩容table数组，
       * 并调用transfer将原有的table数据散列到扩容的table中。
       * 
       * indexFor->h & (length-1)  等于对table的长度求模运算，使桶分布均匀
       */
      map.put("a", "测试1");

      /**
       * HashMap的get 1)如果key为null，则使用getForNullKey，查找table数组第一个entry中的链表，找到则返回
       * 2)如果key不为null，则通过key的hash值，找到table数组中对应索引位置的entry，并遍历entry链表找到元素
       */
      map.get("a");

      /**
       * 使用Arrays.fill将table数组填充为null
       */
      map.clear();

      /**
       * 使用putAllForCreate重新将map数据放入新的map中，key的hash值重新计算
       */
      map.clone();

      /**
       * 使用getEntry(key) != null
       */
      map.containsKey("a");

      /**
       * 如果value为null，遍历table并遍历链表entry，查找null值value返回
       * 如果value不为null，则遍历table并遍历链表entry，查找value equals的值
       */
      map.containsValue("abc");

      /**
       * 效率高，判断size == 0
       */
      map.isEmpty();

      /**
       * 返回size
       */
      map.size();

      /**
       * new了一个KeySet，如果keySet不为空，则使用原来的keySet
       * KeySet内部不存储数据，只有在打印（调用toString）或者遍历，或者调用iterator时才会真正的获得数据
       * 1)当调用toString打印时，因为KeySet继承了AbstractSet->AbstractCollection在这个类里重写了toString，是调用的iterator()遍历数据
       * 2)当调用iterator()时，返回了一个KeyIterator，在new KeyIterator的时候，在它的父类构造的时候，先找到桶中第一个不为空的entry链表
       *    在调用nextEntry时，继续以相同的方式向后查找，KeyIterator中重写next，获得entry的key
       */
      map.keySet();
      
      /**
       * entrySet, values与keySet逻辑雷同
       */
      map.entrySet();
      map.values();

      /**
       * 扩容后调用，循环调用put
       */
      map.putAll(new HashMap<String, String>());
      
      /**
       * 通过key的hash值找到桶的位置，并获得entry链表，循环entry链表，找到key对应的元素，然后将这个元素的前后拼接到一起
       */
      map.remove("abc");

   }

   public static void main(String[] args) {
      Map<Integer, String> map = new HashMap<Integer, String>();
      map.put(1, "a");
      map.put(3, "c");
      map.put(2, "b");

      for(Integer i : map.keySet()) {
         System.err.println(i + ":" + map.get(i));
      }

      TestIterator t = new TestIterator();
      Set<Integer> set = t.keySet();
      //set其实内部并没有值，只是个容器，当作为输出时，set.toString会调用iterator遍历
      System.out.println(set);
   }

   static class TestIterator {
      public Set<Integer> keySet() {
         final ArrayList<Integer> result = new ArrayList<Integer>();
         result.add(1);
         result.add(2);
         result.add(6);

         Set<Integer> keySet = new AbstractSet<Integer>() {
            public Iterator<Integer> iterator() {
               return new Iterator<Integer>() {
                  private Iterator<Integer> i = result.iterator();

                  @Override
                  public boolean hasNext() {
                     return i.hasNext();
                  }

                  @Override
                  public Integer next() {
                     return i.next();
                  }

                  @Override
                  public void remove() {
                     i.remove();
                  }
               };
            }

            @Override
            public int size() {
               return 0;
            }
         };

         return keySet;
      }
   }
}
