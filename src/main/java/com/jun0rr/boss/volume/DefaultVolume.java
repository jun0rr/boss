/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.volume;

import com.jun0rr.binj.buffer.BinBuffer;
import com.jun0rr.binj.buffer.BufferAllocator;
import com.jun0rr.binj.buffer.DefaultBinBuffer;
import com.jun0rr.binj.buffer.DefaultBufferAllocator;
import com.jun0rr.boss.Volume;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import com.jun0rr.boss.Block;
import java.nio.MappedByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 *
 * @author F6036477
 */
public class DefaultVolume { /*implements Volume {
  
  public static final byte METADATA_ID = 55;
  
  private final String id;
  
  private final int blockSize;
  
  private final BufferAllocator malloc;
  
  private final List<ByteBuffer> buffers;
  
  private final Queue<OffsetBuffer1> freebufs;
  
  private final AtomicLong woffset;
  
  private final AtomicLong metaidx;
  
  private final boolean loaded;
  
  public DefaultVolume(String id, int blockSize, List<ByteBuffer> bufs, BufferAllocator ba) {
    this.id = Objects.requireNonNull(id);
    this.malloc = Objects.requireNonNull(ba);
    this.blockSize = blockSize;
    this.freebufs = new ConcurrentLinkedQueue<>();
    this.buffers = new CopyOnWriteArrayList<>(bufs);
    this.loaded = !buffers.isEmpty();
    this.woffset = new AtomicLong(blockSize);
    this.metaidx = new AtomicLong(-1);
    loadFreebufs();
  }
  
  public DefaultVolume(String id, int blockSize, BufferAllocator ba) {
    this.id = Objects.requireNonNull(id);
    this.malloc = Objects.requireNonNull(ba);
    this.blockSize = blockSize;
    this.freebufs = new ConcurrentLinkedQueue<>();
    this.buffers = new CopyOnWriteArrayList<>();
    this.woffset = new AtomicLong(blockSize);
    this.metaidx = new AtomicLong(-1);
    this.loaded = false;
  }
  
  private void loadFreebufs() {
    if(buffers.isEmpty()) return;
    Block b = get(0);
    if(METADATA_ID == b.buffer().get()) {
      woffset.set(b.buffer().getInt());
      metaidx.set(b.buffer().getInt());
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
  
  private OffsetBuffer1 getOffsetBuffer(int offset) {
    if(offset < 0) return null;
    int idx = offset / malloc.bufferSize();
    int pos = offset - idx * malloc.bufferSize();
    //System.out.printf("Volume.getOffsetBuffer(%d): idx=%d, pos=%s, buffers.size=%s, thread=%s%n", offset, idx, pos, buffers.size(), Thread.currentThread());
    System.out.printf("DefaultVolume.getOffsetBuffer(%d): idx=%d, pos=%s, buffer=%s%n", offset, idx, pos, buffers.get(idx));
    ByteBuffer bb = buffers.get(idx).clear().position(pos).limit(pos + blockSize);
    return new OffsetBuffer1(offset, bb.slice());
  }
  
  private void forceWrite(int offset) {
    if(offset < 0) return;
    int idx = offset / malloc.bufferSize();
    if(idx >= 0 && idx < buffers.size()) {
      //System.out.printf("DefaultVolume.forceWrite(%d): idx=%d, pos=%s, buffer=%s%n", offset, idx, pos, buffers.get(idx));
      ((MappedByteBuffer)buffers.get(idx)).force();
    }
  }
  
  private synchronized OffsetBuffer1 allocateFreeBuffer() {
    OffsetBuffer1 buf = null;
    if(!freebufs.isEmpty()) {
      buf = freebufs.poll();
    }
    else {
      if((woffset.get() + blockSize) > capacity()) {
        buffers.add(malloc.alloc());
      }
      buf = getOffsetBuffer(woffset.getAndAdd(blockSize));
    }
    System.out.printf("Volume.allocateFreeBuffer(): offset=%s, thread=%s, buf=%s%n", buf, Thread.currentThread(), buf.buffer());
    buf.buffer().clear().putInt(-1).clear();
    return buf;
  }
  
  private OffsetBuffer1 last(OffsetBuffer1 buf) {
    OffsetBuffer1 last = buf;
    byte[] bs = new byte[4];
    last.buffer().position(0).get(bs);
    int nos = ByteBuffer.wrap(bs).getInt();
    System.out.printf("* DefaultVolume.last1(%s): bs=%s, nos=%d%n", buf, Arrays.toString(bs), nos);
    while(nos >= 0 && nos != buf.offset()) {
      last = getOffsetBuffer(nos);
      //System.out.printf("* DefaultVolume.last2(%s): nos=%d%n", buf, nos);
      nos = last.buffer().position(0).getInt();
    }
    System.out.printf("* DefaultVolume.last2(%s): last=%s%n", buf, last);
    return last;
  }
  
  private ByteBuffer allocateSlice(OffsetBuffer1 buf) {
    OffsetBuffer1 last = last(buf);
    OffsetBuffer1 ob = allocateFreeBuffer();
    //System.out.printf("* DefaultVolume.allocateSlice1(%s): last=%s, ob=%s%n", buf, last, ob);
    last.buffer().position(0).putInt(ob.offset());
    System.out.printf("* DefaultVolume.allocateSlice2(%s): (%d)->(%d)%n", buf, last.offset(), last.buffer().position(0).getInt());
    //forceWrite(last.offset());
    return ob.buffer().position(Integer.BYTES).slice();
  }
  
  @Override
  public Block allocate() {
    return allocate(blockSize);
  }

  @Override
  public Block allocate(int size) {
    OffsetBuffer1 buf = allocateFreeBuffer();
    BufferAllocator alloc = new DefaultBufferAllocator(blockSize - Integer.BYTES) {
      @Override
      public ByteBuffer alloc() {
        return allocateSlice(buf);
      }
    };
    List<ByteBuffer> bufs = new ArrayList<>();
    bufs.add(buf.buffer().position(Integer.BYTES).slice());
    BinBuffer buffer = new DefaultBinBuffer(alloc, bufs);
    Block b = Block.of(this, buffer, buf.offset());
    //System.out.printf("Volume.allocate( %d ): block=%s, thread=%s%n", size, b, Thread.currentThread());
    return b;
  }

  @Override
  public Volume release(Block blk) {
    return release(blk.offset());
  }

  @Override
  public Volume release(long offset) {
    System.out.printf("DefaultVolume.release(%d)%n", offset);
    metaidx.compareAndSet(offset, -1);
    long nos = offset;
    do {
      OffsetBuffer1 buf = getOffsetBuffer(nos);
      nos = buf.buffer().position(0).getInt();
      buf.buffer().position(0).putInt(-1);
      if(buf.offset() > 0 && !freebufs.contains(buf)) {
        freebufs.add(buf);
      }
    } while(nos > 0 && nos != offset);
    return this;
  }

  @Override
  public Block get(int offset) {
    OffsetBuffer1 buf = getOffsetBuffer(offset);
    BufferAllocator alloc = new DefaultBufferAllocator(blockSize - Integer.BYTES) {
      @Override
      public ByteBuffer alloc(int size) {
        return allocateSlice(buf);
      }
    };
    OffsetBuffer1 last = last(buf);
    System.out.printf("* DefaultVolume.get(%d): buf=%s, last=%s%n", offset, buf, last);
    List<ByteBuffer> bufs = new ArrayList<>();
    last = buf;
    while(last != null) {
      bufs.add(last.buffer().position(Integer.BYTES).slice());
      int next = last.buffer().position(0).getInt();
      last = next != offset && next != last.offset() ? getOffsetBuffer(next) : null;
    }
    BinBuffer buffer = new DefaultBinBuffer(alloc, bufs);
    return new DefaultBlock(this, buffer, buf.offset());
  }
  
  @Override
  public Block metadata() {
    if(metaidx.get() < 0) {
      Block b = allocate();
      metaidx.set(b.offset());
      return b;
    }
    else {
      return get(metaidx.get());
    }
  }

  @Override
  public void close() {
    int[] offsets = new int[freebufs.size()];
    AtomicInteger i = new AtomicInteger(0);
    freebufs.forEach(o->offsets[i.getAndIncrement()] = o.offset());
    release(0);
    Block b = get(0);
    b.buffer().put(METADATA_ID)
        .putInt(woffset.get())
        .putInt(metaidx.get())
        .putShort((short)offsets.length);
    IntStream.of(offsets)
        //.peek(j->System.out.printf("Volume.close(): freoffset=%d%n", j))
        .forEach(b.buffer()::putInt);
    malloc.close();
  }
  
  @Override
  public boolean isLoaded() {
    return loaded;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 43 * hash + Objects.hashCode(this.id);
    hash = 43 * hash + this.blockSize;
    hash = 43 * hash + Objects.hashCode(this.malloc);
    hash = 43 * hash + Objects.hashCode(this.woffset);
    hash = 43 * hash + (this.loaded ? 1 : 0);
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
    if (this.loaded != other.loaded) {
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
    return "Volume{" + "id=" + id + ", blockSize=" + blockSize + ", buffers=" + buffers.size() + ", freebufs=" + freebufs + ", woffset=" + woffset + ", loaded=" + loaded + '}';
  }
  */
}
