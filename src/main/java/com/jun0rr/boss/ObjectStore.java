/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 *
 * @author F6036477
 */
public interface ObjectStore {
  
  public long store(Object o);
  
  public void update(long id, Object o);
  
  public <T> void update(long id, UnaryOperator<T> update);
  
  public <T> Stream<Stored<T>> find(Class<T> c, Predicate<T> p);
  
  public <T,V> Stream<Stored<T>> find(Class<T> c, String name, Predicate<V> p);
  
  public <T> Optional<T> delete(long id);
  
  public <T> Stream<T> delete(Class<T> c, Predicate<T> p);
  
  public <T,R> void createIndex(Class<T> c, String name, Function<T,R> fn);
  
  public IndexStore indexes();
  
}
