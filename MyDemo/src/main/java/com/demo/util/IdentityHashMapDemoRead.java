package com.demo.util;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map.Entry;

/**
 * 描述： 此类利用哈希表实现 Map 接口，比较键（和值）时使用引用相等性代替对象相等性。换句话说，在 IdentityHashMap 中，当且仅当
 * (k1==k2) 时，才认为两个键 k1 和 k2 相等（在正常 Map 实现（如 HashMap）中，当且仅当满足下列条件时才认为两个键 k1 和 k2
 * 相等：(k1==null ? k2==null : e1.equals(e2))）。
 * 
 * 此类不是 通用 Map 实现！此类实现 Map 接口时，它有意违反 Map 的常规协定，该协定在比较对象时强制使用 equals
 * 方法。此类设计仅用于其中需要引用相等性语义的罕见情况。
 * 
 * 此类的典型用法是拓扑保留对象图形转换，如序列化或深层复制。要执行这样的转换，程序必须维护用于跟踪所有已处理对象引用的“节点表”。节点表一定不等于不同对象，
 * 即使它们偶然相等也如此。此类的另一种典型用法是维护代理对象。例如，调试设施可能希望为正在调试程序中的每个对象维护代理对象。
 * 
 * 此类提供所有的可选映射操作，并且允许 null 值和 null 键。此类对映射的顺序不提供任何保证；特别是不保证顺序随时间的推移保持不变。
 * 
 * 此类提供基本操作（get 和 put）的稳定性能，假定系统标识了将桶间元素正确分开的哈希函数
 * (System.identityHashCode(Object))。
 * 
 * 此类具有一个调整参数（影响性能但不影响语义）：expected maximum
 * size。此参数是希望映射保持的键值映射关系最大数。在内部，此参数用于确定最初组成哈希表的桶数。未指定所期望的最大数量和桶数之间的确切关系。
 * 
 * 如果映射的大小（键值映射关系数）已经超过期望的最大数量，则桶数会增加，增加桶数（“重新哈希”）可能相当昂贵，
 * 因此创建具有足够大的期望最大数量的标识哈希映射更合算。另一方面，对 collection
 * 视图进行迭代所需的时间与哈希表中的桶数成正比，所以如果特别注重迭代性能或内存使用，则不宜将期望的最大数量设置得过高。
 * 
 * 注意，此实现不是同步的。如果多个线程同时访问此映射，并且其中至少一个线程从结构上修改了该映射，则其必须
 * 保持外部同步（结构上的修改是指添加或删除一个或多个映射关系的操作；仅改变与实例已经包含的键关联的值不是结构上的修改。）
 * 这一般通过对自然封装该映射的对象进行同步操作来完成。如果不存在这样的对象，则应该使用 Collections.synchronizedMap
 * 方法来“包装”该映射。最好在创建时完成这一操作，以防止对映射进行意外的不同步访问，如下所示：
 * 
 * Map m = Collections.synchronizedMap(new HashMap(...)); 由所有此类的“collection
 * 视图方法”所返回的迭代器都是快速失败 的：在迭代器创建之后，如果从结构上对映射进行修改，除非通过迭代器自身的 remove 或 add
 * 方法，其他任何时间任何方式的修改，迭代器都将抛出
 * ConcurrentModificationException。因此，面对并发的修改，迭代器很快就会完全失败，
 * 而不冒在将来不确定的时间任意发生不确定行为的风险。
 * 
 * 注意，迭代器的快速失败行为不能得到保证，一般来说，存在不同步的并发修改时，不可能作出任何强有力的保证。快速失败迭代器尽最大努力抛出
 * ConcurrentModificationException。因此，编写依赖于此异常的程序的方式是错误的，正确做法是：
 * 迭代器的快速失败行为应该仅用于检测程序错误。
 * 
 * 实现注意事项：此为简单的线性探头 哈希表，如 Sedgewick 和 Knuth
 * 原文示例中所述。该数组交替保持键和值（对于大型表来说，它比使用独立组保持键和值更具优势）。对于多数 JRE 实现和混合操作，此类比
 * HashMap（它使用链 而不使用线性探头）能产生更好的性能。
 * 
 * 线程安全：线程不安全
 */

public class IdentityHashMapDemoRead {
   public static void testConstruction() {
      /**
       * 初始化capacity=32,threshold（阈值）为 2/3的capacity,数组大小为2*capacity
       */
      IdentityHashMap<String, Object> map = new IdentityHashMap<String, Object>();

      /**
       * 1)传入期望的最大容量100 2)通过期望容量计算出最小容量为 100*3/2(但是源码标注为2/3，感觉源码有错)
       * 3)如果计算出的minCapacity大于1 << 29,则使用最大值，否则通过最小4，向右移位，直到有一个不小于最小值的2的n次方的数为止。
       * 4)然后使用计算出的这个数，做默认构造里的逻辑
       */
      map = new IdentityHashMap<String, Object>(100);

      /**
       * 1)期望容量为传入map的((1 + m.size()) * 1.1)); 2)putAll(map)
       */
      map = new IdentityHashMap<String, Object>(new HashMap<String, Object>());
   }

   public static void test() {
      IdentityHashMap<String, Object> map = new IdentityHashMap<String, Object>();

      /**
       * 1)使用identityHashCode对key求hash值 2)如果hash值所在的table索引数据不为空
       * 3)则在hash值所在的table索引开始，以hash+2循环查找等于key的值（如果hash+2大于table的size，则继续从0开始查找
       * ）， 如果找到则在当前位置的+1位置存储value，并返回老的值
       * 4)如果没有找到，则在hash值所在的table索引下记录key的值，hash+1记录value的值
       * 5)put的过程中如果size达到threshold，则调用resize扩容，将table放大道原来table的两倍。
       * 6)重新hash，将老的table数据放到newTable中，并将加载因子threshold变为newLength的1/3
       * 
       * a）key和value的值实际上都是存储在数组中的，而且val是挨着key存储的。
       * b）当发生冲突的时候，这里采用的方式是依次找下一个，直到有空的位置，或者找到key应有的位置。
       * c）因为在超过阈值后会进行resize的操作，table的长度会被扩大一倍，所以步骤2）一定能找到一个空位置，或者找到之前设置的位置。
       * 如果没有自动扩容机制，则步骤2）很可能会出现死循环。 d）偶数位置为key， 奇数位置value,因为hash方法 ((h << 1) -
       * (h << 8)) & (length - 1); 返回的一定是个偶数，length为2的n次方
       */
      map.put("abc", "1");

      /**
       * 类似put时的查找方式，通过key计算hash值，从hash所在位置想后查找，找到返回，没找到则hash+2继续找，如果到达table尾，
       */
      map.get("abc");

      /**
       * 返回IndentityHashMap中的KeySet->iterator返回IdentityHashMap->KeyIterator,
       * 每一次循环都以i+=2的方式循环
       */
      map.keySet();
      /**
       * 与keySet同理，只是返回时索引加1
       */
      map.values();
      /**
       * 同上
       */
      map.entrySet();

      /**
       * 与get查找方式类似
       */
      map.containsKey("abc");
      /**
       * 从1开始，以i+=2循环，找value相等的数据
       */
      map.containsValue("1");

      /**
       * 将key的hash值所在的索引位置何+1位置设为null
       */
      map.remove("abc");

      map.clear();
      map.clone();
      map.equals(new IdentityHashMap<String, Object>());
      map.hashCode();
      map.isEmpty();
      map.putAll(new HashMap<String, Object>());
      map.size();
   }

   public static void main(String[] args) {
      IdentityHashMap<String, Object> map = new IdentityHashMap<String, Object>();
      map.put(new String("xx"), "first");
      map.put(new String("xx"), "second");
      map.put("xx", "second1");
      map.put("xx", "second2");

      for(Entry<String, Object> entry : map.entrySet()) {
         System.out.println(entry.getKey() + "|" + entry.getValue());
      }

      System.out.println("idenMap=" + map.containsKey("xx"));
      System.out.println("idenMap=" + map.get("xx"));
   }
}
