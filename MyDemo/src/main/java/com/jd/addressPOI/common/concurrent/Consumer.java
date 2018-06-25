package com.jd.addressPOI.common.concurrent;

import java.util.List;

public interface Consumer<T> {
    public void consume(T t);
    public void consumeBlock(List<T> ts);
}
