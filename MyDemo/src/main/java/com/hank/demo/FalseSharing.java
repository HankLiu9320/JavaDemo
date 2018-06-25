package com.hank.demo;

public final class FalseSharing implements Runnable {
   public final static int NUM_THREADS = 4; // change
   public final static long ITERATIONS = 500L * 1000L * 1000L;
   private final int arrayIndex;

   private static VolatileLong[] longs = new VolatileLong[NUM_THREADS];

   static {
      for(int i = 0; i < longs.length; i++) {
         longs[i] = new VolatileLong();
      }
   }

   public FalseSharing(final int arrayIndex) {
      this.arrayIndex = arrayIndex;
   }

   private static void runTest() throws InterruptedException {
      Thread[] threads = new Thread[NUM_THREADS];

      for(int i = 0; i < threads.length; i++) {
         threads[i] = new Thread(new FalseSharing(i));
      }

      for(Thread t : threads) {
         t.start();
      }

      for(Thread t : threads) {
         t.join();
      }
   }

   public void run() {
      long i = ITERATIONS + 1;
      while(0 != --i) {
         longs[arrayIndex].value = i;
      }
   }

   public static void main(final String[] args) throws Exception {
      final long start = System.nanoTime();
      runTest();
      System.out.println("duration = " + ((System.nanoTime() - start) / 1000 / 1000));
   }

   public final static class VolatileLong {
      public volatile long value = 0L;
      public long p1, p2, p3, p4, p5, p6; // comment out 
      //使用p1 p2填充duration =   3989991758
      //不使用p1 p2填充duration = 27483014553
      //http://coderplay.iteye.com/blog/1485760
      //http://coderplay.iteye.com/blog/1486649
   }
}