package com.jd.test;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Demo {
    public static void main(String[] args) {
        SimpleDateFormat minuteSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.err.println(minuteSdf.format(new Date(1510243555331L)));
    }

    public static void main1(String[] args) throws UnsupportedEncodingException, ParseException {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.err.println("2222");
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        System.err.println("1111");
    }

    public static void test() {
        final ExecutorService exeService = Executors.newFixedThreadPool(10);

        for(int i = 0; i < 10; i++) {
            final int flag = i;
            exeService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(flag == 5) {
                            Thread.currentThread().interrupt();
                        }

                        Thread.sleep(10000);
                        System.err.println("============");
                    }
                    catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        exeService.shutdown();
        System.err.println("aaaaaaaaaaaa");

        try {
            exeService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        }
        catch(InterruptedException e) {
            e.printStackTrace();
            System.err.println("e:" + e.getMessage());
        }

        System.err.println("bbbbbbbbbbbb");

    }

    public static byte[] toBytes(int val) {
        byte[] b = new byte[4];
        for(int i = 3; i > 0; i--) {
            b[i] = (byte) val;
            val >>>= 8;
        }
        b[0] = (byte) val;
        return b;
    }
}
