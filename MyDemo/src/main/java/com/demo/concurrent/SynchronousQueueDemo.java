package com.demo.concurrent;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 一种阻塞队列，其中每个插入操作必须等待另一个线程的对应移除操作 ，反之亦然。同步队列没有任何内部容量，甚至连一个队列的容量都没有。不能在同步队列上进行
 * peek，因为仅在试图要移除元素时，该元素才存在；除非另一个线程试图移除某个元素，否则也不能（使用任何方法）插入元素；也不能迭代队列，
 * 因为其中没有元素可用于迭代。队列的头 是尝试添加到队列中的首个已排队插入线程的元素；如果没有这样的已排队线程，则没有可用于移除的元素并且 poll()
 * 将会返回 null。对于其他 Collection 方法（例如 contains），SynchronousQueue 作为一个空
 * collection。此队列不允许 null 元素。
 * 
 * 同步队列类似于 CSP 和 Ada 中使用的 rendezvous
 * 信道。它非常适合于传递性设计，在这种设计中，在一个线程中运行的对象要将某些信息、事件或任务传递给在另一个线程中运行的对象，它就必须与该对象同步。
 * 
 * 对于正在等待的生产者和使用者线程而言，此类支持可选的公平排序策略。默认情况下不保证这种排序。但是，使用公平设置为 true 所构造的队列可保证线程以
 * FIFO 的顺序进行访问。
 * 
 * SynchronousQueue 类实现了 BlockingQueue 接口。 SynchronousQueue
 * 是一个特殊的队列，它的内部同时只能够容纳单个元素。如果该队列已有一元素的话，试图向队列中插入一个新元素的线程将会阻塞，
 * 直到另一个线程将该元素从队列中抽走。 同样，如果该队列为空，试图向队列中抽取一个元素的线程将会阻塞，直到另一个线程向队列中插入了一条新的元素。
 */
public class SynchronousQueueDemo {
    private static void testConstruction() {
        /**
         * 默认以TransferStack创建同步队列
         */
        SynchronousQueue<String> sq = new SynchronousQueue<String>();

        /**
         * fair ? new TransferQueue() : new TransferStack()
         */
        sq = new SynchronousQueue<String>(true);
    }

    private static void test() throws InterruptedException {
        SynchronousQueue<String> sq = new SynchronousQueue<String>();
        /**
         * 如果另一个线程正在等待以便接收指定元素，则将指定元素插入到此队列。
         * 1)判断传入值是否为null，null则抛出NPE
         * 2)使用transferer.transfer(e, true, 0)传递数据
         * 3)
         */
        sq.offer("abc");
        sq.add("abc");
        
        /**
         * 将指定元素添加到此队列，如有必要则等待另一个线程接收它。
         */
        sq.put("abc");
        
        sq.take();
    }

    public static void main(String[] args) throws InterruptedException {
        final SynchronousQueue<String> sq = new SynchronousQueue<String>();

        new Thread() {
            public void run() {
                while(true) {
                    try {
                        System.err.println("start put");
                        sq.put("a");
                        System.err.println("start finish");
                        TimeUnit.MILLISECONDS.sleep(1000);
                    }
                    catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        new Thread() {
            public void run() {
                while(true) {
                    try {
                        System.err.println("take start");
                        sq.take();
                        sq.take();
                        System.err.println("take finish");
                        TimeUnit.MILLISECONDS.sleep(1200);
                    }
                    
                    catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
