/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.store;

import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class DefaultStored<T> implements Stored<T> {
  
  private final long id;
  
  private final int index;
  
  private final T object;
  
  public DefaultStored(long id, int idx, T obj) {
    this.id = id;
    this.index = idx;
    this.object = Objects.requireNonNull(obj);
  }

  @Override
  public long id() {
    return id;
  }

  @Override
  public int index() {
    return index;
  }

  @Override
  public T object() {
    return object;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 47 * hash + (int) (this.id ^ (this.id >>> 32));
    hash = 47 * hash + this.index;
    hash = 47 * hash + Objects.hashCode(this.object);
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
    final DefaultStored other = (DefaultStored) obj;
    if (this.id != other.id) {
      return false;
    }
    if (this.index != other.index) {
      return false;
    }
    return Objects.equals(this.object, other.object);
  }

  @Override
  public String toString() {
    return "Stored{" + "id=" + id + ", index=" + index + ", object=" + object + '}';
  }
  
}
