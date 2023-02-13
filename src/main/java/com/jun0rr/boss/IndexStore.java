/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss;

import com.jun0rr.boss.store.IndexType;
import com.jun0rr.boss.store.IndexValue;
import com.jun0rr.jbom.BinType;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 *
 * @author F6036477
 */
public interface IndexStore {
  
  public Map<BinType,List<Integer>> classIndex();
  
  public Map<Long,Integer> idIndex();
  
  public Map<IndexType,List<IndexValue>> valueIndex();
  
  public default <T> IntStream findByValue(Class c, String name, Predicate<T> p) {
    return valueIndex().entrySet().stream()
        .filter(e->e.getKey().type().isTypeOf(c))
        .filter(e->e.getKey().name().equals(name))
        .map(Entry::getValue)
        .flatMap(List::stream)
        .filter(v->p.test((T)v.value()))
        .mapToInt(IndexValue::index);
  }
  
  public default IntStream findByType(Class c) {
    return classIndex().entrySet().stream()
        .filter(e->e.getKey().isTypeOf(c))
        .map(Entry::getValue)
        .flatMap(List::stream)
        .mapToInt(Integer::intValue);
  }
  
  public default OptionalInt findById(long id) {
    return idIndex().entrySet().stream()
        .filter(e->e.getKey().longValue() == id)
        .map(Entry::getValue)
        .mapToInt(Integer::intValue)
        .findFirst();
  }
  
}
