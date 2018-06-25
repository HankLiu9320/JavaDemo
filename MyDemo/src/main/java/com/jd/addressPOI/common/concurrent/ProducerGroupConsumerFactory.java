package com.jd.addressPOI.common.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 生产消费模式
 * 
 * @author liujianjia
 * @param <T>
 */
public class ProducerGroupConsumerFactory<T> implements PGCF<T> {
    static final int DEFAULT_INITIAL_QUEUE_CAPACITY = 1 << 10; // aka 1024
    private ProducerGroupConsumerBlockingQueue<T> queue;
    private Map<Integer, List<Producer<T>>> groupProducers = new ConcurrentHashMap<Integer, List<Producer<T>>>();
    private List<ExecutorService> produceGroupES;
    private ExecutorService consumerES;
    private List<Consumer<T>> consumers = new ArrayList<Consumer<T>>();
    private Map<String, AtomicBoolean> finishFlags = new ConcurrentHashMap<String, AtomicBoolean>();
    private int consumeCountOnce = 0;
    private int groupNum = 0;
    private int groupActive = 0;

    private volatile AtomicLong produceCount = new AtomicLong(); // 共生产数量
    private volatile AtomicLong consumeCount = new AtomicLong(); // 共消费数量

    /**
     * 构造器
     * 
     * @param initialQueueCapacity
     *            阻塞队列缓存数量
     * @param groupNum
     *            生产者的组数量，也就是生产者的线程池个数（不是线程数！）
     * @param groupActive
     *            每个生产者组中活跃的线程数()
     * @param consumeCountOnce
     *            一次性消费多少，如果设置，则使用consumeBlock消费
     * 
     * 
     */
    public ProducerGroupConsumerFactory(int initialQueueCapacity, int groupNum, int groupActive, int consumeCountOnce) {
        super();

        if(initialQueueCapacity <= 0) {
            initialQueueCapacity = DEFAULT_INITIAL_QUEUE_CAPACITY;
        }

        if(groupNum == 0 || groupActive == 0 || consumeCountOnce == 0) {
            throw new RuntimeException("参数传入有误，不能为0");
        }

        queue = new ProducerGroupConsumerBlockingQueue<T>(initialQueueCapacity, produceCount);
        this.consumeCountOnce = consumeCountOnce;
        this.groupNum = groupNum;
        this.groupActive = groupActive;
    }

    /**
     * 添加生产者
     * 
     * @param producer
     * @return
     */
    public int addProducer(int group, Producer<T> producer) {
        List<Producer<T>> curGroup = groupProducers.get(group);

        if(curGroup == null) {
            curGroup = new ArrayList<Producer<T>>();
            groupProducers.put(group, curGroup);
        }

        curGroup.add(producer);
        return curGroup.size();
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
        produceGroupES = new ArrayList<ExecutorService>(groupNum);

        for(int i = 0; i < groupNum; i++) {
            ExecutorService pes = Executors.newFixedThreadPool(groupActive);
            produceGroupES.add(pes);
        }

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

    @Override
    public Long[] getPCCount() {
        return new Long[] { produceCount.get(), consumeCount.get() };
    }

    /**
     * 返回剩余生产者数量
     * 
     * @return
     */
    public long getRemainProducer() {
        long remainCount = 0;

        for(ExecutorService es : produceGroupES) {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) es;
            long remain = tpe.getTaskCount() - tpe.getCompletedTaskCount();
            remainCount += remain;
        }

        return remainCount;
    }

    /**
     * 返回剩余的消费者数量
     * 
     * @return
     */
    public long getRemainConsumer() {
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) consumerES;
        return tpe.getTaskCount() - tpe.getCompletedTaskCount();
    }

    /**
     * 等待所有线程完成
     */
    public void awaitFinish() {
        for(ExecutorService es : produceGroupES) {
            try {
                es.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            consumerES.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始生产
     */
    private void startProduce() {
        int producerExeNum = getProducerSize();

        // 生产者不能为空
        if(producerExeNum <= 0) {
            throw new RuntimeException("没有生产者实例，请添加");
        }

        queue.setProducerNum(groupNum * groupActive);

        for(int g = 0; g < groupNum; g++) {
            List<Producer<T>> producers = groupProducers.get(g);
            ExecutorService producerES = produceGroupES.get(g);

            if(producers != null && producers.size() > 0) {
                for(int pro = 0; pro < producers.size(); pro++) {
                    final Producer<T> pexe = producers.get(pro);

                    producerES.submit(new Runnable() {
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
            }

            producerES.shutdown();
        }
    }

    /**
     * 开始消费
     */
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
                        List<T> conCache = new ArrayList<T>(consumeCountOnce > 0 ? consumeCountOnce : 0);

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
                                    conCache = new ArrayList<T>(consumeCountOnce > 0 ? consumeCountOnce : 0);
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
     * 计算生产者数量
     * 
     * @return
     */
    private int getProducerSize() {
        int count = 0;

        for(List<Producer<T>> ls : groupProducers.values()) {
            if(ls != null) {
                count += ls.size();
            }
        }

        return count;
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
