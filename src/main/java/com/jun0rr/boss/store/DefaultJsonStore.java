/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.store;

import com.jun0rr.boss.Block;
import com.jun0rr.boss.json.JsonIndex;
import com.jun0rr.boss.json.JsonIndex.IndexCollection;
import com.jun0rr.boss.json.JsonStore;
import com.jun0rr.boss.Stored;
import com.jun0rr.boss.Volume;
import com.jun0rr.boss.config.VolumeConfig;
import com.jun0rr.boss.json.JsonIndex.IndexValue;
import com.jun0rr.boss.query.Either;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 *
 * @author F6036477
 */
public class DefaultJsonStore implements JsonStore {
  
  private final Volume volume;
  
  private final JsonIndex index;
  
  public DefaultJsonStore(VolumeConfig cfg) {
    this(Objects.requireNonNull(cfg).createVolume());
  }

  public DefaultJsonStore(Volume vol) {
    this.volume = Objects.requireNonNull(vol);
    this.index = new JsonIndex();
    load();
  }

  private void load() {
    if(volume.isLoaded()) {
      Block b = volume.metadata();
      int len = b.buffer().getInt();
      byte[] bs = new byte[len];
      b.buffer().get(bs);
      index.fromJson(Buffer.buffer(bs).toJsonObject());
      System.out.printf("=> JsonStore.load: %s%n", index);
      volume.release(b);
    }
  }
  
  @Override
  public boolean isLoaded() {
    return volume.isLoaded();
  }
  
  @Override
  public Stored<JsonObject> store(String collection, JsonObject o) {
    Objects.requireNonNull(collection);
    Objects.requireNonNull(o);
    Block b = volume.allocate();
    byte[] bs = o.toBuffer().getBytes();
    b.buffer().position(Long.BYTES)
        .putInt(bs.length)
        .put(bs);
    int lim = b.buffer().position();
    long sum = b.buffer().position(Long.BYTES).limit(lim).checksum();
    b.buffer().position(0).putLong(sum);
    b.buffer().position(lim);
    b.commit();
    Stored<JsonObject> s = Stored.of(sum, b.offset(), o);
    storeIndex(collection, s);
    return Stored.of(sum, b.offset(), o);
  }
  
  private synchronized void storeIndex(String collection, Stored<JsonObject> s) {
    index.putOffset(collection, s.offset());
    index.putOffset(s.id(), s.offset());
    index.valueOffsets().entrySet().stream()
        .filter(e->e.getKey().collection().equals(collection))
        .forEach(e->insertValueIndex(collection, s, e));
  }
  
  private void insertValueIndex(String collection, Stored<JsonObject> s, Entry<IndexCollection, List<IndexValue>> entry) {
    List<String> names = List.of(entry.getKey().name().split("\\."));
    Object val = s.object();
    for(String name : names) {
      val = Either.of(val)
          .ifNotNull()
          .andIs(JsonObject.class)
          .andIs(j->j.containsKey(name))
          .thenMap(j->j.getValue(name))
          .a();
    }
    Either.of(val)
        .ifNotNull()
        .thenAccept(v->entry.getValue().add(new IndexValue(s.offset(), v)));
  }
  
  @Override
  public Optional<Stored<JsonObject>> get(long id) {
    Long idx = index.idOffsets().get(id);
    if(idx == null) {
      return Optional.empty();
    }
    Block b = volume.get(idx);
    Stored<JsonObject> s = Stored.of(b.buffer().position(0).getLong(), idx, from(b));
    return Optional.of(s);
  }
  
  private JsonObject from(Block b) {
    int len = b.buffer().position(Long.BYTES).getInt();
    byte[] bs = new byte[len];
    b.buffer().get(bs);
    return Buffer.buffer(bs).toJsonObject();
  }

  @Override
  public Stored<JsonObject> update(long id, JsonObject o) {
    long offset = index.findOffsetById(id)
        .orElseThrow(()->new ObjectStoreException("ID not found (%d)", id));
    String col = index.findCollectionByOffset(offset).get();
    delete(id);
    return store(col, o);
  }

  @Override
  public Stored<JsonObject> update(long id, UnaryOperator<JsonObject> update) {
    long offset = index.findOffsetById(id)
        .orElseThrow(()->new ObjectStoreException("ID not found (%d)", id));
    String col = index.findCollectionByOffset(offset).get();
    return store(col, update.apply(delete(id).get().object()));
  }

  @Override
  public Stream<Stored<JsonObject>> find(String collection, Predicate<JsonObject> p) {
    return index.findOffsetByCollection(collection)
        .mapToObj(volume::get)
        .map(b->Stored.of(b.buffer().position(0).getLong(), b.offset(), from(b)))
        //.peek(System.out::println)
        .filter(s->p.test(s.object()));
  }

  @Override
  public <V> Stream<Stored<JsonObject>> find(String collection, String name, V v) {
    return index.findOffsetByValue(collection, name, v)
        .mapToObj(volume::get)
        //.peek(System.out::println)
        .map(b->Stored.of(b.buffer().position(0).getLong(), b.offset(), from(b)));
  }

  @Override
  public Optional<Stored<JsonObject>> delete(long id) {
    OptionalLong o = index.findOffsetById(id);
    if(o.isEmpty()) return Optional.empty();
    index.collectionOffsets().entrySet().stream()
        .map(Entry::getValue)
        .forEach(l->l.remove(o.getAsLong()));
    Block b = volume.get(o.getAsLong());
    Stored<JsonObject> s = Stored.of(b.buffer().position(0).getLong(), o.getAsLong(), from(b));
    removeValueIndex(s);
    volume.release(b);
    return Optional.of(s);
  }

  @Override
  public Stream<Stored<JsonObject>> delete(String collection, Predicate<JsonObject> p) {
    return find(collection, p)
        .peek(s->index.removeOffset(s.id()))
        .peek(s->index.removeOffset(collection, s.offset()))
        .peek(this::removeValueIndex)
        .peek(s->volume.release(s.offset()));
  }
  
  private void removeValueIndex(Stored<JsonObject> s) {
    index.valueOffsets().values().stream()
        .forEach(l->l.stream()
            .filter(v->v.offset() == s.offset())
            .forEach(l::remove));
  }

  @Override
  public <V> void createIndex(String collection, String name, Function<JsonObject,V> fn) {
    List<Long> ofs = index.collectionOffsets().get(collection);
    IndexCollection ic = new IndexCollection(collection, name);
    if(ofs != null && !ofs.isEmpty() && !index.valueOffsets().containsKey(ic)) {
      List<IndexValue> ls = new CopyOnWriteArrayList<>();
      index.findOffsetByCollection(collection)
          .mapToObj(volume::get)
          .map(b->Stored.of(b.buffer().position(0).getLong(), b.offset(), from(b)))
          .map(s->new IndexValue(s.offset(), fn.apply(s.object())))
          .forEach(ls::add);
      index.valueOffsets().put(ic, ls);
    }
  }

  @Override
  public JsonIndex index() {
    return index;
  }
  
  @Override
  public void close() {
    try (volume) {
      Block b = volume.metadata();
      byte[] bs = index.toJson().toBuffer().getBytes();
      b.buffer().putInt(bs.length);
      b.buffer().put(bs);
      b.commit();
    }
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 37 * hash + Objects.hashCode(this.volume);
    hash = 37 * hash + Objects.hashCode(this.index);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final DefaultJsonStore other = (DefaultJsonStore) obj;
    if (!Objects.equals(this.volume, other.volume)) {
      return false;
    }
    return Objects.equals(this.index, other.index);
  }

  @Override
  public String toString() {
    return "ObjectStore{" + "volume=" + volume + ", index=" + index + '}';
  }

}
