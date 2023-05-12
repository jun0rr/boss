/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.volume;

import com.jun0rr.uncheck.Uncheck;
import com.jun0rr.uncheck.Uncheck.ThrowableRunner;
import com.jun0rr.uncheck.Uncheck.ThrowableSupplier;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author F6036477
 */
public class Async<T> {
  
  private final AtomicReference<T> ref;
  
  private final AtomicReference<Throwable> err;
  
  private final List<Consumer<T>> listeners;
  
  private final List<Consumer<Throwable>> errListeners;
  
  private final List<Consumer<Async<T>>> doneListeners;
  
  private final CountDownLatch countdown;
  
  public Async() {
    this.ref = new AtomicReference();
    this.err = new AtomicReference();
    this.listeners = new CopyOnWriteArrayList<>();
    this.errListeners = new CopyOnWriteArrayList<>();
    this.doneListeners = new CopyOnWriteArrayList<>();
    this.countdown = new CountDownLatch(1);
  }
  
  public boolean isCompleted() {
    return isDone() && err.get() == null;
  }
  
  public boolean isFailed() {
    return isDone() && err.get() != null;
  }
  
  public boolean isDone() {
    return countdown.getCount() == 0;
  }
  
  public T get() {
    return ref.get();
  }
  
  public Throwable error() {
    return err.get();
  }
  
  public Async<T> complete(T val) {
    if(ref.compareAndSet(null, val)) {
      listeners.forEach(c->c.accept(val));
      doneListeners.forEach(c->c.accept(this));
      countdown.countDown();
    }
    return this;
  }
  
  public Async<T> fail(Throwable val) {
    if(err.compareAndSet(null, val)) {
      errListeners.forEach(c->c.accept(val));
      doneListeners.forEach(c->c.accept(this));
      countdown.countDown();
    }
    return this;
  }
  
  public Async<T> onComplete(Consumer<T> lst) {
    if(isCompleted()) lst.accept(ref.get());
    else listeners.add(lst);
    return this;
  }
  
  public Async<T> onError(Consumer<Throwable> lst) {
    if(isFailed()) lst.accept(err.get());
    else errListeners.add(lst);
    return this;
  }
  
  public Async<T> onDone(Consumer<Async<T>> lst) {
    if(isDone()) lst.accept(this);
    else doneListeners.add(lst);
    return this;
  }
  
  public Async<T> join(Async<?> a) {
    return onDone(x->a.waitDone());
  }
  
  public <U> Async<U> map(Function<T,U> fn) {
    Async<U> a = new Async();
    onComplete(v->a.complete(fn.apply(v)));
    onError(a::fail);
    return a;
  }
  
  public Async<T> waitDone() {
    System.out.printf("Async.waitDone(1): countdown=%s%n", countdown);
    Uncheck.call(()->countdown.await());
    System.out.printf("Async.waitDone(2): Done!%n");
    return this;
  }
  
  public Async<T> waitDone(long millis, TimeUnit unit) {
    Uncheck.call(()->countdown.await(millis, unit));
    return this;
  }
  
  public static <U> Async<U> exec(ThrowableSupplier<U> su) {
    Async<U> a = new Async();
    ForkJoinPool.commonPool().submit(()->{
      try {
        a.complete(su.get());
      }
      catch(Throwable e) {
        a.fail(e);
      }
    });
    return a;
  }
  
  public static Async<Void> exec(ThrowableRunner rn) {
    Async<Void> a = new Async();
    ForkJoinPool.commonPool().submit(()->{
      try {
        rn.run();
        a.complete(null);
      }
      catch(Throwable e) {
        a.fail(e);
      }
    });
    return a;
  }
  
}
