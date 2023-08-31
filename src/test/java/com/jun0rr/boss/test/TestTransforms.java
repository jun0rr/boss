/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.boss.query.Transforms;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
 *
 * @author F6036477
 */
public class TestTransforms {
  
  @Test public void testAdd() {
    double x = 5.5;
    JsonObject o = JsonObject.of("num", x);
    double y = 10.0;
    Assertions.assertEquals(x + y, Transforms.add("num", y).apply(o));
  }
  
  @Test public void testSub() {
    double x = 5.5;
    JsonObject o = JsonObject.of("num", x);
    double y = 10.0;
    Assertions.assertEquals(x - y, Transforms.sub("num", y).apply(o));
  }
  
  @Test public void testDiv() {
    double x = 5.5;
    JsonObject o = JsonObject.of("num", x);
    double y = 10.0;
    Assertions.assertEquals(x / y, Transforms.div("num", y).apply(o));
  }
  
  @Test public void testX() {
    double x = 5.5;
    JsonObject o = JsonObject.of("num", x);
    double y = 10.0;
    Assertions.assertEquals(x * y, Transforms.x("num", y).apply(o));
  }
  
  @Test public void testPow() {
    double x = 5.5;
    JsonObject o = JsonObject.of("num", x);
    double y = 10.0;
    Assertions.assertEquals(Math.pow(x, y), Transforms.pow("num", y).apply(o));
  }
  
  @Test public void testMax() {
    double x = 5.5;
    JsonObject o = JsonObject.of("num", x);
    double y = 10.0;
    Assertions.assertEquals(Math.max(x, y), Transforms.max("num", y).apply(o));
  }
  
  @Test public void testMin() {
    double x = 5.5;
    JsonObject o = JsonObject.of("num", x);
    double y = 10.0;
    Assertions.assertEquals(Math.min(x, y), Transforms.min("num", y).apply(o));
  }
  
  @Test public void testSubstring() {
    String x = "hello";
    JsonObject o = JsonObject.of("str", x);
    JsonArray y = JsonArray.of(2, 4);
    Assertions.assertEquals(x.substring(2, 4), Transforms.substring("str", y).apply(o));
  }
  
  @Test public void testConcat() {
    String x = "hello";
    JsonObject o = JsonObject.of("str", x);
    String y = " world";
    Assertions.assertEquals(x.concat(y), Transforms.concat("str", y).apply(o));
  }
  
  @Test public void testReplace() {
    String x = "hello";
    JsonObject o = JsonObject.of("str", x);
    JsonArray y = JsonArray.of("ll", "xx");
    Assertions.assertEquals(x.replace(y.getString(0), y.getString(1)), Transforms.replace("str", y).apply(o));
  }
  
  @Test public void testSq() {
    double x = 5.5;
    JsonObject o = JsonObject.of("num", x);
    Assertions.assertEquals(Math.sqrt(x), Transforms.sq("num", null).apply(o));
  }
  
  @Test public void testCeil() {
    double x = 5.5;
    JsonObject o = JsonObject.of("num", x);
    Assertions.assertEquals(Math.ceil(x), Transforms.ceil("num", null).apply(o));
  }
  
  @Test public void testFloor() {
    double x = 5.5;
    JsonObject o = JsonObject.of("num", x);
    Assertions.assertEquals(Math.floor(x), Transforms.floor("num", null).apply(o));
  }
  
  @Test public void testArraySize() {
    JsonArray x = JsonArray.of(0, 1, 2, 3, 4, 5);
    JsonObject o = JsonObject.of("array", x);
    Assertions.assertEquals(x.size(), Transforms.size("array", null).apply(o));
  }
  
  @Test public void testStringSize() {
    String x = "hello world";
    JsonObject o = JsonObject.of("str", x);
    Assertions.assertEquals(x.length(), Transforms.size("str", null).apply(o));
  }
  
  @Test public void testStrip() {
    String x = "   hello world   ";
    JsonObject o = JsonObject.of("str", x);
    Assertions.assertEquals(x.strip(), Transforms.strip("str", null).apply(o));
  }
  
  @Test public void testUpper() {
    String x = "   hello world   ";
    JsonObject o = JsonObject.of("str", x);
    Assertions.assertEquals(x.toUpperCase(), Transforms.upper("str", null).apply(o));
  }
  
  @Test public void testLower() {
    String x = "   HellO WorlD   ";
    JsonObject o = JsonObject.of("str", x);
    Assertions.assertEquals(x.toLowerCase(), Transforms.lower("str", null).apply(o));
  }
  
  @Test public void testSplit() {
    String x = "   HellO WorlD   ";
    JsonObject o = JsonObject.of("str", x);
    String y = " ";
    Assertions.assertArrayEquals(x.split(y), (String[])Transforms.split("str", y).apply(o));
  }
  
}
