package com.hank.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class DelayFactory {
    private Map<String, FutureTask<String>> delayMap = new ConcurrentHashMap<String, FutureTask<String>>();
    private ExecutorService exec = Executors.newCachedThreadPool();
    private static DelayFactory instance = new DelayFactory();

    private DelayFactory() {
        // can not use defalt construction.
    }

    /**
     * 获得工厂实例
     * 
     * @return
     */
    public static DelayFactory getInstance() {
        return instance;
    }

    /**
     * 加入
     * 
     * @param openID
     * @param time
     *            使用秒
     */
    private DelayQueue<Worker> createQueue(String openID, List<Worker> nodes) {
        if(openID == null) {
            throw new RuntimeException("Opend id can be not null!");
        }

        if(nodes != null && !nodes.isEmpty()) {
            DelayQueue<Worker> queue = new DelayQueue<Worker>();
            queue.addAll(nodes);
            return queue;
        }

        return null;
    }

    /**
     * put到延时队列，并运行
     * 
     * @param openID
     * @param times
     */
    public void putAndRun(String openID, List<Worker> nodes) {
        final DelayQueue<Worker> queue = createQueue(openID, nodes);
        Workers ws = new Workers(openID, queue, delayMap);
        FutureTask<String> task = new FutureTask<String>(ws, ws.getName());
        delayMap.put(openID, task);
        exec.submit(task);
    }

    /**
     * 中断一个用户的延时队列
     * 
     * @param openID
     */
    public void interrupt(String openID) {
        System.err.println("openId:" + openID + "|" + delayMap);        
        FutureTask<String> future = delayMap.get(openID);

        if(future != null) {
            future.cancel(true);
        }
    }

    public Set<String> getDelayMapKeys() {
        if(delayMap != null) {
            return delayMap.keySet();
        }

        return null;
    }

    /**
     * 延时队列运行线程
     * 
     * @author Liujianjia
     *
     */
    static class Workers extends Thread {
        private String openID;
        private DelayQueue<Worker> queue;
        private Map<String, FutureTask<String>> map;

        public Workers(String openID, DelayQueue<Worker> queue, Map<String, FutureTask<String>> delayMap) {
            super("Thread - " + openID);
            this.openID = openID;
            this.queue = queue;
            this.map = delayMap;
        }

        @Override
        public void run() {
            for(; !queue.isEmpty();) {
                try {
                    Worker worker = queue.take();
                    worker.work();
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                    // 如果worker中断，则清空此延时队列
                    map.remove(openID);
                    queue.clear();
                    System.err.println("openID:" + openID + "所在的延时队列已停止！,还有：" + map.size() + "个延时队列在运行");
                    break;
                }
            }

            if(queue.isEmpty()) {
                System.err.println("remove:" + openID + "|" + map);
                map.remove(openID);
            }
        }
    }

    /**
     * 延时节点类
     * 
     * @author Liujianjia
     */
    public static class Worker implements Delayed {
        private String openId;
        private long time; // 毫秒
        private long workStartTime = System.currentTimeMillis();
        private String sendContent;

        public Worker(String openId, String sendContent, long time) {
            this.openId = openId;
            this.sendContent = sendContent;
            this.time = time;
        }

        @Override
        public int compareTo(Delayed o) {
            if(this.getExpectTime() > ((Worker) o).getExpectTime()) {
                return 1;
            }
            else if(this.getExpectTime() < ((Worker) o).getExpectTime()) {
                return -1;
            }

            return 0;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long delay = unit.convert(getExpectTime() - System.currentTimeMillis(), TimeUnit.NANOSECONDS);
            return delay;
        }

        /**
         * 开始工作，目前工作只是推送消息到客户微信
         */
        public void work() {
            System.err.println("==============work==========" + (time / 1000));
            System.err.println(this.openId + ": push message [" + sendContent + "] in delay " + time + "s");
        }

        public long getExpectTime() {
            return workStartTime + time;
        }

        public String getOpenId() {
            return openId;
        }

        public long getTime() {
            return time;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Map<String, List<Worker>> map = new HashMap<String, List<Worker>>();
        Random ran = new Random();
        String lid = null;

        for(int i = 0; i < 3; i++) {
            List<Worker> workers = new ArrayList<Worker>();
            lid = UUID.randomUUID().toString();

            for(int j = 0; j < 5; j++) {
                Worker worker = new Worker(lid, "", (long) ran.nextInt(100000));
                workers.add(worker);
            }

            map.put(lid, workers);
        }

        Iterator<Entry<String, List<Worker>>> is = map.entrySet().iterator();

        while(is.hasNext()) {
            Entry<String, List<Worker>> en = is.next();
            DelayFactory.getInstance().putAndRun(en.getKey(), en.getValue());
        }

        DelayFactory.getInstance();
        Thread.sleep(4000);
        DelayFactory.getInstance().interrupt(lid);
    }
}
