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
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.stream.IntStream;

/**
 *
 * @author F6036477
 */
public class FileVolume2 extends DefaultVolume {
  
  public static final byte METADATA_ID = 55;
  
  private final FileChannel channel;
  
  private final boolean loaded;
  
  public FileVolume2(VolumeConfig cfg) {
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
      woffset.set(channel.size());
    }
    catch(IOException e) {
      throw new VolumeException(e);
    }
    loadMetadata();
  }
  
  private void loadMetadata() {
    if(loaded) {
      Block b = get(0);
      System.out.println("Volume.loadMetadata: " + b);
      if(METADATA_ID == b.buffer().get()) {
        woffset.set(b.buffer().getLong());
        metaidx.set(b.buffer().getLong());
        int size = b.buffer().getInt();
        IntStream.range(0, size)
            .forEach(i->freebufs.add(b.buffer().getLong()));
        System.out.printf("Volume.loadMetadata: woffset=%d, metaidx=%d, freebufs=%s%n", woffset.get(), metaidx.get(), freebufs);
      }
    }
  }

  @Override
  protected OffsetBuffer getOffsetBuffer(long offset) {
    OffsetBuffer ob = super.getOffsetBuffer(offset);
    Uncheck.call(()->channel.read(ob.buffer(), offset));
    System.out.printf("FileVolume2.getOffsetBuffer(%d): %s%n", offset, ob);
    return ob;
  }
  
  @Override
  public void close() {
    Block b = get(0);
    b.buffer().put(METADATA_ID);
    b.buffer().putLong(woffset.get());
    b.buffer().putLong(metaidx.get());
    b.buffer().putInt(freebufs.size());
    System.out.printf("Volume.close: woffset=%d, metaidx=%d, freebufs=%d%n", woffset.get(), metaidx.get(), freebufs.size());
    for(long offset : freebufs) {
      b.buffer().putLong(offset);
    }
    b.commit();
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
      System.out.println("Volume.commit: nextOffset=" + nextOffset);
    }
    while(nextOffset > 0 && nextOffset != b.offset());
    return this;
  }
  
}
