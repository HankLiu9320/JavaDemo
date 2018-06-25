package com.demo.util;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import com.demo.util.EnumMapDemo.TestType;

/**
 * 描述： 与枚举类型一起使用的专用 Set 实现。枚举 set 中所有键都必须来自单个枚举类型，该枚举类型在创建 set 时显式或隐式地指定。枚举 set
 * 在内部表示为位向量。此表示形式非常紧凑且高效。此类的空间和时间性能应该很好，足以用作传统上基于 int
 * 的“位标志”的替换形式，具有高品质、类型安全的优势。如果指定的 collection 也是一个枚举 set，则批量操作（如 containsAll 和
 * retainAll）也应运行得非常快。
 * 
 * 由 iterator 方法返回的迭代器按其自然顺序 遍历这些元素（该顺序是声明枚举常量的顺序）。返回的迭代器是弱一致的：它从不抛出
 * ConcurrentModificationException，也不一定显示在迭代进行时发生的任何 set 修改的效果。
 * 
 * 不允许使用 null 元素。试图插入 null 元素将抛出 NullPointerException。但是，试图测试是否出现 null 元素或移除
 * null 元素将不会抛出异常。
 * 
 * 像大多数 collection 一样，EnumSet 是不同步的。如果多个线程同时访问一个枚举 set，并且至少有一个线程修改该 set，则此枚举 set
 * 在外部应该是同步的。这通常是通过对自然封装该枚举 set 的对象执行同步操作来完成的。如果不存在这样的对象，则应该使用
 * Collections.synchronizedSet(java.util.Set) 方法来“包装”该
 * set。最好在创建时完成这一操作，以防止意外的非同步访问：
 * 
 * Set<MyEnum> s = Collections.synchronizedSet(EnumSet.noneOf(Foo.class));
 * 实现注意事项：所有基本操作都在固定时间内执行。虽然并不保证，但它们很可能比其 HashSet 副本更快。如果参数是另一个 EnumSet 实例，则诸如
 * addAll() 和 AbstractSet.removeAll(java.util.Collection) 之类的批量操作也会在固定时间内执行
 *
 * 线程安全：线程不安全
 */
public class EnumSetDemoRead {
   public enum TestType {
         Type2("2b"),
         Type1("1a"),
         Type3("3c");

      private TestType(String value) {
         this.value = value;
      }

      public String getValue() {
         return value;
      }

      public void setValue(String value) {
         this.value = value;
      }

      private String value;
   }

   public static void testCostruction() {
      /**
       * EnumSet是个抽象类，不能构造
       */
   }

   public static void test() {
      EnumSet.allOf(TestType.class);

      // ，通过以下方式构造
      // * 如果枚举中的值个数<64则使用RegularEnumSet
      // * 否则使用JumboEnumSet
      EnumSet<TestType> enumSet = EnumSet.noneOf(TestType.class);
   }

   public static void main(String[] args) {
      // RegularEnumSet
      // JumboEnumSet
      EnumSet<TestType> enumSet = EnumSet.noneOf(TestType.class);
      enumSet.add(TestType.Type3);
      enumSet.add(TestType.Type1);
      enumSet.add(TestType.Type2);
      enumSet.add(TestType.Type2);

      // 貌似顺序来源于枚举定义是的先后顺序
      for(TestType type : enumSet) {
         System.err.println(type.getValue());
      }
   }
}
