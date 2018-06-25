package com.demo.sort;

/**
 * 奇偶排序，可转化成并行奇偶排序
 * 
 * @author Liujianjia
 *
 */
public class OddEvenNumberSort {
    public static void oddEvensort(int[] ary) {
        // 奇偶排序
        boolean flag = true;

        while(flag) {
            boolean odd = false, even = false;
            for(int i = 0; i < ary.length - 1; i += 2) {
                if(ary[i] > ary[i + 1]) {
                    ary[i] = ary[i + 1] + 0 * (ary[i + 1] = ary[i]);
                    odd = true;
                }
            }

            for(int i = 1; i < ary.length - 1; i += 2) {
                if(ary[i] > ary[i + 1]) {
                    ary[i] = ary[i + 1] + 0 * (ary[i + 1] = ary[i]);
                    even = true;
                }
            }

            flag = odd || even; // 若为false，表示不论奇偶序列，一个符合条件的比较都没有
            System.err.println(flag);
        }
    }

    public static void main(String[] args) {
        int[] array = { 2, 1, 3, 5, 23, 10, 6, 3, 7 };
        oddEvensort(array);
        System.err.println(java.util.Arrays.toString(array));
    }
}
