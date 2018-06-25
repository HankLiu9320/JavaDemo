package com.demo.util;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 描述： 与枚举类型键一起使用的专用 Map实现。枚举映射中所有键都必须来自单个枚举类型，该枚举类型在创建映射时显式或隐式地指定。
 * 枚举映射在内部表示为数组。此表示形式非常紧凑且高效。
 * 
 * 枚举映射根据其键的自然顺序 来维护（该顺序是声明枚举常量的顺序）。在集合视图（keySet()、entrySet() 和
 * values()）所返回的迭代器中反映了这一点。
 * 
 * 由集合视图返回的迭代器是弱一致 的：它们不会抛出
 * ConcurrentModificationException，也不一定显示在迭代进行时发生的任何映射修改的效果。
 * 
 * 不允许使用 null 键。试图插入 null 键将抛出 NullPointerException。但是，试图测试是否出现 null 键或移除 null
 * 键将不会抛出异常。允许使用 null 值。
 * 
 * 像大多数集合一样，EnumMap 是不同步的。如果多个线程同时访问一个枚举映射，并且至少有一个线程修改该映射，则此枚举映射在外部应该是同步的。
 * 这一般通过对自然封装该枚举映射的某个对象进行同步来完成。如果不存在这样的对象，则应该使用
 * Collections.synchronizedMap(java.util.Map)方法来“包装”该枚举。最好在创建时完成这一操作，以防止意外的非同步访问：
 * Map<EnumKey, V> m = Collections.synchronizedMap(new EnumMap(...));
 * 实现注意事项：所有基本操作都在固定时间内执行。虽然并不保证，但它们很可能比其 HashMap 副本更快。
 * 
 *线程安全：线程不安全
 *
 */
public class EnumMapDemo {
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
      //内部创建一个传入类的枚举值的数组，数组长度为此枚举中的值得个数
      EnumMap<TestType, String> enumMap = new EnumMap<TestType, String>(TestType.class);
      //将已有的enummap信息复制到当前的enumMap
      enumMap = new EnumMap<TestType, String>(enumMap);
      
      //将map中的值put到enumMap中
      Map<TestType, String> map = new HashMap<TestType, String>();
      map.put(TestType.Type3, "类型3");
      enumMap = new EnumMap<TestType, String>(map);
      System.err.println(enumMap);
   }

   public static void test() {
      EnumMap<TestType, String> enumMap = new EnumMap<TestType, String>(TestType.class);
      
      /**
       * EnumMap的put方法
       * 1)先判断传入的key是否是enumMap初始化时的枚举类型，否则抛出ClassCastException
       * 2)是用enum中的ordinal(一个枚举中的每个枚举值得ordinal不同，0,1,2...递增)去查找对应的数据
       */
      enumMap.put(TestType.Type3, "类型3");
      /**
       * 与put同理
       */
      Map<TestType, String> map = new HashMap<TestType, String>();
      map.put(TestType.Type3, "类型3");
      enumMap.putAll(map);
      
      /**
       * 通过传入的枚举ordinal值，找到数据的索引，返回值。如果为null，返回一个特殊的空值。
       * 传入参数必须为enum
       */
      enumMap.get(TestType.Type3);
      /**
       * 依然使用enum的ordinal做处理
       */
      enumMap.containsKey(TestType.Type3);

      /**
       * 与hashMap的keySet逻辑类似，还是在迭代的时候才读取数组的值
       */
      enumMap.keySet();
      enumMap.entrySet();
      enumMap.values();
      
      /**
       * 给数组所有值填充null
       */
      enumMap.clear();

      /**
       * 循环数组做value的equals
       */
      enumMap.containsValue("");
      
      //逻辑简单，不做描述
      enumMap.clone();
      enumMap.equals(map);
      enumMap.remove(TestType.Type3);
      enumMap.size();
   }

   public static void main(String[] args) {
      testCostruction();

      EnumMap<TestType, String> enumMap = new EnumMap<TestType, String>(TestType.class);
      enumMap.put(TestType.Type3, "类型3");
      enumMap.put(TestType.Type2, "类型2");
      enumMap.put(TestType.Type1, "类型1");
      enumMap.put(TestType.Type3, "类型3");
      
      Iterator<Map.Entry<TestType,String>> i = enumMap.entrySet().iterator();
      
      while(i.hasNext()) {
         Map.Entry<TestType,String> e = i.next();
         System.err.println(e.getKey() + "," + e.getValue() + "," + e.getKey().ordinal());
      }

      // 貌似顺序来源于枚举定义是的先后顺序
      for(TestType type : enumMap.keySet()) {
         System.err.println(type.getValue() + ":" + enumMap.get(type));
      }

      for(Map.Entry<TestType, String> entry : enumMap.entrySet()) {
         System.out.println(entry.getKey() + ":" + entry.getValue());
      }
   }
}
