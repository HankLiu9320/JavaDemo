package com.demo.concurrent;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 在Java多线程应用中，队列的使用率很高，多数生产消费模型的首选数据结构就是队列(先进先出)。
 * Java提供的线程安全的Queue可以分为阻塞队列和非阻塞队列，其中阻塞队列的典型例子是BlockingQueue，
 * 非阻塞队列的典型例子是ConcurrentLinkedQueue，在实际应用中要根据实际需要选用阻塞队列或者非阻塞队列。
 * 
 * 一个基于链接节点的无界线程安全队列。此队列按照 FIFO（先进先出）原则对元素进行排序。队列的头部 是队列中时间最长的元素。队列的尾部
 * 是队列中时间最短的元素。新的元素插入到队列的尾部，队列获取操作从队列头部获得元素。当多个线程共享访问一个公共 collection
 * 时，ConcurrentLinkedQueue 是一个恰当的选择。此队列不允许使用 null 元素。
 * 
 * 此实现采用了有效的“无等待 (wait-free)”算法，该算法基于 Maged M. Michael 和 Michael L. Scott 合著的
 * Simple, Fast, and Practical Non-Blocking and Blocking Concurrent Queue
 * Algorithms 中描述的算法。
 * 
 * 需要小心的是，与大多数 collection 不同，size 方法不是 一个固定时间操作。由于这些队列的异步特性，确定当前元素的数量需要遍历这些元素。
 * 此类及其迭代器实现了 Collection 和 Iterator 接口的所有可选 方法。
 * 内存一致性效果：当存在其他并发 collection 时，将对象放入 ConcurrentLinkedQueue 之前的线程中的操作
 * happen-before 随后通过另一线程从 ConcurrentLinkedQueue 访问或移除该元素的操作。
 */
public class ConcurrentLinkedQueueDemo {
   private void testContruction() {
      /**
       * head和tail指向同一个新建的Node
       * 全局变量，初始化unsafe和head和tail的偏移量offset
       */
      ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<Integer>();
      
      queue = new ConcurrentLinkedQueue<Integer>(new ArrayList<Integer>());
   }

   private static void test() {
      ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<Integer>();
      
      queue.add(100);
      /**
       * 1)判断插入的值是否为空，null则抛出NPE
       * 2)循环查找tail节点的next节点为null的节点
       * 3)CAS方式设置插入的值，设置成功则返回true
       */
      queue.offer(100);
      queue.poll();
      
   }

   private static ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<Integer>();
   private static int count = 2; // 线程个数
   // CountDownLatch，一个同步辅助类，在完成一组正在其他线程中执行的操作之前，它允许一个或多个线程一直等待。
   private static CountDownLatch latch = new CountDownLatch(count);

   /**
    * 生产
    */
   public static void offer() {
      for(int i = 0; i < 100000; i++) {
         queue.offer(i);
      }
   }

   /**
    * 消费
    */
   static class Poll implements Runnable {
      public void run() {
         // 使用queue.size消耗的时间远远大于queue.isEmpty
         // while(queue.size() > 0) {
         while(!queue.isEmpty()) {
            System.out.println(queue.poll());
         }
         latch.countDown();
      }
   }


   public static void main(String[] args) {
      test();
   }

   public static void main1(String[] args) throws InterruptedException {
      long timeStart = System.currentTimeMillis();
      ExecutorService es = Executors.newFixedThreadPool(4);
      ConcurrentLinkedQueueDemo.offer();

      for(int i = 0; i < count; i++) {
         es.submit(new Poll());
      }

      latch.await(); // 使得主线程(main)阻塞直到latch.countDown()为零才继续执行
      System.out.println("cost time " + (System.currentTimeMillis() - timeStart) + "ms");
      es.shutdown();
   }
}
