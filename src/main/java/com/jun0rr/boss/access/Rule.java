/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.access;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author F6036477
 */
public record Rule(String name, List<Subject> subjects, List<Permission> permissions) {
  
  public Rule(String name) {
    this(name, new LinkedList<>(), new LinkedList<>());
  }
  
  public Rule add(Permission p) {
    if(p != null && !permissions.contains(p)) {
      permissions.add(p);
    }
    return this;
  }
  
  public Rule add(Subject s) {
    if(s != null && !subjects.contains(s)) {
      subjects.add(s);
    }
    return this;
  }
  
  public boolean match(Subject s, Permission p) {
    return subjects.contains(s) 
        && (permissions.contains(p)
        || permissions.contains(Permission.ALL))
        && !permissions.contains(Permission.DENY);
  }
  
}
