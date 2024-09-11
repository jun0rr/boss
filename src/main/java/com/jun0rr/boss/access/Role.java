/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.jun0rr.boss.access;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 *
 * @author F6036477
 */
public record Role(Class clazz, Predicate test, List<Permission> permissions) {
  
  public Role(Class clazz) {
    this(clazz, x->true, new LinkedList<>());
  }
  
  public Role(Class clazz, Permission... ps) {
    this(clazz, x->true, List.of(Objects.requireNonNull(ps)));
  }
  
  public Role add(Permission p) {
    if(p != null && !permissions.contains(p)) {
      permissions.add(p);
    }
    return this;
  }
  
  public boolean match(Object o, Permission p) {
    return o != null && clazz.isAssignableFrom(o.getClass()) && test.test(o) && permissions.contains(p);
  }
  
  public boolean match(Class c, Permission p) {
    return c != null && clazz.isAssignableFrom(c) && permissions.contains(p);
  }
  
}
