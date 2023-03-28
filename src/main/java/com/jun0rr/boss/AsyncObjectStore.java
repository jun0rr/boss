/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss;

import com.jun0rr.boss.volume.Async;
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
public interface AsyncObjectStore extends Closeable {
  
  public <T> Async<Stored<T>> store(T o);
  
  public <T> Async<Optional<Stored<T>>> get(long id);
  
  public <T> Async<Stored<T>> update(long id, T o);
  
  public <T> Async<Stored<T>> update(long id, UnaryOperator<T> update);
  
  public <T> Async<Stream<Stored<T>>> find(Class<T> c, Predicate<T> p);
  
  public <T,V> Async<Stream<Stored<T>>> find(Class<T> c, String name, V v);
  
  public <T> Async<Optional<Stored<T>>> delete(long id);
  
  public <T> Async<Stream<Stored<T>>> delete(Class<T> c, Predicate<T> p);
  
  public <T,R> void createIndex(Class<T> c, String name, Function<T,R> fn);
  
  public Index index();
  
  public boolean isLoaded();
  
  @Override public void close();
  
}
