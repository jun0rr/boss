/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss;

import java.io.Closeable;
import java.util.Map;

/**
 *
 * @author F6036477
 */
public interface Volume extends Closeable {
  
  public String id();
  
  public int blockSize();
  
  public Block allocate();
  
  public Block allocate(int size);
  
  public Volume release(Block blk);
  
  public Volume release(int offset);
  
  public Block get(int offset);
  
  @Override public void close();
  
  public boolean isLoaded();
  
}
