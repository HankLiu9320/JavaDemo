package com.demo.base;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class ManagementFactoryDemo {
   public static void main(String[] args) {
      ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
      ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false, false);
      
      for(ThreadInfo info : threadInfos) {
         System.err.println(info.getThreadId() + "," + info.getThreadId() + "," + info.getThreadName());
      }
   }
}
