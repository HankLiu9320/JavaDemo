package com.demo.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * TreeMap实现SortMap接口，能够把它保存的记录根据键排序,默认是按键值的升序排序，也可以指定排序的比较器，当用Iterator
 * 遍历TreeMap时，得到的记录是排过序的。 一般情况下，我们用的最多的是HashMap,在Map 中插入、删除和定位元素，HashMap
 * 是最好的选择。但如果您要按自然顺序或自定义顺序遍历键，那么TreeMap会更好。如果需要输出的顺序和输入的相同,那么用LinkedHashMap
 * 可以实现,它还可以按读取顺序来排列.
 */
public class TreeMapDemo {
   public static void main(String[] args) {
      Map<Integer, String> map = new TreeMap<Integer, String>(new Comparator<Integer>() {
         @Override
         public int compare(Integer o1, Integer o2) {
            return o2 - o1;
         }
      });

      map.put(6, "apple");
      map.put(8, "banana");
      map.put(2, "pear");

      for(Iterator it = map.keySet().iterator(); it.hasNext();) {
         Object key = it.next();
         System.out.println(key + "=" + map.get(key));
      }
   }
}
