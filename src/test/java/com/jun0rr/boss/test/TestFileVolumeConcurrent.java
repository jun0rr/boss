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
public class TestFileVolumeConcurrent {
  
  @Test public void test() {
    try {
      Path p = Paths.get("./TestFileVolume.dat");
      Uncheck.call(()->Files.deleteIfExists(p));
      BufferConfig bc = new BufferConfig(BufferConfig.Type.DIRECT, 64, 1*1024*1024L);
      VolumeConfig vc = new VolumeConfig("TestFileVolume", bc, p);
      System.out.println(vc);
      Volume v1 = vc.createVolume();
      
      Async<Block> put1 = Async.exec(()->{
        Block b = v1.allocate(448);
        for(int i = 0; i < 100; i++) {
          b.buffer().putInt(i);
        }
        return b.commit();
      }).onDone(a->System.out.printf("async put1 completed: %s%n", a.get()));
      
      Async<Block> put2 = Async.exec(()->{
        Block b = v1.allocate(448);
        for(int i = 100; i < 200; i++) {
          b.buffer().putInt(i);
        }
        return b.commit();
      }).onDone(a->System.out.printf("async put2 completed: %s%n", a.get()));
      
      put2.join(put1).waitDone();
      v1.close();
      System.out.println("-----------------------------");
      
      Volume v2 = new FileVolume(vc);
      Async<Block> get1 = Async.exec(()->{
        Block b = v2.get(put1.get().offset());
        for(int i = 0; i < 100; i++) {
          int x = b.buffer().getInt();
          Assertions.assertEquals(i, x);
        }
        return b;
      });
      
      Async<Block> get2 = Async.exec(()->{
        Block b = v2.get(put2.get().offset());
        for(int i = 100; i < 200; i++) {
          int x = b.buffer().getInt();
          Assertions.assertEquals(i, x);
        }
        return b;
      });
      
      get2.join(get1).onDone(x->v2.close()).waitDone();
    }
    catch(Throwable e) {
      e.printStackTrace();
      throw Uncheck.uncheck(e);
    }
  }
  
}
