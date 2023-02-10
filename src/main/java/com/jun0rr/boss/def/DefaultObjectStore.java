/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.def;

import com.jun0rr.boss.Block;
import com.jun0rr.boss.IndexStore;
import com.jun0rr.boss.IndexType;
import com.jun0rr.boss.IndexValue;
import com.jun0rr.boss.ObjectStore;
import com.jun0rr.boss.ObjectStoreException;
import com.jun0rr.boss.Stored;
import com.jun0rr.boss.Volume;
import com.jun0rr.jbom.BinContext;
import com.jun0rr.jbom.ContextEvent;
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
public class DefaultObjectStore implements ObjectStore {
  
  private final Volume volume;
  
  private final BinContext context;
  
  private final IndexStore index;
  
  public DefaultObjectStore(Volume v, BinContext c) {
    this.volume = Objects.requireNonNull(v);
    this.context = Objects.requireNonNull(c);
    this.index = new DefaultIndexStore();
  }

  @Override
  public long store(Object o) {
    Block b = volume.allocate();
    ContextEvent evt = context.write(b.buffer().position(Long.BYTES), o);
    b.buffer().position(0).putLong(evt.checksum());
    index.idIndex().put(evt.checksum(), b.offset());
    List<Integer> is = index.classIndex().get(evt.codec().bintype());
    if(is == null) {
      is = new CopyOnWriteArrayList<>();
      index.classIndex().put(evt.codec().bintype(), is);
    }
    is.add(b.offset());
    Optional<Entry<IndexType,List<IndexValue>>> entry = index.valueIndex().entrySet().stream()
        .filter(e->e.getKey().type().equals(evt.codec().bintype()))
        .findAny();
    if(entry.isPresent()) {
      Object val = context.mapper().extractStrategy().stream()
          .flatMap(s->s.extractors(o.getClass()).stream())
          .filter(f->f.name().equals(entry.get().getKey().name()))
          .findAny().get().extract(o);
      entry.get().getValue().add(new DefaultIndexValue(val, b.offset()));
    }
    return evt.checksum();
  }
  
  @Override
  public <T> Optional<Stored<T>> get(long id) {
    Integer idx = index.idIndex().get(id);
    if(idx == null) {
      return Optional.empty();
    }
    Block b = volume.get(idx);
    Stored<T> s = Stored.of(b.buffer().position(0).getLong(), idx, context.read(b.buffer()));
    return Optional.of(s);
  }

  @Override
  public long update(long id, Object o) {
    Integer idx = index.idIndex().get(id);
    if(idx == null) {
      throw new ObjectStoreException("ID not found (%d)", id);
    }
    volume.release(idx);
    return store(o);
  }

  @Override
  public <T> long update(long id, UnaryOperator<T> update) {
    Integer idx = index.idIndex().get(id);
    if(idx == null) {
      throw new ObjectStoreException("ID not found (%d)", id);
    }
    Block b = volume.get(idx);
    T o = context.read(b.buffer().position(Long.BYTES));
    volume.release(b.offset());
    return store(update.apply(o));
  }

  @Override
  public <T> Stream<Stored<T>> find(Class<T> c, Predicate<T> p) {
    return index.findByType(c).mapToObj(volume::get)
        .map(b->Stored.<T>of(b.buffer().position(0).getLong(), b.offset(), context.read(b.buffer())))
        .filter(s->p.test(s.object()));
  }

  @Override
  public <T, V> Stream<Stored<T>> find(Class<T> c, String name, Predicate<V> p) {
    return index.findByValue(c, name, p)
        .mapToObj(volume::get)
        .map(b->Stored.<T>of(b.buffer().position(0).getLong(), b.offset(), context.read(b.buffer())));
  }

  @Override
  public <T> Optional<Stored<T>> delete(long id) {
    Integer idx = index.idIndex().get(id);
    if(idx == null) {
      return Optional.empty();
    }
    Block b = volume.get(idx);
    Stored<T> s = Stored.of(b.buffer().position(0).getLong(), idx, context.read(b.buffer()));
    volume.release(b);
    return Optional.of(s);
  }

  @Override
  public <T> Stream<Stored<T>> delete(Class<T> c, Predicate<T> p) {
    return find(c, p).peek(s->volume.release(s.index()));
  }

  @Override
  public <T, R> void createIndex(Class<T> c, String name, Function<T, R> fn) {
    IndexType t = new DefaultIndexType(context.getBinType(c), name);
    List<IndexValue> ls = new CopyOnWriteArrayList<>();
    index.findByType(c)
        .mapToObj(volume::get)
        .map(b->Stored.of(b.buffer().position(0).getLong(), b.offset(), context.read(b.buffer())))
        .map(s->new DefaultIndexValue(fn.apply(s.object()), s.index()))
        .forEach(ls::add);
    index.valueIndex().put(t, ls);
  }

  @Override
  public IndexStore index() {
    return index;
  }
  
}
