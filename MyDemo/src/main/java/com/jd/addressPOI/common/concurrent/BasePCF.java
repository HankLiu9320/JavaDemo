package com.jd.addressPOI.common.concurrent;

public interface BasePCF<T> {
    public void run();

    public int getQueueSize();

    public Long[] getPCCount();

    public void awaitFinish();
}
