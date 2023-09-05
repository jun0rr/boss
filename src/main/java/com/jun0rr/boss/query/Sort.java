/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.query;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author F6036477
 */
public record Sort(List<String> fields, boolean sortAsc) {

  public Sort() {
    this(new LinkedList<>(), true);
  }
  
  public Sort add(String field) {
    fields.add(field);
    return this;
  }
  
  public Stream<String> stream() {
    return fields.stream();
  }
  
}
