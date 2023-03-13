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
import com.jun0rr.unchecked.Unchecked;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 *
 * @author F6036477
 */
public class FileVolume implements Volume {
  
  public static final byte METADATA_ID = 55;
  
  private final VolumeConfig config;
  
  private final BufferAllocator malloc;
  
  private final FileChannel channel;
  
  private final Map<Long,Cached<OffsetBuffer>> cache;
  
  private final Queue<Long> freebufs;
  
  private final AtomicLong woffset;
  
  private final AtomicLong metaidx;
  
  private final boolean loaded;
  
  public FileVolume(VolumeConfig cfg) {
    this.config = Objects.requireNonNull(cfg);
    this.malloc = config.buffer().bufferAllocator();
    this.cache = new ConcurrentSkipListMap<>();
    this.freebufs = new ConcurrentLinkedQueue<>();
    this.woffset = new AtomicLong(config.buffer().size());
    this.metaidx = new AtomicLong(-1L);
    try {
      this.loaded = Files.exists(config.store().path());
      this.channel = FileChannel.open(config.store().path(), 
          StandardOpenOption.CREATE, 
          StandardOpenOption.READ,
          StandardOpenOption.WRITE,
          StandardOpenOption.DSYNC
      );
    }
    catch(IOException e) {
      throw new VolumeException(e);
    }
    loadMetadata();
  }
  
  private void loadMetadata() {
    Block b = get(0);
    if(METADATA_ID == b.buffer().get()) {
      woffset.set(b.buffer().getLong());
      metaidx.set(b.buffer().getLong());
      int size = b.buffer().getInt();
      IntStream.range(0, size)
          .forEach(i->freebufs.add(b.buffer().getLong()));
    }
  }

  @Override
  public VolumeConfig config() {
    return config;
  }
  
  private Cached<OffsetBuffer> putCached(OffsetBuffer o) {
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

  private OffsetBuffer getOffsetBuffer(long offset) {
    if(offset < 0 || offset > Unchecked.call(()->channel.size())) {
      throw new IllegalArgumentException("Bad offset: " + offset);
    }
    Cached<OffsetBuffer> ob = cache.get(offset);
    if(ob == null) {
      ByteBuffer bb = malloc.alloc();
      Unchecked.call(()->channel.read(bb, offset));
      ob = putCached(OffsetBuffer.of(offset, bb));
    }
    return ob.content();
  }
  
  private long getNextOffset(OffsetBuffer buf) {
    long next = buf.buffer().position(0).getLong();
    buf.buffer().clear();
    return next;
  }

  private void setNextOffset(OffsetBuffer buf, long next) {
    buf.buffer().position(0).putLong(next);
    buf.buffer().clear();
  }

  private OffsetBuffer allocateOffsetBuffer() {
    long offset = !freebufs.isEmpty() 
        ? freebufs.poll() 
        : woffset.getAndAdd(config.buffer().size());
    OffsetBuffer ob = OffsetBuffer.of(offset, malloc.alloc());
    setNextOffset(ob, -1L);
    return putCached(ob).content();
  }
  
  private OffsetBuffer last(OffsetBuffer buf) {
    OffsetBuffer last = buf;
    long nextOffset = getNextOffset(last);
    while(nextOffset >= 0 && nextOffset != buf.offset()) {
      last = getOffsetBuffer(nextOffset);
      nextOffset = getNextOffset(last);
    }
    return last;
  }
  
  private ByteBuffer slicedBuffer(OffsetBuffer buf) {
    return buf.buffer().position(Long.BYTES).slice();
  }
  
  private OffsetBuffer allocateNextBuffer(OffsetBuffer buf) {
    OffsetBuffer ob = allocateOffsetBuffer();
    setNextOffset(last(buf), ob.offset());
    return ob;
  }
  
  private BufferAllocator innerAllocator(OffsetBuffer buf) {
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
    OffsetBuffer buf = allocateOffsetBuffer();
    int total = buf.buffer().capacity() - Long.BYTES;
    List<ByteBuffer> bufs = new LinkedList<>();
    bufs.add(slicedBuffer(buf));
    while(total < size) {
      OffsetBuffer ob = allocateNextBuffer(buf);
      total += ob.buffer().capacity() - Long.BYTES;
      bufs.add(slicedBuffer(ob));
    }
    BinBuffer bb = new DefaultBinBuffer(innerAllocator(buf), bufs);
    return Block.of(this, bb, buf.offset());
  }

  public Async<Block> allocAsync() {
    Async<Block> a = new Async();
    ForkJoinPool.commonPool().submit(a.exec(()->allocate()));
    return a;
  }

  public Async<Block> allocAsync(int size) {
    Async<Block> a = new Async();
    ForkJoinPool.commonPool().submit(a.exec(()->allocate(size)));
    return a;
  }

  @Override
  public Volume release(Block blk) {
    return release(blk.offset());
  }

  @Override
  public Volume release(long offset) {
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
    if(offset < 0 || offset > Unchecked.call(()->channel.size())) {
      throw new IllegalArgumentException("Bad offset: " + offset);
    }
    List<ByteBuffer> bufs = new LinkedList<>();
    OffsetBuffer buf = null;
    long nextOffset = offset;
    while(nextOffset >= 0) {
      buf = getOffsetBuffer(nextOffset);
      nextOffset = getNextOffset(buf);
      bufs.add(slicedBuffer(buf));
    }
    BinBuffer bb = new DefaultBinBuffer(innerAllocator(buf), bufs);
    return Block.of(this, bb, offset);
  }
  
  public Async<Block> getAsync(long offset) {
    Async<Block> a = new Async<>();
    ForkJoinPool.commonPool().submit(a.exec(()->get(offset)));
    return a;
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

  public Async<Block> metadataAsync() {
    Async<Block> a = new Async<>();
    ForkJoinPool.commonPool().submit(a.exec(()->metadata()));
    return a;
  }

  @Override
  public void close() {
    Block b = get(0);
    b.buffer().put(METADATA_ID);
    b.buffer().putLong(woffset.get());
    b.buffer().putLong(metaidx.get());
    b.buffer().putInt(freebufs.size());
    for(long offset : freebufs) {
      b.buffer().putLong(offset);
    }
    b.commit();
  }

  @Override
  public boolean isLoaded() {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  @Override
  public Volume commit(Block b) {
    long nextOffset = b.offset();
    while(nextOffset >= 0) {
      OffsetBuffer buf = getOffsetBuffer(nextOffset);
      Unchecked.call(()->channel.write(buf.buffer().clear(), buf.offset()));
      nextOffset = getNextOffset(buf);
    }
    return this;
  }
  
  public Async<Block> commitAsync(Block b) {
    Async<Block> a = new Async<>();
    ForkJoinPool.commonPool().submit(a.exec(()->{
      commit(b);
      return b;
    }));
    return a;
  }
  
}
