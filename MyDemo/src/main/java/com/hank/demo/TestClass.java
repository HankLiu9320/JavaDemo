package com.hank.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class TestClass {
   public final static String a = "a";

   private static void testMethod() {
      System.out.println("testMethod");
   }

   private static void test(StringBuilder s1, StringBuilder s2) {
      s1.append(s2);
      s2.append("aaaaaaaa");
      s2 = s1;
      System.err.println(s2);
   }

   public static void main1(String[] args) {
      ((TestClass) null).testMethod();
      int a = 17;
      int b = Integer.highestOneBit((a - 1) << 1);
      System.err.println(b);

      StringBuilder s1 = new StringBuilder("a");
      StringBuilder s2 = new StringBuilder("b");
      TestClass t = new TestClass();
      t.test(s1, s2);
      System.err.println(s1 + "," + s2);

   }

   public static Set readTxtFile(String filePath) {
      Set<String> ids = new HashSet<String>();
      int i = 0;

      try {
         String encoding = "GBK";
         File file = new File(filePath);
         if(file.isFile() && file.exists()) { // 判断文件是否存在
            InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while((lineTxt = bufferedReader.readLine()) != null) {
               lineTxt = lineTxt.trim();
               i++;
               ids.add(lineTxt);
            }
            read.close();
         }
         else {
            System.out.println("找不到指定的文件");
         }
      }
      catch(Exception e) {
         System.out.println("读取文件内容出错");
         e.printStackTrace();
      }
      System.err.println(i);
      return ids;
   }

   public static void main(String[] args) {
      Set<String> ids = readTxtFile("d:\\a.txt");

      for(String id : ids) {
         System.err.println(id);
      }
      System.err.println(ids.size());
   }
}