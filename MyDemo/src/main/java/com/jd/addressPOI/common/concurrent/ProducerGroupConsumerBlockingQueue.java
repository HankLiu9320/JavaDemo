package com.jd.addressPOI.common.concurrent;

import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 限制Linkedblockingqueue 方法在生产消费时调用
 * 
 * @author liujianjia
 */
public class ProducerGroupConsumerBlockingQueue<T> implements PSQueue<T> {
    private LinkedBlockingDeque<T> queue = null;
    private AtomicLong count;
    private int producerNum = 0;
    private int Max = 15;
    private int Min = 5;
    private Random rand = new Random(128);

    public ProducerGroupConsumerBlockingQueue(int capacity, AtomicLong count) {
        queue = new LinkedBlockingDeque<T>(capacity);
        this.count = count;
    }

    public void put(T e) throws InterruptedException {
        queue.put(e);
        count.incrementAndGet();
    }

    public void put(T e, int limit, int second) throws InterruptedException {
        queue.put(e);
        count.incrementAndGet();
        // 均匀限速
        long nanos = TimeUnit.SECONDS.toNanos(1);
        long sleepTime = (long) (nanos / (limit * 1.0 / second * 1.0)) * producerNum;
        int r = rand.nextInt(Max - Min);
        double rate = (r + Min) / 10.0;
        long nseepTime = (long) (sleepTime * rate);
        TimeUnit.NANOSECONDS.sleep(nseepTime);
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
