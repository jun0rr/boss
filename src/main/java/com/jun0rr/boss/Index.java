/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss;

import com.jun0rr.binj.BinType;
import com.jun0rr.binj.mapping.Binary;
import com.jun0rr.boss.store.IndexType;
import com.jun0rr.boss.store.IndexValue;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalInt;
import java.util.stream.IntStream;

/**
 *
 * @author F6036477
 */
public interface Index {
  
  @Binary
  public Map<BinType,List<Integer>> classIndex();
  
  @Binary
  public Map<Long,Integer> idIndex();
  
  @Binary
  public Map<IndexType,List<IndexValue>> valueIndex();
  
  @Binary
  public List<BinType> types();
  
  public default <T> IntStream findByValue(Class c, String name, T t) {
    return valueIndex().entrySet().stream()
        .filter(e->e.getKey().type().isTypeOf(c))
        .filter(e->e.getKey().name().equals(name))
        .map(Entry::getValue)
        .flatMap(List::stream)
        .filter(v->v.value().equals(t))
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
