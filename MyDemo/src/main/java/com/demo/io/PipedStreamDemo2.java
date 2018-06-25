package com.demo.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.SequenceInputStream;

public class PipedStreamDemo2 {
   public static void main(String[] args) throws IOException {
//      PipedInputStream in = new PipedInputStream();
//      PipedOutputStream out = new PipedOutputStream(in);
//      Thread t = new Thread(new inputThread(in));
//      Thread t1 = new Thread(new outputThread(out));
      ByteArrayInputStream i1 = new ByteArrayInputStream("张三".getBytes());
      ByteArrayInputStream i2 = new ByteArrayInputStream("abdd".getBytes());
      SequenceInputStream in = new SequenceInputStream(i1, i2);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      int data = -1;
      
      while((data = in.read()) != -1) {
         out.write(data);
      }
      System.out.println(new String(out.toByteArray()));
      
      
//      t.start();
//      t1.start();
   }

   static class inputThread implements Runnable {
      private PipedInputStream in;

      public inputThread(PipedInputStream in) {
         this.in = in;
      }

      @Override
      public void run() {
         System.out.println("inputStream --> Begin");
         int data = -1;
         
         try {
            while((data = in.read()) != -1) {
               System.out.println((char)data);
            }
            System.out.println("-------in------");
         }
         catch(IOException e) {
            e.printStackTrace();
         }
      }
   }

   static class outputThread implements Runnable {
      private PipedOutputStream out;

      public outputThread(PipedOutputStream out) {
         this.out = out;
      }

      @Override
      public void run() {
         System.out.println("outputStream --> Begin - >等待2秒");
         try {
            String content = "abcdefg";
            ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes());
            int data = -1;
            while((data = input.read()) != -1) {
               out.write(data);
            }
            
            System.err.println("================out------------");
            out.close();
         }
         catch(IOException e) {
            e.printStackTrace();
         }
      }
   }
}
