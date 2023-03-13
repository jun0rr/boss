/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.volume;

import com.jun0rr.binj.buffer.BinBuffer;
import com.jun0rr.binj.buffer.BufferAllocator;
import com.jun0rr.binj.buffer.DefaultBinBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Juno
 */
public class OffsetBinBuffer2 implements BinBuffer {
  
  private final List<OffsetBuffer> offsets;
  
  private final OffsetBufferAllocator malloc;
  
  private final BinBuffer buffer;
  
  private OffsetBinBuffer2(OffsetBufferAllocator ba, List<OffsetBuffer> offsets, BinBuffer buffer) {
    this.malloc = Objects.requireNonNull(ba);
    this.buffer = Objects.requireNonNull(buffer);
    this.offsets = new ArrayList<>(offsets);
  }
  
  public OffsetBinBuffer2(OffsetBufferAllocator ba) {
    this(ba, Collections.EMPTY_LIST);
  }
  
  public OffsetBinBuffer2(OffsetBufferAllocator ba, List<OffsetBuffer> buffers) {
    this.malloc = Objects.requireNonNull(ba);
    this.offsets = new ArrayList<>(buffers);
    this.buffer = new DefaultBinBuffer(allocator(), buffers.stream()
        .map(OffsetBuffer::buffer)
        .toList()
    );
  }
  
  @Override
  public BufferAllocator allocator() {
    return new BufferAllocator() {
      @Override
      public int bufferSize() {
        return malloc.bufferSize();
      }
      @Override
      public ByteBuffer alloc() {
        OffsetBuffer buf = malloc.alloc();
        offsets.add(buf);
        return buf.buffer();
      }
    };
  }
  
  @Override
  public List<ByteBuffer> byteBuffers() {
    return buffer.byteBuffers();
  }
  
  public List<OffsetBuffer> offsetBuffers() {
    return List.copyOf(offsets);
  }
  
  @Override
  public int capacity() {
    return buffer.capacity();
  }
  
  @Override
  public void capacity(int cap) {
    buffer.capacity(cap);
  }
  
  @Override
  public BinBuffer clear() {
    buffer.clear();
    return this;
  }

  @Override
  public BinBuffer compact() {
    buffer.compact();
    return this;
  }

  @Override
  public BinBuffer duplicate() {
    return new OffsetBinBuffer2(malloc, offsets, buffer.duplicate());
  }
  
  @Override
  public BinBuffer flip() {
    buffer.flip();
    return this;
  }
  
  @Override
  public byte get() {
    return buffer.get();
  }

  @Override
  public BinBuffer get(byte[] array) {
    buffer.get(array);
    return this;
  }

  @Override
  public BinBuffer get(byte[] array, int offset, int length) {
    buffer.get(array, offset, length);
    return this;
  }

  @Override
  public char getChar() {
    return buffer.getChar();
  }

  @Override
  public double getDouble() {
    return buffer.getDouble();
  }

  @Override
  public float getFloat() {
    return buffer.getFloat();
  }

  @Override
  public int getInt() {
    return buffer.getInt();
  }

  @Override
  public long getLong() {
    return buffer.getLong();
  }

  @Override
  public short getShort() {
    return buffer.getShort();
  }
  
  @Override
  public String getString(Charset cs) {
    return buffer.getString(cs);
  }

  @Override
  public BinBuffer get(ByteBuffer buf) {
    buffer.get(buf);
    return this;
  }
  
  @Override
  public BinBuffer get(BinBuffer buf) {
    buffer.get(buf);
    return this;
  }
  
  @Override
  public boolean hasRemaining() {
    return buffer.hasRemaining();
  }

  @Override
  public int limit() {
    return buffer.limit();
  }
  
  @Override
  public BinBuffer limit(int lim) {
    buffer.limit(lim);
    return this;
  }

  @Override
  public BinBuffer mark() {
    buffer.mark();
    return this;
  }

  @Override
  public int position() {
    return buffer.position();
  }
  
  @Override
  public BinBuffer position(int pos) {
    buffer.position(pos);
    return this;
  }
  
  @Override
  public BinBuffer put(byte b) {
    buffer.put(b);
    return this;
  }

  @Override
  public BinBuffer put(byte[] array) {
    buffer.put(array);
    return this;
  }

  @Override
  public BinBuffer put(byte[] array, int offset, int length) {
    buffer.put(array, offset, length);
    return this;
  }

  @Override
  public BinBuffer putChar(char s) {
    buffer.putChar(s);
    return this;
  }
  
  @Override
  public BinBuffer putShort(short s) {
    buffer.putShort(s);
    return this;
  }
  
  @Override
  public BinBuffer putInt(int s) {
    buffer.putInt(s);
    return this;
  }
  
  @Override
  public BinBuffer putLong(long s) {
    buffer.putLong(s);
    return this;
  }
  
  @Override
  public BinBuffer putFloat(float s) {
    buffer.putFloat(s);
    return this;
  }
  
  @Override
  public BinBuffer putDouble(double s) {
    buffer.putDouble(s);
    return this;
  }
  
  @Override
  public BinBuffer put(String str, Charset cs) {
    buffer.put(str, cs);
    return this;
  }
  
  @Override
  public BinBuffer put(ByteBuffer buf) {
    buffer.put(buf);
    return this;
  }

  @Override
  public BinBuffer put(BinBuffer buf) {
    buffer.put(buf);
    return this;
  }
  
  @Override
  public int remaining() {
    return buffer.remaining();
  }

  @Override
  public BinBuffer reset() {
    buffer.reset();
    return this;
  }

  @Override
  public BinBuffer rewind() {
    buffer.rewind();
    return this;
  }

  @Override
  public OffsetBinBuffer2 slice() {
    List<OffsetBuffer> sliced = offsets.stream()
        .filter(o->o.buffer().hasRemaining())
        .map(o->new OffsetBuffer(o.offset(), o.buffer().slice()))
        .toList();
    return new OffsetBinBuffer2(malloc, sliced, new DefaultBinBuffer(
        allocator(), sliced.stream().map(OffsetBuffer::buffer).toList())
    );
  }
  
  @Override
  public byte[] hash(String algorithm) {
    return buffer.hash(algorithm);
  }
  
  @Override
  public long checksum() {
    return buffer.checksum();
  }
  
  @Override
  public String contentString() {
    return buffer.contentString();
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 53 * hash + Objects.hashCode(this.offsets);
    hash = 53 * hash + Objects.hashCode(this.malloc);
    hash = 53 * hash + Objects.hashCode(this.buffer);
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
    final OffsetBinBuffer2 other = (OffsetBinBuffer2) obj;
    if (!Objects.equals(this.offsets, other.offsets)) {
      return false;
    }
    if (!Objects.equals(this.malloc, other.malloc)) {
      return false;
    }
    return Objects.equals(this.buffer, other.buffer);
  }

  @Override
  public String toString() {
    return "OffsetBinBuffer2{" + "offsets=" + offsets + ", malloc=" + malloc + ", buffer=" + buffer + '}';
  }
  
}
