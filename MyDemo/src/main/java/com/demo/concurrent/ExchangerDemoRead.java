package com.demo.concurrent;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Exchanger可以在两个线程之间交换数据，只能是2个线程，他不支持更多的线程之间互换数据。
 * 当线程A调用Exchange对象的exchange()方法后，他会陷入阻塞状态，直到线程B也调用了exchange()方法，然后以线程安全的方式交换数据，
 * 之后线程A和B继续运行
 */
public class ExchangerDemoRead {
    private void testConstruction() {
        Exchanger<String> exchanger = new Exchanger<String>();

    }

    private void test() throws InterruptedException, TimeoutException {
        Exchanger<String> exchanger = new Exchanger<String>();

        exchanger.exchange("abc");
        
        /**
         * private Object doExchange(Object item, boolean timed, long nanos) {
                Node me = new Node(item);
                int index = hashIndex(); // 根据thread id计算出自己要去的那个交易位置（slot)
                int fails = 0;
        
                or(;;) {asdfsadfadsfasdfasdfasdf
                    Object y;
                    Slot slot = arena[index];
                    if(slot == null)
                        createSlot(index); // slot = null，创建一个slot，然后会回到for循环，再次开始
                    else if((y = slot.get()) != null && // slot里面有人等着(有Node)，则尝试和其交换
                            slot.compareAndSet(y, null)) { // 关键点1：slot清空，Node拿出来，俩人在Node里面交互。把Slot让给后面的人，做交互地点
                        Node you = (Node) y;
                        if(you.compareAndSet(null, item)) {// 把Node里面的东西，换成自己的
                            LockSupport.unpark(you.waiter); // 唤醒对方
                            return you.item; // 自己把对方的东西拿走
                        } // 关键点2：如果你运气不好，在Node里面要交换的时候，被另一个线程抢了，回到for循环，重新开始
                    }
                    else if(y == null && // slot里面为空(没有Node)，则自己把位置占住
                            slot.compareAndSet(null, me)) {
                        if(index == 0) // 如果是0这个位置，自己阻塞，等待别人来交换
                            return timed ? awaitNanos(me, slot, nanos) : await(me, slot);
                        Object v = spinWait(me, slot); // 不是0这个位置，自旋等待
                        if(v != CANCEL) // 自旋等待的时候，运气好，有人来交换了，返回
                            return v;
                        me = new Node(item); // 自旋的时候，没人来交换。走执行下面的，index减半，挪个位置，重新开始for循环
                        int m = max.get();
                        if(m > (index >>>= 1))
                            max.compareAndSet(m, m - 1);
                    }
                    else if(++fails > 1) { // 失败 case1: slot有人，要交互，但被人家抢了 case2:
                                           // slot没人，自己要占位置，又被人家抢了
                        int m = max.get();
                        if(fails > 3 && m < FULL && max.compareAndSet(m, m + 1))
                            index = m + 1; // 3次匹配失败，把index扩大，再次开始for循环
                        else if(--index < 0)
                            index = m;
                    }
                }
            }
         */

        exchanger.exchange("abc", 1000, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<String>();
        ExchangerRunnable exchangerRunnable1 = new ExchangerRunnable(exchanger, "A");
        ExchangerRunnable exchangerRunnable2 = new ExchangerRunnable(exchanger, "B");
        new Thread(exchangerRunnable1).start();
        new Thread(exchangerRunnable2).start();
    }
}

class ExchangerRunnable implements Runnable {
    Exchanger<String> exchanger = null;
    String object = null;

    public ExchangerRunnable(Exchanger<String> exchanger, String object) {
        this.exchanger = exchanger;
        this.object = object;
    }

    public void run() {
        try {
            Object previous = this.object;
            this.object = this.exchanger.exchange(this.object);

            System.out.println(Thread.currentThread().getName() + " exchanged " + previous + " for " + this.object);
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
