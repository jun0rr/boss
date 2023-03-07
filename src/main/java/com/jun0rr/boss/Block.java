/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss;

import com.jun0rr.binj.buffer.BinBuffer;

/**
 *
 * @author F6036477
 */
public interface Block {
  
  public Volume volume();
  
  public BinBuffer buffer();
  
  public int offset();
  
}
