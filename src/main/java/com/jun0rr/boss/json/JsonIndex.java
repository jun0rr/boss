/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.json;

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
public record JsonIndex(Map<Long,Long> idIndex, Map<String,List<Long>> collectionIndex, Map<IndexCollection,List<IndexValue>> valueIndex) {
  
  public static record IndexCollection(String collection, String name) implements Comparable<IndexCollection> {
    @Override
    public int compareTo(IndexCollection o) {
      int r = this.collection.compareTo(o.collection);
      if(r == 0) {
        r = this.name.compareTo(o.name);
      }
      return r;
    }
  }
  
  public static record IndexValue(long offset, Object value) implements Comparable<IndexValue> {


    @Override
    public int compareTo(IndexValue o) {
      int r = 0;
      if(Comparable.class.isAssignableFrom(value.getClass())
          && Comparable.class.isAssignableFrom(o.value.getClass())) {
        r = ((Comparable)value).compareTo(o.value);
      }
      if(r == 0) {
        r = Long.compare(offset, o.offset);
      }
      return r;
    }
  }

  
  public JsonIndex() {
    this(new ConcurrentSkipListMap<>(), new ConcurrentSkipListMap<>(), new ConcurrentSkipListMap<>());
  }
  
  public JsonIndex putIndex(String collection, long offset) {
    List<Long> idx = collectionIndex.get(collection);
    if(idx == null) {
      idx = new CopyOnWriteArrayList<>();
      collectionIndex.put(collection, idx);
    }
    idx.add(offset);
    return this;
  }
  
  public JsonIndex putIndex(long id, long offset) {
    idIndex.put(id, offset);
    return this;
  }
  
  public <T> JsonIndex putIndex(String collection, String name, T value, long offset) {
    IndexCollection it = new IndexCollection(collection, name);
    List<IndexValue> vs = valueIndex.get(it);
    if(vs == null) {
      vs = new CopyOnWriteArrayList<>();
      valueIndex.put(it, vs);
    }
    vs.add(new IndexValue(offset, value));
    return this;
  }
  
  public JsonIndex removeIndex(String collection, long offset) {
    List<Long> idx = collectionIndex.get(collection);
    if(idx != null) {
      idx.remove(offset);
    }
    return this;
  }
  
  public JsonIndex removeIndex(long id) {
    idIndex.remove(id);
    return this;
  }
  
  public <T> JsonIndex removeIndex(String collection, String name, long offset) {
    IndexCollection it = new IndexCollection(collection, name);
    List<IndexValue> vs = valueIndex.get(it);
    if(vs != null) {
      vs.stream()
          .filter(i->i.offset() == offset)
          .findFirst()
          .ifPresent(vs::remove);
    }
    return this;
  }
  
  public boolean containsIdIndex(long id) {
    return idIndex.containsKey(id);
  }
  
  public OptionalLong findIndexById(long id) {
    return idIndex().entrySet().stream()
        .filter(e->e.getKey() == id)
        .map(Entry::getValue)
        .mapToLong(Long::longValue)
        .findFirst();
  }
  
  public LongStream findIndexByCollection(String collection) {
    return collectionIndex().entrySet().stream()
        .filter(e->e.getKey().equals(collection))
        .map(Entry::getValue)
        .flatMap(List::stream)
        .mapToLong(Long::longValue);
  }
  
  public <T> LongStream findIndexByValue(String collection, String name, T value) {
    return valueIndex().entrySet().stream()
        .filter(e->e.getKey().collection().equals(collection))
        .filter(e->e.getKey().name().equals(name))
        .map(Entry::getValue)
        .flatMap(List::stream)
        .filter(v->v.value().equals(value))
        .mapToLong(IndexValue::offset);
  }
  
}
