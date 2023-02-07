/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss;

import com.jun0rr.jbom.buffer.BinBuffer;

/**
 *
 * @author F6036477
 */
public interface Block {
  
  public int offset();
  
  public int size();
  
  public BinBuffer buffer();
  
  public Volume volume();
  
  public boolean hasNext();
  
  public Block next();
  
  public int nextOffset();
  
  public Block setNextOffset(int offset);
  
  public Block lastBlock();
  
}
