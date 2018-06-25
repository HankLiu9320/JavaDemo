package com.jd.addressPOI.common.concurrent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        final ProducerConsumerFactory<String> f = new ProducerConsumerFactory<String>(1024, 100);

        f.addProducer(new Producer<String>() {
            @Override
            public void produce(PSQueue<String> queue) {
                for(int i = 0; i < 100000; i++) {
                    try {
                        queue.put("1-" + i + "");
                    }
                    catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        for(int i = 0; i < 10; i++) {
            f.addConsumer(new Consumer<String>() {
                @Override
                public void consume(String t) {
                }

                @Override
                public void consumeBlock(List<String> ts) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    }
                    catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.err.println("ts:" + ts.size() + "," + f.getQueueSize());
                }
            });
        }

        f.run();
    }
}
