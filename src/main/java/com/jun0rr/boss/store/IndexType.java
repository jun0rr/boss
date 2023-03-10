/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.store;

import com.jun0rr.binj.BinType;
import com.jun0rr.binj.mapping.Binary;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class IndexType {
  
  private final BinType type;
  
  private final String name;
  
  public IndexType(BinType type, String name) {
    this.type = Objects.requireNonNull(type);
    this.name = Objects.requireNonNull(name);
  }

  @Binary
  public BinType type() {
    return type;
  }

  @Binary
  public String name() {
    return name;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 61 * hash + Objects.hashCode(this.type);
    hash = 61 * hash + Objects.hashCode(this.name);
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
    final IndexType other = (IndexType) obj;
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    return Objects.equals(this.type, other.type);
  }

  @Override
  public String toString() {
    return "IndexType{" + "type=" + type + ", name=" + name + '}';
  }
  
}
