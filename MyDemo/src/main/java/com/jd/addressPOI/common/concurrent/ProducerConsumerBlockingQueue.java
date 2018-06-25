package com.jd.addressPOI.common.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 限制arrayblockingqueue 方法在生产消费时调用
 * 
 * @author liujianjia
 */
public class ProducerConsumerBlockingQueue<T> implements PSQueue<T> {
    private ArrayBlockingQueue<T> queue = null;
    private AtomicLong count;
    private int producerNum = 0;

    public ProducerConsumerBlockingQueue(int capacity, AtomicLong count) {
        queue = new ArrayBlockingQueue<T>(capacity);
        this.count = count;
    }

    public void put(T e) throws InterruptedException {
        queue.put(e);
        count.incrementAndGet();
    }

    @Override
    public void put(T e, int limit, int second) throws InterruptedException {
        queue.put(e);
        count.incrementAndGet();
        // 限速
        long nanos = TimeUnit.SECONDS.toNanos(1);
        long sleepTime = (long) (nanos / (limit * 1.0 / second * 1.0)) * producerNum;
        TimeUnit.NANOSECONDS.sleep(sleepTime);
    }

    @Override
    public void setProducerNum(int num) {
        this.producerNum = num;
    }

    public T poll() {
        return queue.poll();
    }

    public int size() {
        return queue.size();
    }
}
