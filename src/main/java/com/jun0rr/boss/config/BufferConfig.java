/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.config;

/**
 *
 * @author F6036477
 */
public record BufferConfig(Type type, int size) {

  public static enum Type {
    HEAP, DIRECT
  }
  
}
