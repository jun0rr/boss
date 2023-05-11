/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss;

import com.jun0rr.boss.config.VolumeConfig;

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
  
}
