package com.jd.addressPOI.common.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 生产消费模式
 * 
 * @author liujianjia
 * @param <T>
 */
public class ProducerConsumerFactory<T> implements PCF<T> {
    static final int DEFAULT_INITIAL_QUEUE_CAPACITY = 1 << 10; // aka 1024
    private ProducerConsumerBlockingQueue<T> queue;
    private ExecutorService produceES;
    private ExecutorService consumerES;
    private List<Producer<T>> producers = new ArrayList<Producer<T>>();
    private List<Consumer<T>> consumers = new ArrayList<Consumer<T>>();
    private Map<String, AtomicBoolean> finishFlags = new ConcurrentHashMap<String, AtomicBoolean>();
    private int consumeCountOnce = 0;

    private volatile AtomicLong produceCount = new AtomicLong(); // 共生产数量
    private volatile AtomicLong consumeCount = new AtomicLong(); // 共消费数量

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
     * @param initialQueueCapacity
     *            阻塞队列缓存数量
     */
    public ProducerConsumerFactory(int initialQueueCapacity) {
        super();
        queue = new ProducerConsumerBlockingQueue<T>(initialQueueCapacity, produceCount);
    }

    /**
     * 构造器
     * 
     * @param initialQueueCapacity
     *            阻塞队列缓存数量
     * @param consumeCountOnce
     *            一次性消费多少，如果设置，则使用consumeBlock消费
     */
    public ProducerConsumerFactory(int initialQueueCapacity, int consumeCountOnce) {
        super();
        queue = new ProducerConsumerBlockingQueue<T>(initialQueueCapacity, produceCount);
        this.consumeCountOnce = consumeCountOnce;
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

    private void init() {
        produceES = Executors.newFixedThreadPool(producers.size());
        consumerES = Executors.newFixedThreadPool(consumers.size());
    }

    /**
     * 返回阻塞队列中元素数量
     * 
     * @return
     */
    public int getQueueSize() {
        return queue.size();
    }

    /**
     * 获得生产和消费的数量
     * 
     * @return
     */
    public Long[] getPCCount() {
        return new Long[] { produceCount.get(), consumeCount.get() };
    }

    /**
     * 等待所有线程完成
     */
    public void awaitFinish() {
        try {
            produceES.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            consumerES.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        }
        catch(InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    private void startProduce() {
        int producerExeNum = producers.size();

        // 生产者不能为空
        if(producerExeNum <= 0) {
            throw new RuntimeException("没有生产者实例，请添加");
        }

        queue.setProducerNum(producerExeNum);

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
                        boolean isBlockExe = consumeCountOnce > 0;
                        List<T> conCache = new ArrayList<T>(consumeCountOnce);

                        while(true) {
                            T elem = queue.poll();

                            if(elem == null) {
                                if(checkFinish()) {
                                    break;
                                }

                                continue;
                            }

                            if(isBlockExe) {
                                conCache.add(elem);

                                if(conCache.size() >= consumeCountOnce) {
                                    List<T> toConsume = conCache;
                                    cexe.consumeBlock(toConsume);
                                    consumeCount.getAndAdd(toConsume.size());
                                    conCache = new ArrayList<T>(consumeCountOnce);
                                }
                            }
                            else {
                                cexe.consume(elem);
                                consumeCount.incrementAndGet();
                            }
                        }

                        if(isBlockExe) {
                            if(conCache.size() >= 0) {
                                cexe.consumeBlock(conCache);
                                consumeCount.getAndAdd(conCache.size());
                            }
                        }
                    }
                }
            });
        }

        consumerES.shutdown();
    }

    /**
     * 检查是否生产完数据
     * 
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
