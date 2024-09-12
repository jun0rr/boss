/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.access;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public record BossUser(String name, long salt, byte[] hash, List<BossGroup> groups) implements Subject {
  
  public BossUser {
    Objects.requireNonNull(name, "Name cannot be null");
    if(salt == 0) {
      throw new IllegalArgumentException("Salt cannot be zero");
    }
    Objects.requireNonNull(hash, "Hash cannot be null");
    Objects.requireNonNull(groups, "Groups cannot be null");
  }

  public BossUser(String name, long salt, byte[] hash) {
    this(name, salt, hash, new LinkedList<>());
  }
  
  public BossUser add(BossGroup g) {
    if(g != null && !groups.contains(g)) {
      groups.add(g);
    }
    return this;
  }
  
}
