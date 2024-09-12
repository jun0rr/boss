/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Juno
 */
public class TestCharsetEncode {
  
  @Test 
  public void test() {
    ByteBuffer buf = StandardCharsets.UTF_8.encode("hello world!");
    System.out.printf("* buffer = %s%n", buf);
    System.out.printf("* string = %s%n", StandardCharsets.UTF_8.decode(buf));
  }
  
}
