package com.demo.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ArrayList 
 * 描述：
 *    List 接口的大小可变数组的实现。实现了所有可选列表操作，并允许包括 null 在内的所有元素。除了实现 List
 *    接口外，此类还提供一些方法来操作内部用来存储列表的数组的大小。（此类大致上等同于 Vector 类，除了此类是不同步的。）
 *    size、isEmpty、get、set、iterator 和 listIterator 操作都以固定时间运行。add 操作以分摊的固定时间
 *    运行，也就是说，添加 n 个元素需要 O(n) 时间。其他所有操作都以线性时间运行（大体上讲）。与用于 LinkedList
 *    实现的常数因子相比，此实现的常数因子较低。
 * 
 *    每个 ArrayList 实例都有一个容量。该容量是指用来存储列表元素的数组的大小。它总是至少等于列表的大小。随着向 ArrayList
 *    中不断添加元素，其容量也自动增长。并未指定增长策略的细节，因为这不只是添加元素会带来分摊固定时间开销那样简单。
 * 
 *    在添加大量元素前，应用程序可以使用 ensureCapacity 操作来增加 ArrayList 实例的容量。这可以减少递增式再分配的数量。
 *    注意，此实现不是同步的。如果多个线程同时访问一个 ArrayList 实例，而其中至少一个线程从结构上修改了列表，那么它必须
 *    保持外部同步。（结构上的修改是指任何添加或删除一个或多个元素的操作，或者显式调整底层数组的大小；仅仅设置元素的值不是结构上的修改。）
 *    这一般通过对自然封装该列表的对象进行同步操作来完成。如果不存在这样的对象，则应该使用 Collections.synchronizedList
 *    方法将该列表“包装”起来。这最好在创建时完成，以防止意外对列表进行不同步的访问：
 *    List list = Collections.synchronizedList(new ArrayList(...)); 此类的 iterator 和
 *    listIterator 方法返回的迭代器是快速失败的：在创建迭代器之后，除非通过迭代器自身的 remove 或 add
 *    方法从结构上对列表进行修改，否则在任何时间以任何方式对列表进行修改，迭代器都会抛出
 *    ConcurrentModificationException。因此，面对并发的修改，迭代器很快就会完全失败，
 *    而不是冒着在将来某个不确定时间发生任意不确定行为的风险。
 * 
 *    注意，迭代器的快速失败行为无法得到保证，因为一般来说，不可能对是否出现不同步并发修改做出任何硬性保证。快速失败迭代器会尽最大努力抛出
 *    ConcurrentModificationException。因此，为提高这类迭代器的正确性而编写一个依赖于此异常的程序是错误的做法：
 *    迭代器的快速失败行为应该仅用于检测 bug。
 * 
 * 线程安全 :线程不安全 
 * 数据结构 :是实现了基于动态数组的数据结构
 * 性能优化 : 使用前尽量先使用ensureCapacity设置好list的size
 * 
 *实现的接口 1、List
 *         <E>接口：我们会出现这样一个疑问，在查看了ArrayList的父类AbstractList也实现了List
 *         <E>接口，那为什么子类ArrayList还是去实现一遍呢？这是想不通的地方，所以我就去查资料，有的人说是为了查看代码方便，
 *         使观看者一目了然，说法不一，但每一个让我感觉合理的，但是在stackOverFlow中找到了答案，这里其实很有趣，网址贴出来
 *         http://stackoverflow.com/questions/2165204/why-does-linkedhashsete-
 *         extend-hashsete-and-implement-sete开发这个collection
 *         的作者Josh说。这其实是一个mistake，因为他写这代码的时候觉得这个会有用处，但是其实并没什么用，但因为没什么影响，
 *         就一直留到了现在。
 * 
 *         2、RandomAccess接口：这个是一个标记性接口，通过查看api文档，它的作用就是用来快速随机存取，有关效率的问题，
 *         在实现了该接口的话，那么使用普通的for循环来遍历，性能更高，例如arrayList。而没有实现该接口的话，使用Iterator来迭代，
 *         这样性能更高，例如linkedList。所以这个标记性只是为了让我们知道我们用什么样的方式去获取数据性能更好，可以参考这篇博文，http:
 *         //blog.csdn.net/keda8997110/article/details/8635005
 * 
 *         3、Cloneable接口：实现了该接口，就可以使用Object.Clone()方法了。
 * 
 *         4、Serializable接口：实现该序列化接口，表明该类可以被序列化，什么是序列化？简单的说，就是能够从类变成字节流传输，
 *         然后还能从字节流变成原来的类。
 */
public class ArrayListDemoRead {
   /**
    * 测试构造方法
    */
   private static void testConstruction() {
      /**
       *    public ArrayList()
       *    设置initialCapacity为10，elementData数组大小为10
       *    public ArrayList(int initialCapacity)
       *    elementData数组大小为initialCapacity
       *    public ArrayList(Collection<? extends E> c)
       */
      Collection<Object> cs = new ArrayList<Object>();
      cs.add("a");
      cs.add("b");
      cs.add("c");

      //elementData = c.toArray();
      List<Object> list = new ArrayList<Object>(cs);
      System.err.println(list);
   }

   private static void test() {
      ArrayList<Object> list = new ArrayList<Object>();
      /**
       * list的add方法
       * 1) list中的数组长度以oldCapacity + (oldCapacity >> 1)增加
       * 2) list数组最长为Integer.MAX_VALUE
       * 3) 使用System.arraycopy移动数据
       */
      list.add("abc");
      list.add("abc");
      list.add(1, "abc");
      list.addAll(new ArrayList<String>());
      list.addAll(1, new ArrayList<String>());
      /**
       * 把数组中所有元素设置为null,但是list内部的数组长度还保存原有长度
       */
      list.clear();
      
      System.err.println(list.size());
      ArrayList list2 = (ArrayList)list.clone();
      System.err.println(list2.size());
      
      /**
       * list的contains是从数组的第一个元素开始，对传入内容做equals比较
       */
      list.add("abc");
      list.contains("abc");
      
      /**
       * 如果传入数字大于默认值10，则将数组长度设置成传入值大小
       */
      list.ensureCapacity(40);
      
      /**
       * 以数组下标形式获得元素
       */
      list.get(0);
      
      /**
       * list的indexOf是从数组的第一个元素开始，对传入内容做equals比较,第一个相等则返回下标
       */
      list.clear();
      list.add("a");
      list.add("abc");
      list.add("c");
      list.add("abc");
      int idx = list.indexOf("abc");
      System.err.println("idx:" + idx + ",size:" + list.size());
      
      /**
       * 判断size == 0
       */
      list.isEmpty();
      
      /**
       * list的lastIndexOf是从数组的最后一个元素开始，对传入内容做equals比较,第一个相等则返回下标
       */
      list.lastIndexOf("abc");
      
      /**
       * list的remove是从数组的第一个元素开始，对传入内容做equals比较,相等则将数组向前移动一个位置，并将数组最后一位置null
       */
      list.remove("abc");
      list.remove(2);
      
      /**
       * 将对应下标的数组内容替换为传入的内容
       */
      list.set(2, "abc");
      
      /**
       * Arrays.copyOf
       */
      list.toArray();
   }

   public static void main(String[] args) {
      //testConstruction();
      test();
   }
}
