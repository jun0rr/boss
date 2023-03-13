/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.volume;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author F6036477
 */
public record OffsetBuffer(long offset, ByteBuffer buffer) implements Comparable<OffsetBuffer> {
  
  public OffsetBuffer {
    if(offset < 0) throw new IllegalArgumentException("Bad negative offset: " + offset);
    if(buffer == null) throw new IllegalArgumentException("Bad null ByteBuffer");
  }

  @Override
  public int compareTo(OffsetBuffer o) {
    return Long.compare(offset, o.offset);
  }
  
  public OffsetBuffer with(ByteBuffer bb) {
    return of(offset, bb);
  }
  
  public OffsetBuffer with(long offset) {
    return of(offset, buffer);
  }
  
  private static final AtomicLong INC_OFFSET = new AtomicLong(1L);
  
  public static OffsetBuffer of(ByteBuffer buffer) {
    return new OffsetBuffer(INC_OFFSET.getAndIncrement(), buffer);
  }
  
  public static OffsetBuffer of(long offset, ByteBuffer buffer) {
    return new OffsetBuffer(offset, buffer);
  }
  
}
