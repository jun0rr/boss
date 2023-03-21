/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.volume;

import com.jun0rr.binj.buffer.BinBuffer;
import com.jun0rr.binj.buffer.BufferAllocator;
import com.jun0rr.binj.buffer.DefaultBinBuffer;
import com.jun0rr.boss.Block;
import com.jun0rr.boss.Volume;
import com.jun0rr.boss.config.VolumeConfig;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author F6036477
 */
public class DefaultVolume implements Volume {
  
  protected final VolumeConfig config;
  
  protected final BufferAllocator malloc;
  
  protected final Map<Long,Cached<OffsetBuffer>> cache;
  
  protected final Queue<Long> freebufs;
  
  protected final AtomicLong woffset;
  
  protected final AtomicLong metaidx;
  
  public DefaultVolume(VolumeConfig cfg) {
    this.config = Objects.requireNonNull(cfg);
    this.malloc = config.buffer().bufferAllocator();
    this.cache = new ConcurrentSkipListMap<>();
    this.freebufs = new ConcurrentLinkedQueue<>();
    this.woffset = new AtomicLong(config.buffer().size());
    this.metaidx = new AtomicLong(-1L);
  }
  
  @Override
  public VolumeConfig config() {
    return config;
  }
  
  protected Cached<OffsetBuffer> putCached(OffsetBuffer o) {
    if(cache.values().stream()
        .map(Cached::content)
        .mapToLong(b->b.buffer().capacity())
        .sum() >= config.buffer().maxCacheSize()
    ) {
      cache.entrySet().stream()
          .min((a,b)->a.getValue().compareTo(b.getValue()))
          .ifPresent(e->cache.remove(e.getKey()));
    }
    Cached<OffsetBuffer> cached = Cached.of(o);
    cache.put(o.offset(), cached);
    return cached;
  }

  protected OffsetBuffer getOffsetBuffer(long offset) {
    if(offset < 0) {
      throw new IllegalArgumentException("Bad offset: " + offset);
    }
    Cached<OffsetBuffer> ob = cache.get(offset);
    if(ob == null) {
      ByteBuffer bb = malloc.alloc();
      ob = putCached(OffsetBuffer.of(offset, bb));
    }
    return ob.content();
  }
  
  protected long getNextOffset(OffsetBuffer buf) {
    long next = buf.buffer().position(0).getLong();
    buf.buffer().clear();
    return next;
  }

  protected void setNextOffset(OffsetBuffer buf, long next) {
    buf.buffer().position(0).putLong(next);
    buf.buffer().clear();
  }

  protected OffsetBuffer allocateOffsetBuffer() {
    long offset = !freebufs.isEmpty() 
        ? freebufs.poll() 
        : woffset.getAndAdd(config.buffer().size());
    OffsetBuffer ob = OffsetBuffer.of(offset, malloc.alloc());
    setNextOffset(ob, -1L);
    return putCached(ob).content();
  }
  
  protected OffsetBuffer last(OffsetBuffer buf) {
    OffsetBuffer last = buf;
    long nextOffset = getNextOffset(last);
    while(nextOffset >= 0 && nextOffset != buf.offset()) {
      last = getOffsetBuffer(nextOffset);
      nextOffset = getNextOffset(last);
    }
    return last;
  }
  
  protected ByteBuffer slicedBuffer(OffsetBuffer buf) {
    return buf.buffer().position(Long.BYTES).slice();
  }
  
  protected OffsetBuffer allocateNextBuffer(OffsetBuffer buf) {
    OffsetBuffer ob = allocateOffsetBuffer();
    setNextOffset(last(buf), ob.offset());
    return ob;
  }
  
  protected BufferAllocator innerAllocator(OffsetBuffer buf) {
    return new BufferAllocator() {
      @Override
      public ByteBuffer alloc() {
        return slicedBuffer(allocateNextBuffer(buf));
      }
      @Override
      public int bufferSize() {
        return config.buffer().size();
      }
    };
  }
  
  @Override
  public Block allocate() {
    OffsetBuffer buf = allocateOffsetBuffer();
    BinBuffer bb = new DefaultBinBuffer(innerAllocator(buf), List.of(slicedBuffer(buf)));
    return Block.of(this, bb, buf.offset());
  }

  @Override
  public Block allocate(int size) {
    OffsetBuffer first = allocateOffsetBuffer();
    int total = first.buffer().capacity() - Long.BYTES;
    List<ByteBuffer> bufs = new LinkedList<>();
    bufs.add(slicedBuffer(first));
    OffsetBuffer buf = first;
    while(total < size) {
      OffsetBuffer next = allocateOffsetBuffer();
      setNextOffset(buf, next.offset());
      total += next.buffer().capacity() - Long.BYTES;
      bufs.add(slicedBuffer(next));
      buf = next;
    }
    BinBuffer bb = new DefaultBinBuffer(innerAllocator(first), bufs);
    return Block.of(this, bb, first.offset());
  }

  @Override
  public Volume release(Block blk) {
    return release(blk.offset());
  }

  @Override
  public Volume release(long offset) {
    metaidx.compareAndSet(offset, -1L);
    long nextOffset = offset;
    while(nextOffset > 0) {
      OffsetBuffer buf = getOffsetBuffer(nextOffset);
      cache.remove(nextOffset);
      freebufs.add(nextOffset);
      nextOffset = getNextOffset(buf);
    }
    return this;
  }
  
  @Override
  public Block get(long offset) {
    if(offset < 0 || offset >= woffset.get()) {
      throw new IllegalArgumentException("Bad offset: " + offset);
    }
    List<ByteBuffer> bufs = new LinkedList<>();
    OffsetBuffer buf = null;
    long nextOffset = offset;
    do {
      buf = getOffsetBuffer(nextOffset);
      nextOffset = getNextOffset(buf);
      bufs.add(slicedBuffer(buf));
    }
    while(nextOffset > 0 && nextOffset != offset);
    BinBuffer bb = new DefaultBinBuffer(innerAllocator(buf), bufs);
    return Block.of(this, bb, offset);
  }
  
  @Override
  public Block metadata() {
    Block b;
    if(metaidx.get() > 0) {
      b = get(metaidx.get());
    }
    else {
      b = allocate();
      metaidx.set(b.offset());
    }
    return b;
  }

  @Override public void close() {}

  @Override
  public Volume commit(Block b) {
    return this;
  }

  @Override
  public boolean isLoaded() {
    return false;
  }
  
}
