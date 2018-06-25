package com.demo.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 对其所有操作使用内部 CopyOnWriteArrayList 的 Set。因此，它共享以下相同的基本属性：
 * 它最适合于具有以下特征的应用程序：set 大小通常保持很小，只读操作远多于可变操作，需要在遍历期间防止线程间的冲突。 它是线程安全的。
 * 因为通常需要复制整个基础数组，所以可变操作（add、set 和 remove 等等）的开销很大。 迭代器不支持可变 remove 操作。
 * 使用迭代器进行遍历的速度很快，并且不会与其他线程发生冲突。在构造迭代器时，迭代器依赖于不变的数组快照。
 *
 */
public class CopyOnWriteArraySetDemoRead {
    private void testConstruction() {
        /**
         * 全局初始化一个CopyOnWriteArrayList
         */
        CopyOnWriteArraySet<String> set = new CopyOnWriteArraySet<String>();
        
    }

    private void test() {
        CopyOnWriteArraySet<String> set = new CopyOnWriteArraySet<String>();
        
        /**
         * 使用CopyOnWriteArrayList的addIfAbsent方法
         */
        set.add("abc");
        
    }

    public static void main(String[] args) throws InterruptedException {
        List<String> a = new ArrayList<String>();
        a.add("a");
        a.add("b");
        a.add("c");
        a.add("c");
        final CopyOnWriteArraySet<String> set = new CopyOnWriteArraySet<String>(a);

        Thread t = new Thread(new Runnable() {
            int count = -1;

            @Override
            public void run() {
                while(true) {
                    set.add(count++ + "");
                }
            }
        });

        t.setDaemon(true);
        t.start();

        Thread.currentThread().sleep(3);
        System.err.println("size:" + set.size());

        for(String s : set) {
            System.out.println(s);
        }
    }
}
