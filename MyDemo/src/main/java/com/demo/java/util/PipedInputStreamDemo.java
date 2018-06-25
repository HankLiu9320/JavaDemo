package com.demo.java.util;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class PipedInputStreamDemo {
   public static void main(String[] args) throws IOException {
      PipedInputStream input = new PipedInputStream();
      PipedOutputStream out = new PipedOutputStream();
      input.connect(out);
      ReadThread readTh = new ReadThread(input);
      WriteThread writeTh = new WriteThread(out);
      new Thread(readTh).start();
      new Thread(writeTh).start();
   }
}

class ReadThread implements Runnable {
   private PipedInputStream pin;

   ReadThread(PipedInputStream pin) {
      this.pin = pin;
   }

   @Override
   public void run() {
      try {
         System.out.println("R:读取前没有数据,阻塞中...等待数据传过来再输出到控制台...");
         byte[] buf = new byte[1024];
         int len = pin.read(buf);
         System.out.println(len);
         System.out.println("R:读取数据成功,阻塞解除...");
         pin.close();
      }
      catch(IOException e) {
         e.printStackTrace();
      }
   }
}

class WriteThread implements Runnable {
   private PipedOutputStream pout;

   WriteThread(PipedOutputStream pout) {
      this.pout = pout;
   }

   public void run() {
      try {
         System.out.println("W:开始将数据写入:但等个5秒让我们观察...");
         Thread.sleep(5000); // 释放cpu执行权5秒
         pout.write("W: writePiped 数据...".getBytes()); // 管道输出流
         pout.close();
      }
      catch(Exception e) {
         throw new RuntimeException("W:WriteThread写入失败...");
      }
   }
}
