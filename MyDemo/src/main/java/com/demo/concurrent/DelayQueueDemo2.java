package com.demo.concurrent;

import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayQueueDemo2 {
   private DelayQueue<DelayTask> delayQueue = new DelayQueue<DelayTask>();

   public static void main(String args[]) throws InterruptedException {
      final DelayQueueDemo2 tester = new DelayQueueDemo2();
      Random random = new Random();
      long startTime = System.currentTimeMillis();

      for(int i = 0; i < 3; i++) {
         tester.delayQueue.offer(new DelayQueueDemo2().new DelayTask(random.nextInt(20 * 1000), startTime));
      }

      for(int i = 0; i < 3; i++) {
         DelayTask task = tester.delayQueue.take();
         task.print();
         System.err.println("size:" + tester.delayQueue.size());
      }
   }

      class DelayTask implements Delayed {
      //
      private final long timeStamp;
      // 记录开始时间
      private final long startTime;
      private final long delayTime;

      public DelayTask(final long delayTime, final long startTime) {
         this.delayTime = delayTime;
         this.timeStamp = System.currentTimeMillis();

         this.startTime = startTime;
      }

      public long getExpectTime() {
         return timeStamp + delayTime;
      }

      @Override
      public int compareTo(Delayed o) {
         System.err.println("o:" + o);
         if(this.getExpectTime() > ((DelayTask) o).getExpectTime()) {
            return 1;
         }
         else if(this.getExpectTime() < ((DelayTask) o).getExpectTime()) {
            return -1;
         }

         return 0;
      }

      @Override
      public long getDelay(TimeUnit unit) {
         long delay = unit.convert((this.timeStamp + delayTime) - System.currentTimeMillis(), TimeUnit.NANOSECONDS);
         //System.err.println(Thread.currentThread() + ",delay:" + delay);
         return delay;
      }

      /*
       */
      public void print() {
         long now = System.currentTimeMillis();
         long realDelayTime = now - this.timeStamp;
         long Deviation = realDelayTime - this.delayTime;

         System.out.println(Thread.currentThread().getName() + "延迟:" + this.delayTime
               + ",真实延迟:" + realDelayTime + Deviation + ",完成共经历时间: " + (now - startTime));
      }
   }
}
