package com.demo.threadutil;

import java.util.concurrent.BlockingQueue;

public interface Producer<T> {
    public void produce(BlockingQueue<T> queue);
}
