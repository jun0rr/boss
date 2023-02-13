/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.volume;

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
  
  private final int offset;
  
  public DefaultBlock(Volume vol, BinBuffer buf, int offset) {
    this.volume = Objects.requireNonNull(vol);
    this.buffer = Objects.requireNonNull(buf);
    if(offset < 0) {
      throw new IllegalArgumentException(String.format("Bad offset (%d)", offset));
    }
    this.offset = offset;
  }

  @Override
  public BinBuffer buffer() {
    return buffer;
  }

  @Override
  public Volume volume() {
    return volume;
  }

  @Override
  public int index() {
    return offset;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 47 * hash + Objects.hashCode(this.volume);
    hash = 47 * hash + this.offset;
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
    if (this.offset != other.offset) {
      return false;
    }
    return Objects.equals(this.volume, other.volume);
  }

  @Override
  public String toString() {
    return "Block{" + "volumeID=" + volume.id() + ", offset=" + offset + ", buffer=" + buffer + '}';
  }
  
}
