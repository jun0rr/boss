/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.def;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class FreeBuffer {
  
  private final int offset;
  
  private final ByteBuffer buffer;
  
  
  public FreeBuffer(int offset, ByteBuffer buf) {
    this.offset = offset;
    this.buffer = Objects.requireNonNull(buf);
  }
  
  public int offset() {
    return offset;
  }
  
  public ByteBuffer buffer() {
    return buffer;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 61 * hash + this.offset;
    hash = 61 * hash + Objects.hashCode(this.buffer);
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
    final FreeBuffer other = (FreeBuffer) obj;
    if (this.offset != other.offset) {
      return false;
    }
    return Objects.equals(this.buffer, other.buffer);
  }

  @Override
  public String toString() {
    return "FreeBuffer{" + "offset=" + offset + ", buffer=" + buffer + '}';
  }
  
}
