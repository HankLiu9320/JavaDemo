package com.demo.concurrent;

/**
 * 一个基于已链接节点的、任选范围的阻塞双端队列。 可选的容量范围构造方法参数是一种防止过度膨胀的方式。如果未指定容量，
 * 那么容量将等于 Integer.MAX_VALUE。只要插入元素不会使双端队列超出容量，每次插入后都将动态地创建链接节点。 
 * 大多数操作都以固定时间运行（不计阻塞消耗的时间）。
 * 异常包括 remove、removeFirstOccurrence、removeLastOccurrence、contains、iterator.remove() 以及批量操作，它们均以线性时间运行。

 * LinkedBlockingDeque是双向链表实现的双向并发阻塞队列。该阻塞队列同时支持FIFO和FILO两种操作方式，即可以从队列的头和尾同时操作(
 * 插入/删除)；并且，该阻塞队列是支持线程安全。
 * 
 * 此外，LinkedBlockingDeque还是可选容量的(防止过度膨胀)，即可以指定队列的容量。如果不指定，默认容量大小等于Integer.
 * MAX_VALUE。
 * 
 * LinkedBlockingDeque是“线程安全”的队列，而LinkedList是非线程安全的。
 * 下面是“多个线程同时操作并且遍历queue”的示例
 * (01) 当queue是LinkedBlockingDeque对象时，程序能正常运行。
 * (02) 当queue是LinkedList对象时，程序会产生ConcurrentModificationException异常。
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class LinkedBlockingDequeDemoRead {
   private void testConstruction() {
      /**
       * capacity = Integer.MAX_VALUE
       * 全局构造了一个ReentrantLock lock
       * Condition notEmpty = lock.newCondition()
       * Condition notFull = lock.newCondition()
       * first节点
       * last节点
       */
      LinkedBlockingDeque<String> queue = new LinkedBlockingDeque<String>();
      /**
       * capaciy = 100
       */
      queue = new LinkedBlockingDeque<String>(100);
      
      queue = new LinkedBlockingDeque<String>(new ArrayList<String>());
   }

   private void test() throws InterruptedException {
      LinkedBlockingDeque<String> queue = new LinkedBlockingDeque<String>();
      
      /**
       * 入队阻塞的方法
       * 锁住，并将传入的值插入到队列尾，如果容量已满，则生产线程阻塞。
       * 如果入队成功，则唤醒消费者线程
       */
      queue.put("abc");
      queue.putFirst("abc");
      queue.putLast("abc");

      /**
       * 不阻塞入队，入队成功则唤醒消费者线程
       */
      queue.offer("abc");
      queue.offer("abc", 1000, TimeUnit.MILLISECONDS);
      queue.offerFirst("abc");
      queue.offerFirst("abc", 1000, TimeUnit.MILLISECONDS);
      queue.offerLast("abc");
      queue.offerLast("abc", 1000, TimeUnit.MILLISECONDS);

      /**
       * addFirst
       */
      queue.push("abc");

      /**
       * 在不违反容量限制的情况下，将指定的元素插入此双端队列的末尾。
       * 1)调用addLast-offerLast
       * 2)在offerLast中，上锁并调用linkLast将给入的值创建成Node加入到双端队列尾，并notEmpty.signal 通知阻塞的消费者线程获取
       */
      queue.add("abc");
      /**
       * 如果立即可行且不违反容量限制，则将指定的元素插入此双端队列的开头；如果当前没有空间可用，则抛出 IllegalStateException。
       * 添加到双端队列头，并通知阻塞的消费者线程获取
       */
      queue.addFirst("abc");
      /**
       * 如果立即可行且不违反容量限制，则将指定的元素插入此双端队列的末尾；如果当前没有空间可用，则抛出 IllegalStateException。
       * 同add方法
       */
      queue.addLast("abc");
      /**
       * 循环调用add
       */
      queue.addAll(new ArrayList<String>());
      
      /**
       * 获取并移除此双端队列表示的队列的头部（即此双端队列的第一个元素），必要时将一直等待可用元素
       * 
       */
      queue.take();
      queue.takeFirst();
      queue.takeLast();

      /**
       * 获取但不移除此双端队列表示的队列的头部（即此双端队列的第一个元素）；如果此双端队列为空，则返回 null。
       */
      queue.peek();
      queue.peekFirst();
      queue.peekLast();

      /**
       * 获取并移除此双端队列表示的队列的头部（即此双端队列的第一个元素）；如果此双端队列为空，则返回 null。
       */
      queue.poll();
      queue.poll(1000, TimeUnit.MICROSECONDS);
      queue.pollFirst();
      queue.pollFirst(1000, TimeUnit.MICROSECONDS);
      queue.pollLast();
      queue.pollLast(1000, TimeUnit.MICROSECONDS);
      
      /**
       * 从此双端队列所表示的堆栈中弹出一个元素。removeFirst
       */
      queue.pop();

      queue.clear();
      queue.contains("abc");
      
      /**
       * 返回在此双端队列的元素上以逆向连续顺序进行迭代的迭代器。元素将按从最后一个（尾部）到第一个（头部）的顺序返回。返回的 Iterator 是一个不会抛出 ConcurrentModificationException 的“弱一致”迭代器，能确保遍历迭代器构造后存在的所有元素，并可以（但并不保证）反映构造后的任何修改。 
       */
      queue.descendingIterator();
      queue.drainTo(new ArrayList<String>());
      queue.drainTo(new ArrayList<String>(), 10);
      
      /**
       * getFirst
       */
      queue.element();
      queue.getFirst();
      queue.getLast();
      queue.iterator();
      
      
      queue.remainingCapacity();
      
      queue.remove();
      queue.remove("abc");
      queue.removeFirst();
      queue.removeFirstOccurrence("abc");
      queue.removeLast();
      queue.removeLastOccurrence("abc");
      
      queue.size();
   }

   private static Queue<String> queue = new LinkedBlockingDeque<String>();

   public static void main(String[] args) {

      // 同时启动两个线程对queue进行操作！
      new MyThread("ta").start();
      new MyThread("tb").start();
   }

   private static void printAll() {
      String value;
      Iterator iter = queue.iterator();
      while(iter.hasNext()) {
         value = (String) iter.next();
         System.out.print(value + ", ");
      }
      System.out.println();
   }

   private static class MyThread extends Thread {
      MyThread(String name) {
         super(name);
      }

      @Override
      public void run() {
         int i = 0;
         while(i++ < 6) {
            // “线程名” + "-" + "序号"
            String val = Thread.currentThread().getName() + i;
            queue.add(val);
            // 通过“Iterator”遍历queue。
            printAll();
         }
      }
   }
}
