/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss.volume;

/**
 *
 * @author F6036477
 */
public interface OffsetBufferAllocator {
  
  public OffsetBuffer alloc();
  
  public int bufferSize();
  
}
