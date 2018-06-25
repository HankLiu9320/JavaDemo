package com.demo.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 一个基于已链接节点的、范围任意的 blocking queue。此队列按 FIFO（先进先出）排序元素。队列的头部 是在队列中时间最长的元素。队列的尾部
 * 是在队列中时间最短的元素。新元素插入到队列的尾部，并且队列获取操作会获得位于队列头部的元素。链接队列的吞吐量通常要高于基于数组的队列，
 * 但是在大多数并发应用程序中，其可预知的性能要低。
 * 
 * 可选的容量范围构造方法参数作为防止队列过度扩展的一种方法。如果未指定容量，则它等于
 * Integer.MAX_VALUE。除非插入节点会使队列超出容量，否则每次插入后会动态地创建链接节点。
 */
public class LinkedBlockingQueueDemoRead {
   private void testContruction() {
      /**
       * capicity=Integer.MAX_VALUE last = head = new Node<E>(null);
       */
      LinkedBlockingQueue<String> names = new LinkedBlockingQueue<String>();

      /**
       * capacity = 100
       */
      names = new LinkedBlockingQueue<String>(100);

      names = new LinkedBlockingQueue<String>(new ArrayList<String>());
   }

   private static void test() throws InterruptedException {
      LinkedBlockingQueue<String> names = new LinkedBlockingQueue<String>();
      
      /**
       * 如果队列满了，则put将会阻塞
       * 1)判断传入的值是否为空，空则抛出NPE
       * 2)以传入的值创建Node节点，并使用putLock，可中断方式上锁
       * 3)判断count是否等于capacity，如果等于，说明队列已满，则notFull等待，当前线程挂起，并释放锁。
       * 4)如果队列未满，则将节点放入队列尾，并更新last节点
       * 5)如果队列元素总和（c+1）小于capacity，则说明队列未满，则唤醒其他put线程，继续put操作
       * 6)当c=0时，即意味着之前的队列是空队列,出队列的线程都处于等待状态，
       *   现在新添加了一个新的元素,即队列不再为空,因此它会唤醒正在等待获取元素的线程。
       */
      names.put("demo");
      /**
       * 逻辑与put差不多，只是不进行阻塞操作。如果队列已满，不插入，否则插入。
       * 因为不阻塞线程，所以锁使用的是不可中断锁，不存在阻塞通过终端结束线程的操作。put存在
       */
      names.offer("demo");
      /**
       * 如果队列已满，则通过notFull.awaitNanos阻塞一段时间，然后继续判断是否已满，满了则返回，否则插入
       */
      names.offer("demo", 1000, TimeUnit.SECONDS);
      
      /**
       * 获取并移除此队列的头部，在元素变得可用之前一直等待（如果有必要）。
       * 1)使用takeLock以可中断方式锁住线程
       * 2)判断count是否等于0，如果等于0，说明队列为空，则takeLock衍生出的condition notEmpty进行等待。直到被唤醒
       * 3)唤醒或者队列不为空,则获取并移除队列头
       * 4)判断队列元素个数大于1，如果大于，说明队列中最少还有一个元素，则唤醒notEmpty阻塞的线程，使take线程被唤醒，继续获取元素
       * 5) 当c==capaitcy时，即在获取当前元素之前，队列已经满了，而此时获取元素之后，队列就会空出一个位置，
       *    故当前线程会唤醒执行插入操作的线
       *    程通知其他中的一个可以进行插入操作。
       */
      names.take();
      /**
       * 获取并移除此队列的头，如果此队列为空，则返回 null。
       * 与take逻辑一样，只是在队列中没有元素时不进行阻塞，而是返回一个null
       */
      names.poll();
      names.poll(1000, TimeUnit.SECONDS);

      /**
       * 获取但不移除此队列的头；如果此队列为空，则返回 null。
       */
      names.peek();
      
      names.size();
      names.drainTo(new ArrayList<String>());
      names.drainTo(new ArrayList<String>(), 10);
      names.clear();
   }

   public static void testPut() throws InterruptedException {
      LinkedBlockingQueue<String> names = new LinkedBlockingQueue<String>(1);
      names.put("a");
      System.err.println("put a");
      // 从这一句开始后面的就不会被执行了
      names.put("b");
      System.err.println("put b");
      System.out.println("程序执行到此...");
   }

   public static void testAdd() {
      LinkedBlockingQueue<String> names = new LinkedBlockingQueue<String>();
      names.add("a");
      System.err.println("put a");
      names.add("b");
      System.err.println("put b");
      System.out.println("程序执行到此...");
   }

   public static void testDrainTo() throws InterruptedException {
      LinkedBlockingQueue<String> names = new LinkedBlockingQueue<String>(5);

      for(int i = 0; i < 10; i++) {
         names.put("[" + i + "]");

         System.err.println(names.size());
         if(names.size() == 4) {
            List<String> list = new ArrayList();
            names.drainTo(list, 2);
            System.err.println("drainTo1:" + java.util.Arrays.toString(list.toArray()));
            list = new ArrayList();
            names.drainTo(list);
            System.err.println("drainTo2:" + java.util.Arrays.toString(list.toArray()));
         }
      }
   }

   public static void main(String[] args) throws Exception {
      // test put function
      // testPut();

      // test add function
      // testAdd();

      // test drainTo function
      // testDrainTo();
      
//      test();
      AtomicInteger count = new AtomicInteger();
      System.err.println(count);
      System.err.println(count.getAndIncrement());
      System.err.println(count.getAndDecrement());
   }

}
