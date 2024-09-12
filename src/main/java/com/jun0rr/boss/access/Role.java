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
public record Role(Class clazz, Predicate test, List<Rule> rules) {
  
  public Role {
    Objects.requireNonNull(clazz, "Class cannot be null");
    Objects.requireNonNull(test, "Test predicate cannot be null");
    Objects.requireNonNull(rules, "Permissions list cannot be null");
  }
  
  public Role(Class clazz) {
    this(clazz, x->true, new LinkedList<>());
  }
  
  public Role(Class clazz, Rule... rs) {
    this(clazz, x->true, List.of(Objects.requireNonNull(rs)));
  }
  
  public Role add(Rule r) {
    if(r != null && !rules.contains(r)) {
      rules.add(r);
    }
    return this;
  }
  
  public boolean match(Object o, Subject s, Permission p) {
    return o != null 
        && test.test(o) 
        && match(o.getClass(), s, p);
  }
  
  public boolean match(Class c, Subject s, Permission p) {
    return clazz.isAssignableFrom(c) 
        && rules.stream().anyMatch(r->r.match(s, p));
  }
  
}
