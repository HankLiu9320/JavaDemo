package com.jd.addressPOI.common.concurrent;

public interface Producer<T> {
    public void produce(PSQueue<T> queue);
}
