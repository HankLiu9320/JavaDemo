package com.demo.concurrent;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 一个无界阻塞队列，它使用与类 PriorityQueue 相同的顺序规则，并且提供了阻塞获取操作。虽然此队列逻辑上是无界的，但是资源被耗尽时试图执行
 * add 操作也将失败（导致 OutOfMemoryError）。此类不允许使用 null
 * 元素。依赖自然顺序的优先级队列也不允许插入不可比较的对象（这样做会导致抛出 ClassCastException）。
 * 
 * 此类及其迭代器可以实现 Collection 和 Iterator 接口的所有可选 方法。iterator() 方法中提供的迭代器并不
 * 保证以特定的顺序遍历 PriorityBlockingQueue 的元素。如果需要有序地进行遍历，则应考虑使用
 * Arrays.sort(pq.toArray())。此外，可以使用方法 drainTo 按优先级顺序移除 全部或部分元素，并将它们放在另一个
 * collection 中。
 * 
 * 在此类上进行的操作不保证具有同等优先级的元素的顺序。如果需要实施某一排序，那么可以定义自定义类或者比较器，比较器可使用修改键断开主优先级值之间的联系。例如
 * ，以下是应用先进先出 (first-in-first-out) 规则断开可比较元素之间联系的一个类。要使用该类，则需要插入一个新的
 * FIFOEntry(anEntry) 来替换普通的条目对象。
 * 
 * @author Administrator
 *
 */
public class PriorityBlockingQueueDemoRead {
   private void testConstruction() {
      /**
       * 数据结构与PriorityQueue相同，只是在构造的时候创建了一个lock，和notEmpty的condition
       */
      PriorityBlockingQueue<PriorityEntity> q = new PriorityBlockingQueue<PriorityEntity>();
      q = new PriorityBlockingQueue<PriorityEntity>(100);
      // ....
   }

   private void test() {
      PriorityBlockingQueue<PriorityEntity> q = new PriorityBlockingQueue<PriorityEntity>();

      /**
       * 锁住扩容，入队。与Priority操作方式相同
       */
      q.offer(new PriorityEntity(100, 1));
   }

   static Random r = new Random(47);

   public static void main(String args[]) throws InterruptedException {
      final PriorityBlockingQueue<PriorityEntity> q = new PriorityBlockingQueue<PriorityEntity>();
      ExecutorService se = Executors.newCachedThreadPool();

      // execute producer
      se.execute(new Runnable() {
         public void run() {
            int i = 0;
            while(true) {
               q.put(new PriorityEntity(r.nextInt(10), i++));

               try {
                  TimeUnit.MILLISECONDS.sleep(r.nextInt(1000));
               }
               catch(InterruptedException e) {
                  e.printStackTrace();
               }
            }
         }
      });

      // execute consumer
      se.execute(new Runnable() {
         public void run() {
            while(true) {
               try {
                  System.out.println("take-- " + q.take() + " leave:-- [" + q.toString() + "]");
                  TimeUnit.MILLISECONDS.sleep(r.nextInt(2000));
               }
               catch(InterruptedException e) {
                  e.printStackTrace();
               }
            }
         }
      });

      try {
         TimeUnit.SECONDS.sleep(5);
      }
      catch(InterruptedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

}

class PriorityEntity implements Comparable<PriorityEntity> {
   private static int count = 0;
   private int id = count++;
   private int priority;
   private int index = 0;

   public PriorityEntity(int _priority, int _index) {
      this.priority = _priority;
      this.index = _index;
   }

   public String toString() {
      return id + "# [index=" + index + " priority=" + priority + "]";
   }

   // 数字小，优先级高
   public int compareTo(PriorityEntity o) {
      return this.priority > o.priority ? 1
            : this.priority < o.priority ? -1 : 0;
   }

   // 数字大，优先级高
   // public int compareTo(PriorityEntity o) {
   // return this.priority < o.priority ? 1
   // : this.priority > o.priority ? -1 : 0;
   // }
}