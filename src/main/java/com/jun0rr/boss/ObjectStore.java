/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss;

import java.io.Closeable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 *
 * @author F6036477
 */
public interface ObjectStore extends Closeable {
  
  public <T> Stored<T> store(T o);
  
  public <T> Optional<Stored<T>> get(long id);
  
  public <T> Stored<T> update(long id, T o);
  
  public <T> Stored<T> update(long id, UnaryOperator<T> update);
  
  public <T> Stream<Stored<T>> find(Class<T> c, Predicate<T> p);
  
  public <T,V> Stream<Stored<T>> find(Class<T> c, String name, V v);
  
  public <T> Stream<Stored<T>> find(Class<T> c, Map<String,Object> values);
  
  public <T> Optional<Stored<T>> delete(long id);
  
  public <T> Stream<Stored<T>> delete(Class<T> c, Predicate<T> p);
  
  public <T,R> void createIndex(Class<T> c, String name, Function<T,R> fn);
  
  public <T, R> void createIndex(Class<T> c, Map<String,Function<T,R>> map);
  
  public Index index();
  
  public boolean isLoaded();
  
  @Override public void close();
  
}
