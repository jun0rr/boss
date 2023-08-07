/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.json;

import com.jun0rr.boss.query.Either;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.LongStream;

/**
 *
 * @author F6036477
 */
public record JsonIndex(Map<Long,Long> idOffsets, Map<String,List<Long>> collectionOffsets, Map<IndexCollection,List<IndexValue>> valueOffsets) {
  
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
  
  public JsonIndex putOffset(String collection, long offset) {
    List<Long> idx = collectionOffsets.get(collection);
    if(idx == null) {
      idx = new CopyOnWriteArrayList<>();
      collectionOffsets.put(collection, idx);
    }
    idx.add(offset);
    return this;
  }
  
  public JsonIndex putOffset(long id, long offset) {
    idOffsets.put(id, offset);
    return this;
  }
  
  public <T> JsonIndex putOffset(String collection, String name, T value, long offset) {
    IndexCollection it = new IndexCollection(collection, name);
    List<IndexValue> vs = valueOffsets.get(it);
    if(vs == null) {
      vs = new CopyOnWriteArrayList<>();
      valueOffsets.put(it, vs);
    }
    vs.add(new IndexValue(offset, value));
    return this;
  }
  
  public JsonIndex removeOffset(String collection, long offset) {
    List<Long> idx = collectionOffsets.get(collection);
    if(idx != null) {
      idx.remove(offset);
    }
    valueOffsets().entrySet().stream()
        .filter(e->e.getKey().collection().equals(collection))
        .map(Entry::getValue)
        .forEach(l->l.stream()
            .filter(v->v.offset() == offset)
            .forEach(l::remove));
    return this;
  }
  
  public JsonIndex removeOffset(long id) {
    idOffsets.remove(id);
    return this;
  }
  
  public boolean containsId(long id) {
    return idOffsets.containsKey(id);
  }
  
  public OptionalLong findOffsetById(long id) {
    return Optional.ofNullable(idOffsets.get(id))
        .stream()
        .mapToLong(Long::longValue)
        .findAny();
  }
  
  public LongStream findOffsetByCollection(String collection) {
    return collectionOffsets().entrySet().stream()
        .filter(e->e.getKey().equals(collection))
        .map(Entry::getValue)
        .flatMap(List::stream)
        .mapToLong(Long::longValue);
  }
  
  public Optional<String> findCollectionByOffset(long offset) {
    return collectionOffsets.entrySet().stream()
        .filter(e->e.getValue().contains(offset))
        .map(Entry::getKey)
        .findFirst();
  }
  
  public Optional<String> findCollectionById(long id) {
    return findOffsetById(id).stream()
        .mapToObj(this::findCollectionByOffset)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
  }
  
  public <T> LongStream findOffsetByValue(String collection, String name, T value) {
    return valueOffsets().entrySet().stream()
        .filter(e->e.getKey().collection().equals(collection))
        .filter(e->e.getKey().name().equals(name))
        .map(Entry::getValue)
        .flatMap(List::stream)
        //.peek(v->System.out.printf("=> findOffsetByValue: %s%n", v))
        .filter(v->v.value().equals(value))
        .peek(v->System.out.printf("=> findOffsetByValue.filtered: %s%n", v))
        .mapToLong(IndexValue::offset);
  }
  
  public JsonObject toJson() {
    JsonObject index = new JsonObject();
    JsonArray ids = new JsonArray();
    JsonArray collections = new JsonArray();
    JsonArray values = new JsonArray();
    idOffsets.entrySet().stream()
        .map(e->new JsonObject()
            .put("id", e.getKey())
            .put("offset", e.getValue()))
        .forEach(ids::add);
    index.put("ids", ids);
    
    for(Entry<String,List<Long>> e : collectionOffsets.entrySet()) {
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
    
    for(Entry<IndexCollection,List<IndexValue>> e : valueOffsets.entrySet()) {
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
          .forEach(j->putOffset(j.getLong("id"), j.getLong("offset")));
    }
    if(o.containsKey("collections")) {
      JsonArray collections = o.getJsonArray("collections");
      for(int i = 0; i < collections.size(); i++) {
        JsonObject c = collections.getJsonObject(i);
        JsonArray a = c.getJsonArray("offsets");
        for(int j = 0; j < a.size(); j++) {
          putOffset(c.getString("collection"), a.getLong(j));
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
          putOffset(
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
