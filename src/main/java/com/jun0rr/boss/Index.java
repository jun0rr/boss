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
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.LongStream;

/**
 *
 * @author F6036477
 */
public record Index(Map<Long,Long> idIndex, Map<BinType,List<Long>> classIndex, Map<IndexType,List<IndexValue>> valueIndex) {
  
  public Index() {
    this(new ConcurrentSkipListMap<>(), new ConcurrentSkipListMap<>(), new ConcurrentSkipListMap<>());
  }
  
  public Index putIndex(BinType type, long offset) {
    List<Long> idx = classIndex.get(type);
    if(idx == null) {
      idx = new CopyOnWriteArrayList<>();
      classIndex.put(type, idx);
    }
    idx.add(offset);
    return this;
  }
  
  public Index putIndex(long id, long offset) {
    idIndex.put(id, offset);
    return this;
  }
  
  public <T> Index putIndex(BinType type, String name, T value, long offset) {
    IndexType it = new IndexType(type, name);
    List<IndexValue> vs = valueIndex.get(it);
    if(vs == null) {
      vs = new CopyOnWriteArrayList<>();
      valueIndex.put(it, vs);
    }
    vs.add(new IndexValue(offset, value));
    return this;
  }
  
  public Index removeIndex(BinType type, long offset) {
    List<Long> idx = classIndex.get(type);
    if(idx != null) {
      idx.remove(offset);
    }
    return this;
  }
  
  public Index removeIndex(long id) {
    idIndex.remove(id);
    return this;
  }
  
  public <T> Index removeIndex(BinType type, String name, long offset) {
    IndexType it = new IndexType(type, name);
    List<IndexValue> vs = valueIndex.get(it);
    if(vs == null) {
      vs = new CopyOnWriteArrayList<>();
      valueIndex.put(it, vs);
    }
    vs.add(new IndexValue(offset, value));
    return this;
  }
  
  public boolean containsIdIndex(long id) {
    return idIndex.containsKey(id);
  }
  
  public OptionalLong findIndexById(long id) {
    return idIndex().entrySet().stream()
        .filter(e->e.getKey().longValue() == id)
        .map(Entry::getValue)
        .mapToLong(Long::longValue)
        .findFirst();
  }
  
  public LongStream findIndexByType(BinType type) {
    return classIndex().entrySet().stream()
        .filter(e->e.getKey().equals(type))
        .map(Entry::getValue)
        .flatMap(List::stream)
        .mapToLong(Long::longValue);
  }
  
  public LongStream findIndexByType(Class cls) {
    return classIndex().entrySet().stream()
        .filter(e->e.getKey().isTypeOf(cls))
        .map(Entry::getValue)
        .flatMap(List::stream)
        .mapToLong(Long::longValue);
  }
  
  public <T> LongStream findIndexByValue(Class cls, String name, T value) {
    return valueIndex().entrySet().stream()
        .filter(e->e.getKey().type().isTypeOf(cls))
        .filter(e->e.getKey().name().equals(name))
        .map(Entry::getValue)
        .flatMap(List::stream)
        .filter(v->v.value().equals(value))
        .mapToLong(IndexValue::offset);
  }
  
}
