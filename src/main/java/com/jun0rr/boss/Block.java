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
public record Block(Volume volume, BinBuffer buffer, long offset) implements AutoCloseable {
  
  public Block commit() {
    volume.commit(this);
    return this;
  }

  @Override
  public void close() {
    commit();
  }
  
  public static Block of(Volume v, BinBuffer b, long offset) {
    return new Block(v, b, offset);
  }
  
}
