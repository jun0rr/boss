/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.boss.query.Conditions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
 *
 * @author F6036477
 */
public class TestConditions {
  
  @Test public void testEquals() {
    JsonObject o = new JsonObject().put("f", 10);
    Assertions.assertTrue(Conditions.eq("f", 10).test(o));
    Assertions.assertFalse(Conditions.eq("f", 5).test(o));
  }
  
  @Test public void testNotEquals() {
    JsonObject o = new JsonObject().put("f", 10);
    Assertions.assertTrue(Conditions.ne("f", 5).test(o));
    Assertions.assertFalse(Conditions.ne("f", 10).test(o));
  }
  
  @Test public void testGreater() {
    JsonObject o = new JsonObject().put("f", 10);
    Assertions.assertTrue(Conditions.gt("f", 5).test(o));
    Assertions.assertFalse(Conditions.gt("f", 10).test(o));
  }
  
  @Test public void testGreaterEquals() {
    JsonObject o = new JsonObject().put("f", 10);
    Assertions.assertTrue(Conditions.ge("f", 10).test(o));
    Assertions.assertFalse(Conditions.ge("f", 15).test(o));
  }
  
  @Test public void testLesser() {
    JsonObject o = new JsonObject().put("f", 10);
    Assertions.assertTrue(Conditions.lt("f", 15).test(o));
    Assertions.assertFalse(Conditions.lt("f", 5).test(o));
  }
  
  @Test public void testLesserEquals() {
    JsonObject o = new JsonObject().put("f", 10);
    Assertions.assertTrue(Conditions.le("f", 10).test(o));
    Assertions.assertFalse(Conditions.le("f", 5).test(o));
  }
  
  @Test public void testBetween() {
    JsonObject o = JsonObject.of("f", 10);
    Assertions.assertTrue(Conditions.bt("f", JsonArray.of(5, 15)).test(o));
    Assertions.assertFalse(Conditions.bt("f", JsonArray.of(5, 10)).test(o));
  }
  
}
