/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.volume;

import com.jun0rr.binj.buffer.BinBuffer;
import com.jun0rr.binj.buffer.BufferAllocator;
import com.jun0rr.boss.Block;
import com.jun0rr.boss.Volume;
import com.jun0rr.boss.config.VolumeConfig;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
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
    loadFreebufs();
  }
  
  private void loadFreebufs() {
    if(cache.isEmpty()) return;
    Block b = get(0);
    if(METADATA_ID == b.buffer().get()) {
      woffset.set(b.buffer().getInt());
      metaidx.set(b.buffer().getInt());
      int size = b.buffer().getShort();
      IntStream.range(0, size).forEach(i->freebufs.add(getOffsetBuffer(b.buffer().getInt())));
    }
  }

  @Override
  public VolumeConfig config() {
    return config;
  }
  
  private OffsetBuffer allocateOffsetBuffer() {
    long offset = !freebufs.isEmpty() ? freebufs.poll() : woffset.getAndAdd(config.buffer().size());
    OffsetBuffer ob = OffsetBuffer.of(offset, malloc.alloc());
    putCache(ob);
    return ob;
  }
  
  private OffsetBuffer last(OffsetBuffer buf) {
    OffsetBuffer last = buf;
    byte[] bs = new byte[4];
    last.buffer().position(0).get(bs);
    int nos = ByteBuffer.wrap(bs).getInt();
    //System.out.printf("* DefaultVolume.last1(%s): bs=%s, nos=%d%n", buf, Arrays.toString(bs), nos);
    while(nos >= 0 && nos != buf.offset()) {
      last = ;
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
  
  private OffsetBufferAllocator offsetAllocator() {
    return new OffsetBufferAllocator() {
      @Override
      public OffsetBuffer alloc() {
        return allocateOffsetBuffer();
      }
      @Override
      public int bufferSize() {
        return config.buffer().size();
      }
    };
  }
  
  private void putCache(OffsetBuffer o) {
    if(cache.values().stream()
        .map(Cached::content)
        .mapToLong(b->b.buffer().capacity())
        .sum() >= config.buffer().maxCacheSize()
    ) {
      cache.entrySet().stream()
          .min((a,b)->a.getValue().compareTo(b.getValue()))
          .ifPresent(e->cache.remove(e.getKey()));
    }
    cache.put(o.offset(), new Cached(o));
  }

  @Override
  public Block allocate() {
    OffsetBuffer buf = allocateOffsetBuffer();
    BinBuffer bb = new OffsetBinBuffer2(offsetAllocator(), List.of(buf));
    return Block.of(this, bb, buf.offset());
  }

  @Override
  public Block allocate(int size) {
    OffsetBuffer buf = allocateOffsetBuffer();
    int total = buf.buffer().capacity();
    List<OffsetBuffer> bufs = new LinkedList<>();
    bufs.add(buf);
    while(total < size) {
      OffsetBuffer ob = allocateOffsetBuffer();
      total += ob.buffer().capacity();
      bufs.add(ob);
    }
    BinBuffer bb = new OffsetBinBuffer2(offsetAllocator(), bufs);
    return Block.of(this, bb, buf.offset());
  }

  @Override
  public Volume release(Block blk) {
    
  }

  @Override
  public Volume release(int offset) {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  @Override
  public Block get(int offset) {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  @Override
  public Block metadata() {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  @Override
  public void close() {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  @Override
  public boolean isLoaded() {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  @Override
  public Volume commit(Block b) {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

}
