package com.demo.concurrent.locks;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * https://my.oschina.net/adan1/blog/158107
 * @author Liujianjia
 */
public class ReentrantReadWriteLockDemoRead {
   private final Map<String, Object> m = new TreeMap<String, Object>();
   private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
   private final ReentrantReadWriteLock.ReadLock r = rwl.readLock();
   private final ReentrantReadWriteLock.WriteLock w = rwl.writeLock();

   private static void test() {
      ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
      ReentrantReadWriteLock.WriteLock wlock = rwl.writeLock();
      
      /**
       * 排它锁获取
       * 在Sync类中重写了tryAcquire
       * 已经获得排它锁的线程不是当前线程，则获得失败,编程被挂起。否则状态累加，排它锁获得成功
       */
      wlock.lock();
      
      /**
       * Sync的tryRelease，减掉1，并重置state
       */
      wlock.unlock();
      
      /**
       * 写入锁tryLock()也就是tryWriteLock()成功的条件是: 没有写入锁或者写入锁是当前线程，并且尝试一次修改state成功。
       */
      wlock.tryLock();

      ReentrantReadWriteLock.ReadLock rlock = rwl.readLock();
      
      /**
       * 在Sync类中重写tryAcquireShared
       * 1)如果一个线程获得了写锁（排它锁），则读锁获取失败。当前读线程被挂起
       * 2)通过设置状态，读线程获得锁，成功后则通过ThreadLocal记录每个线程多的锁的count
       * 3)失败则通过fullTryAcquireShared继续获取
       */
      rlock.lock();
      
      rlock.unlock();

      /**
       * 读取锁tryLock()也就是tryReadLock()成功的条件是：没有写入锁或者写入锁是当前线程，并且读线程共享锁数量没有超过65535个。
       */
      rlock.tryLock();
      
      
      //降级所:锁降级指的是写锁降级为读锁：把持住当前拥有的写锁，再获取到读锁，随后释放先前拥有的写锁的过程。
      //而锁升级是将读锁变成写锁，但是ReentrantReadWriteLock不支持这种方式。

      /***************************/
      
      rwl.isWriteLockedByCurrentThread();
      rwl.getReadHoldCount();
   }

   public Object get(String key) {
      r.lock();
      try {
         return m.get(key);
      }
      finally {
         r.unlock();
      }
   }

   public String[] allKeys() {
      r.lock();
      try {
         return (String[]) m.keySet().toArray();
      }
      finally {
         r.unlock();
      }
   }

   public Object put(String key, Object value) {
      w.lock();
      try {
         return m.put(key, value);
      }
      finally {
         w.unlock();
      }
   }

   public void clear() {
      w.lock();
      try {
         m.clear();
      }
      finally {
         w.unlock();
      }
   }

   public static void main(String[] args) {
//      test();
      System.err.println(Integer.toBinaryString((1 << 16) - 1));
   }
}
