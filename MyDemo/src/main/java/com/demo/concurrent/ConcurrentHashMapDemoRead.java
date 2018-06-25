package com.demo.concurrent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 测试HashMap和ConcurrentHashMap的并发性能差别。
 */
public class ConcurrentHashMapDemoRead {
    private void testConstruction() {
       /**
        * ConcurrentHashMap使用了Unsafe类的4个方法来读/写segments[ ]和table[ ]数组，分别是
        * getObject() --普通读 
        * getObjectVolatile() --volatile read
        * putOrderedObject() --有序写（延迟写） 
        * compareAndSwapObject --CAS写，volatile write
        */

        /**
         * capacity=16 load_factor=0.75 concurrency_level=16
         * 1) 计算ssize(Segment数组的大小，即为并发数的大小)，ssize为>=concurrencyLevel的第一个2的n次方的数。
         *    如果concurrencyLevel为16，则ssize为16，如果concurrencyLevel为17则ssize为32
         * 2) 计算Segment数组中，HashEntry的大小。先计算c=initialCapacity/ssize，也就是需要分多少个Segment。
         *    然后计算出一个>=c的第一个2进制的数，最小为2。如果c=17，也就是说每个Segment中的HashEntry需要能容纳17个元素。
         *    但是最终会以2的n次方初始化，也是就32了。
         * 3) 计算好大小，生成一个Segment，传入HashEntry，扩容比等信息
         * 4) 创建一个大小为ssize的Segment数组，Unsafe.putOrderedObject()将刚创建的Segment写到数组的第0个位置。
         *    并将数组付给全局
         */
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
        
        //其他构造方法，只是可自定义参数
    }

    private void test() {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
        
        /**
         * http://www.blogjava.net/DLevin/archive/2013/10/18/405030.html
         * 1)判断value是否为null，如果null，则抛出NPE
         * 2)计算key的hash值，寻找此hash值所在的索引位置的Segment
         * 3)如果找到的Segment为null，则使用ensureSegment创建Segment对象并采用CAS操作放入数组内
         * 3)然后调用Segment的put方法，将数据put到HashEntry中
         */
        map.put("abc", 1000);
        
        /**
         * 1)计算key的hash值，根据hash值所在的索引，找到对应的Segment
         * 2)然后获得Segment的HashEntry，然后遍历HashEntry，找到对应的key的值
         */
        map.get("abc");
        
        map.size();
    }

    static final int threads = 1000;
    static final int NUMBER = 1000;

    public static void main(String[] args) {
       ConcurrentHashMapDemoRead demo = new ConcurrentHashMapDemoRead();
       demo.testConstruction();
    }

    public static void main1(String[] args) throws Exception {
        Map<String, Integer> hashmapSync = Collections.synchronizedMap(new HashMap<String, Integer>());
        Map<String, Integer> concurrentHashMap = new ConcurrentHashMap<String, Integer>();
        Map<String, Integer> hashtable = new Hashtable<String, Integer>();
        long totalA = 0;
        long totalB = 0;
        long totalC = 0;

        for(int i = 0; i <= 10; i++) {
            totalA += testPut(hashmapSync);
            totalB += testPut(concurrentHashMap);
            totalC += testPut(hashtable);
        }

        System.out.println("Put time HashMapSync=" + totalA + "ms.");
        System.out.println("Put time ConcurrentHashMap=" + totalB + "ms.");
        System.out.println("Put time Hashtable=" + totalC + "ms.");
        totalA = 0;
        totalB = 0;
        totalC = 0;

        for(int i = 0; i <= 10; i++) {
            totalA += testGet(hashmapSync);
            totalB += testGet(concurrentHashMap);
            totalC += testGet(hashtable);
        }

        System.out.println("Get time HashMapSync=" + totalA + "ms.");
        System.out.println("Get time ConcurrentHashMap=" + totalB + "ms.");
        System.out.println("Get time Hashtable=" + totalC + "ms.");
    }

    public static long testPut(Map<String, Integer> map) throws Exception {
        long start = System.currentTimeMillis();

        for(int i = 0; i < threads; i++) {
            new MapPutThread(map).start();
        }

        while(MapPutThread.counter > 0) {
            Thread.sleep(1);
        }

        return System.currentTimeMillis() - start;
    }

    public static long testGet(Map<String, Integer> map) throws Exception {
        long start = System.currentTimeMillis();
        for(int i = 0; i < threads; i++) {
            new MapPutThread(map).start();
        }
        while(MapPutThread.counter > 0) {
            Thread.sleep(1);
        }
        return System.currentTimeMillis() - start;
    }
}

class MapPutThread extends Thread {
    static int counter = 0;
    static Object lock = new Object();
    private Map<String, Integer> map;
    private String key = this.getId() + "";

    MapPutThread(Map<String, Integer> map) {
        synchronized(lock) {
            counter++;
        }
        this.map = map;
    }

    public void run() {
        for(int i = 1; i <= ConcurrentHashMapDemoRead.NUMBER; i++) {
            map.put(key, i);
        }
        synchronized(lock) {
            counter--;
        }
    }
}

class MapGetThread extends Thread {
    static int counter = 0;
    static Object lock = new Object();
    private Map<String, Integer> map;
    private String key = this.getId() + "";

    MapGetThread(Map<String, Integer> map) {
        synchronized(lock) {
            counter++;
        }
        this.map = map;
    }

    public void run() {
        for(int i = 1; i <= ConcurrentHashMapDemoRead.NUMBER; i++) {
            map.get(key);
        }
        synchronized(lock) {
            counter--;
        }
    }
}
