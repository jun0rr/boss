/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.def;

import com.jun0rr.boss.Block;
import com.jun0rr.boss.Volume;
import com.jun0rr.jbom.buffer.BufferAllocator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author F6036477
 */
public class DefaultVolume implements Volume, BufferAllocator {
  
  private final String id;
  
  private final int blockSize;
  
  private final BufferAllocator malloc;
  
  private final List<ByteBuffer> buffers;
  
  private final Queue<FreeBuffer> freebufs;
  
  private final int bufferSize;
  
  private int woffset;
  
  public DefaultVolume(String id, int blockSize, BufferAllocator ba) {
    this.id = Objects.requireNonNull(id);
    this.malloc = Objects.requireNonNull(ba);
    this.blockSize = blockSize;
    this.bufferSize = blockSize * 10;
    this.freebufs = new ConcurrentLinkedQueue<>();
    this.buffers = new CopyOnWriteArrayList<>();
    this.woffset = 0;
  }
  
  @Override
  public String id() {
    return id;
  }

  @Override
  public int blockSize() {
    return blockSize;
  }
  
  private int index() {
    return woffset / bufferSize;
  }
  
  public int capacity() {
    return buffers.stream().mapToInt(ByteBuffer::capacity).sum();
  }

  @Override
  public Block allocate(int size) {
    int blocks = size / blockSize + (size % blockSize > 0 ? 1 : 0);
    List<ByteBuffer> bufs = new ArrayList<>(blocks);
    while(blocks > 0 && !freebufs.isEmpty()) {
      bufs.add(freebufs.poll())
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

  @Override
  public int bufferSize() {
    return blockSize;
  }

  @Override
  public ByteBuffer alloc() {
    if()
  }

  @Override
  public ByteBuffer alloc(int size) {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

}
