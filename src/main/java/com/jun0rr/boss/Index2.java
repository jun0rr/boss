/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss;

import com.jun0rr.binj.BinType;
import com.jun0rr.boss.store.IndexType;
import com.jun0rr.boss.store.IndexValue;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalLong;
import java.util.stream.LongStream;

/**
 *
 * @author F6036477
 */
public interface Index2 {
  
  public Map<BinType,List<Long>> classIndex();
  
  public Map<Long,Long> idIndex();
  
  public Map<IndexType,List<IndexValue>> valueIndex();
  
  public Index2 putIndex(BinType type, long offset);
  
  public Index2 putIndex(long id, long offset);
  
  public <T> Index2 putIndex(BinType type, String name, T value, long offset);
  
  public boolean containsIdIndex(long id);
  
  public default OptionalLong getIndexById(long id) {
    return idIndex().entrySet().stream()
        .filter(e->e.getKey().longValue() == id)
        .map(Entry::getValue)
        .mapToLong(Long::longValue)
        .findFirst();
  }
  
  public default LongStream streamClassIndex(BinType type) {
    return classIndex().entrySet().stream()
        .filter(e->e.getKey().equals(type))
        .map(Entry::getValue)
        .flatMap(List::stream)
        .mapToLong(Long::longValue);
  }
  
  public default LongStream streamClassIndex(Class cls) {
    return classIndex().entrySet().stream()
        .filter(e->e.getKey().isTypeOf(cls))
        .map(Entry::getValue)
        .flatMap(List::stream)
        .mapToLong(Long::longValue);
  }
  
  public default <T> LongStream streamValueIndex(Class cls, String name, T value) {
    return valueIndex().entrySet().stream()
        .filter(e->e.getKey().type().isTypeOf(cls))
        .filter(e->e.getKey().name().equals(name))
        .map(Entry::getValue)
        .flatMap(List::stream)
        .filter(v->v.value().equals(value))
        .mapToLong(IndexValue::index);
  }
  
}
