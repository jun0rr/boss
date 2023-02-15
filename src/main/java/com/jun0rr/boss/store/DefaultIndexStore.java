/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.store;

import com.jun0rr.binj.BinType;
import com.jun0rr.boss.IndexStore;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author F6036477
 */
public class DefaultIndexStore implements IndexStore {
  
  private final Map<BinType, List<Integer>> classIndex;
  
  private final Map<Long, Integer> idIndex;
  
  private final Map<IndexType, List<IndexValue>> valueIndex;
  
  public DefaultIndexStore(Map<BinType, List<Integer>> classIndex, Map<Long, Integer> idIndex, Map<IndexType, List<IndexValue>> valueIndex) {
    this.classIndex = Objects.requireNonNull(classIndex);
    this.idIndex = Objects.requireNonNull(idIndex);
    this.valueIndex = Objects.requireNonNull(valueIndex);
  }
  
  public DefaultIndexStore() {
    this.classIndex = new ConcurrentHashMap<>();
    this.idIndex = new ConcurrentHashMap<>();
    this.valueIndex = new ConcurrentHashMap<>();
  }

  @Override
  public Map<BinType, List<Integer>> classIndex() {
    return classIndex;
  }

  @Override
  public Map<Long, Integer> idIndex() {
    return idIndex;
  }

  @Override
  public Map<IndexType, List<IndexValue>> valueIndex() {
    return valueIndex;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 31 * hash + Objects.hashCode(this.classIndex);
    hash = 31 * hash + Objects.hashCode(this.idIndex);
    hash = 31 * hash + Objects.hashCode(this.valueIndex);
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
    final DefaultIndexStore other = (DefaultIndexStore) obj;
    if (!Objects.equals(this.classIndex, other.classIndex)) {
      return false;
    }
    if (!Objects.equals(this.idIndex, other.idIndex)) {
      return false;
    }
    return Objects.equals(this.valueIndex, other.valueIndex);
  }

  @Override
  public String toString() {
    return "DefaultIndexStore{" + "classIndex=" + classIndex + ", idIndex=" + idIndex + ", valueIndex=" + valueIndex + '}';
  }
  
}
