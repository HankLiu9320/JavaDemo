package com.demo.memcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;
import net.spy.memcached.MemcachedClient;

public class MemcachedJava {
   private static int size = 100;
   private static List<MemcachedClient> factory = new ArrayList<MemcachedClient>(size);

   public static void init() {
      for(int i = 0; i < size; i++) {
         try {
            MemcachedClient mcc = new MemcachedClient(new InetSocketAddress("192.168.230.131", 11211));
            factory.add(mcc);
         }
         catch(IOException e) {
            e.printStackTrace();
         }
      }
   }

   public static MemcachedClient getConn() {
      Random r = new Random();
      int idx = r.nextInt(size);
      return factory.get(idx);
   }

   public static void main(String[] args) {
      init();
      MemcachedClient mcc = getConn();

      try {
         // 连接本地的 Memcached 服务
         System.out.println("Connection to server sucessful.");
         // 存储数据
         Future fo = mcc.set("runoob", 900, "Free Education");
         // 查看存储状态
         System.out.println("set status:" + fo.get());
         // 输出值
         System.out.println("runoob value in cache - " + mcc.get("runoob"));
         // 关闭连接
         mcc.shutdown();
      }
      catch(Exception ex) {
         System.out.println(ex.getMessage());
      }
   }
}