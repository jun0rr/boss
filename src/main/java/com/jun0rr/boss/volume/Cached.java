/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.volume;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author F6036477
 */
public record Cached<T>(T content, AtomicInteger count, Instant time) implements Comparable<Cached> {
  
  public Cached(T content) {
    this(content, new AtomicInteger(0), Instant.now());
  }
  
  @Override
  public int compareTo(Cached o) {
    int c = Integer.compare(count().get(), o.count().get());
    return c != 0 ? c : time().compareTo(o.time());
  }

  public static <U> Cached<U> of(U cont) {
    return new Cached(cont);
  }
  
}
