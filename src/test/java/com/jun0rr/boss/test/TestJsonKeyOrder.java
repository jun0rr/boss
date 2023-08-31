/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import io.vertx.core.json.JsonObject;
import java.util.Map.Entry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
 *
 * @author F6036477
 */
public class TestJsonKeyOrder {
  
  @Test public void test() {
    JsonObject o = new JsonObject();
    o.put("field", "age")
        .put("x", 2)
        .put("sub", 10)
        .put("add", 5);
    System.out.printf("%s%n", o);
    o.stream()
        .forEach(e->System.out.printf("  -> %s: %s%n", e.getKey(), e.getValue()));
    Assertions.assertEquals(o.stream().map(Entry::getKey).limit(1).findAny().get(), "field");
    Assertions.assertEquals(o.stream().map(Entry::getKey).skip(1).limit(1).findAny().get(), "x");
    Assertions.assertEquals(o.stream().map(Entry::getKey).skip(2).limit(1).findAny().get(), "sub");
    Assertions.assertEquals(o.stream().map(Entry::getKey).skip(3).limit(1).findAny().get(), "add");
  }
  
  @Test public void testFromString() {
    String json = "{\"field\": \"age\", \"x\": 2, \"sub\": 5, \"add\": 10, \"sq\": null}";
    JsonObject o = new JsonObject(json);
    Assertions.assertEquals(o.stream().map(Entry::getKey).limit(1).findAny().get(), "field");
    Assertions.assertEquals(o.stream().map(Entry::getKey).skip(1).limit(1).findAny().get(), "x");
    Assertions.assertEquals(o.stream().map(Entry::getKey).skip(2).limit(1).findAny().get(), "sub");
    Assertions.assertEquals(o.stream().map(Entry::getKey).skip(3).limit(1).findAny().get(), "add");
    Assertions.assertEquals(o.stream().map(Entry::getKey).skip(4).limit(1).findAny().get(), "sq");
    //Assertions.assertTrue(o.stream().map(Entry::getValue).skip(4).limit(1).findAny().isEmpty());
    Assertions.assertNull(o.getValue("sq"));
  }
  
  @Test public void testDate() {
    String json = "{\"date1\": \"2023-08-31T12:38:00Z\"}";
    JsonObject o = new JsonObject(json);
    System.out.printf("-> o.date1(%s).getClass: %s%n", o.getValue("date1"), o.getValue("date1").getClass());
    System.out.printf("-> o.date1(%s).getInstant: %s%n", o.getValue("date1"), o.getInstant("date1"));
    json = "{\"date1\": \"2023-08-31\"}";
    o = new JsonObject(json);
    System.out.printf("-> o.date1(%s).getClass: %s%n", o.getValue("date1"), o.getValue("date1").getClass());
    System.out.printf("-> o.date1(%s).getInstant: %s%n", o.getValue("date1"), o.getInstant("date1"));
  }
  
}
