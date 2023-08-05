/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss.query;

import io.vertx.core.json.JsonObject;
import java.util.Objects;

/**
 *
 * @author f6036477
 */
public interface Conditions {
  
  public static <T> Condition<T> equals(String field, T expected) {
    return Condition.of(field, expected, (v,o)->Objects.equals(expected, value(field, o)));
  }
  
  public static <T> T value(String field, JsonObject o) {
    return null;
  }
  
}
