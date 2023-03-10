/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.volume;

import com.jun0rr.binj.buffer.BinBuffer;
import com.jun0rr.binj.buffer.BufferAllocator;
import com.jun0rr.binj.buffer.BufferAllocator.DirectAllocator;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

/**
 *
 * @author Juno
 */
public class OffsetBinBuffer implements BinBuffer {
  
  protected final List<OffsetBuffer> buffers;
  
  protected final OffsetBufferAllocator malloc;
  
  protected int mark;
  
  public OffsetBinBuffer(OffsetBufferAllocator ba) {
    this.malloc = Objects.requireNonNull(ba);
    this.buffers = new ArrayList<>();
  }
  
  public OffsetBinBuffer(OffsetBufferAllocator ba, List<OffsetBuffer> buffers) {
    if(buffers == null || buffers.isEmpty()) {
      throw new IllegalArgumentException("Bad null/empty buffers List");
    }
    this.malloc = Objects.requireNonNull(ba);
    this.buffers = new ArrayList<>(buffers);
  }
  
  @Override
  public BufferAllocator allocator() {
    return new BufferAllocator() {
      @Override
      public int bufferSize() {
        return malloc.bufferSize();
      }
      @Override
      public ByteBuffer alloc(int size) {
        return malloc.alloc().buffer();
      }
    };
  }
  
  @Override
  public int capacity() {
    return buffers.stream().map(OffsetBuffer::buffer).mapToInt(ByteBuffer::capacity).sum();
  }
  
  @Override
  public void capacity(int cap) {
    while(cap > capacity()) {
      allocate();
    }
  }
  
  @Override
  public BinBuffer clear() {
    buffers.stream().map(OffsetBuffer::buffer).forEach(ByteBuffer::clear);
    return this;
  }

  @Override
  public BinBuffer compact() {
    ByteBuffer temp = DirectAllocator.class.isAssignableFrom(malloc.getClass()) 
        ? ByteBuffer.allocateDirect(remaining()) 
        : ByteBuffer.allocate(remaining());
    get(temp);
    clear();
    return put(temp.flip());
  }

  @Override
  public BinBuffer duplicate() {
    return new OffsetBinBuffer(malloc, buffers);
  }
  
  @Override
  public BinBuffer flip() {
    buffers.stream().map(OffsetBuffer::buffer).forEach(ByteBuffer::flip);
    return this;
  }
  
  protected void allocate() {
    buffers.add(malloc.alloc());
  }
  
  protected int _index() {
    for(int i = 0; i < buffers.size(); i++) {
      if(buffers.get(i).buffer().hasRemaining()) {
        return i;
      }
    }
    return buffers.size() -1;
  }
  
  @Override
  public byte get() {
    if(!hasRemaining()) {
      throw new BufferUnderflowException();
    }
    return buffers.get(_index()).buffer().get();
  }

  @Override
  public BinBuffer get(byte[] array) {
    if(remaining() < array.length) {
      throw new BufferUnderflowException();
    }
    int total = 0;
    while(total < array.length) {
      ByteBuffer b = buffers.get(_index()).buffer();
      int min = Math.min(array.length, b.remaining());
      min = Math.min(min, array.length - total);
      b.get(array, total, min);
      total += min;
    }
    return this;
  }

  @Override
  public BinBuffer get(byte[] array, int offset, int length) {
    if(remaining() < length) {
      throw new BufferUnderflowException();
    }
    int total = 0;
    while(total < length) {
      ByteBuffer b = buffers.get(_index()).buffer();
      int min = Math.min(length, b.remaining());
      min = Math.min(min, length - total);
      b.get(array, offset + total, min);
      total += min;
    }
    return this;
  }

  @Override
  public char getChar() {
    if(remaining() < Character.BYTES) {
      throw new BufferUnderflowException();
    }
    ByteBuffer b = buffers.get(_index()).buffer();
    if(b.remaining() < Character.BYTES) {
      b = ByteBuffer.allocate(Character.BYTES);
      get(b);
      b.flip();
    }
    return b.getChar();
  }

  @Override
  public double getDouble() {
    if(remaining() < Double.BYTES) {
      throw new BufferUnderflowException();
    }
    ByteBuffer b = buffers.get(_index()).buffer();
    if(b.remaining() < Double.BYTES) {
      b = ByteBuffer.allocate(Double.BYTES);
      get(b);
      b.flip();
    }
    return b.getDouble();
  }

  @Override
  public float getFloat() {
    if(remaining() < Float.BYTES) {
      throw new BufferUnderflowException();
    }
    ByteBuffer b = buffers.get(_index()).buffer();
    if(b.remaining() < Float.BYTES) {
      b = ByteBuffer.allocate(Float.BYTES);
      get(b);
      b.flip();
    }
    return b.getFloat();
  }

  @Override
  public int getInt() {
    if(remaining() < Integer.BYTES) {
      throw new BufferUnderflowException();
    }
    ByteBuffer b = buffers.get(_index()).buffer();
    if(b.remaining() < Integer.BYTES) {
      b = ByteBuffer.allocate(Integer.BYTES);
      get(b);
      b.flip();
    }
    return b.getInt();
  }

  @Override
  public long getLong() {
    if(remaining() < Long.BYTES) {
      throw new BufferUnderflowException();
    }
    ByteBuffer b = buffers.get(_index()).buffer();
    if(b.remaining() < Long.BYTES) {
      b = ByteBuffer.allocate(Long.BYTES);
      get(b);
      b.flip();
    }
    return b.getLong();
  }

  @Override
  public short getShort() {
    if(remaining() < Short.BYTES) {
      throw new BufferUnderflowException();
    }
    ByteBuffer b = buffers.get(_index()).buffer();
    if(b.remaining() < Short.BYTES) {
      b = ByteBuffer.allocate(Short.BYTES);
      get(b);
      b.flip();
    }
    return b.getShort();
  }
  
  @Override
  public String getString(Charset cs) {
    int len = getShort();
    if(remaining() < len) {
      throw new BufferUnderflowException();
    }
    ByteBuffer b = buffers.get(_index()).buffer();
    if(b.remaining() < len) {
      b = ByteBuffer.allocate(len);
      get(b);
      b.flip();
    }
    int lim = b.limit();
    b.limit(b.position() + len);
    String s = cs.decode(b).toString();
    b.limit(lim);
    return s;
  }

  @Override
  public BinBuffer get(ByteBuffer buf) {
    while(buf.hasRemaining() && hasRemaining()) {
      ByteBuffer b = buffers.get(_index()).buffer();
      int lim = b.limit();
      int min = Math.min(buf.remaining(), b.remaining());
      b.limit(b.position() + min);
      buf.put(b);
      b.limit(lim);
    }
    return this;
  }
  
  @Override
  public BinBuffer get(BinBuffer buf) {
    if(!hasRemaining()) {
      throw new BufferUnderflowException();
    }
    buffers.stream()
        .map(OffsetBuffer::buffer)
        .filter(ByteBuffer::hasRemaining)
        .forEach(buf::put);
    return this;
  }
  
  @Override
  public boolean hasRemaining() {
    return remaining() > 0;
  }

  @Override
  public int limit() {
    return buffers.stream().map(OffsetBuffer::buffer).mapToInt(ByteBuffer::limit).sum();
  }
  
  @Override
  public BinBuffer limit(int lim) {
    while(lim > capacity()) {
      allocate();
    }
    int i = 0;
    int _lim = lim;
    while(i < buffers.size()) {
      ByteBuffer b = buffers.get(i++).buffer();
      int l = Math.min(b.capacity(), _lim);
      b.limit(l);
      _lim -= l;
    }
    return this;
  }

  @Override
  public BinBuffer mark() {
    this.mark = position();
    return this;
  }

  @Override
  public int position() {
    return buffers.stream().map(OffsetBuffer::buffer).mapToInt(ByteBuffer::position).sum();
  }
  
  @Override
  public BinBuffer position(int pos) {
    while(pos > capacity()) {
      allocate();
    }
    int i = 0;
    int _pos = pos;
    while(i < buffers.size()) {
      ByteBuffer b = buffers.get(i++).buffer();
      int l = Math.min(b.limit(), _pos);
      b.position(l);
      _pos -= l;
    }
    return this;
  }
  
  @Override
  public BinBuffer put(byte b) {
    if(!hasRemaining()) {
      allocate();
    }
    buffers.get(_index()).buffer().put(b);
    return this;
  }

  @Override
  public BinBuffer put(byte[] array) {
    while(remaining() < array.length) {
      allocate();
    }
    int total = 0;
    while(total < array.length) {
      ByteBuffer b = buffers.get(_index()).buffer();
      int min = Math.min(array.length, b.remaining());
      min = Math.min(min, array.length - total);
      b.put(array, total, min);
      total += min;
    }
    return this;
  }

  @Override
  public BinBuffer put(byte[] array, int offset, int length) {
    while(remaining() < length) {
      allocate();
    }
    int total = 0;
    while(total < length) {
      ByteBuffer b = buffers.get(_index()).buffer();
      int min = Math.min(length, b.remaining());
      min = Math.min(min, length - total);
      b.put(array, total + offset, min);
      total += min;
    }
    return this;
  }

  @Override
  public BinBuffer putChar(char s) {
    while(remaining() < Character.BYTES) {
      allocate();
    }
    ByteBuffer b = buffers.get(_index()).buffer();
    if(b.remaining() >= Character.BYTES) {
      b.putChar(s);
    }
    else {
      b = ByteBuffer.allocate(Character.BYTES);
      b.putChar(s);
      b.flip();
      put(b);
    }
    return this;
  }
  
  @Override
  public BinBuffer putShort(short s) {
    while(remaining() < Short.BYTES) {
      allocate();
    }
    ByteBuffer b = buffers.get(_index()).buffer();
    if(b.remaining() >= Short.BYTES) {
      b.putShort(s);
    }
    else {
      b = ByteBuffer.allocate(Short.BYTES);
      b.putShort(s);
      b.flip();
      put(b);
    }
    return this;
  }
  
  @Override
  public BinBuffer putInt(int s) {
    while(remaining() < Integer.BYTES) {
      allocate();
    }
    ByteBuffer b = buffers.get(_index()).buffer();
    if(b.remaining() >= Integer.BYTES) {
      b.putInt(s);
    }
    else {
      b = ByteBuffer.allocate(Integer.BYTES);
      b.putInt(s);
      b.flip();
      put(b);
    }
    return this;
  }
  
  @Override
  public BinBuffer putLong(long s) {
    while(remaining() < Long.BYTES) {
      allocate();
    }
    ByteBuffer b = buffers.get(_index()).buffer();
    if(b.remaining() >= Long.BYTES) {
      b.putLong(s);
    }
    else {
      b = ByteBuffer.allocate(Long.BYTES);
      b.putLong(s);
      b.flip();
      put(b);
    }
    return this;
  }
  
  @Override
  public BinBuffer putFloat(float s) {
    while(remaining() < Float.BYTES) {
      allocate();
    }
    ByteBuffer b = buffers.get(_index()).buffer();
    if(b.remaining() >= Float.BYTES) {
      b.putFloat(s);
    }
    else {
      b = ByteBuffer.allocate(Float.BYTES);
      b.putFloat(s);
      b.flip();
      put(b);
    }
    return this;
  }
  
  @Override
  public BinBuffer putDouble(double s) {
    while(remaining() < Double.BYTES) {
      allocate();
    }
    ByteBuffer b = buffers.get(_index()).buffer();
    if(b.remaining() >= Double.BYTES) {
      b.putDouble(s);
    }
    else {
      b = ByteBuffer.allocate(Double.BYTES);
      b.putDouble(s);
      b.flip();
      put(b);
    }
    return this;
  }
  
  @Override
  public BinBuffer put(String str, Charset cs) {
    ByteBuffer bs = cs.encode(str);
    putShort((short)bs.remaining());
    put(bs);
    return this;
  }
  
  @Override
  public BinBuffer put(ByteBuffer buf) {
    while(remaining() < buf.remaining()) {
      allocate();
    }
    int total = 0;
    while(buf.hasRemaining()) {
      ByteBuffer b = buffers.get(_index()).buffer();
      int lim = buf.limit();
      int min = Math.min(buf.remaining(), b.remaining());
      buf.limit(buf.position() + min);
      b.put(buf);
      total += min;
      buf.limit(lim);
    }
    return this;
  }

  @Override
  public BinBuffer put(BinBuffer buf) {
    buf.get(this);
    return this;
  }
  
  @Override
  public int remaining() {
    return buffers.stream().map(OffsetBuffer::buffer).mapToInt(ByteBuffer::remaining).sum();
  }

  @Override
  public BinBuffer reset() {
    position(mark);
    return this;
  }

  @Override
  public BinBuffer rewind() {
    position(0);
    mark = 0;
    return this;
  }

  @Override
  public BinBuffer slice() {
    return new OffsetBinBuffer(malloc, buffers.stream()
        .filter(o->o.buffer().hasRemaining())
        .map(o->OffsetBuffer.of(o.offset(), o.buffer().slice()))
        .collect(Collectors.toList())
    );
  }
  
  @Override
  public byte[] hash(String algorithm) {
    try {
      MessageDigest md = MessageDigest.getInstance(algorithm);
      buffers.stream()
          .map(OffsetBuffer::buffer)
          .filter(ByteBuffer::hasRemaining)
          .forEach(md::update);
      return md.digest();
    }
    catch(NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
  
  @Override
  public long checksum() {
    CRC32 crc = new CRC32();
    buffers.stream()
        .map(OffsetBuffer::buffer)
        .filter(ByteBuffer::hasRemaining)
        .forEach(crc::update);
    return crc.getValue();
  }
  
  @Override
  public String contentString() {
    StringBuffer sb = new StringBuffer("[");
    while(hasRemaining()) {
      sb.append(get()).append(", ");
    }
    if(sb.length() > 1) {
      sb.delete(sb.length() - 2, sb.length());
    }
    return sb.append("]").toString();
  }
  
  @Override
  public String toString() {
    return "OffsetBinBuffer{" + "pos=" + position() + ", lim=" + limit() + ", buffers=" + buffers.size() + '}';
  }
  
}
