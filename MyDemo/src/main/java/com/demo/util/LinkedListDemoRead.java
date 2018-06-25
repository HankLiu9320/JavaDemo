package com.demo.util;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * LinkedList 
 * 描述 : 
 *    List 接口的链接列表实现。实现所有可选的列表操作，并且允许所有元素（包括 null）。除了实现 List
 *    接口外，LinkedList 类还为在列表的开头及结尾 get、remove 和 insert
 *    元素提供了统一的命名方法。这些操作允许将链接列表用作堆栈、队列或双端队列。
 * 
 *    此类实现 Deque 接口，为 add、poll 提供先进先出队列操作，以及其他堆栈和双端队列操作。
 *    所有操作都是按照双重链接列表的需要执行的。在列表中编索引的操作将从开头或结尾遍历列表（从靠近指定索引的一端）。
 * 
 *    注意，此实现不是同步的。如果多个线程同时访问一个链接列表，而其中至少一个线程从结构上修改了该列表，则它必须
 *    保持外部同步。（结构修改指添加或删除一个或多个元素的任何操作；仅设置元素的值不是结构修改。）这一般通过对自然封装该列表的对象进行同步操作来完成。
 *    如果不存在这样的对象，则应该使用 Collections.synchronizedList
 *    方法来“包装”该列表。最好在创建时完成这一操作，以防止对列表进行意外的不同步访问，如下所示：
 * 
 *    List list = Collections.synchronizedList(new LinkedList(...));此类的 iterator 和
 *    listIterator 方法返回的迭代器是快速失败 的：在迭代器创建之后，如果从结构上对列表进行修改，除非通过迭代器自身的 remove 或 add
 *    方法，其他任何时间任何方式的修改，迭代器都将抛出
 *    ConcurrentModificationException。因此，面对并发的修改，迭代器很快就会完全失败，
 *    而不冒将来不确定的时间任意发生不确定行为的风险。
 * 
 *    注意，迭代器的快速失败行为不能得到保证，一般来说，存在不同步的并发修改时，不可能作出任何硬性保证。快速失败迭代器尽最大努力抛出
 *    ConcurrentModificationException。因此，编写依赖于此异常的程序的方式是错误的，正确做法是：
 *    迭代器的快速失败行为应该仅用于检测程序错误。
 * 
 * 线程安全 :线程不安全 
 * 数据结构 : 基于链表的数据结构 
 * 性能：
 * 1)对于随机访问get和set，ArrayList觉对优于LinkedList，因为LinkedList要移动指针。
 * 2)对于新增和删除操作add和remove，LinedList比较占优势，因为ArrayList要移动数据
 * 
 * http://blog.csdn.net/vjrmlio/article/details/7950887#
 * http://blog.csdn.net/h11h03/article/details/3157968
 * 
 * @author Administrator
 *
 */
public class LinkedListDemoRead {
   /**
    * 测试构造方法
    */
   private void testConstruction() {
      LinkedList<String> lList = new LinkedList<String>();
      lList = new LinkedList<String>(new ArrayList<String>());
   }

   private void test() {
      LinkedList<String> lList = new LinkedList<String>();
      /**
       * LinkedList的add方法是调用linkLast，将传入内容放到链表最后，并维护上一个node的next信息
       */
      lList.add("aa");
      lList.offer("abc"); //调用了add

      /**
       * 如果index等于list的size，则使用linkLast插入到最后
       * 否则使用linkBefore，先通过node(index)找到此index所在的node(查找规则，如果index小于size的一般，从链表的浅表找next，
       * 否则从后边找prev)，然后通过传入的值创建当前node，并维护好next，prev，当前node的前后元素node
       */
      lList.add(2, "bb");
      
      /**
       * 通过node(index)找到对应 node，以node为起点，循环创建传入list的array数据，并维护好各节点的先后node
       */
      lList.addAll(new ArrayList<String>());
      lList.addAll(2, new ArrayList<String>());
      
      /**
       * 调用linkFirst，在链表头加入元素node
       */
      lList.addFirst("abc");
      lList.offerFirst("abc"); //调用了addFirst

      /**
       * 调用linkLast，在链表尾加入元素node
       */
      lList.addLast("abc");
      lList.offerLast("abc"); //调用了addLast

      /**
       * 从链表头开始循环，将所有node元素中的item,next,prev设置为null
       */
      lList.clear();
      
      /**
       * 克隆一个空的链表，并用老数据进行初始化
       */
      lList.clone();
      
      /**
       * 通过indexOf进行链表扫描，如果找到相等的item，则返回对应的链表位置
       */
      lList.contains("abc");
      
      /**
       * element调用getFirst，返回头元素
       */
      lList.element();
      
      /**
       * 通过node(index)查找，并返回node的item
       */
      lList.get(3);
      
      /**
       * 因为链表中全局维护了first和last元素node，以下两个方法直接对这两个node获取item
       */
      lList.getFirst();
      lList.getLast();
      
      /**
       * 从链表头遍历寻找与传入值相等的元素，并返回对应的位置
       */
      lList.indexOf("abc");

      /**
       * 从链表尾遍历寻找与传入值相等的元素，并返回对应的位置
       */
      lList.lastIndexOf("abc");
      
      /**
       * 通过node(index)找到对应node，并返回LinkedList内部类ListItr
       */
      lList.listIterator(3);
      
      /**
       * 获取不移除操作
       */
      lList.peek();
      lList.peekFirst();
      lList.peekLast();
      
      /**
       * 获取并移除此，unlinkXXX
       */
      lList.poll();
      lList.pollFirst();
      lList.pollLast();
      
      
      lList.pop(); //removeFirst
      lList.push("abc"); //addFirst
      
      
      lList.remove();
      lList.remove(2); //unlink
      
      lList.set(2, "abc");
      lList.size();
      
      //从此列表中移除第一次出现的指定元素（从头部到尾部遍历列表时）。
      lList.removeFirstOccurrence("abc");
      //从此列表中移除最后一次出现的指定元素（从头部到尾部遍历列表时）。
      lList.removeLastOccurrence("abc");
      
      /**
       * new 一个size大小的数组，并遍历链表将item放入到数组中
       */
      lList.toArray();
   }

   public static void main(String[] args) {
      LinkedList<String> lList = new LinkedList<String>();
      lList.add("1");
      lList.add("2");
      lList.add("3");
      lList.add("4");
      lList.add("5");

      System.out.println("链表的第一个元素是 : " + lList.getFirst());
      System.out.println("链表最后一个元素是 : " + lList.getLast());
   }
}
