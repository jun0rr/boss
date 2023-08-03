/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.json;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalLong;
import java.util.Set;
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
  
  public JsonObject toJson() {
    JsonObject index = new JsonObject();
    JsonArray ids = new JsonArray();
    JsonArray collections = new JsonArray();
    JsonArray values = new JsonArray();
    idIndex.entrySet().stream()
        .map(e->new JsonObject()
            .put("id", e.getKey())
            .put("offset", e.getValue()))
        .forEach(ids::add);
    index.put("ids", ids);
    
    for(Entry<String,List<Long>> e : collectionIndex.entrySet()) {
      JsonObject o = new JsonObject();
      o.put("collection", e.getKey());
      JsonArray a = new JsonArray();
      for(Long l : e.getValue()) {
        a.add(l);
      }
      o.put("offsets", a);
      collections.add(o);
    }
    index.put("collections", collections);
    
    for(Entry<IndexCollection,List<IndexValue>> e : valueIndex.entrySet()) {
      JsonObject o = new JsonObject();
      o.put("collection", e.getKey().collection());
      o.put("name", e.getKey().name());
      JsonArray a = new JsonArray();
      for(IndexValue v : e.getValue()) {
        JsonObject ov = new JsonObject();
        ov.put("value", v.value());
        ov.put("offset", v.offset());
        a.add(ov);
      }
      o.put("values", a);
      values.add(o);
    }
    index.put("values", values);
    return index;
  }
  
  public JsonIndex fromJson(JsonObject o) {
    if(o.containsKey("ids")) {
      JsonArray ids = o.getJsonArray("ids");
      ids.stream()
          .map(j->(JsonObject)j)
          .forEach(j->putIndex(j.getLong("id"), j.getLong("offset")));
    }
    if(o.containsKey("collections")) {
      JsonArray collections = o.getJsonArray("collections");
      for(int i = 0; i < collections.size(); i++) {
        JsonObject c = collections.getJsonObject(i);
        JsonArray a = c.getJsonArray("offsets");
        for(int j = 0; j < a.size(); j++) {
          putIndex(c.getString("collection"), a.getLong(j));
        }
      }
    }
    if(o.containsKey("values")) {
      JsonArray values = o.getJsonArray("values");
      for(int i = 0; i < values.size(); i++) {
        JsonObject oc = values.getJsonObject(i);
        JsonArray a = oc.getJsonArray("values");
        for(int j = 0; j < a.size(); j++) {
          JsonObject ov = a.getJsonObject(j);
          putIndex(
              oc.getString("collection"), 
              oc.getString("name"), 
              ov.getValue("value"), 
              ov.getLong("offset")
          );
        }
      }
    }
    return this;
  }
  
}
