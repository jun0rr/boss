/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.query;

import io.vertx.core.json.JsonObject;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 * @author F6036477
 */
public record Select(String field, List<Function> functions) implements Function<JsonObject,Object> {
  
  public Select(String field) {
    this(field, new LinkedList<>());
  }
  
  public Select add(Function f) {
    functions.add(f);
    return this;
  }
  
  public Stream<Function> stream() {
    return functions.stream();
  }
  
  @Override
  public Object apply(JsonObject o) {
    Object val = JsonExtract.extract(field, o);
    for(Function f : functions) {
      val = f.apply(val);
    }
    return val;
  }
  
}
