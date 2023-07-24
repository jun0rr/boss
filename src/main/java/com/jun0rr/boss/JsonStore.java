/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss;

import io.vertx.core.json.JsonObject;
import java.io.Closeable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 *
 * @author F6036477
 */
public interface JsonStore extends Closeable {
  
  public Stored<JsonObject> store(JsonObject o);
  
  public Optional<Stored<JsonObject>> get(long id);
  
  public Stored<JsonObject> update(long id, JsonObject o);
  
  public Stored<JsonObject> update(long id, UnaryOperator<JsonObject> update);
  
  public Stream<Stored<JsonObject>> find(String collection, Predicate<JsonObject> p);
  
  public Stream<Stored<JsonObject>> find(String collection, String name, Object value);
  
  public Optional<Stored<JsonObject>> delete(long id);
  
  public Stream<Stored<JsonObject>> delete(String collection, Predicate<JsonObject> p);
  
  public <R> void createIndex(String collection, String name, Function<JsonObject,R> fn);
  
  public JsonIndex index();
  
  public boolean isLoaded();
  
  @Override public void close();
  
}
