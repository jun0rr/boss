/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss.query;

import io.vertx.core.json.JsonObject;
import java.util.List;

/**
 *
 * @author F6036477
 */
public interface JsonExtract {
  
  public static Object extract(String field, Object o) {
    if(JsonObject.class.isAssignableFrom(o.getClass())) {
      List<String> names = List.of(field.split("\\."));
      Object val = o;
      for(String name : names) {
        val = Either.of(val)
            .ifNotNull()
            .andIs(JsonObject.class)
            .andIs(j->j.containsKey(name))
            .thenMap(j->j.getValue(name))
            .a();
      }
      return val;
    }
    else return o;
  }
  
}
