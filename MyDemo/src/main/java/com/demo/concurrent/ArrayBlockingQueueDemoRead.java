package com.demo.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 一个由数组支持的有界阻塞队列。此队列按 FIFO（先进先出）原则对元素进行排序。队列的头部 是在队列中存在时间最长的元素。队列的尾部
 * 是在队列中存在时间最短的元素。新元素插入到队列的尾部，队列获取操作则是从队列头部开始获得元素。
 * 
 * 这是一个典型的“有界缓存区”，固定大小的数组在其中保持生产者插入的元素和使用者提取的元素。一旦创建了这样的缓存区，就不能再增加其容量。
 * 试图向已满队列中放入元素会导致操作受阻塞；试图从空队列中提取元素将导致类似阻塞。
 * 
 * 此类支持对等待的生产者线程和使用者线程进行排序的可选公平策略。默认情况下，不保证是这种排序。然而，通过将公平性 (fairness) 设置为 true
 * 而构造的队列允许按照 FIFO 顺序访问线程。公平性通常会降低吞吐量，但也减少了可变性和避免了“不平衡性”。
 */
public class ArrayBlockingQueueDemoRead {
   /**
    * 构造方法描述
    */
   private void testConstruction() {
      /**
       * ArrayBlockingQueue内部使用了一个ReentrantLock提供阻塞功能。一下构造为capacity容量为10,
       * ReentrantLock为 非公平锁模式，并创建两个condition 
       * notEmpty = lock.newCondition();
       * notFull = lock.newCondition();
       */
      ArrayBlockingQueue<String> abq = new ArrayBlockingQueue<String>(10);

      abq = new ArrayBlockingQueue<String>(16, false);
   }

   private void test() throws InterruptedException {
      ArrayBlockingQueue<String> abq = new ArrayBlockingQueue<String>(10);

      /**
       * 将指定的元素插入到此队列的尾部（如果立即可行且不会超过该队列的容量），在成功时返回 true，如果此队列已满，则返回 false。
       * 此方法没有使用notFull.await(),所以当插入时，发现数组满的时候不会阻塞。
       * 1)判断传入的值是否为null，是则抛出NPE 
       * 2)使用lock阻塞插入逻辑，并判断如果插入的数量，不等于数组长度，则将插入的数据放到数组最大索引上
       *   再插入过程中，如果插入元素到达数组尾，则索引变为0，变为从头插入。以此来循环向数组中放入数据。
       * 3)插入完数据之后，notEmpty.signal(),通知notEmpty所在的线程，已经不是not empty了。
       */
      abq.offer("test");
      
      /**
       * 将指定的元素插入此队列的尾部，如果该队列已满，则在到达指定的等待时间之前等待可用的空间。
       * 1)在插入时，如果发现数组已满，则notFull.awaitNanos(xx)，将当前线程挂起指定时间。
       * 2)如果挂起时间达到，则自动唤醒，当前线程开始抢占锁，如果获得锁，则继续添加操作
       */
      abq.offer("test", 1000, TimeUnit.SECONDS);

      /**
       * 调用了offer(xx),当满了的时候会抛出异常
       */
      abq.add("test");
      abq.addAll(new ArrayList<String>());

      /**
       * 如offer逻辑一样，只是在发现数组长度满了的时候，则会调用notFull.await();阻塞插入操作。需要调用notFull.single()才能唤醒
       */
      abq.put("test");

      /**
       * 获取并移除此队列的头，如果此队列为空，则返回 null。
       * 此方法不阻塞
       * 1)lock住poll的逻辑，并调用extract
       * 2)调用第一次poll的时候，将第0个元素设置成空，并维护count数，返回至空前的值。下一次在调用poll的时候，
       *   则从操作第1个元素，内部维护了poll后的索引。当获取的索引到达数组尾，则索引takeIndex设置为0，从头继续
       *   获取。
       */
      String polled = abq.poll();
      abq.poll(1000, TimeUnit.SECONDS);

      /**
       * 获取但不移除此队列的头；如果此队列为空，则返回 null。
       * 此方法不阻塞线程
       * 1)上锁
       * 2)itemAt(takeIndex)取到对应索引的元素。
       */
      abq.peek();

      /**
       * 获取，但是不移除此队列的头。此方法与 peek 唯一的不同在于：此队列为空时将抛出一个异常。
       */
      abq.element();

      /**
       * 获取并移除此队列的头部，在元素变得可用之前一直等待（如果有必要）。
       * 1)上锁，使用extract获取数组items[takeIndex]位置元素，并至空
       * 2)如发现count为0，则notEmpty.await()，take线程阻塞，此时数组为not empty状态，等待notEmpty.single()操作
       */
      abq.take();

      /**
       * 获取并移除此队列的头。此方法与 poll 唯一的不同在于：此队列为空时将抛出一个异常。 
       */
      abq.remove();

      /**
       * 上锁，清空数组所有元素，并调用notFull.signalAll()
       */
      abq.clear();
      
      /**
       * 上锁循环匹配
       */
      abq.contains("test");
      
      /**
       * 移除此队列中所有可用的元素，并将它们添加到给定 collection 中
       * 
       */
      abq.drainTo(new ArrayList<String>());
      
      /**
       * 获取的时候上锁
       */
      abq.iterator();
      
      /**
       * 返回在无阻塞的理想情况下（不存在内存或资源约束）此队列能接受的其他元素数量。
       */
      abq.remainingCapacity();
      abq.remove("test");
      abq.size();
      abq.toArray();

      /**
       * 将指定的元素插入到此队列的尾部（如果立即可行且不会超过该队列的容量），在成功时返回 true，如果此队列已满，则抛出
       * IllegalStateException。
       */
      abq.add("test");
   }

   public static void main(String[] args) throws Exception {
      // test put function
      // testPut();

      // test add function
      //testAdd();

      // test drainTo function
      // testDrainTo();

      // test offer,add,put,....
//      testCRUD();

//        fetchBlocking();
      //
       ArrayBlockingQueue<String> abq = new ArrayBlockingQueue<String>(10);
       testProducerConsumer(abq);
   }

   /**
    * 此方法展示了 ArrayBlockingQueue 的插入阻塞特性 ：如果队列已经满了，那么插入的操作就会被阻塞，程序执行就会被迫暂停。
    * 
    * @throws InterruptedException
    */
   public static void testPut() throws InterruptedException {
      ArrayBlockingQueue<String> names = new ArrayBlockingQueue<String>(1);
      names.put("a");
      System.err.println("put a");
      // 从这一句开始后面的就不会被执行了，满了就阻塞
      names.put("b");
      System.err.println("put b");
      System.out.println("程序执行到此...");
   }

   public static void testAdd() {
      ArrayBlockingQueue<String> names = new ArrayBlockingQueue<String>(1);
      names.add("a");
      System.err.println("put a");
      // 如果满了就抛异常
      names.add("b");
      System.err.println("put b");
      System.out.println("程序执行到此...");
   }

   public static void testDrainTo() throws InterruptedException {
      ArrayBlockingQueue<String> names = new ArrayBlockingQueue<String>(5);

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

   public static void testCRUD() throws InterruptedException {
      ArrayBlockingQueue<String> names = new ArrayBlockingQueue<String>(5);
      names.offer("a");
      String p = names.peek();
      System.err.println("peek rs:" + p + ",size:" + names.size());
      p = names.poll();
      System.err.println("poll rs:" + p + ",size:" + names.size());
      // 可阻塞方法
      names.put("b");
      names.take();
      
      names.take();
   }

   /**
    * 此方法展示了 ArrayBlockingQueue 的取出阻塞特性 ：如果队列为空，程序执行就会报错。
    * 
    */
   public static void fetchBlocking() throws InterruptedException {
      ArrayBlockingQueue<String> names = new ArrayBlockingQueue<String>(1);
      names.put("a");
      names.remove();
      //空了抛异常
      names.remove();
      names.put("b");

      System.out.println("程序执行到此...");
   }

   /**
    * @作用 此方法用来测试生产者和消费者 为了让程序在获取不到元素时不报错有两种方式： 1.让生产者的生产速度大于消费者的消费速度
    *     2.在消费者获取资源出错时让消费者线程暂停一段时间，不输出错误。
    * @param abq
    */
   public static void testProducerConsumer(ArrayBlockingQueue<String> abq) {
      Thread tConsumer = new Consumer(abq);
      Thread tProducer = new Producer(abq);
      tConsumer.start();
      tProducer.start();
   }

}

/**
 * @作用 定义消费者
 * 
 */
class Consumer extends Thread {
   ArrayBlockingQueue<String> abq = null;

   public Consumer(ArrayBlockingQueue<String> abq) {
      super();
      this.abq = abq;
   }

   @Override
   public void run() {
      while(true) {
         try {
            Thread.sleep(1500);
            String msg = abq.remove();
            System.out.println("取数据：====" + msg + "\t剩余数据量：" + abq.size());
         }
         catch(Exception e) {
            try {
               Thread.sleep(2000);
            }
            catch(InterruptedException e1) {
               e1.printStackTrace();
            }
         }
      }
   }
}

/**
 * @作用 定义生产者
 * 
 */
class Producer extends Thread {
   ArrayBlockingQueue<String> abq = null;

   public Producer(ArrayBlockingQueue<String> abq) {
      this.abq = abq;
   }

   @Override
   public void run() {
      int i = 0;
      while(true) {
         try {
            Thread.sleep(500);
            abq.put("" + i);
            System.out.println("存放数据：====" + i + "\t剩余数据量：" + abq.size());
            i++;
         }
         catch(Exception e) {
            e.printStackTrace();
         }
      }
   }
}
