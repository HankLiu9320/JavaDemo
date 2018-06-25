package com.demo.base;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

public class SortDemo {
   public static void main(String[] args) {
      String[] resource = new String[] { "hot", "dog", "fish", "apple", "zoo", "happy", "yellow" };

      for(int i = 0; i < resource.length; i++) {
         for(int j = 0; j < resource.length - i - 1; j++) {
            char f = resource[j].charAt(0);
            char ff = resource[j + 1].charAt(0);
            if(ff < f) {
               String temp = resource[j];
               resource[j] = resource[j + 1];
               resource[j + 1] = temp;
            }
         }
      }

      Date d = new Date();
      Calendar c = Calendar.getInstance();
      c.setTime(d);
      c.add(Calendar.DAY_OF_MONTH, -1);
      System.err.println(10.00 / 3.0);

      double intereatRate = 0.009; // 日息
      BigDecimal pr = new BigDecimal(359); // 本金
      int p = 200;
      double rs = pr.intValue() * intereatRate * p;
      BigDecimal b1 = BigDecimal.valueOf(intereatRate).multiply(BigDecimal.valueOf(p).multiply(pr));
      BigDecimal b = BigDecimal.valueOf(pr.intValue() * intereatRate * p);
      System.err.println(b1.doubleValue() + "," + rs + "," + b + "," + (pr.intValue() * intereatRate * p));
   }

   public static int partition(int[] array, int lo, int hi) {
      // 固定的切分方式
      int key = array[lo];
      while(lo < hi) {
         while(array[hi] >= key && hi > lo) {// 从后半部分向前扫描
            hi--;
         }
         array[lo] = array[hi];
         while(array[lo] <= key && hi > lo) {// 从前半部分向后扫描
            lo++;
         }
         array[hi] = array[lo];
      }
      array[hi] = key;
      return hi;
   }

   public static void sort(int[] array, int lo, int hi) {
      if(lo >= hi) {
         return;
      }
      int index = partition(array, lo, hi);
      sort(array, lo, index - 1);
      sort(array, index + 1, hi);
   }
}
