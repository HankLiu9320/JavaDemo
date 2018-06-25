package com.demo.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 一个同步辅助类，它允许一组线程互相等待，直到到达某个公共屏障点 (common barrier
 * point)。在涉及一组固定大小的线程的程序中，这些线程必须不时地互相等待，此时 CyclicBarrier 很有用。因为该 barrier
 * 在释放等待线程后可以重用，所以称它为循环 的 barrier。 CyclicBarrier 支持一个可选的 Runnable
 * 命令，在一组线程中的最后一个线程到达之后（但在释放所有线程之前），该命令只在每个屏障点运行一次。若在继续所有参与线程之前更新共享状态，此屏障操作 很有用。
 *
 * http://www.tuicool.com/articles/ERbIven
 */
public class CyclicBarrierDemoRead {
    private void testConstruction() {
        /**
         * 初始化一个lock
         */
        CyclicBarrier cb = new CyclicBarrier(3);

        cb = new CyclicBarrier(3, new Runnable() {
            @Override
            public void run() {
                // to do
            }
        });

    }

    private void test() throws InterruptedException, BrokenBarrierException, TimeoutException {
        CyclicBarrier cb = new CyclicBarrier(3);

        /**
         * 1)全局记录传入的parties = 3
         * 2)await进入，上锁。然后检测generation（栅栏状态标识）是否已经结束。并判断线程是否被中断。
         * 3)如果线程正常运行，则做--count操作，判断结果是否等于0，如果不等，则trip(lock的condition).await()
         * 自旋等待，挂起线程。
         * 4)如果进入的线程--count的值为0，则执行传入的Runnable，并trip.signalAll，唤醒trip上的所有等待线程。
         */
        cb.await();
        
        cb.await(1000, TimeUnit.MICROSECONDS);

        /**
         * 返回当前在屏障处等待的参与者数目。
         */
        cb.getNumberWaiting();
        
        /**
         * 返回要求启动此 barrier 的参与者数目。
         */
        cb.getParties();

        /**
         * breakBarrier，并充值generation
         */
        cb.reset();
    }

    private static final int THREAD_NUM = 5;

    public static class WorkerThread implements Runnable {
        CyclicBarrier barrier;

        public WorkerThread(CyclicBarrier b) {
            this.barrier = b;
        }

        @Override
        public void run() {
            try {
                // 线程在这里等待，直到所有线程都到达barrier。
                System.out.println("ID:" + Thread.currentThread().getId() + " Working");

                if(barrier.getNumberWaiting() > 2) {
                    // 如果barrier中的线程意外终端，会导致barrier的栅栏线程不被调用。
                    // Thread.currentThread().interrupt();
                }
            }
            finally {
                try {
                    barrier.await();
                }
                catch(InterruptedException | BrokenBarrierException e) {
                    System.err.println(Thread.currentThread().getName() + ":" + e);
                }
            }
        }
    }

    /**
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        final CyclicBarrier cb = new CyclicBarrier(THREAD_NUM, new Runnable() {
            // 当所有线程到达barrier时执行
            @Override
            public void run() {
                System.out.println("Inside Barrier");
            }
        });

        for(int i = 0; i < THREAD_NUM; i++) {
            new Thread(new WorkerThread(cb)).start();
            System.err.println("waiting num:" + cb.getNumberWaiting());
        }
    }
}
