package com.demo.concurrent;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author amber2012
 * 
 *         1、ConcurrentSkipListSet<E>: public class ConcurrentSkipListSet
 *         <E> extends AbstractSet<E> implements NavigableSet<E>, Cloneable,
 *         Serializable
 * 
 *         关于ConcurrentSkipListSet<E>在jdk的API的文档说明： 1）ConcurrentSkipListSet
 *         <E>是jdk6新增的类，位于java.util.concurrent并发库下；
 * 
 *         2）ConcurrentSkipListSet<E>和TreeSet一样，都是支持自然排序，并且可以在构造的时候定义Comparator
 *         <E> 的比较器，该类的方法基本和TreeSet中方法一样（方法签名一样）；
 * 
 *         3）和其他的Set集合一样，ConcurrentSkipListSet
 *         <E>都是基于Map集合的，ConcurrentSkipListMap便是它的底层实现；
 * 
 *         4）在多线程的环境下，ConcurrentSkipListSet
 *         <E>中的contains、add、remove操作是安全的，多个线程可以安全地并发 执行插入、移除和访问操作。但是对于批量操作
 *         addAll、removeAll、retainAll 和 containsAll并不能保证以原子方式执行，
 *         理由很简单，因为addAll、removeAll、retainAll底层调用的还是contains、add、remove的方法，
 *         在批量操作时，只能保证
 *         每一次的contains、add、remove的操作是原子性的（即在进行contains、add、remove三个操作时，
 *         不会被其他线程打断），而 不能保证每一次批量的操作都不会被其他线程打断。
 * 
 *         5）此类不允许使用 null 元素，因为无法可靠地将 null 参数及返回值与不存在的元素区分开来。
 * 
 *         2、public class CopyOnWriteArraySet<E>extends AbstractSet<E>implements
 *         Serializable 对于CopyOnWriteArraySet<E>类： 1）它最适合于具有以下特征的应用程序：set
 *         大小通常保持很小，只读操作远多于可变操作，需要在遍历期间防止线程间的冲突。 2）它是线程安全的,
 *         底层的实现是CopyOnWriteArrayList； 3）因为通常需要复制整个基础数组，所以可变操作（add、set 和 remove
 *         等等）的开销很大。 4）迭代器不支持可变 remove 操作。
 *         5）使用迭代器进行遍历的速度很快，并且不会与其他线程发生冲突。在构造迭代器时，迭代器依赖于不变的数组快照。
 * 
 */
public class ConcurrentSkipListSetDemo {
   private static ConcurrentSkipListSet<String> listSet = new ConcurrentSkipListSet<String>();

   static {
      for(int i = 1; i <= 30; i++) {
         listSet.add("new String :" + i);
      }
      
      System.err.println(listSet);
   }

   public static void main(String[] args) {

      ExecutorService threadPool = Executors.newFixedThreadPool(3);
      threadPool.execute(new Runnable() {
         public void run() {
            sop(Thread.currentThread().getName() + " first: " + listSet.pollFirst()); // 获取并移除第一个（最低）元素
         }
      });

      threadPool.execute(new Runnable() {
         public void run() {
            sop(Thread.currentThread().getName() + " last: " + listSet.pollLast()); // 获取并移除最后（最高）元素
         }
      });

      threadPool.execute(new Runnable() {
         public void run() {
            sop(Thread.currentThread().getName() + " : " + listSet.pollFirst()); // 获取并移除第一个（最低）元素
         }
      });
      
      threadPool.shutdown();
   }

   private static void sop(Object obj) {
      System.out.println(obj);
   }
}