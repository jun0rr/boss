/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.def;

import com.jun0rr.boss.Block;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Consumer;
import com.jun0rr.boss.BufferAllocEvent;
import com.jun0rr.jbom.buffer.BufferAllocator;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @author F6036477
 */
public class VolumeBufferAllocator implements BufferAllocator {
  
  private final BufferAllocator malloc;
  
  private final int blockSize;
  
  private final AtomicReference<ByteBuffer> mainBuffer;
  
  private final AtomicInteger offset;
  
  private Block block;
  
  public VolumeBufferAllocator(BufferAllocator ba, int blockSize, ByteBuffer main, int offset) {
    this.malloc = Objects.requireNonNull(ba);
    this.blockSize = blockSize;
    this.mainBuffer = main != null ? new AtomicReference<>(main) : new AtomicReference<>();
    this.offset = new AtomicInteger(offset);
  }
  
  public Block block() {
    return block;
  }
  
  public VolumeBufferAllocator setBlock(Block b) {
    this.block = Objects.requireNonNull(b);
    return this;
  }

  @Override
  public int bufferSize() {
    return blockSize;
  }

  @Override
  public ByteBuffer alloc() {
    if(block == null) {
      throw new IllegalStateException("Bad null Block");
    }
    if(mainBuffer.get() == null || mainBuffer.get().remaining() < blockSize) {
      mainBuffer.compareAndSet(null, malloc.alloc());
      offset.set(Integer.BYTES);
    }
    int pos = offset.get();
    ByteBuffer buf = mainBuffer.get().position(pos).limit(pos + blockSize).slice();
    offset.compareAndSet(pos, pos + blockSize);
    mainBuffer.get().putInt(0, offset.get());
    //notify block here
    return buf;
  }

  @Override
  public ByteBuffer alloc(int size) {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }
  
}
