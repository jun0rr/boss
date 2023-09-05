/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.boss.query.Transforms;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
 *
 * @author F6036477
 */
public class TestTransforms {
  
  @Test public void testAdd() {
    try {
    double x = 5.5;
    JsonObject o = JsonObject.of("num", x);
    double y = 10.0;
    Assertions.assertEquals(x + y, Transforms.add("num", y).apply(o));
    } catch(Throwable th) {
      th.printStackTrace();
      throw th;
    }
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
    Assertions.assertEquals(JsonArray.of(x.split(y)), Transforms.split("str", y).apply(o));
  }
  
  @Test public void testTold() {
    //String str = "2023-09-01 13:23:19";
    String str = "2023-09-01";
    JsonObject o = JsonObject.of("date", str);
    System.out.printf("---> testTold <---%n");
    LocalDate dt = (LocalDate) Transforms.told("date", null).apply(o);
    System.out.printf("-> told: %s -> %s%n", str, dt);
  }
  
  @Test public void testTodt() {
    String str = "2023-09-01 13:23:19";
    JsonObject o = JsonObject.of("date", str);
    System.out.printf("---> testTodt <---%n");
    LocalDateTime dt = (LocalDateTime) Transforms.todt("date", null).apply(o);
    System.out.printf("-> todt: %s -> %s%n", str, dt);
  }
  
  @Test public void testTozd() {
    String str = "2023-09-01 13:23:19";
    JsonObject o = JsonObject.of("date", str);
    System.out.printf("---> testTozd <---%n");
    ZonedDateTime dt = (ZonedDateTime) Transforms.tozd("date", null).apply(o);
    System.out.printf("-> tozd: %s -> %s%n", str, dt);
  }
  
  @Test public void testGetdt() {
    LocalDateTime dt = LocalDateTime.of(2023, 9, 1, 13, 23, 19);
    JsonObject o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(1, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(13, Transforms.getdt("date", "hour").apply(o));
    Assertions.assertEquals(23, Transforms.getdt("date", "minute").apply(o));
    Assertions.assertEquals(19, Transforms.getdt("date", "second").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "weekday").apply(o));
  }
  
  @Test public void testSetdt() {
    LocalDateTime dt = LocalDateTime.of(2023, 9, 1, 13, 23, 19);
    JsonObject o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(1, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(13, Transforms.getdt("date", "hour").apply(o));
    Assertions.assertEquals(23, Transforms.getdt("date", "minute").apply(o));
    Assertions.assertEquals(19, Transforms.getdt("date", "second").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "weekday").apply(o));
    JsonArray a = JsonArray.of("day", 5);
    dt = (LocalDateTime) Transforms.setdt("date", a).apply(o);
    o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(13, Transforms.getdt("date", "hour").apply(o));
    Assertions.assertEquals(23, Transforms.getdt("date", "minute").apply(o));
    Assertions.assertEquals(19, Transforms.getdt("date", "second").apply(o));
    Assertions.assertEquals(2, Transforms.getdt("date", "weekday").apply(o));
  }
  
  @Test public void testAdddt() {
    LocalDateTime dt = LocalDateTime.of(2023, 9, 1, 13, 23, 19);
    JsonObject o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(1, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(13, Transforms.getdt("date", "hour").apply(o));
    Assertions.assertEquals(23, Transforms.getdt("date", "minute").apply(o));
    Assertions.assertEquals(19, Transforms.getdt("date", "second").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "weekday").apply(o));
    JsonArray a = JsonArray.of("day", 4);
    dt = (LocalDateTime) Transforms.add("date", a).apply(o);
    o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(13, Transforms.getdt("date", "hour").apply(o));
    Assertions.assertEquals(23, Transforms.getdt("date", "minute").apply(o));
    Assertions.assertEquals(19, Transforms.getdt("date", "second").apply(o));
    Assertions.assertEquals(2, Transforms.getdt("date", "weekday").apply(o));
  }
  
  @Test public void testSubdt() {
    LocalDateTime dt = LocalDateTime.of(2023, 9, 5, 13, 23, 19);
    JsonObject o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(13, Transforms.getdt("date", "hour").apply(o));
    Assertions.assertEquals(23, Transforms.getdt("date", "minute").apply(o));
    Assertions.assertEquals(19, Transforms.getdt("date", "second").apply(o));
    Assertions.assertEquals(2, Transforms.getdt("date", "weekday").apply(o));
    JsonArray a = JsonArray.of("day", 4);
    dt = (LocalDateTime) Transforms.sub("date", a).apply(o);
    o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(1, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(13, Transforms.getdt("date", "hour").apply(o));
    Assertions.assertEquals(23, Transforms.getdt("date", "minute").apply(o));
    Assertions.assertEquals(19, Transforms.getdt("date", "second").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "weekday").apply(o));
  }
  
  @Test public void testGetzdt() {
    ZonedDateTime dt = LocalDateTime.of(2023, 9, 1, 13, 23, 19).atZone(ZoneId.systemDefault());
    JsonObject o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(1, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(13, Transforms.getdt("date", "hour").apply(o));
    Assertions.assertEquals(23, Transforms.getdt("date", "minute").apply(o));
    Assertions.assertEquals(19, Transforms.getdt("date", "second").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "weekday").apply(o));
  }
  
  @Test public void testSetzdt() {
    ZonedDateTime dt = LocalDateTime.of(2023, 9, 1, 13, 23, 19).atZone(ZoneId.systemDefault());
    JsonObject o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(1, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(13, Transforms.getdt("date", "hour").apply(o));
    Assertions.assertEquals(23, Transforms.getdt("date", "minute").apply(o));
    Assertions.assertEquals(19, Transforms.getdt("date", "second").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "weekday").apply(o));
    JsonArray a = JsonArray.of("day", 5);
    dt = (ZonedDateTime) Transforms.setdt("date", a).apply(o);
    o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(13, Transforms.getdt("date", "hour").apply(o));
    Assertions.assertEquals(23, Transforms.getdt("date", "minute").apply(o));
    Assertions.assertEquals(19, Transforms.getdt("date", "second").apply(o));
    Assertions.assertEquals(2, Transforms.getdt("date", "weekday").apply(o));
  }
  
  @Test public void testAddzdt() {
    ZonedDateTime dt = LocalDateTime.of(2023, 9, 1, 13, 23, 19).atZone(ZoneId.systemDefault());
    JsonObject o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(1, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(13, Transforms.getdt("date", "hour").apply(o));
    Assertions.assertEquals(23, Transforms.getdt("date", "minute").apply(o));
    Assertions.assertEquals(19, Transforms.getdt("date", "second").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "weekday").apply(o));
    JsonArray a = JsonArray.of("day", 4);
    dt = (ZonedDateTime) Transforms.add("date", a).apply(o);
    o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(13, Transforms.getdt("date", "hour").apply(o));
    Assertions.assertEquals(23, Transforms.getdt("date", "minute").apply(o));
    Assertions.assertEquals(19, Transforms.getdt("date", "second").apply(o));
    Assertions.assertEquals(2, Transforms.getdt("date", "weekday").apply(o));
  }
  
  @Test public void testSubzdt() {
    ZonedDateTime dt = LocalDateTime.of(2023, 9, 5, 13, 23, 19).atZone(ZoneId.systemDefault());
    JsonObject o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(13, Transforms.getdt("date", "hour").apply(o));
    Assertions.assertEquals(23, Transforms.getdt("date", "minute").apply(o));
    Assertions.assertEquals(19, Transforms.getdt("date", "second").apply(o));
    Assertions.assertEquals(2, Transforms.getdt("date", "weekday").apply(o));
    JsonArray a = JsonArray.of("day", 4);
    dt = (ZonedDateTime) Transforms.sub("date", a).apply(o);
    o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(1, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(13, Transforms.getdt("date", "hour").apply(o));
    Assertions.assertEquals(23, Transforms.getdt("date", "minute").apply(o));
    Assertions.assertEquals(19, Transforms.getdt("date", "second").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "weekday").apply(o));
  }
  
  @Test public void testGetld() {
    LocalDate dt = LocalDate.of(2023, 9, 1);
    JsonObject o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(1, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "weekday").apply(o));
  }
  
  @Test public void testSetld() {
    //LocalDate dt = LocalDate.of(2023, 9, 1);
    JsonObject o = JsonObject.of("date", "2023-09-01");
    Assertions.assertEquals(2023, Transforms.getdt(null, "year").apply(Transforms.told("date", null).apply(o)));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(Transforms.told("date", null).apply(o)));
    Assertions.assertEquals(1, Transforms.getdt("date", "day").apply(Transforms.told("date", null).apply(o)));
    Assertions.assertEquals(5, Transforms.getdt("date", "weekday").apply(Transforms.told("date", null).apply(o)));
    JsonArray a = JsonArray.of("day", 5);
    LocalDate dt = (LocalDate) Transforms.setdt("date", a).apply(Transforms.told("date", null).apply(o));
    o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(2, Transforms.getdt("date", "weekday").apply(o));
  }
  
  @Test public void testAddld() {
    LocalDate dt = LocalDate.of(2023, 9, 1);
    JsonObject o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(1, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "weekday").apply(o));
    JsonArray a = JsonArray.of("day", 4);
    dt = (LocalDate) Transforms.add("date", a).apply(o);
    o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(2, Transforms.getdt("date", "weekday").apply(o));
  }
  
  @Test public void testSubld() {
    LocalDate dt = LocalDate.of(2023, 9, 5);
    JsonObject o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(2, Transforms.getdt("date", "weekday").apply(o));
    JsonArray a = JsonArray.of("day", 4);
    dt = (LocalDate) Transforms.sub("date", a).apply(o);
    o = JsonObject.of("date", dt);
    Assertions.assertEquals(2023, Transforms.getdt("date", "year").apply(o));
    Assertions.assertEquals(9, Transforms.getdt("date", "month").apply(o));
    Assertions.assertEquals(1, Transforms.getdt("date", "day").apply(o));
    Assertions.assertEquals(5, Transforms.getdt("date", "weekday").apply(o));
  }
  
}
