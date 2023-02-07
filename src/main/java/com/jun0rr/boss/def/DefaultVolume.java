/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.def;

import com.jun0rr.boss.Block;
import com.jun0rr.boss.Volume;
import com.jun0rr.jbom.buffer.BinBuffer;
import com.jun0rr.jbom.buffer.BufferAllocator;
import com.jun0rr.jbom.buffer.DefaultBinBuffer;
import com.jun0rr.jbom.buffer.DefaultBufferAllocator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author F6036477
 */
public class DefaultVolume implements Volume {
  
  private final String id;
  
  private final int blockSize;
  
  private final BufferAllocator malloc;
  
  private final List<ByteBuffer> buffers;
  
  private final Queue<FreeBuffer> freebufs;
  
  private AtomicInteger woffset;
  
  public DefaultVolume(String id, int blockSize, BufferAllocator ba) {
    this.id = Objects.requireNonNull(id);
    this.malloc = Objects.requireNonNull(ba);
    this.blockSize = blockSize;
    this.freebufs = new ConcurrentLinkedQueue<>();
    this.buffers = new CopyOnWriteArrayList<>();
    this.woffset = new AtomicInteger(0);
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
    return woffset.get() / malloc.bufferSize();
  }
  
  public int capacity() {
    return buffers.stream().mapToInt(ByteBuffer::capacity).sum();
  }
  
  private FreeBuffer getFreeBuffer(int offset) {
    if(offset < 0) return null;
    int idx = offset / malloc.bufferSize();
    int pos = offset - idx * malloc.bufferSize();
    ByteBuffer bb = buffers.get(idx).clear().position(pos).limit(pos + blockSize);
    return new FreeBuffer(offset, bb.slice());
  }
  
  private FreeBuffer allocateFreeBuffer() {
    FreeBuffer buf = null;
    if(!freebufs.isEmpty()) {
      buf = freebufs.poll();
    }
    else {
      if((woffset.get() + blockSize) > capacity()) {
        buffers.add(malloc.alloc());
      }
      buf = getFreeBuffer(woffset.getAndAdd(blockSize));
    }
    buf.buffer().clear().putInt(-1).clear();
    return buf;
  }
  
  private FreeBuffer last(FreeBuffer buf) {
    FreeBuffer last = buf;
    int nos = last.buffer().position(0).getInt();
    while(nos >= 0) {
      last = getFreeBuffer(nos);
      nos = last.buffer().position(0).getInt();
    }
    return last;
  }
  
  private ByteBuffer allocateSlice(FreeBuffer buf) {
    FreeBuffer last = last(buf);
    FreeBuffer fb = allocateFreeBuffer();
    last.buffer().position(0).putInt(fb.offset());
    return fb.buffer().position(Integer.BYTES).slice();
  }
  
  @Override
  public Block allocate() {
    return allocate(blockSize);
  }

  @Override
  public Block allocate(int size) {
    FreeBuffer buf = allocateFreeBuffer();
    BufferAllocator alloc = new DefaultBufferAllocator(blockSize - Integer.BYTES) {
      @Override
      public ByteBuffer alloc(int size) {
        return allocateSlice(buf);
      }
    };
    List<ByteBuffer> bufs = new ArrayList<>();
    bufs.add(buf.buffer().position(Integer.BYTES).slice());
    BinBuffer buffer = new DefaultBinBuffer(alloc, bufs);
    buffer.capacity(size);
    return new DefaultBlock(this, buffer, BinBuffer.fixedSizeBuffer(buf.buffer().position(0).limit(Integer.BYTES).slice()), buf.offset());
  }

  @Override
  public Volume release(Block blk) {
    Objects.requireNonNull(blk);
    int nos = blk.offset();
    do {
      FreeBuffer buf = getFreeBuffer(nos);
      freebufs.add(buf);
      nos = buf.buffer().position(0).getInt();
    } while(nos >= 0);
    return this;
  }

  @Override
  public Block get(int offset) {
    FreeBuffer buf = getFreeBuffer(offset);
    BufferAllocator alloc = new DefaultBufferAllocator(blockSize - Integer.BYTES) {
      @Override
      public ByteBuffer alloc(int size) {
        return allocateSlice(buf);
      }
    };
    List<ByteBuffer> bufs = new ArrayList<>();
    FreeBuffer last = buf;
    while(last != null) {
      bufs.add(buf.buffer().position(Integer.BYTES).slice());
      last = getFreeBuffer(last.buffer().position(0).getInt());
    }
    BinBuffer buffer = new DefaultBinBuffer(alloc, bufs);
    return new DefaultBlock(this, buffer, BinBuffer.fixedSizeBuffer(buf.buffer().position(0).limit(Integer.BYTES).slice()), buf.offset());
  }

  @Override
  public void close() {
    malloc.close();
  }

}
