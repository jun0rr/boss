/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.boss.Block;
import com.jun0rr.boss.Volume;
import com.jun0rr.boss.volume.DefaultVolume;
import com.jun0rr.binj.BinContext;
import com.jun0rr.binj.buffer.BufferAllocator;
import com.jun0rr.binj.buffer.FileNameSupplier;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestVolume {
  
  @Test
  public void testHeap() {
    Volume v = new DefaultVolume("testHeap", 14, BufferAllocator.heapAllocator(28));
    //System.out.println(v);
    Block b = v.allocate();
    //System.out.println(b);
    byte[] bs = new byte[30];
    for(int i = 0; i < bs.length; i++) {
      bs[i] = (byte) (Math.random() * Byte.MAX_VALUE * 2);
    }
    b.buffer().put(bs);
    //System.out.println(v);
    b = v.get(b.index());
    byte[] gs = new byte[bs.length];
    b.buffer().get(gs);
    Assertions.assertArrayEquals(bs, gs);
    v.release(b);
    v.close();
    //System.out.println(v);
  }
  
  @Test
  public void testMapped() {
    try {
    BinContext ctx = BinContext.newContext();
    Path path = Paths.get("./").toAbsolutePath();
    Supplier<String> fname = new FileNameSupplier("testMapped", "db");
    BufferAllocator alloc = BufferAllocator.mappedFileAllocator(path, new FileNameSupplier("testMapped", "db"), 128, false);
    List<ByteBuffer> bufs = new LinkedList<>();
    while(Files.exists(path.resolve(fname.get()))) {
      bufs.add(alloc.alloc());
    }
    Volume v = new DefaultVolume("testMapped", 64, bufs, BufferAllocator.mappedFileAllocator(
        path, new FileNameSupplier("testMapped", "db"), 128, false)
    );
    //System.out.println(v);
    Block b = v.allocate();
    //System.out.println(b);
    byte[] bs = new byte[300];
    for(int i = 0; i < bs.length; i++) {
      bs[i] = (byte) (Math.random() * Byte.MAX_VALUE * 2);
    }
    b.buffer().put(bs);
    b = v.get(b.index());
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
  
}
