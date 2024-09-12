/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.store;

import com.jun0rr.binj.BinCodec;
import com.jun0rr.binj.BinContext;
import com.jun0rr.binj.BinType;
import com.jun0rr.binj.ContextEvent;
import com.jun0rr.binj.codec.ArrayCodec;
import com.jun0rr.binj.codec.EnumCodec;
import com.jun0rr.binj.codec.ObjectCodec;
import com.jun0rr.boss.Block;
import com.jun0rr.boss.Index;
import com.jun0rr.boss.Index.IndexType;
import com.jun0rr.boss.Index.IndexValue;
import com.jun0rr.boss.ObjectStore;
import com.jun0rr.boss.Stored;
import com.jun0rr.boss.Volume;
import com.jun0rr.boss.config.BossConfig;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
public class DefaultObjectStore implements ObjectStore {
  
  private final BossConfig config;
  
  private final Volume volume;
  
  private final BinContext context;
  
  private final Index index;
  
  public DefaultObjectStore(BossConfig cfg) {
    this.config = Objects.requireNonNull(cfg);
    this.volume = config.volume().createVolume();
    this.context = config.mapping().context();
    this.index = new Index();
    load();
  }

  private void load() {
    if(volume.isLoaded()) {
      Block b = volume.metadata();
      List<BinType> types = context.read(b.buffer());
      //System.out.println("ObjectStore.load(): types=" + types);
      types.stream()
          .filter(t->!context.codecs().containsKey(t))
          .forEach(t->{
            BinCodec codec;
            if(t.type().isEnum()) {
              codec = new EnumCodec(context);
            }
            else if(t.type().isArray()) {
              codec = new ArrayCodec(context, t);
            }
            else {
              codec = new ObjectCodec(context, t);
            }
            context.codecs().put(t, codec);
          });
      Index i = context.read(b.buffer());
      index.classIndex().putAll(i.classIndex());
      index.idIndex().putAll(i.idIndex());
      index.valueIndex().putAll(i.valueIndex());
      //System.out.println(index);
      volume.release(b);
    }
  }
  
  @Override
  public boolean isLoaded() {
    return volume.isLoaded();
  }

  @Override
  public <T> Stored<T> store(T o) {
    Block b = volume.allocate();
    ContextEvent evt = context.write(b.buffer().position(Long.BYTES), o);
    //System.out.printf("* ObjectStore.store( %s ): evt=%s%n", o, evt);
    b.buffer().position(0).putLong(evt.checksum());
    b.commit();
    storeIndex(o, b, evt);
    return Stored.of(evt.checksum(), b.offset(), o);
  }
  
  private synchronized void storeIndex(Object o, Block b, ContextEvent evt) {
    index.putIndex(evt.checksum(), b.offset());
    index.putIndex(evt.codec().bintype(), b.offset());
    index.valueIndex().entrySet().stream()
        .filter(e->e.getKey().type().equals(evt.codec().bintype()))
        .forEach(e->insertValueIndex(o, b, e));
  }
  
  private void insertValueIndex(Object val, Block b, Entry<IndexType, List<IndexValue>> entry) {
    List<String> names = List.of(entry.getKey().name().split("\\."));
    //System.out.printf("ObjectStore.updateValueIndex(%s): names=%s%n", entry.getKey().name(), names);
    for(String name : names) {
      final Class cls = val.getClass();
      val = context.mapper().extractStrategies().strategies().values().stream()
        .flatMap(s->s.invokers(cls).stream())
        .filter(f->f.name().equals(name))
        .findFirst().get().extract(val);
      //System.out.println("ObjectStore.updateValueIndex(): val=" + val);
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
        //.peek(System.out::println)
        .filter(s->p.test(s.object()));
  }

  @Override
  public <T, V> Stream<Stored<T>> find(Class<T> c, String name, V v) {
    return index.findIndexByValue(c, name, v)
        .mapToObj(volume::get)
        //.peek(System.out::println)
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
          val = context.mapper().extractStrategies().strategies().values().stream()
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
      List<BinType> types = new LinkedList<>();
      context.codecs().keySet().stream()
          .filter(t->!BinCodec.DEFAULT_BINTYPES.contains(t))
          .forEach(types::add);
      context.write(b.buffer(), types);
      context.write(b.buffer(), index);
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
    final DefaultObjectStore other = (DefaultObjectStore) obj;
    if (!Objects.equals(this.volume, other.volume)) {
      return false;
    }
    return Objects.equals(this.index, other.index);
  }

  @Override
  public String toString() {
    return "ObjectStore{" + "volume=" + volume + ", index=" + index + '}';
  }

  @Override
  public <T> Stream<Stored<T>> find(Class<T> c, Map<String, Object> values) {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  @Override
  public <T, R> void createIndex(Class<T> c, Map<String, Function<T, R>> map) {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

}
