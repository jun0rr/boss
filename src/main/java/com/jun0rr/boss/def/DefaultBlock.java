/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.def;

import com.jun0rr.boss.Block;
import com.jun0rr.boss.Volume;
import com.jun0rr.jbom.buffer.BinBuffer;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class DefaultBlock implements Block {
  
  private final Volume volume;
  
  private final BinBuffer buffer;
  
  private final BinBuffer nextbuf;
  
  private final int offset;
  
  public DefaultBlock(Volume vol, BinBuffer buf, BinBuffer next, int offset) {
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
    return buffer.capacity();
  }

  @Override
  public BinBuffer buffer() {
    return buffer;
  }
  
  @Override
  public int nextOffset() {
    return nextbuf.position(0).getInt();
  }

  @Override
  public boolean hasNext() {
    return nextOffset() >= 0;
  }

  @Override
  public Block next() {
    int nof = nextOffset();
    if(nof < 0) return null;
    return volume.get(nof);
  }

  @Override
  public Volume volume() {
    return volume;
  }

  @Override
  public Block setNextOffset(int offset) {
    nextbuf.position(0).putInt(offset);
    return this;
  }

  @Override
  public Block lastBlock() {
    Block last = this;
    while(hasNext()) {
      last = next();
    }
    return last;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 67 * hash + Objects.hashCode(this.volume);
    hash = 67 * hash + this.offset();
    hash = 67 * hash + this.nextOffset();
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final DefaultBlock other = (DefaultBlock) obj;
    if (this.offset() != other.offset()) {
      return false;
    }
    if (this.nextOffset() != other.nextOffset()) {
      return false;
    }
    return Objects.equals(this.volume, other.volume);
  }

  @Override
  public String toString() {
    return "DefaultBlock{" + "volume=" + volume + ", buffer=" + buffer + ", offset=" + offset() + ", nextOffset=" + nextOffset() + '}';
  }
  
}
