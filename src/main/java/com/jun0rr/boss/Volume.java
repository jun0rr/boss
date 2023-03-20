/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss;

import com.jun0rr.boss.config.VolumeConfig;
import com.jun0rr.boss.volume.DefaultVolume;
import com.jun0rr.boss.volume.FileVolume;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public interface Volume extends AutoCloseable {
  
  public VolumeConfig config();
  
  public Block allocate();
  
  public Block allocate(int size);
  
  public Volume release(Block blk);
  
  public Volume release(long offset);
  
  public Block get(long offset);
  
  public Block metadata();
  
  @Override public void close();
  
  public boolean isLoaded();
  
  public Volume commit(Block b);
  
  
  public static Volume createVolume(VolumeConfig cfg) {
    Objects.requireNonNull(cfg);
    return cfg.storePath() != null 
        ? new FileVolume(cfg) 
        : new DefaultVolume(cfg);
  }
  
}
