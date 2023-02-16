/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.store;

import com.jun0rr.binj.BinType;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import com.jun0rr.boss.Index;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author F6036477
 */
public class DefaultIndex implements Index {
  
  private final Map<BinType, List<Integer>> classIndex;
  
  private final Map<Long, Integer> idIndex;
  
  private final Map<IndexType, List<IndexValue>> valueIndex;
  
  private final List<BinType> types;
  
  public DefaultIndex(List<BinType> types, Map<BinType, List<Integer>> classIndex, Map<Long, Integer> idIndex, Map<IndexType, List<IndexValue>> valueIndex) {
    this.types = Objects.requireNonNull(types);
    this.classIndex = Objects.requireNonNull(classIndex);
    this.idIndex = Objects.requireNonNull(idIndex);
    this.valueIndex = Objects.requireNonNull(valueIndex);
  }
  
  public DefaultIndex() {
    this.types = new CopyOnWriteArrayList<>();
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
  public List<BinType> types() {
    return types;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 67 * hash + Objects.hashCode(this.classIndex);
    hash = 67 * hash + Objects.hashCode(this.idIndex);
    hash = 67 * hash + Objects.hashCode(this.valueIndex);
    hash = 67 * hash + Objects.hashCode(this.types);
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
    final DefaultIndex other = (DefaultIndex) obj;
    if (!Objects.equals(this.classIndex, other.classIndex)) {
      return false;
    }
    if (!Objects.equals(this.idIndex, other.idIndex)) {
      return false;
    }
    if (!Objects.equals(this.valueIndex, other.valueIndex)) {
      return false;
    }
    return Objects.equals(this.types, other.types);
  }

  @Override
  public String toString() {
    return "Index{" + "classIndex=" + classIndex + ", idIndex=" + idIndex + ", valueIndex=" + valueIndex + ", types=" + types + '}';
  }

}
