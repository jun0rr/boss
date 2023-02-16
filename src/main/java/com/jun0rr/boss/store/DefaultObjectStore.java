/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.store;

import com.jun0rr.binj.BinCodec;
import com.jun0rr.binj.BinContext;
import com.jun0rr.binj.ContextEvent;
import com.jun0rr.binj.codec.ArrayCodec;
import com.jun0rr.binj.codec.EnumCodec;
import com.jun0rr.binj.codec.ObjectCodec;
import com.jun0rr.binj.mapping.ExtractFunction;
import com.jun0rr.boss.Block;
import com.jun0rr.boss.ObjectStore;
import com.jun0rr.boss.Volume;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import com.jun0rr.boss.Index;

/**
 *
 * @author F6036477
 */
public class DefaultObjectStore implements ObjectStore {
  
  public static final String KEY_BINTYPES = "bintypes";
  
  public static final String KEY_INDEXES = "indexes";
  
  
  private final Volume volume;
  
  private final Volume idxvol;
  
  private final BinContext context;
  
  private final Index index;
  
  public DefaultObjectStore(Volume v, Volume x, BinContext c, Index i) {
    this.volume = Objects.requireNonNull(v);
    this.idxvol = Objects.requireNonNull(x);
    this.context = Objects.requireNonNull(c);
    this.index = Objects.requireNonNull(i);
    load();
  }

  public DefaultObjectStore(Volume v, Volume x, BinContext c) {
    this.volume = Objects.requireNonNull(v);
    this.idxvol = Objects.requireNonNull(x);
    this.context = Objects.requireNonNull(c);
    this.index = new DefaultIndex();
    load();
  }
  
  private void load() {
    if(idxvol.isLoaded()) {
      Index i = context.read(idxvol.get(idxvol.blockSize()).buffer());
      index.classIndex().putAll(i.classIndex());
      index.idIndex().putAll(i.idIndex());
      index.valueIndex().putAll(i.valueIndex());
      index.types().addAll(i.types());
      index.types().stream()
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
    b.buffer().position(0).putLong(evt.checksum());
    index.idIndex().put(evt.checksum(), b.index());
    List<Integer> is = index.classIndex().get(evt.codec().bintype());
    if(is == null) {
      is = new CopyOnWriteArrayList<>();
      index.classIndex().put(evt.codec().bintype(), is);
    }
    is.add(b.index());
    Optional<Entry<IndexType,List<IndexValue>>> entry = index.valueIndex().entrySet().stream()
        .filter(e->e.getKey().type().equals(evt.codec().bintype()))
        .findAny();
    if(entry.isPresent()) {
      Object val = context.mapper().extractStrategy().stream()
          .flatMap(s->s.invokers(o.getClass()).stream())
          .filter(f->f.name().equals(entry.get().getKey().name()))
          .findAny().get().extract(o);
      entry.get().getValue().add(new IndexValue(val, b.index()));
    }
    return Stored.of(evt.checksum(), b.index(), o);
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
  public <T> Stored<T> update(long id, T o) {
    Integer idx = index.idIndex().get(id);
    if(idx == null) {
      throw new ObjectStoreException("ID not found (%d)", id);
    }
    volume.release(idx);
    return store(o);
  }

  @Override
  public <T> Stored<T> update(long id, UnaryOperator<T> update) {
    Integer idx = index.idIndex().get(id);
    if(idx == null) {
      throw new ObjectStoreException("ID not found (%d)", id);
    }
    Block b = volume.get(idx);
    T o = context.read(b.buffer().position(Long.BYTES));
    volume.release(b.index());
    return store(update.apply(o));
  }

  @Override
  public <T> Stream<Stored<T>> find(Class<T> c, Predicate<T> p) {
    return index.findByType(c).mapToObj(volume::get)
        .map(b->Stored.<T>of(b.buffer().position(0).getLong(), b.index(), context.read(b.buffer())))
        .filter(s->p.test(s.object()));
  }

  @Override
  public <T, V> Stream<Stored<T>> find(Class<T> c, String name, V v) {
    return index.findByValue(c, name, v)
        .mapToObj(volume::get)
        .map(b->Stored.<T>of(b.buffer().position(0).getLong(), b.index(), context.read(b.buffer())));
  }

  @Override
  public <T> Optional<Stored<T>> delete(long id) {
    Integer idx = index.idIndex().remove(id);
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
        .peek(l->System.out.printf("ObjectStore.delete(classIndex): %d->%s%n", s.index(), l))
        .findFirst()
        .ifPresent(l->l.remove(Integer.valueOf(s.index()))))
        .peek(s->index.idIndex().remove(s.id()))
        .peek(this::removeValueIndex)
        .peek(s->volume.release(s.index()));
  }
  
  private <T> void removeValueIndex(Stored<T> s) {
    index.valueIndex().entrySet().stream()
        .filter(e->e.getKey().type().isTypeOf(s.object().getClass()))
        .forEach(e->{
          Optional<ExtractFunction> fn = context.mapper().extractStrategy().stream()
              .flatMap(i->i.invokers(s.object().getClass()).stream())
              .filter(f->f.name().equals(e.getKey().name()))
              .findFirst();
          if(fn.isPresent()) {
            Object v = fn.get().extract(s.object());
            System.out.printf("ObjectStore.removeValueIndex( %s ): value=%s -> %s%n", s, v, e.getValue());
            e.getValue().stream().filter(i->i.value().equals(v)).forEach(e.getValue()::remove);
          }
        });
  }

  @Override
  public <T, R> void createIndex(Class<T> c, String name, Function<T, R> fn) {
    IndexType t = new IndexType(context.getBinType(c), name);
    List<IndexValue> ls = new CopyOnWriteArrayList<>();
    index.findByType(c)
        .mapToObj(volume::get)
        .map(b->Stored.of(b.buffer().position(0).getLong(), b.index(), context.read(b.buffer())))
        .map(s->new IndexValue(fn.apply(s.object()), s.index()))
        .forEach(ls::add);
    index.valueIndex().put(t, ls);
  }

  @Override
  public Index index() {
    return index;
  }
  
  @Override
  public void close() {
    Block b = idxvol.isLoaded() ? idxvol.get(idxvol.blockSize()) : idxvol.allocate();
    context.codecs().keySet().stream()
        .filter(t->!BinCodec.DEFAULT_BINTYPES.contains(t))
        .filter(t->!index.types().contains(t))
        .forEach(index.types()::add);
    context.write(b.buffer(), index);
    volume.close();
    idxvol.close();
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 37 * hash + Objects.hashCode(this.volume);
    hash = 37 * hash + Objects.hashCode(this.idxvol);
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
    if (!Objects.equals(this.idxvol, other.idxvol)) {
      return false;
    }
    return Objects.equals(this.index, other.index);
  }

  @Override
  public String toString() {
    return "ObjectStore{" + "volume=" + volume + ", idxvol=" + idxvol + ", index=" + index + '}';
  }

}
