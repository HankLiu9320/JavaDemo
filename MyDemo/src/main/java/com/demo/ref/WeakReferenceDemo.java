package com.demo.ref;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class WeakReferenceDemo {
   static ReferenceQueue referenceQueue = new ReferenceQueue();

   public static void main(String[] args) {
      final Map<Object, Object> map = new HashMap<>();
      Thread thread = new Thread() {
         public void run() {
            try {
               int cnt = 0;
               WeakReference<byte[]> k;
               while((k = (WeakReference) referenceQueue.remove()) != null) {
                  System.out.println((cnt++) + "回收了:" + k + "," + map.size());
               }
            }
            catch(InterruptedException e) {
               // 结束循环
            }
         }
      };
      thread.setDaemon(true);
      thread.start();

      Object value = new Object();
      
      for(int i = 0; i < 10000; i++) {
         byte[] bytes = new byte[10*1024*1024];
         WeakReference<byte[]> weakReference = new WeakReference<byte[]>(bytes, referenceQueue);
         map.put(weakReference, value);
      }
      System.out.println("map.size->" + map.size());
   }
}
