/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.access;

import com.jun0rr.boss.access.Role.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public record Rule(String name, List<Role> roles, List<BossGroup> groups) {
  
  public Rule(String name) {
    this(name, new LinkedList<>(), new LinkedList<>());
  }
  
  public Rule add(Role r) {
    roles.add(Objects.requireNonNull(r));
    return this;
  }
  
  public Rule add(BossGroup g) {
    groups.add(Objects.requireNonNull(g));
    return this;
  }
  
  public boolean match(String uri, Method m, BossUser u) {
    
  }
  
}
