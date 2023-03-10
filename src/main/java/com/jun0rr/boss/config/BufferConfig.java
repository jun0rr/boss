/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.config;

import com.jun0rr.binj.buffer.BufferAllocator;
import java.util.Map;

/**
 *
 * @author F6036477
 */
public record BufferConfig(Type type, int size, long maxCacheSize) {

  public static enum Type {
    HEAP, DIRECT;
    
    public static Type from(String s) {
      Type t = null;
      if(HEAP.name().equalsIgnoreCase(s)) {
        t = HEAP;
      }
      else if(DIRECT.name().equalsIgnoreCase(s)) {
        t = DIRECT;
      }
      return t;
    }
  }
  
  public BufferAllocator bufferAllocator() {
    return switch(type) {
      case HEAP -> BufferAllocator.heapAllocator(size);
      case DIRECT -> BufferAllocator.directAllocator(size);
      default -> throw new IllegalStateException("Bad BufferConfig.type: " + type);
    };
  }
  
  private static long parseSize(String s) {
    char last = s.charAt(s.length()-1);
    if(Character.isDigit(last)) {
      return Long.parseLong(s);
    }
    long size = Long.parseLong(s.substring(0, s.length()-1));
    return switch(Character.toUpperCase(last)) {
      //Byte
      case 'B' -> size * 1;
      //Kilobyte
      case 'K' -> size * 1024;
      //Megabyte
      case 'M' -> size * Math.round(Math.pow(1024, 2));
      //Gigabyte
      case 'G' -> size * Math.round(Math.pow(1024, 3));
      //Terabyte
      case 'T' -> size * Math.round(Math.pow(1024, 4));
      //Petabyte
      case 'P' -> size * Math.round(Math.pow(1024, 5));
      //Exabyte
      case 'E' -> size * Math.round(Math.pow(1024, 6));
      default -> throw new IllegalArgumentException(String.format("Bad size unit (%s) in %s", last, s));
    };
  }
  
  public static BufferConfig from(Map map) {
    String st = (String) map.get("type");
    if(st == null) {
      throw new BossConfigException("Bad null BufferConfig.type");
    }
    String ss = (String)map.get("size");
    if(ss == null) {
      throw new BossConfigException("Bad null BufferConfig.size");
    }
    String ms = (String)map.get("max_cache_size");
    if(ms == null) {
      throw new BossConfigException("Bad null BufferConfig.maxCacheSize");
    }
    return new BufferConfig(Type.from(st), (int)parseSize(ss), parseSize(ms));
  }
  
}
