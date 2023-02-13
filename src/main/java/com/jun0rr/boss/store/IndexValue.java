/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.store;

import com.jun0rr.binj.mapping.Binary;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class IndexValue {
  
  private final Object value;
  
  private final int index;
  
  public IndexValue(Object o, int i) {
    this.value = Objects.requireNonNull(o);
    this.index = i;
  }

  @Binary
  public Object value() {
    return value;
  }

  @Binary
  public int index() {
    return index;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 83 * hash + Objects.hashCode(this.value);
    hash = 83 * hash + this.index;
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
    final IndexValue other = (IndexValue) obj;
    if (this.index != other.index) {
      return false;
    }
    return Objects.equals(this.value, other.value);
  }

  @Override
  public String toString() {
    return "IndexValue{" + "value=" + value + ", index=" + index + '}';
  }
  
}
