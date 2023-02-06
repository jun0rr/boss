/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss;

import java.nio.ByteBuffer;

/**
 *
 * @author F6036477
 */
public interface VolumeBuffer {
  
  public int offset();
  
  public ByteBuffer buffer();
  
  public int next();
  
  public boolean hasNext();
  
  public VolumeBuffer setNext(int offset);
  
}
