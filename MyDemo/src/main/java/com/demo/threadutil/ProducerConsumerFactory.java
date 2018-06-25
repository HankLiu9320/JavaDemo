package com.demo.threadutil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 生产消费模式
 * 
 * @author liujianjia
 * @param <T>
 */
public class ProducerConsumerFactory<T> {
    static final int DEFAULT_INITIAL_QUEUE_CAPACITY = 1 << 10; // aka 1024
    private ArrayBlockingQueue<T> queue;
    private ExecutorService produceES;
    private ExecutorService consumerES;
    private List<Producer<T>> producers = new ArrayList<Producer<T>>();
    private List<Consumer<T>> consumers = new ArrayList<Consumer<T>>();
    private Map<String, AtomicBoolean> finishFlags = new ConcurrentHashMap<String, AtomicBoolean>();

    /**
     * 构造器
     * 
     * @param producerNum
     *            生产者数量
     * @param consumerNum
     *            消费者数量
     */
    public ProducerConsumerFactory() {
        this(DEFAULT_INITIAL_QUEUE_CAPACITY);
    }

    /**
     * 构造器
     * 
     * @param producerNum
     *            生产者数量
     * @param consumerNum
     *            消费者数量
     */
    public ProducerConsumerFactory(int initialQueueCapacity) {
        super();
        queue = new ArrayBlockingQueue<T>(initialQueueCapacity);
    }

    /**
     * 添加生产者
     * 
     * @param producer
     * @return
     */
    public int addProducer(Producer<T> producer) {
        synchronized(producers) {
            producers.add(producer);
            return producers.size();
        }
    }

    /**
     * 添加消费者
     * 
     * @param producer
     * @return
     */
    public int addConsumer(Consumer<T> consumer) {
        synchronized(consumers) {
            consumers.add(consumer);
            return consumers.size();
        }
    }

    public void run() {
        init();
        startProduce();
        startConsumer();
    }

    public void init() {
        produceES = Executors.newFixedThreadPool(producers.size());
        consumerES = Executors.newFixedThreadPool(consumers.size());
    }

    private void startProduce() {
        int producerExeNum = producers.size();

        // 生产者不能为空
        if(producerExeNum <= 0) {
            throw new RuntimeException("没有生产者实例，请添加");
        }

        for(int i = 0; i < producerExeNum; i++) {
            final Producer<T> pexe = producers.get(i);

            produceES.submit(new Runnable() {
                @Override
                public void run() {
                    String key = Thread.currentThread().getName();
                    finishFlags.put(key, new AtomicBoolean(false));

                    if(pexe != null) {
                        pexe.produce(queue);
                    }

                    finishFlags.get(key).set(true);
                }
            });
        }
        
        produceES.shutdown();
    }

    private void startConsumer() {
        int consumerExeNum = consumers.size();

        // 生产者不能为空
        if(consumerExeNum <= 0) {
            throw new RuntimeException("没有消费者实例，请添加");
        }

        for(int i = 0; i < consumerExeNum; i++) {
            final Consumer<T> cexe = consumers.get(i);

            consumerES.submit(new Runnable() {
                @Override
                public void run() {
                    if(cexe != null) {
                        while(true) {
                            T elem = queue.poll();

                            if(elem == null) {
                                if(checkFinish()) {
                                    break;
                                }

                                continue;
                            }

                            cexe.consume(elem);
                        }
                    }
                }
            });
        }

        consumerES.shutdown();
    }

    /**
     * 检查是否生产完数据
     * @return
     */
    private boolean checkFinish() {
        if(finishFlags.size() > 0) {
            boolean finish = true;

            Collection<AtomicBoolean> flgs = finishFlags.values();

            for(AtomicBoolean ab : flgs) {
                finish = finish & ab.get();

                if(!finish) {
                    return false;
                }
            }

            return finish;
        }
        else {
            return false;
        }
    }
}
