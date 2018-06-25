package com.demo.util;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeSet;

/**
 * 一个基于优先级堆的无界优先级队列。优先级队列的元素按照其自然顺序进行排序，或者根据构造队列时提供的 Comparator
 * 进行排序，具体取决于所使用的构造方法。优先级队列不允许使用 null 元素。依靠自然顺序的优先级队列还不允许插入不可比较的对象（这样做可能导致
 * ClassCastException）。
 * 
 * 此队列的头 是按指定排序方式确定的最小 元素。如果多个元素都是最小值，则头是其中一个元素——选择方法是任意的。队列获取操作
 * poll、remove、peek 和 element 访问处于队列头的元素。
 * 
 * 优先级队列是无界的，但是有一个内部容量，控制着用于存储队列元素的数组大小。它通常至少等于队列的大小。随着不断向优先级队列添加元素，其容量会自动增加。
 * 无需指定容量增加策略的细节。
 * 
 * 此类及其迭代器实现了 Collection 和 Iterator 接口的所有可选 方法。方法 iterator() 中提供的迭代器不
 * 保证以任何特定的顺序遍历优先级队列中的元素。如果需要按顺序遍历，请考虑使用 Arrays.sort(pq.toArray())。
 * 
 * http://blog.csdn.net/kobejayandy/article/details/46832797
 *
 */
public class PriorityQueueDemoRead {
   private void testConstruction() {
      /**
       * 默认初始化queue数组capacity为11，comparator为null
       */
      PriorityQueue<Node> pqueue = new PriorityQueue<Node>();

      /**
       * capacity为10，comparator为null
       */
      pqueue = new PriorityQueue<Node>(10);

      /**
       * 使用pqueue的compatator，然后放入数据到当前的queue
       */
      pqueue = new PriorityQueue<Node>(pqueue);

      pqueue = new PriorityQueue<Node>(new TreeSet<Node>());
      pqueue = new PriorityQueue<Node>(10, new Comparator<Node>() {
         @Override
         public int compare(Node o1, Node o2) {
            // to do
            return 0;
         }
      });
   }

   private void test() {
      PriorityQueue<Node> pqueue = new PriorityQueue<Node>();

      /**
       * 将指定的元素插入此优先级队列。 
       * 1)如果传入对象为null，则抛出NPE
       * 2)判断size是否达到数组长度，达到则扩容（扩容规则：如果已有数组长度<64，则以+2的方式增长，如果>=64,则以长度的50%增长） 
       * 3)然后以siftUp方式，排序并入队(查看类注释网址)
       */
      pqueue.offer(new Node("abc", 1));
      pqueue.add(new Node("abc", 1));
      
      /**
       * 获取但不移除此队列的头；如果此队列为空，则返回 null。
       */
      pqueue.peek();
      /**
       * 获取并移除此队列的头，如果此队列为空，则返回 null。
       * siftDown
       */
      pqueue.poll();

      pqueue.remove(new Node("abc", 1));
      pqueue.comparator();
   }

   public static void main(String args[]) {
      Comparator<Node> OrderIsdn = new Comparator<Node>() {
         public int compare(Node o1, Node o2) {
            // TODO Auto-generated method stub
            int numbera = o1.getPopulation();
            int numberb = o2.getPopulation();
            if(numberb > numbera) {
               return 1;
            }
            else if(numberb < numbera) {
               return -1;
            }
            else {
               return 0;
            }
         }
      };

      Queue<Node> priorityQueue = new PriorityQueue<Node>(11, OrderIsdn);
      Node t1 = new Node("t1", 1);
      Node t3 = new Node("t3", 3);
      Node t2 = new Node("t2", 2);
      Node t4 = new Node("t4", 0);
      priorityQueue.add(t1);
      priorityQueue.add(t3);
      priorityQueue.add(t2);
      priorityQueue.add(t4);
      System.out.println(priorityQueue.poll().toString());

      Queue<String> strQueue = new PriorityQueue<String>(11);
      strQueue.add("d");
      strQueue.add("c");
      strQueue.add("a");
      strQueue.add("b");
      System.err.println(strQueue.poll());
   }

   static class Node {
      private String name;
      private int population;

      public Node(String name, int population) {
         this.name = name;
         this.population = population;
      }

      public String getName() {
         return this.name;
      }

      public int getPopulation() {
         return this.population;
      }

      public String toString() {
         return getName() + " - " + getPopulation();
      }
   }
}
