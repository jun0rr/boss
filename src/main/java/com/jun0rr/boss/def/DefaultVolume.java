/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.def;

import com.jun0rr.boss.Block;
import com.jun0rr.boss.Volume;
import com.jun0rr.jbom.buffer.BinBuffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author F6036477
 */
public class DefaultVolume implements Volume {
  
  private final BinBuffer buffer;
  
  private final String id;
  
  private final Queue<ByteBuffer> released;
  
  private final int blockSize;
  
  private int woffset;
  
  public DefaultVolume(String id, BinBuffer buf, int blockSize) {
    this.id = Objects.requireNonNull(id);
    this.buffer = Objects.requireNonNull(buf);
    this.released = new ConcurrentLinkedQueue<>();
    this.woffset = 0;
    this.blockSize = blockSize;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public int blockSize() {
    return blockSize;
  }

  @Override
  public Block allocate(int size) {
    int blocks = size / blockSize + (size % blockSize > 0 ? 1 : 0);
    List<ByteBuffer> bufs = new ArrayList<>(blocks);
    int count = 0;
    while(count < blocks && !released.isEmpty()) {
      bufs.add(released.poll());
      count++;
    }
    while(count < blocks) {
      
    }
  }

  @Override
  public Volume release(Block blk) {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  @Override
  public Volume releaseAll(Block blk) {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  @Override
  public Block get(int offset) {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  @Override
  public void close() {
    malloc.close();
  }

}
