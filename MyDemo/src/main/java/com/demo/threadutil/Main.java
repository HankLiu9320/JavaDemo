package com.demo.threadutil;

import java.util.concurrent.BlockingQueue;

public class Main {
    public static void main(String[] args) {
        final String dao = "A";
        ProducerConsumerFactory<String> f = new ProducerConsumerFactory<String>();

        f.addProducer(new Producer<String>() {
            @Override
            public void produce(BlockingQueue<String> queue) {
                for(int i = 0; i < 10; i++) {
                    try {
                        queue.put("1-" + i + "");
                    }
                    catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        f.addProducer(new Producer<String>() {
            @Override
            public void produce(BlockingQueue<String> queue) {
                System.err.println(dao);
                for(int i = 0; i < 10; i++) {
                    try {
                        queue.put("2-" + i + "");
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
                    System.err.println("consumer:" + t);
                }
            });
        }

        f.run();
    }
}
