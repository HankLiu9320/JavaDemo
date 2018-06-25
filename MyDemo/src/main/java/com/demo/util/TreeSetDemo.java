package com.demo.util;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * TreeSet是SortedSet接口的唯一实现类，TreeSet可以确保集合元素处于排序状态。TreeSet支持两种排序方式，自然排序
 * 和定制排序，其中自然排序为默认的排序方式。向TreeSet中加入的应该是同一个类的对象。
 * TreeSet判断两个对象不相等的方式是两个对象通过equals方法返回false，或者通过CompareTo方法比较没有返回0 自然排序
 * 自然排序使用要排序元素的CompareTo（Object obj）方法来比较元素之间大小关系，然后将元素按照升序排列。
 * Java提供了一个Comparable接口，该接口里定义了一个compareTo(Object
 * obj)方法，该方法返回一个整数值，实现了该接口的对象就可以比较大小。
 * obj1.compareTo(obj2)方法如果返回0，则说明被比较的两个对象相等，如果返回一个正数，则表明obj1大于obj2，如果是
 * 负数，则表明obj1小于obj2。 如果我们将两个对象的equals方法总是返回true，则这两个对象的compareTo方法返回应该返回0 定制排序
 * 自然排序是根据集合元素的大小，以升序排列，如果要定制排序，应该使用Comparator接口，实现 int compare(T o1,T o2)方法
 *
 */
public class TreeSetDemo {
   public static void main(String[] args) {
      Set<Integer> set = new TreeSet<Integer>(new Comparator<Integer>() {
         @Override
         public int compare(Integer o1, Integer o2) {
            return o2 - o1;
         }
      });
      
      set.add(3);
      set.add(222);
      set.add(11);
      set.add(22);

      // set 的默认顺序？
      for(Integer i : set) {
         System.err.println(i);
      }
   }
}
