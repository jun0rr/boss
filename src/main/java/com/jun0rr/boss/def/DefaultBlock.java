/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.def;

import com.jun0rr.boss.Block;
import com.jun0rr.boss.Volume;
import com.jun0rr.jbom.buffer.BinBuffer;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class DefaultBlock implements Block {
  
  private final Volume volume;
  
  private final BinBuffer buffer;
  
  private final ByteBuffer nextbuf;
  
  private final int offset;
  
  public DefaultBlock(Volume vol, BinBuffer buf, ByteBuffer next, int offset) {
    this.volume = Objects.requireNonNull(vol);
    this.buffer = Objects.requireNonNull(buf);
    this.nextbuf = Objects.requireNonNull(next);
    this.offset = offset;
  }
  
  @Override
  public int offset() {
    return offset;
  }

  @Override
  public int size() {
    return nextbuf.getInt(0);
  }

  @Override
  public BinBuffer buffer() {
    return buffer;
  }

  @Override
  public boolean hasNext() {
    return nextbuf.getInt(Integer.BYTES) >= 0;
  }

  @Override
  public Block next() {
    int noffset = nextbuf.getInt(Integer.BYTES);
    if(noffset < 0) return null;
    return volume.get(noffset);
  }

  @Override
  public Volume volume() {
    return volume;
  }

  @Override
  public Block setNext(Block blk) {
    nextbuf.putInt(Integer.BYTES, Objects.requireNonNull(blk).offset());
    return this;
  }
  
}
