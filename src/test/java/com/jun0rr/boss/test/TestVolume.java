/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.boss.Block;
import com.jun0rr.boss.Volume;
import com.jun0rr.boss.volume.DefaultVolume;
import com.jun0rr.binj.buffer.BufferAllocator;
import com.jun0rr.binj.buffer.MappedBufferAllocator;
import com.jun0rr.binj.buffer.PathSupplier;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestVolume {
  
  @Test
  public void testHeap() {
    System.out.println("---+--- testHeap ---+---");
    Volume v = new DefaultVolume("testHeap", 14, BufferAllocator.heapAllocator(28));
    System.out.println(v);
    Block b = v.allocate();
    System.out.println(b);
    byte[] bs = new byte[30];
    for(int i = 0; i < bs.length; i++) {
      bs[i] = (byte) (Math.random() * Byte.MAX_VALUE * 2);
    }
    b.buffer().put(bs);
    System.out.println(v);
    b = v.get(b.index());
    byte[] gs = new byte[bs.length];
    b.buffer().get(gs);
    Assertions.assertArrayEquals(bs, gs);
    v.release(b);
    v.close();
    System.out.println(v);
  }
  
  @Test
  public void testMapped() throws Exception {
    System.out.println("---+--- testMapped ---+---");
    try {
      Path root = Paths.get("./");
      PathSupplier ps = PathSupplier.of(root, "testMapped", "db");
      ps.existing().forEach(this::delete);
      MappedBufferAllocator ma = new MappedBufferAllocator(ps, 128);
      Volume v = new DefaultVolume("testMapped", 64, ma.readBuffers(), ma);
      System.out.println(v);
      Block b = v.allocate();
      System.out.println(b);
      byte[] bs = new byte[300];
      for(int i = 0; i < bs.length; i++) {
        bs[i] = (byte) (Math.random() * Byte.MAX_VALUE * 2);
      }
      b.buffer().put(bs);
      System.out.println(b);
      b = v.get(b.index());
      System.out.println(b);
      byte[] gs = new byte[bs.length];
      b.buffer().get(gs);
      Assertions.assertArrayEquals(bs, gs);
      v.release(b);
      System.out.println(v);
      System.out.println("closing...");
      v.close();
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
  private void delete(Path p) {
    try {
      Files.deleteIfExists(p);
    }
    catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
  
}
