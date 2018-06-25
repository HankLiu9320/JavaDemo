package com.demo.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ArrayList 的一个线程安全的变体，其中所有可变操作（add、set 等等）都是通过对底层数组进行一次新的复制来实现的。
 * 
 * 这一般需要很大的开销，但是当遍历操作的数量大大超过可变操作的数量时，这种方法可能比其他替代方法更
 * 有效。在不能或不想进行同步遍历，但又需要从并发线程中排除冲突时，它也很有用。“快照”风格的迭代器方法在创建迭代器时使用了对数组状态的引用。
 * 此数组在迭代器的生存期内不会更改，因此不可能发生冲突，并且迭代器保证不会抛出
 * ConcurrentModificationException。创建迭代器以后，迭代器就不会反映列表的添加、移除或者更改。在迭代器上进行的元素更改操作（
 * remove、set 和 add）不受支持。这些方法将抛出 UnsupportedOperationException。
 * 
 * 允许使用所有元素，包括 null。
 * 
 * 内存一致性效果：当存在其他并发 collection 时，将对象放入 CopyOnWriteArrayList 之前的线程中的操作
 * happen-before 随后通过另一线程从 CopyOnWriteArrayList 中访问或移除该元素的操作。
 * 
 */
public class CopyOnWriteArrayListDemoRead {
    private void testConstruction() {
        /**
         * 初始化一个ReentrantLock，并将一个长度为0的Object的数组，放到全局
         */
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        
        /**
         * 把传入的list toArray，放到全局的数组上
         */
        list = new CopyOnWriteArrayList<String>(new ArrayList<String>());
        
        list = new CopyOnWriteArrayList<String>(new String[] {});
    }

    private void test() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        //对list最修改的方法都要上锁，add/set/remove

        /**
         * 1)上锁
         * 2)获取原有数组，并通过Arrays.copyof，拷贝出一个比原来长度大1的新数据，把传入的值放到数组尾
         */
        list.add("abc");
        /**
         * 上锁，并在index位置做拷贝动作
         */
        list.add(2, "abc");
        list.addAll(new ArrayList<String>());
        
        /**
         * 添加元素（如果不存在）。
         */
        list.addIfAbsent("abc"); 
        list.addAllAbsent(new ArrayList<String>());
        
        list.get(2);
        list.remove(2);
        list.iterator();
    }

    /**
     * 读线程
     */
    private static class ReadTask implements Runnable {
        List<String> list;

        public ReadTask(List<String> list) {
            this.list = list;
        }

        public void run() {
            for(String str : list) {
                System.out.println("read:" + str);
            }
        }
    }

    /**
     * 写线程
     */
    private static class WriteTask implements Runnable {
        List<String> list;
        int index;

        public WriteTask(List<String> list, int index) {
            this.list = list;
            this.index = index;
        }

        public void run() {
            list.remove(index);
            list.add(index, "write_" + index);
            System.err.println("write:" + index + "," + "write_" + index);
        }
    }

    public void run() {
        final int NUM = 10;
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

        for(int i = 0; i < NUM; i++) {
            list.add("main_" + i);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(NUM);

        for(int i = 0; i < NUM; i++) {
            executorService.execute(new ReadTask(list));
            executorService.execute(new WriteTask(list, i));
        }

        executorService.shutdown();
    }

    public static void main(String[] args) {
        new CopyOnWriteArrayListDemoRead().run();
    }
}