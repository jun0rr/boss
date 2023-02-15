/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.binj.mapping.Binary;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class Person {

private final String name;
    
  private final String last;

  private final LocalDate birth;

  private final Address address;

  private final long[] ids;

  //@MapConstructor({"name", "last", "birth", "address", "ids"})
  public Person(String name, String last, LocalDate birth, Address address, long[] ids) {
    this.name = Objects.requireNonNull(name);
    this.last = Objects.requireNonNull(last);
    this.birth = Objects.requireNonNull(birth);
    this.address = Objects.requireNonNull(address);
    this.ids = ids;
  }

  @Binary
  public String name() {
    return name;
  }

  @Binary
  public String last() {
    return last;
  }

  @Binary
  public LocalDate birth() {
    return birth;
  }

  @Binary
  public long[] ids() {
    return ids;
  }

  @Binary
  public Address address() {
    return address;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 89 * hash + Objects.hashCode(this.name);
    hash = 89 * hash + Objects.hashCode(this.last);
    hash = 89 * hash + Objects.hashCode(this.birth);
    hash = 89 * hash + Objects.hashCode(this.address);
    hash = 89 * hash + Arrays.hashCode(this.ids);
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
    final Person other = (Person) obj;
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    if (!Objects.equals(this.last, other.last)) {
      return false;
    }
    if (!Objects.equals(this.birth, other.birth)) {
      return false;
    }
    if (!Objects.equals(this.address, other.address)) {
      return false;
    }
    return Arrays.equals(this.ids, other.ids);
  }

  @Override
  public String toString() {
    return "Person{" + "name=" + name + ", last=" + last + ", birth=" + birth + ", address=" + address + ", ids=" + Arrays.toString(ids) + '}';
  }

}
