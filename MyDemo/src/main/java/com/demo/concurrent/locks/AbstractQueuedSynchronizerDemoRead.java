package com.demo.concurrent.locks;

import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * 描述： 为实现依赖于先进先出 (FIFO) 等待队列的阻塞锁定和相关同步器（信号量、事件，等等）提供一个框架。此类的设计目标是成为依靠单个原子 int
 * 值来表示状态的大多数同步器的一个有用基础。子类必须定义更改此状态的受保护方法，并定义哪种状态对于此对象意味着被获取或被释放。假定这些条件之后，
 * 此类中的其他方法就可以实现所有排队和阻塞机制。子类可以维护其他状态字段，但只是为了获得同步而只追踪使用 getState()、setState(int)
 * 和 compareAndSetState(int, int) 方法来操作以原子方式更新的 int 值。
 * 
 * 应该将子类定义为非公共内部帮助器类，可用它们来实现其封闭类的同步属性。类 AbstractQueuedSynchronizer
 * 没有实现任何同步接口。而是定义了诸如 acquireInterruptibly(int)
 * 之类的一些方法，在适当的时候可以通过具体的锁定和相关同步器来调用它们，以实现其公共方法。
 * 
 * 此类支持默认的独占 模式和共享
 * 模式之一，或者二者都支持。处于独占模式下时，其他线程试图获取该锁定将无法取得成功。在共享模式下，多个线程获取某个锁定可能（但不是一定）会获得成功。此类并不
 * “了解”这些不同，除了机械地意识到当在共享模式下成功获取某一锁定时，下一个等待线程（如果存在）也必须确定自己是否可以成功获取该锁定。
 * 处于不同模式下的等待线程可以共享相同的 FIFO 队列。通常，实现子类只支持其中一种模式，但两种模式都可以在（例如）ReadWriteLock
 * 中发挥作用。只支持独占模式或者只支持共享模式的子类不必定义支持未使用模式的方法。
 * 
 * 此类通过支持独占模式的子类定义了一个嵌套的 AbstractQueuedSynchronizer.ConditionObject 类，可以将这个类用作
 * Condition 实现。isHeldExclusively() 方法将报告同步对于当前线程是否是独占的；使用当前 getState() 值调用
 * release(int) 方法则可以完全释放此对象；如果给定保存的状态值，那么 acquire(int)
 * 方法可以将此对象最终恢复为它以前获取的状态。没有别的 AbstractQueuedSynchronizer
 * 方法创建这样的条件，因此，如果无法满足此约束，则不要使用它。AbstractQueuedSynchronizer.ConditionObject
 * 的行为当然取决于其同步器实现的语义。
 * 
 * 此类为内部队列提供了检查、检测和监视方法，还为 condition 对象提供了类似方法。可以根据需要使用用于其同步机制的
 * AbstractQueuedSynchronizer 将这些方法导出到类中。
 * 
 * 此类的序列化只存储维护状态的基础原子整数，因此已序列化的对象拥有空的线程队列。需要可序列化的典型子类将定义一个 readObject
 * 方法，该方法在反序列化时将此对象恢复到某个已知初始状态。
 * 
 *  公平锁与非公平锁的区别：
 *  公平锁：每个线程获取锁时，都是进入到同步队列排队，排到则获取
 *  非公平锁：每个线程进入时都先获取锁，如果锁被占用，则排队获取，否则直接获取成功。
 */
public class AbstractQueuedSynchronizerDemoRead {
   private static class Sync extends AbstractQueuedSynchronizer {
      private static final long serialVersionUID = -5954589565559858929L;

      /**
       * 以源码的方式解释可继承的方法
       * @throws InterruptedException 
       */
      private void test() throws InterruptedException {
         /**
          * 使用AbstractQueuedSynchronizer作为同步将需要重定义以下方法：
          * 每个方法默认实现都是throws UnsupportedOperationException，定义这些方法是使用AbstractQueuedSynchronizer的唯一途径。
          * isHeldExclusively()：该线程是否正在独占资源。只有用到condition才需要去实现它。
          * tryAcquire(int)：独占方式。尝试获取资源，成功则返回true，失败则返回false。
          * tryRelease(int)：独占方式。尝试释放资源，成功则返回true，失败则返回false。
          * tryAcquireShared(int)：共享方式。尝试获取资源。负数表示失败；0表示成功，但没有剩余可用资源；正数表示成功，且有剩余资源。
          * tryReleaseShared(int)：共享方式。尝试释放资源，成功则返回true，失败则返回false。
          */
         
         /**
          * int waitStatus描述:
          * 表示节点的状态。其中包含的状态有：
          * CANCELLED，值为1，表示当前的线程被取消；
          * SIGNAL，值为-1，表示当前节点的后继节点包含的线程需要运行，也就是unpark；
          * CONDITION，值为-2，表示当前节点在等待condition，也就是在condition队列中；
          * PROPAGATE，值为-3，表示当前场景下后续的acquireShared能够得以执行；
          * 值为0，表示当前节点在sync队列中，等待着获取锁。
          */

         /**
          * 1) tryAcquire尝试获取同步状态，此方法用来继承重写。
          *    本类Sync在tryAcquire方法中使用了同步器提供的对state操作的方法，
          *    利用compareAndSet保证只有一个线程能够对状态进行成功修改，而没有成功修改的线程将进入sync队列排队 
          * 2)如果tryAcquire获取状态失败，返回false 
          * 3)并且调用addWaiter，快速将当前线程创建的Node以独占模式放到队尾。为保证多线程入队的线程安全问题，使用原子操作的
          *   compareAndSetTail进行比较（compareAndSetTail(pred, node)会比较pred和tail是否指向同一个节点，如果是，
          *   才将tail更新为node。）
          * 4)如果入队失败，也就是说pred与tail不指向同一个节点，则有其他线程提前入队成功
          * 5)入队失败则使用enq，使用死循环方式和compareAndSetTail原子性方法执行入队逻辑，直到入队成功，形成线程的等待队列
          * 6)将入队成功的Node传给acquireQueued，使其在自旋方式下获取锁。如果当前节点的前置节点是head，则尝试获得锁，获得成功
          *   则终止自旋，持有锁后执行逻辑。获得锁的顺序是为FIFO顺序，只有head后的一个Node才可以获得下一次的锁。
          *   acquireQueued虽然以自旋（死循环）方式获取锁，但是并不损耗多少性能。当其他线程获得锁失败后，则会被LockSupport.park
          *   将线程交于操作系统处理，挂起线程，在此线程未释放锁之前，其他企图获得所的线程均是挂起状态，则自旋不会吃太多性能。
          *   当锁被释放，只有head后一节点才可以获得到锁，其他线程再次挂起
          * 7)如果tryAcquire失败，但是自旋获取锁成功，如果返回true，说明Thread.interrupted()为true，则此线程被中断过，
          *   因为在阻塞状态下，并未响应中断，所以执行selfInterrupt，来自己给自己产生一个中断。补救之前未响应的中断。
          *   在acquireQueued()中，即使是线程在阻塞状态被中断唤醒而获取到cpu执行权利；但是，如果该线程的前面还有其它等待锁的线程，
          *   根据公平性原则，该线程依然无法获取到锁。它会再次阻塞！ 该线程再次阻塞，直到该线程被它的前面等待锁的线程锁唤醒；线程才会
          *   获取锁，然后“真正执行起来”！
          *   也就是说，在该线程“成功获取锁并真正执行起来”之前，它的中断会被忽略并且中断标记会被清除！ 
          *   因为在parkAndCheckInterrupt()中，我们线程的中断状态时调用了Thread.interrupted()。该函数不同于Thread的
          *   isInterrupted()函数，isInterrupted()仅仅返回中断状态，而interrupted()在返回当前中断状态之后，还会清除中断状态。 
          *   正因为之前的中断状态被清除了，所以这里需要调用selfInterrupt()重新产生一个中断！
          */
         super.acquire(1);

         /**
          * 1)如果线程中断，则抛出InterruptedException
          * 2)将当前线程以独占的方式创建Node，并加入到同步队列中
          * 3)轮训方式获得同步状态，同acquire
          * 4)判断如果线程被中断过，获得锁后直接抛出InterruptedException异常
          */
         try {
            super.acquireInterruptibly(1);
         }
         catch(InterruptedException e) {
            e.printStackTrace();
         }
         
         /**
          * 试图以独占模式获取对象，如果被中断则中止，如果到了给定超时时间，则会失败。
          * doAcquireNanos，入队之前记录时间，唤醒计算时间差
          */
         super.tryAcquireNanos(1, 1000);
         
         /**
          * 1)tryRelease尝试释放锁，尝试成功则进行释放
          * 2)成功释放锁之后，唤醒后置节点，unparkSuccessor通过head的next节点开始，找到一个不为空并且waitStatus<0的节点唤醒（unpark）。
          */
         super.release(1);
         
         /**
          * 1)判断tryAcquireShared大于0获取共享锁成功，否则进入队列自旋获取
          * 2)获取共享锁失败，则把当前线程以共享模式创建Node，放入到sync同步队列中
          * 3)进入自旋状态，如果新进入的线程的前驱是head，则继续尝试获取共享锁
          * 4)如果共享锁获取成功，即tryAcquireShared>=0，则当前前程退出自旋，并持有一个共享锁
          * 5)在获取共享锁成功的同时，将当前新进入的线程变为head节点，并判断propagate是否大于0，
          *   当前节点的后置节点是否是共享模式，如果是则doReleaseShared，如果当前节点的waitStatus为SIGNAL，则unparkSuccessor当前节点
          *   unparkSuccessor实际工作是唤醒下一个节点，或者下一个waitStatus的节点
          */
         super.acquireShared(1);
         
         /**
          * 1)如果线程中断，则抛出InterruptedException
          * 2)与doAcquireShared逻辑基本相同，只是在挂起时发现线程被中断过，则直接抛出InterruptedException
          */
         try {
            super.acquireSharedInterruptibly(1);
         }
         catch(InterruptedException e) {
            e.printStackTrace();
         }
         
         /**
          * 与tryAcquireNanos同理
          */
         super.tryAcquireSharedNanos(1, 1000);

         /**
          * 1) tryReleaseShared释放
          * 2) 释放成功则唤醒后置节点，使其继续自旋获得共享锁
          */
         super.releaseShared(1);
         
         /**
          * 独占锁与共享锁的工作流程区别
          * 独占锁：当head线程获得到锁后，他的所有后置节点都将阻塞，进入挂起状态。即使head线程运行完毕，后置线程也不可获得到锁。只有
          *        当调用release后使head所在线程释放锁才可以。也就是说独占锁只有在release调用unpark后，后置线程才可以被唤醒。
          * 共享锁：当head线程获得到共享锁后，会检查后置节点是否是共享模式，如果是，则通过doReleaseShared，唤醒后置节点，使后置节点从
          *        自旋状态启动，并参与获得共享锁。与独占锁的区别是，在获得共享锁成功后，就会唤醒后置线程，而不是等到调用releaseShared
          */
         
         
         /*****************************************************************/
         
         /**
          * 如果当前状态值等于预期值，则以原子方式将同步状态设置为给定的更新值。此操作具有 volatile 读和写的内存语义
          */
         super.compareAndSetState(0, 1);
         
         /**
          * 返回包含可能正以独占模式等待获取的线程 collection。
          * 从同步队列尾向前遍历，将所有不是共享模式的节点中的线程放到一个list中返回
          */
         super.getExclusiveQueuedThreads();
         
         /**
          * 同上，将是共享模式的放到list中返回
          */
         super.getSharedQueuedThreads();
         /**
          * 从同步队列尾部向前查找一个不为空的线程
          * 为什么从尾部向头部循环，而不是直接获得head的线程？？？？？？？
          */
         super.getFirstQueuedThread();
         
         /**
          * 返回等待获取的线程数估计值
          * 
          * 从尾部向前遍历，记录不为空的节点中不为空的线程数。如果中间的某一个节点的prev节点是空，则计算值不准
          */
         super.getQueueLength();
         
         /**
          * 查询是否其他线程也曾争着获取此同步器；也就是说，是否某个 acquire 方法已经阻塞
          * head != null
          */
         super.hasContended();

         /**
          * 查询是否有正在等待获取的任何线程
          * head != tail
          */
         super.hasQueuedThreads();
         
         /**
          * 查询是否有线程一直等待获得超过当前线程。
          */
         super.hasQueuedPredecessors();
         
         /**
          * 如果给定线程的当前已加入队列，则返回 true。
          */
         super.isQueued(Thread.currentThread());

         ConditionObject condition = new ConditionObject();
         
         /**
          * 1)将当前线程加入到等待队列中，如果等待队列中没有元素则firstWaiter,lastWaiter都为null
          *   addConditionWaiter进入后，先清空等待队列中已关闭的线程，并且以Condition模式创建一个Node放入队列中，
          *   此时firstWaiter，lastWaiter指向同一个Node对象，下一次await操作时，一个新的Node会加到lastWaiter的nextWaiter中，
          *   因为第一次入队时firstWaiter，lastWaiter指向同一个Node，则第二次放到尾节点的nextWaiter会与firstWaiter同步，也就是说
          *   firstWaiter的nextWaiter也变成了先入队的Node，addConditionWaiter的最后会将lastWaiter指向新入队的Node。这样则形成了
          *   等待单向链表
          * 2)释放当前线程的锁，也就是唤醒同步队列中的下一个等待节点。
          *   fullyRelease调用release，unpark head节点，也就是唤醒head后置为SINGLE的节点，使其可以获得锁
          * 
          * 3)判断第一步新建的Node是否在同步队列中，如果不在则park当前线程，当前线程被挂起。等待其他线程对此等待线程的唤醒。
          *   当某一个线程调用当前condition对象的signal时，此await挂起的线程被唤醒。则继续让当前线程调用acquireQueued
          *   获得锁，直到成功获得锁，继续执行await方法之后的逻辑。
          */
         condition.await();

         /**
          * 1) isHeldExclusively（用户重写）判断是否已独占方式运行，否则抛出异常。
          * 2) doSignal->transferForSignal 将当前condition对象中的等待队列的firstWaiter节点拿出，并断掉firstWaiter的单向链。
          *    也就是说去掉firstWaiter的nextWaiter，并将next作为头结点。然后将firstWaiter节点放入到锁中的同步队列尾部（enq方法）
          *    并unpark firstWaiter所在的线程，唤醒后，则出发await方法继续向下执行（await后续可能是刚加入的这个节点挂起）。 
          */
         condition.signal();

         /**
          * 以下逻辑与await和signal雷同
          */
         condition.await(1000, TimeUnit.MICROSECONDS);
         condition.awaitNanos(1000);
         condition.awaitUninterruptibly();
         condition.awaitUntil(new Date());
         condition.signalAll();

         super.getWaitingThreads(condition);
         super.getWaitQueueLength(condition);
         super.owns(condition);
         super.toString();
      }

      protected boolean isHeldExclusively() {
         return super.getState() == 1;
      }

      public boolean tryAcquire(int qcquires) {
         if(compareAndSetState(0, 1)) {
            setExclusiveOwnerThread(Thread.currentThread());
            return true;
         }

         return false;
      }

      protected boolean tryRelease(int releases) {
         if(getState() == 0) {
            throw new IllegalMonitorStateException();
         }

         setExclusiveOwnerThread(null);
         setState(0);
         return true;
      }

      Condition newCondition() {
         return new ConditionObject();
      }
   }

   public static class Mutex {
      private final Sync sync = new Sync();

      public void lock() {
         sync.acquire(1);
      }

      public boolean tryLock() {
         return sync.tryAcquire(1);
      }

      public void unlock() {
         sync.release(1);
      }

      public Condition newCondition() {
         return sync.newCondition();
      }

      public boolean islLocked() {
         return sync.isHeldExclusively();
      }

      public boolean hasQueuedThreads() {
         return sync.hasQueuedThreads();
      }

      public Collection<Thread> getQueuedThreads() {
         return sync.getQueuedThreads();
      }

      public void lockInterruptibly() throws InterruptedException {
         sync.acquireInterruptibly(1);
      }

      public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
         return sync.tryAcquireNanos(1, unit.toNanos(timeout));
      }
   }

   /**
    * 测试实现的独占锁Mutex
    * @param args
    */
   public static void testCustomerLock() {
      final Mutex lock = new Mutex();
      
      new Thread(new Runnable() {
         @Override
         public void run() {
            lock.lock();
            try {
               System.err.println("Work by " + Thread.currentThread().getName());
               TimeUnit.SECONDS.sleep(10);
            }
            catch(InterruptedException e) {
               e.printStackTrace();
            }
            lock.unlock();
         }
      }, "线程1").start();

      new Thread(new Runnable() {
         @Override
         public void run() {
            while(!lock.tryLock()) {
               try {
                  TimeUnit.SECONDS.sleep(1);
               }
               catch(InterruptedException e) {
                  e.printStackTrace();
               }
            }

            System.err.println("Work by " + Thread.currentThread().getName());
            lock.unlock();
         }
      }, "线程2").start();
   }

   /**
    * 测试同步队列中唤醒顺序，FIFO
    * @param args
    * @throws InterruptedException
    */
   public static void testFIFO() throws InterruptedException {
      /**
       * 1)创建一个线程先独占锁
       * 2)然后启动四个线程竞争锁，测试是否是FIFO顺序
       * 3)当外边的线程获得锁后，其他线程通过LockSupport.park挂起，等待锁的释放。释放后，必定只有head后置节点可以获得锁。FIFO
       */
      final Mutex lock = new Mutex();
      final CountDownLatch d = new CountDownLatch(4);

      new Thread(new Runnable() {
         @Override
         public void run() {
            lock.lock();
            System.err.println("------locked----");
            try {
               TimeUnit.SECONDS.sleep(3);
            }
            catch(InterruptedException e) {
               e.printStackTrace();
            }

            lock.unlock();
         }
      }).start();
      try {
         TimeUnit.SECONDS.sleep(1);
      }
      catch(InterruptedException e) {
         e.printStackTrace();
      }

      for(int i = 0; i < 4; i++) {
         new Thread(new Runnable() {
            @Override
            public void run() {
               int s = new Random().nextInt(10);
               s = s <= 0 ? s = -s+1 : s;
               d.countDown();
               lock.lock();
               System.err.println(Thread.currentThread().getName() + ", sleep :" + s);
               try {
                  TimeUnit.MILLISECONDS.sleep(new Random().nextInt(s));
               }
               catch(InterruptedException e) {
                  e.printStackTrace();
               }
               System.err.println(Thread.currentThread().getName() + " finish");
               lock.unlock();
            }
         }, "my-thread-" + i).start();
      }
      
      d.await();
      System.err.println(lock.getQueuedThreads());
   }

   /**
    * 测试独占锁在线程结束后不释放，同步队列中其他节点是否可以获得锁
    * @throws InterruptedException 
    */
   private static void testExclusiveLockAwaken() throws InterruptedException {
      final Mutex lock = new Mutex();

      new Thread(new Runnable() {
         @Override
         public void run() {
            try {
               lock.lock();
               System.err.println("----runing----");
            }
            finally {
               System.err.println("------run finish by dont release lock----");
            }
         }
      }).start();

      try {
         TimeUnit.SECONDS.sleep(1);
      }
      catch(InterruptedException e) {
         e.printStackTrace();
      }

      for(int i = 0; i < 4; i++) {
         new Thread(new Runnable() {
            @Override
            public void run() {
               //睡眠一下，入队不要太激烈，不要太快。为了打印同步队列里有值
               try {
                  TimeUnit.SECONDS.sleep(new Random().nextInt(5));
               }
               catch(InterruptedException e) {
                  e.printStackTrace();
               }
               System.err.println(Thread.currentThread().getName() + "|" + lock.getQueuedThreads());
               lock.lock();
               System.err.println("======in=======" + Thread.currentThread().getName() );
               System.err.println(Thread.currentThread().getName() + " finish");
               lock.unlock();
            }
         }, "my-thread-" + i).start();
      }
      
      System.err.println(Thread.currentThread().getName() + "|" + lock);
   }

   public static void testCondition() throws InterruptedException {
      ReentrantLock lock = new ReentrantLock();
      Condition condition = lock.newCondition();
      
      
   }

   public static void main(String[] args) throws InterruptedException {
//      testCustomerLock();
//      testFIFO();
//      testExclusiveLockAwaken();
      testCondition();
   }
}
