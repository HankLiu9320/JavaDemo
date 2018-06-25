package com.jd.addressPOI.common.concurrent;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class MainGroup {
    public static void main(String[] args) {
        int group = 4;
        final ProducerGroupConsumerFactory<String> f = new ProducerGroupConsumerFactory<String>(1024, group, 3, -1);
        final AtomicLong cnt = new AtomicLong();

        for(int i = 0; i < group; i++) {
            for(int j = 0; j < 512; j++) {
                f.addProducer(i, new Producer<String>() {
                    @Override
                    public void produce(PSQueue<String> queue) {
                        while(true) {
                            try {
                                queue.put("1-" + new Date().getTime(), 4, 3);
                            }
                            catch(InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }

        for(int i = 0; i < 10; i++) {
            f.addConsumer(new Consumer<String>() {
                @Override
                public void consume(String t) {
                    cnt.getAndIncrement();
                }

                @Override
                public void consumeBlock(List<String> ts) {
                }
            });
        }

        // listener
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        System.err.println(cnt.get());
                    }
                    catch(InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(f.getRemainProducer() == 0 && f.getRemainConsumer() == 0) {
                        break;
                    }
                }
            }
        }).start();

        f.run();
        f.awaitFinish();
        System.err.println("++++++++++++++++++++++++++++++++++");
    }
}
