/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.volume;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class OffsetBuffer1 {
  
  private final ByteBuffer buffer;
  
  private final int offset;
  
  
  public OffsetBuffer1(int offset, ByteBuffer buf) {
    this.buffer = Objects.requireNonNull(buf);
    this.offset = offset;
  }
  
  public ByteBuffer buffer() {
    return buffer;
  }
  
  public int offset() {
    return offset;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 79 * hash + Objects.hashCode(this.buffer);
    hash = 79 * hash + this.offset;
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
    final OffsetBuffer1 other = (OffsetBuffer1) obj;
    if (this.offset != other.offset) {
      return false;
    }
    return Objects.equals(this.buffer, other.buffer);
  }

  @Override
  public String toString() {
    return "OffsetBuffer{" + "offset=" + offset + ", buffer=" + buffer + '}';
  }
  
}
