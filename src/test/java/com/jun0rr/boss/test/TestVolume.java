/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.boss.Block;
import com.jun0rr.boss.Volume;
import com.jun0rr.boss.def.DefaultVolume;
import com.jun0rr.boss.def.NoPersistStrategy;
import com.jun0rr.jbom.buffer.BufferAllocator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestVolume {
  
  @Test
  public void test() {
    Volume v = new DefaultVolume("test01", 14, BufferAllocator.heapAllocator(28), new NoPersistStrategy());
    System.out.println(v);
    Block b = v.allocate();
    System.out.println(b);
    byte[] bs = new byte[30];
    for(int i = 0; i < bs.length; i++) {
      bs[i] = (byte) (Math.random() * Byte.MAX_VALUE * 2);
    }
    b.buffer().put(bs);
    System.out.println(v);
    b = v.get(b.offset());
    byte[] gs = new byte[bs.length];
    b.buffer().get(gs);
    Assertions.assertArrayEquals(bs, gs);
    v.release(b);
    System.out.println(v);
    v.close();
    System.out.println(v.metadata());
  }
  
}
