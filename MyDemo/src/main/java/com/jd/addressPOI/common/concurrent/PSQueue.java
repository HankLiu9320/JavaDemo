package com.jd.addressPOI.common.concurrent;

public interface PSQueue<T> {
    public void put(T e) throws InterruptedException;
    public void put(T e, int limit, int second) throws InterruptedException;
    public void setProducerNum(int num);
    public T poll();
    public int size();
}
