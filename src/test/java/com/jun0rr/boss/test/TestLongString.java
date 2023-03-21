/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestLongString {
  
  public long[] stringToLong(String str) {
    int longs = Double.valueOf(Math.ceil(str.length() / Integer.valueOf(Long.BYTES).doubleValue())).intValue();
    ByteBuffer buf = ByteBuffer.allocate(longs * Long.BYTES);
    buf.put(StandardCharsets.UTF_8.encode(str));
    buf.clear();
    long[] ls = new long[longs];
    for(int i = 0; i < longs; i++) {
      ls[i] = buf.getLong();
    }
    return ls;
  }
  
  public String longToString(long... ls) {
    ByteBuffer buf = ByteBuffer.allocate(ls.length * Long.BYTES);
    for(int i = 0; i < ls.length; i++) {
      buf.putLong(ls[i]);
    }
    buf.flip();
    return StandardCharsets.UTF_8.decode(buf).toString();
  }
  
  public int[] stringToInt(String str) {
    int ints = Double.valueOf(Math.ceil(str.length() / Integer.valueOf(Integer.BYTES).doubleValue())).intValue();
    ByteBuffer buf = ByteBuffer.allocate(ints * Integer.BYTES);
    buf.put(StandardCharsets.UTF_8.encode(str));
    buf.clear();
    int[] is = new int[ints];
    for(int i = 0; i < ints; i++) {
      is[i] = buf.getInt();
    }
    return is;
  }
  
  public String intToString(int... is) {
    ByteBuffer buf = ByteBuffer.allocate(is.length * Integer.BYTES);
    for(int i = 0; i < is.length; i++) {
      buf.putInt(is[i]);
    }
    buf.flip();
    return StandardCharsets.UTF_8.decode(buf).toString();
  }
  
  @Test public void testLongs() {
    String str = "12345678";
    long[] ls = stringToLong(str);
    System.out.printf("'%s' -> %s -> '%s'%n", str, Arrays.toString(ls), longToString(ls));
    str = "Hello World!!";
    ls = stringToLong(str);
    System.out.printf("'%s' -> %s -> '%s'%n", str, Arrays.toString(ls), longToString(ls));
    str = "Super Tomahawk!!";
    ls = stringToLong(str);
    System.out.printf("'%s' -> %s -> '%s'%n", str, Arrays.toString(ls), longToString(ls));
    str = "Lorem ipsum dolor sit amet";
    ls = stringToLong(str);
    System.out.printf("'%s' -> %s -> '%s'%n", str, Arrays.toString(ls), longToString(ls));
  }
  
  @Test public void testInts() {
    String str = "12345678";
    int[] ls = stringToInt(str);
    System.out.printf("'%s' -> %s -> '%s'%n", str, Arrays.toString(ls), intToString(ls));
    str = "Hello World!!";
    ls = stringToInt(str);
    System.out.printf("'%s' -> %s -> '%s'%n", str, Arrays.toString(ls), intToString(ls));
    str = "Super Tomahawk!!";
    ls = stringToInt(str);
    System.out.printf("'%s' -> %s -> '%s'%n", str, Arrays.toString(ls), intToString(ls));
    str = "Lorem ipsum dolor sit amet";
    ls = stringToInt(str);
    System.out.printf("'%s' -> %s -> '%s'%n", str, Arrays.toString(ls), intToString(ls));
    
    ls = new int[5];
    for(int i = 0; i < ls.length; i++) {
      ls[i] = Double.valueOf(Math.random() * Integer.MAX_VALUE).intValue();
    }
    str = intToString(ls);
    System.out.printf("%s -> '%s' -> %s%n", Arrays.toString(ls), str, stringToInt(str));
  }
  
}
