/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.boss.Block;
import com.jun0rr.boss.Volume;
import com.jun0rr.boss.config.BufferConfig;
import com.jun0rr.boss.config.VolumeConfig;
import com.jun0rr.boss.volume.Async;
import com.jun0rr.boss.volume.FileVolume;
import com.jun0rr.uncheck.Uncheck;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
/**
 *
 * @author Juno
 */
public class TestFileVolume {
  
  @Test public void test() {
    try {
      Path p = Paths.get("./TestFileVolume.dat");
      Uncheck.call(()->Files.deleteIfExists(p));
      BufferConfig bc = new BufferConfig(BufferConfig.Type.DIRECT, 64, 1*1024*1024L);
      VolumeConfig vc = new VolumeConfig("TestFileVolume", bc, p);
      System.out.println(vc);
      Volume v = new FileVolume(vc);
      Block b = v.allocate(448);
      //System.out.println("allocated: " + b);
      for(int i = 0; i < 100; i++) {
        b.buffer().putInt(i);
        //System.out.printf("putInt( %d ): %s%n", i, b);
      }
      //System.out.println("commit: " + b);
      Async<Block> a = Async.exec(()->b.commit())
          .onComplete(c->System.out.println("async completed: " + c));
      //a.waitDone();
      
      Block c = v.metadata();
      //System.out.println("metadata: " + c);
      for(int i = 100; i < 125; i++) {
        c.buffer().putInt(i);
        //System.out.printf("metadata putInt( %d ): %s%n", i, c);
      }
      a = Async.exec(()->c.commit())
          .onComplete(f->System.out.println("async completed: " + f));
      a.waitDone();
      
      v.release(448);
      //System.out.println("Volume.close()");
      Volume v1 = v;
      a.onDone(x->v1.close()).waitDone();
      //v.close();
      
      //System.out.println("-----------------------------");
      v = new FileVolume(vc);
      Block d = v.get(64);
      //System.out.println(d);
      for(int i = 0; i < 100; i++) {
        int x = d.buffer().getInt();
        //System.out.printf("getInt( %d ): %d%n", i, x);
        Assertions.assertEquals(i, x);
      }

      Block e = v.metadata();
      //System.out.println("metadata: " + e);
      for(int i = 100; i < 125; i++) {
        int x = e.buffer().getInt();
        //System.out.printf("metadata getInt( %d ): %d%n", i, x);
        Assertions.assertEquals(i, x);
      }

      v.close();
    }
    catch(Throwable e) {
      e.printStackTrace();
      throw Uncheck.uncheck(e);
    }
  }
  
}
