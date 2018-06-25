package com.demo.concurrent;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Delayed 元素的一个无界阻塞队列，只有在延迟期满时才能从中提取元素。该队列的头部 是延迟期满后保存时间最长的 Delayed
 * 元素。如果延迟都还没有期满，则队列没有头部，并且 poll 将返回 null。当一个元素的 getDelay(TimeUnit.NANOSECONDS)
 * 方法返回一个小于等于 0 的值时，将发生到期。即使无法使用 take 或 poll 移除未到期的元素，也不会将这些元素作为正常元素对待。例如，size
 * 方法同时返回到期和未到期元素的计数。此队列不允许使用 null 元素。
 * 
 */
public class DelayQueueDemoRead {
   private void testConstruction() {
      /**
       * 初始化实例，全局有一个lock，和一个condition available
       */
      DelayQueue<Student> q = new DelayQueue<Student>();

      q = new DelayQueue<Student>(new ArrayList<Student>());
   }

   private void test() throws InterruptedException {
      DelayQueue<Student> q = new DelayQueue<Student>();
      
      /**
       * 将指定元素插入此延迟队列。
       * 1)上锁， 并将传入数据放入PriorityQueue队列中。
       * 2)peek出PriorityQueue的元素，如果放入元素等于取出元素，说明放入成功。available.signal阻塞的其他放入线程
       */
      q.offer(new Student());
      q.offer(new Student(), 1000, TimeUnit.SECONDS);
      q.put(new Student());
      q.add(new Student());

      /**
       * 获取并移除此队列的头部，在可从此队列获得到期延迟的元素之前一直等待（如有必要）。
       * 1)可中断方式上锁
       * 2)自旋方式peek出队列中的数据，如果getDelay大于0，说明时间没有到，则继续等待
       */
      q.take();

      /**
       * 上锁，使用PriorityQueue peek
       */
      q.peek();
      q.poll();
      q.poll(1000, TimeUnit.MICROSECONDS);
      q.remove();
   }

   static final int STUDENT_SIZE = 5;

   public static void main(String[] args) {
      Random r = new Random();
      ExecutorService exec = Executors.newCachedThreadPool();
      DelayQueue<Student> students = new DelayQueue<Student>();

      for(int i = 0; i < STUDENT_SIZE; i++) {
         students.put(new Student("学生" + (i + 1), 3000 + r.nextInt(9000)));
      }

      students.put(new Student.EndExam(12000, exec));// 1200为考试结束时间
      exec.execute(new Teacher(students, exec));
   }
}

class Student implements Runnable, Delayed {
   private String name;
   private long submitTime;// 交卷时间
   private long workTime;// 考试时间

   public Student() {

   }

   public Student(String name, long submitTime) {
      super();
      this.name = name;
      workTime = submitTime;
      // 都转为转为ns
      this.submitTime = TimeUnit.NANOSECONDS.convert(submitTime, TimeUnit.MILLISECONDS) + System.nanoTime();
   }

   @Override
   public void run() {
      System.out.println(name + " 交卷,用时" + workTime / 100 + "分钟");
   }

   @Override
   public long getDelay(TimeUnit unit) {
      return unit.convert(submitTime - System.nanoTime(), unit.NANOSECONDS);
   }

   @Override
   public int compareTo(Delayed o) {
      Student that = (Student) o;
      return submitTime > that.submitTime ? 1 : (submitTime < that.submitTime ? -1 : 0);
   }

   public static class EndExam extends Student {
      private ExecutorService exec;

      public EndExam(int submitTime, ExecutorService exec) {
         super(null, submitTime);
         this.exec = exec;
      }

      @Override
      public void run() {
         exec.shutdownNow();
      }
   }
}

class Teacher implements Runnable {
   private DelayQueue<Student> students;
   private ExecutorService exec;

   public Teacher(DelayQueue<Student> students, ExecutorService exec) {
      super();
      this.students = students;
      this.exec = exec;
   }

   @Override
   public void run() {
      try {
         System.out.println("考试开始……");
         while(!Thread.interrupted()) {
            students.take().run();
         }
         System.out.println("考试结束……");
      }
      catch(InterruptedException e) {
         e.printStackTrace();
      }
   }
}
