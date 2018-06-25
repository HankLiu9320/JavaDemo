package com.jd.addressPOI.common.concurrent;

public interface PGCF<T> extends BasePCF<T> {
    public int addProducer(int group, Producer<T> producer);

    public int addConsumer(Consumer<T> consumer);
    
    public long getRemainProducer();
}
