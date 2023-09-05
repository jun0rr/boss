/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.query;

import io.vertx.core.json.JsonObject;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author f6036477
 */
public record Query(String collection, List<Select> fields, Sort sort, List<Select> queryStream) implements Predicate<JsonObject> {

  public Query(String collection) {
    this(collection, new LinkedList(), null, new LinkedList());
  }
  
  public Stream stream() {
    return queryStream.stream();
  }
  
  public Query add(Select e) {
    queryStream.add(e);
    return this;
  }
  
  public Query sort(Sort s) {
    return new Query(collection, fields, s, queryStream);
  }
  
  @Override
  public boolean test(JsonObject o) {
    Object
    
  }
  
  @Override
  public Object apply(JsonObject o) {
  }
  
}
