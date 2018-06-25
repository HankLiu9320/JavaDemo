package com.demo.disruptor;

import com.lmax.disruptor.EventFactory;

public class LongEventFactory implements EventFactory {
   @Override
   public Object newInstance() {
      return new LongEvent();
   }
}