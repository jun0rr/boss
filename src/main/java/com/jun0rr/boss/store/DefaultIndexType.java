/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.store;

import com.jun0rr.jbom.BinType;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class DefaultIndexType implements IndexType {
  
  private final BinType type;
  
  private final String name;
  
  public DefaultIndexType(BinType t, String name) {
    this.type = Objects.requireNonNull(t);
    this.name = Objects.requireNonNull(name);
  }

  @Override
  public BinType type() {
    return type;
  }

  @Override
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
    final DefaultIndexType other = (DefaultIndexType) obj;
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
