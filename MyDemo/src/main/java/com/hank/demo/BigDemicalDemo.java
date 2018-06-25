package com.hank.demo;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BigDemicalDemo {
    public static void main1(String[] args) {
        double a = 0.009;
        int days = 365;
        double b = 125;
        BigDecimal big = BigDecimal.valueOf(a);
        System.err.println(big.doubleValue() + "," + a);
        BigDecimal big2 = BigDecimal.valueOf(a).multiply(BigDecimal.valueOf(days).multiply(BigDecimal.valueOf(125)));
        DecimalFormat format = new DecimalFormat("#.00");
        System.err.println("保留两位小数：" + format.format(big2.doubleValue()));
        double rs = big2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        System.err.println("四舍五入保留两位小数：" + rs);
        System.err.println("正常计算值:" + big2.doubleValue());
        System.err.println((a * days * b));
        BigDecimal d = new BigDecimal(a);
        System.err.println(d.doubleValue() + "," + d);
    }

    public static void main(String[] args) {
        int num = 6;
        int width = Double.valueOf(Math.sqrt(num)).intValue();
        
        int leval = num / width;
        int first = num % width;
        leval = (first == 0) ? leval : (leval + 1);

        for(int i = 0; i < leval; i++) {
            int len = i == 0 ? first : width;

            for(int j = 0; j < len; j++) {
                System.err.print("^_^  ");
            }
            
            System.err.println("");
        }
    }
}
