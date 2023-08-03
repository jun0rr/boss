/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.store;

import com.jun0rr.binj.BinCodec;
import com.jun0rr.binj.BinContext;
import com.jun0rr.binj.BinType;
import com.jun0rr.binj.codec.ArrayCodec;
import com.jun0rr.binj.codec.EnumCodec;
import com.jun0rr.binj.codec.ObjectCodec;
import com.jun0rr.boss.Block;
import com.jun0rr.boss.Index;
import com.jun0rr.boss.Index.IndexType;
import com.jun0rr.boss.json.JsonIndex;
import com.jun0rr.boss.json.JsonIndex.IndexCollection;
import com.jun0rr.boss.json.JsonStore;
import com.jun0rr.boss.Stored;
import com.jun0rr.boss.Volume;
import com.jun0rr.boss.config.BossConfig;
import com.jun0rr.boss.json.JsonIndex.IndexValue;
import com.jun0rr.boss.query.Either;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
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
  
  private final BossConfig config;
  
  private final Volume volume;
  
  private final BinContext context;
  
  private final JsonIndex index;
  
  public DefaultJsonStore(BossConfig cfg) {
    this.config = Objects.requireNonNull(cfg);
    this.volume = config.volume().createVolume();
    this.context = config.mapper().createBinContext();
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
    storeIndex(collection, o, sum, b);
    return Stored.of(sum, b.offset(), o);
  }
  
  private synchronized void storeIndex(String collection, JsonObject o, long sum, Block b) {
    index.putIndex(collection, b.offset());
    index.putIndex(sum, b.offset());
    index.valueIndex().entrySet().stream()
        .filter(e->e.getKey().collection().equals(collection))
        .forEach(e->insertValueIndex(collection, o, b, e));
  }
  
  private void insertValueIndex(String collection, JsonObject o, Block b, Entry<IndexCollection, List<IndexValue>> entry) {
    List<String> names = List.of(entry.getKey().name().split("\\."));
    Object val = o;
    for(String name : names) {
      final Class cls = val.getClass();
      val = Either.of(val)
          .ifNotNull()
          .andIs(JsonObject.class)
          .thenMap(j->j.getValue(name))
          .a();
    }
    entry.getValue().add(new IndexValue(b.offset(), val));
  }
  
  @Override
  public <T> Optional<Stored<T>> get(long id) {
    Long idx = index.idIndex().get(id);
    if(idx == null) {
      return Optional.empty();
    }
    Block b = volume.get(idx);
    Stored<T> s = Stored.of(b.buffer().position(0).getLong(), idx, context.read(b.buffer()));
    return Optional.of(s);
  }

  @Override
  public <T> Stored<T> update(long id, T o) {
    delete(id).orElseThrow(()->new ObjectStoreException("ID not found (%d)", id));
    return store(o);
  }

  @Override
  public <T> Stored<T> update(long id, UnaryOperator<T> update) {
    Stored<T> s = this.<T>delete(id).orElseThrow(()->new ObjectStoreException("ID not found (%d)", id));
    return store(update.apply(s.object()));
  }

  @Override
  public <T> Stream<Stored<T>> find(Class<T> c, Predicate<T> p) {
    return index.findIndexByType(c).mapToObj(volume::get)
        .map(b->Stored.<T>of(b.buffer().position(0).getLong(), b.offset(), context.read(b.buffer())))
        .peek(System.out::println)
        .filter(s->p.test(s.object()));
  }

  @Override
  public <T, V> Stream<Stored<T>> find(Class<T> c, String name, V v) {
    return index.findIndexByValue(c, name, v)
        .mapToObj(volume::get)
        .peek(System.out::println)
        .map(b->Stored.<T>of(b.buffer().position(0).getLong(), b.offset(), context.read(b.buffer())));
  }

  @Override
  public <T> Optional<Stored<T>> delete(long id) {
    Long idx = index.idIndex().remove(id);
    if(idx == null) {
      return Optional.empty();
    }
    Block b = volume.get(idx);
    Stored<T> s = Stored.of(b.buffer().position(0).getLong(), idx, context.read(b.buffer()));
    index.classIndex().entrySet().stream()
        .filter(e->e.getKey().isTypeOf(s.object().getClass()))
        .map(Entry::getValue)
        .forEach(l->l.remove(idx));
    removeValueIndex(s);
    volume.release(b);
    return Optional.of(s);
  }

  @Override
  public <T> Stream<Stored<T>> delete(Class<T> c, Predicate<T> p) {
    return find(c, p).peek(s->index.classIndex().entrySet().stream()
        .filter(e->e.getKey().isTypeOf(c))
        .map(Entry::getValue)
        .findFirst()
        .ifPresent(l->l.remove(s.offset())))
        .peek(s->index.idIndex().remove(s.id()))
        .peek(this::removeValueIndex)
        .peek(s->volume.release(s.offset()));
  }
  
  private <T> void removeValueIndex(Stored<T> s) {
    for(Entry<IndexType,List<IndexValue>> e : index.valueIndex().entrySet()) {
      if(e.getKey().type().isTypeOf(s.object().getClass())) {
        List<String> names = List.of(e.getKey().name().split("\\."));
        //System.out.printf("ObjectStore.removeValueIndex(%s): names=%s%n", e.getKey().name(), names);
        Object val = s.object();
        for(String name : names) {
          final Object ob = val;
          val = context.mapper().extractStrategies().stream()
            .flatMap(t->t.invokers(ob.getClass()).stream())
            .filter(f->f.name().equals(name))
            .findFirst().get().extract(val);
          //System.out.println("ObjectStore.removeValueIndex(): val=" + val);
        }
        final Object vl = val;
        e.getValue().stream()
            .filter(v->v.value().equals(vl))
            .forEach(e.getValue()::remove);
      }
    }
  }

  @Override
  public <T, R> void createIndex(Class<T> c, String name, Function<T, R> fn) {
    IndexType t = new IndexType(context.getBinType(c), name);
    List<IndexValue> ls = new CopyOnWriteArrayList<>();
    index.findIndexByType(c)
        .mapToObj(volume::get)
        .map(b->Stored.of(b.buffer().position(0).getLong(), b.offset(), context.read(b.buffer())))
        .map(s->new IndexValue(s.offset(), fn.apply((T)s.object())))
        .forEach(ls::add);
    index.valueIndex().put(t, ls);
  }

  @Override
  public Index index() {
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
