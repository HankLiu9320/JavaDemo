package com.jd.addressPOI.common.concurrent;

public interface PCF<T> extends BasePCF<T> {
    public int addProducer(Producer<T> producer);

    public int addConsumer(Consumer<T> consumer);
}
