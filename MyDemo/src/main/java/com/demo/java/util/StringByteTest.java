package com.demo.java.util;

public class StringByteTest {
   public static void main(String[] args) {
      String test = "张三啊啊啊";
      String testString = chineseToString(test);
      System.out.println(testString.toString());
      String a = stringToChinese(testString);
      System.out.println(a);
   }

   public static String chineseToString(String chinese) {
      StringBuffer sb = new StringBuffer();
      byte[] testByte = chinese.getBytes();
      byte[] temp = null;

      for(int i = 0; i < testByte.length; i++) {
         temp = new byte[4];
         temp[0] = testByte[i];
         temp[1] = 0;
         temp[2] = 0;
         temp[3] = 0;
         sb.append(lBytesToInt(temp));
         if(i < testByte.length - 1) {
            sb.append("@");
         }
      }

      return sb.toString();
   }

   public static int lBytesToInt(byte[] b) {
      int s = 0;
      for(int i = 0; i < 3; i++) {
         if(b[3 - i] >= 0) {
            s = s + b[3 - i];
         }
         else {
            s = s + 256 + b[3 - i];
         }
         s = s * 256;
      }
      
      if(b[0] >= 0) {
         s = s + b[0];
      }
      else {
         s = s + 256 + b[0];
      }
      return s;
   }

   public static String stringToChinese(String stc) {
      String[] s = stc.split("@");
      if(s.length > 0) {
         byte[] b = new byte[s.length];
         for(int i = 0; i < s.length; i++) {
            b[i] = (byte) Integer.parseInt(s[i]);
         }

         return new String(b);
      }
      else {
         return "";
      }
   }
}
