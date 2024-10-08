/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.volume;

import com.jun0rr.boss.Block;
import com.jun0rr.boss.Volume;
import com.jun0rr.boss.config.VolumeConfig;
import com.jun0rr.uncheck.Uncheck;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.stream.IntStream;

/**
 *
 * @author F6036477
 */
public class FileVolume extends DefaultVolume {
  
  public static final byte METADATA_ID = 55;
  
  private final FileChannel channel;
  
  private final boolean loaded;
  
  public FileVolume(VolumeConfig cfg) {
    super(cfg);
    if(config.storePath() == null) {
      throw new IllegalArgumentException("Bad VolumeConfig.storePath(): " + cfg.storePath());
    }
    try {
      this.loaded = Files.exists(config.storePath());
      this.channel = FileChannel.open(config.storePath(), 
          StandardOpenOption.CREATE, 
          StandardOpenOption.READ,
          StandardOpenOption.WRITE,
          StandardOpenOption.DSYNC
      );
    }
    catch(IOException e) {
      throw Uncheck.uncheck(e);
    }
    loadMetadata();
  }
  
  private void loadMetadata() {
    if(loaded) {
      Block b = get(0);
      if(METADATA_ID == b.buffer().get()) {
        woffset.set(b.buffer().getLong());
        metaidx.set(b.buffer().getLong());
        int size = b.buffer().getInt();
        IntStream.range(0, size)
            .forEach(i->freebufs.add(b.buffer().getLong()));
      }
    }
  }

  @Override
  protected OffsetBuffer getOffsetBuffer(long offset) {
    if(offset < 0) {
      throw new IllegalArgumentException("Bad offset: " + offset);
    }
    Cached<OffsetBuffer> ob = cache.get(offset);
    if(ob == null) {
      ByteBuffer bb = malloc.alloc();
      Uncheck.call(()->channel.read(bb, offset));
      ob = putCached(OffsetBuffer.of(offset, bb));
    }
    return ob.content();
  }
  
  private void writeMetadata() {
    Block b = get(0);
    b.buffer().put(METADATA_ID);
    b.buffer().putLong(woffset.get());
    b.buffer().putLong(metaidx.get());
    b.buffer().putInt(freebufs.size());
    freebufs.stream().forEach(b.buffer()::putLong);
    b.commit();
  }
  
  @Override
  public void close() {
    writeMetadata();
    Uncheck.call(()->channel.close());
  }

  @Override
  public boolean isLoaded() {
    return loaded;
  }

  @Override
  public Volume commit(Block b) {
    long nextOffset = b.offset();
    do {
      OffsetBuffer buf = getOffsetBuffer(nextOffset);
      Uncheck.call(()->channel.write(buf.buffer().clear(), buf.offset()));
      nextOffset = getNextOffset(buf);
    }
    while(nextOffset > 0 && nextOffset != b.offset());
    return this;
  }

}
