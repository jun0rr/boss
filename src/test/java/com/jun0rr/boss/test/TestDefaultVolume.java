/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.boss.Block;
import com.jun0rr.boss.config.BufferConfig;
import com.jun0rr.boss.config.VolumeConfig;
import com.jun0rr.boss.volume.Async;
import com.jun0rr.boss.volume.DefaultVolume;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Juno
 */
public class TestDefaultVolume {
  
  @Test public void test() {
    BufferConfig bc = new BufferConfig(BufferConfig.Type.DIRECT, 64, 1*1024*1024L);
    VolumeConfig vc = new VolumeConfig("TestDefaultVolume", bc, null);
    System.out.println(vc);
    DefaultVolume v = new DefaultVolume(vc);
    Block b = v.allocate(448);
    System.out.println("allocated: " + b);
    for(int i = 0; i < 100; i++) {
      b.buffer().putInt(i);
      System.out.printf("putInt( %d ): %s%n", i, b);
    }
    System.out.println("commit: " + b);
    Async<Block> a = Async.exec(()->b.commit())
        .onComplete(c->System.out.println("async completed: " + c));
    a.waitDone();

    Block c = v.metadata();
    System.out.println("metadata: " + c);
    for(int i = 100; i < 125; i++) {
      c.buffer().putInt(i);
      System.out.printf("metadata putInt( %d ): %s%n", i, c);
    }
    a = Async.exec(()->c.commit())
        .onComplete(f->System.out.println("async completed: " + f));
    a.waitDone();

    Block d = v.get(64);
    System.out.println(b);
    for(int i = 0; i < 100; i++) {
      int x = d.buffer().getInt();
      System.out.printf("getInt( %d ): %d%n", i, x);
      Assertions.assertEquals(i, x);
    }

    Block e = v.metadata();
    System.out.println("metadata: " + e);
    for(int i = 100; i < 125; i++) {
      int x = e.buffer().getInt();
      System.out.printf("metadata getInt( %d ): %d%n", i, x);
      Assertions.assertEquals(i, x);
    }
    v.release(448);

    Block f = v.allocate(20*4);
    System.out.println("allocated: " + f);
    for(int i = 0; i < 20; i++) {
      f.buffer().putInt(i);
      System.out.printf("putInt( %d ): %s%n", i, f);
    }
    Async<Block> aa = Async.exec(()->f.commit())
        .onComplete(g->System.out.println("async completed: " + g));
    aa.waitDone();

    Block h = v.get(f.offset());
    System.out.println(h);
    for(int i = 0; i < 20; i++) {
      int x = h.buffer().getInt();
      System.out.printf("getInt( %d ): %d%n", i, x);
      Assertions.assertEquals(i, x);
    }
  }
  
}
