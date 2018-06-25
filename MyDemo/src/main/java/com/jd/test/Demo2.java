package com.jd.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

public class Demo2 {
    public static void main(String[] args) throws ParseException {
        String start = "2017-11-09 20:33:00";
        String end = "2017-11-09 20:48:00";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long helfHour = 5 * 1000;

        if(StringUtils.isBlank(start)) {
            start = "2017-11-10 00:00:00";
        }

        if(StringUtils.isBlank(end)) {
            end = "2017-11-10 14:00:00";
        }

        long startTime = sdf.parse(start).getTime();
        long endTime = sdf.parse(end).getTime();
        long currTime = System.currentTimeMillis();
        int runtimes = 0;

        while(true) {
            boolean isFinish = currTime == endTime;
            if(currTime - startTime >= helfHour || isFinish) {
                long s = startTime;
                long e = currTime;
                System.err.println(
                        "runtimes:" + ++runtimes + "[" + sdf.format(new Date(s)) + "," + sdf.format(new Date(e)) + "]");

                try {
                    // run
                    Thread.sleep(5000);
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }
                finally {
                    startTime = e;
                }

                if(isFinish) {
                    break;
                }
            }
            else {
                System.err.println("sleep");
                try {
                    // TimeUnit.MINUTES.sleep(1);
                    TimeUnit.SECONDS.sleep(1);
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }

                long nc = System.currentTimeMillis();
                currTime = nc >= endTime ? endTime : nc;
            }
        }
    }
}
