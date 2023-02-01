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
public class DefaultVolume implements Volume {
  
  private final String id;
  
  private final int blockSize;
  
  private final BufferAllocator malloc;
  
  private final List<ByteBuffer> buffers;
  
  private final Queue<Block> released;
  
  private int woffset;
  
  public DefaultVolume(String id, int blockSize, BufferAllocator ba) {
    this.id = Objects.requireNonNull(id);
    this.malloc = Objects.requireNonNull(ba);
    if(blockSize >= malloc.bufferSize()) {
      throw new IllegalArgumentException(String.format(
          "Bad block size (%d). Must be lesser then BufferAllocator.bufferSize(%d)", 
          blockSize, malloc.bufferSize())
      );
    }
    this.blockSize = blockSize;
    this.released = new ConcurrentLinkedQueue<>();
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
    return woffset / malloc.bufferSize();
  }
  
  public int capacity() {
    return buffers.stream().mapToInt(ByteBuffer::capacity).sum();
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
      int idx = woffset / blockSize;
      ByteBuffer blk;
      if(woffset + blockSize > capacity()) {
        ByteBuffer buf = malloc.alloc();
        blk = buf.limit(blockSize).slice();
        buf.limit(buf.capacity());
        buffers.add(buf);
      }
      else {
        
      }
      
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
