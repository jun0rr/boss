/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.volume;

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
import com.jun0rr.boss.Block;
import java.util.stream.IntStream;

/**
 *
 * @author F6036477
 */
public class DefaultVolume implements Volume {
  
  public static final byte METADATA_ID = 55;
  
  private final String id;
  
  private final int blockSize;
  
  private final BufferAllocator malloc;
  
  private final List<ByteBuffer> buffers;
  
  private final Queue<OffsetBuffer> freebufs;
  
  private final AtomicInteger woffset;
  
  public DefaultVolume(String id, int blockSize, List<ByteBuffer> bufs, BufferAllocator ba) {
    this.id = Objects.requireNonNull(id);
    this.malloc = Objects.requireNonNull(ba);
    this.blockSize = blockSize;
    this.freebufs = new ConcurrentLinkedQueue<>();
    this.buffers = new CopyOnWriteArrayList<>();
    buffers.addAll(bufs);
    this.woffset = new AtomicInteger(blockSize);
    loadFreebufs();
  }
  
  public DefaultVolume(String id, int blockSize, BufferAllocator ba) {
    this.id = Objects.requireNonNull(id);
    this.malloc = Objects.requireNonNull(ba);
    this.blockSize = blockSize;
    this.freebufs = new ConcurrentLinkedQueue<>();
    this.buffers = new CopyOnWriteArrayList<>();
    this.woffset = new AtomicInteger(blockSize);
  }
  
  private void loadFreebufs() {
    if(buffers.isEmpty()) return;
    Block b = get(0);
    if(METADATA_ID == b.buffer().get()) {
      woffset.set(b.buffer().getInt());
      int size = b.buffer().getShort();
      IntStream.range(0, size).forEach(i->freebufs.add(getOffsetBuffer(b.buffer().getInt())));
    }
  }
  
  @Override
  public String id() {
    return id;
  }

  @Override
  public int blockSize() {
    return blockSize;
  }
  
  public int capacity() {
    return buffers.stream().mapToInt(ByteBuffer::capacity).sum();
  }
  
  private OffsetBuffer getOffsetBuffer(int offset) {
    if(offset < 0) return null;
    int idx = offset / malloc.bufferSize();
    int pos = offset - idx * malloc.bufferSize();
    ByteBuffer bb = buffers.get(idx).clear().position(pos).limit(pos + blockSize);
    return new OffsetBuffer(offset, bb.slice());
  }
  
  private OffsetBuffer allocateFreeBuffer() {
    OffsetBuffer buf = null;
    if(!freebufs.isEmpty()) {
      buf = freebufs.poll();
    }
    else {
      if((woffset.get() + blockSize) > capacity()) {
        buffers.add(malloc.alloc());
      }
      buf = getOffsetBuffer(woffset.getAndAdd(blockSize));
    }
    buf.buffer().clear().putInt(-1).clear();
    return buf;
  }
  
  private OffsetBuffer last(OffsetBuffer buf) {
    OffsetBuffer last = buf;
    int nos = last.buffer().position(0).getInt();
    while(nos >= 0) {
      last = getOffsetBuffer(nos);
      nos = last.buffer().position(0).getInt();
    }
    return last;
  }
  
  private ByteBuffer allocateSlice(OffsetBuffer buf) {
    OffsetBuffer last = last(buf);
    OffsetBuffer ob = allocateFreeBuffer();
    last.buffer().position(0).putInt(ob.offset());
    return ob.buffer().position(Integer.BYTES).slice();
  }
  
  @Override
  public Block allocate() {
    return allocate(blockSize);
  }

  @Override
  public Block allocate(int size) {
    OffsetBuffer buf = allocateFreeBuffer();
    BufferAllocator alloc = new DefaultBufferAllocator(blockSize - Integer.BYTES) {
      @Override
      public ByteBuffer alloc(int size) {
        return allocateSlice(buf);
      }
    };
    List<ByteBuffer> bufs = new ArrayList<>();
    bufs.add(buf.buffer().position(Integer.BYTES).slice());
    BinBuffer buffer = new DefaultBinBuffer(alloc, bufs);
    return new DefaultBlock(this, buffer, buf.offset());
  }

  @Override
  public Volume release(Block blk) {
    return release(blk.index());
  }

  @Override
  public Volume release(int offset) {
    int nos = offset;
    do {
      OffsetBuffer buf = getOffsetBuffer(nos);
      freebufs.add(buf);
      nos = buf.buffer().position(0).getInt();
      buf.buffer().position(0).putInt(-1);
    } while(nos >= 0);
    return this;
  }

  @Override
  public Block get(int offset) {
    OffsetBuffer buf = getOffsetBuffer(offset);
    BufferAllocator alloc = new DefaultBufferAllocator(blockSize - Integer.BYTES) {
      @Override
      public ByteBuffer alloc(int size) {
        return allocateSlice(buf);
      }
    };
    List<ByteBuffer> bufs = new ArrayList<>();
    OffsetBuffer last = buf;
    while(last != null) {
      bufs.add(last.buffer().position(Integer.BYTES).slice());
      int next = last.buffer().position(0).getInt();
      //System.out.println("Volume.get(): next=" + next);
      last = next != offset ? getOffsetBuffer(next) : null;
    }
    BinBuffer buffer = new DefaultBinBuffer(alloc, bufs);
    return new DefaultBlock(this, buffer, buf.offset());
  }

  @Override
  public void close() {
    int[] offsets = new int[freebufs.size()];
    AtomicInteger i = new AtomicInteger(0);
    freebufs.forEach(o->offsets[i.getAndIncrement()] = o.offset());
    Block b = get(0);
    release(b);
    b.buffer().put(METADATA_ID).putInt(woffset.get()).putShort((short)offsets.length);
    IntStream.of(offsets).forEach(b.buffer()::putInt);
    malloc.close();
  }
  
  @Override
  public int hashCode() {
    int hash = 5;
    hash = 17 * hash + Objects.hashCode(this.id);
    hash = 17 * hash + this.blockSize;
    hash = 17 * hash + Objects.hashCode(this.malloc);
    hash = 17 * hash + Objects.hashCode(this.woffset);
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
    final DefaultVolume other = (DefaultVolume) obj;
    if (this.blockSize != other.blockSize) {
      return false;
    }
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    if (!Objects.equals(this.malloc, other.malloc)) {
      return false;
    }
    return Objects.equals(this.woffset, other.woffset);
  }

  @Override
  public String toString() {
    return "Volume{" + "id=" + id + ", blockSize=" + blockSize + ", woffset=" + woffset + ", buffers=" + buffers.size() + ", freebufs=" + freebufs + '}';
  }

}
