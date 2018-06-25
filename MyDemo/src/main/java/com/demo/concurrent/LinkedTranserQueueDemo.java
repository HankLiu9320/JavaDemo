package com.demo.concurrent;

import java.util.Random;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

/**
 * LinkedTransferQueue实现了一个重要的接口TransferQueue,该接口含有下面几个重要方法： 
 * 1. transfer(E e)
 *    若当前存在一个正在等待获取的消费者线程，即立刻移交之；否则，会插入当前元素e到队列尾部，并且等待进入阻塞状态，
 *    到有消费者线程取走该元素。 
 * 2.tryTransfer(E e) 
 *    若当前存在一个正在等待获取的消费者线程（使用take()或者poll()函数），使用该方法会即刻转移/传输对象元素e；
 *    若不存在，则返回false，并且不进入队列。这是一个不阻塞的操作。
 * 3. tryTransfer(E e, long timeout, TimeUnit unit) 
 *    若当前存在一个正在等待获取的消费者线程，会立即传输给它; 否则将插入元素e到队列尾部，并且等待被消费者线程获取消费掉,
 *    若在指定的时间内元素e无法被消费者线程获取，则返回false，同时该元素被移除。 
 * 4. hasWaitingConsumer() 判断是否存在消费者线程
 * 5. getWaitingConsumerCount() 获取所有等待获取元素的消费线程数量
 *
 */
public class LinkedTranserQueueDemo {
   /**
    * 测试构造方法
    */
   private void testConstruction() {
      
   }

   /**
    * 测试基本方法
    */
   private void test() {
      
   }

   public static void main(String[] args) {
      TransferQueue<String> queue = new LinkedTransferQueue<String>();
      Thread producer = new Thread(new Producer(queue));
      producer.setDaemon(true); // 设置为守护进程使得线程执行结束后程序自动结束运行
      producer.start();

      for(int i = 0; i < 10; i++) {
         Thread consumer = new Thread(new Consumer(queue));
         consumer.setDaemon(true);
         consumer.start();
         try {
            // 消费者进程休眠一秒钟，以便以便生产者获得CPU，从而生产产品
            Thread.sleep(1000);
         }
         catch(InterruptedException e) {
            e.printStackTrace();
         }
      }
   }

   static class Producer implements Runnable {
      private final TransferQueue<String> queue;

      public Producer(TransferQueue<String> queue) {
         this.queue = queue;
      }

      private String produce() {
         return " your lucky number " + (new Random().nextInt(100));
      }

      @Override
      public void run() {
         try {
            while(true) {
               if(queue.hasWaitingConsumer()) {
                  queue.transfer(produce());
               }
               TimeUnit.SECONDS.sleep(1);// 生产者睡眠一秒钟,这样可以看出程序的执行过程
            }
         }
         catch(InterruptedException e) {
         }
      }
   }

   static class Consumer implements Runnable {
      private final TransferQueue<String> queue;

      public Consumer(TransferQueue<String> queue) {
         this.queue = queue;
      }

      @Override
      public void run() {
         try {
            System.out.println(" Consumer " + Thread.currentThread().getName()
                  + queue.take());
         }
         catch(InterruptedException e) {
         }
      }
   }
}
