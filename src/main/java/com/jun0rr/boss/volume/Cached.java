/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.volume;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author F6036477
 */
public class Cached<T> implements Comparable<Cached> {
  
  private final T content;
  
  private final AtomicInteger count;
  
  private final Instant time;
  
  public Cached(T cont) {
    this.content = Objects.requireNonNull(cont);
    this.count = new AtomicInteger(0);
    this.time = Instant.now();
  }
  
  public T content() {
    count.incrementAndGet();
    return content;
  }
  
  public int count() {
    return count.get();
  }
  
  public Instant time() {
    return time;
  }
  
  @Override
  public int compareTo(Cached o) {
    int c = Integer.compare(count(), o.count());
    return c != 0 ? c : time().compareTo(o.time());
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 83 * hash + Objects.hashCode(this.content);
    hash = 83 * hash + Objects.hashCode(this.count);
    hash = 83 * hash + Objects.hashCode(this.time);
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
    final Cached<?> other = (Cached<?>) obj;
    if (!Objects.equals(this.content, other.content)) {
      return false;
    }
    if (!Objects.equals(this.count, other.count)) {
      return false;
    }
    return Objects.equals(this.time, other.time);
  }

  @Override
  public String toString() {
    return "Cached{" + "content=" + content + ", count=" + count + ", time=" + time + '}';
  }
  
}
