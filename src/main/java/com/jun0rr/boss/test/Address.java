/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.binj.mapping.Binary;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class Address {

private final String street;
    
  private final String city;

  private final int number;

  //@MapConstructor({"street", "city", "number"})
  public Address(String street, String city, int number) {
    this.street = Objects.requireNonNull(street);
    this.city = Objects.requireNonNull(city);
    this.number = number;
  }

  @Binary
  public String street() {
    return street;
  }

  @Binary
  public String city() {
    return city;
  }

  @Binary
  public int number() {
    return number;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 29 * hash + Objects.hashCode(this.street);
    hash = 29 * hash + Objects.hashCode(this.city);
    hash = 29 * hash + this.number;
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
    final Address other = (Address) obj;
    if (this.number != other.number) {
      return false;
    }
    if (!Objects.equals(this.street, other.street)) {
      return false;
    }
    return Objects.equals(this.city, other.city);
  }

  @Override
  public String toString() {
    return "Address{" + "street=" + street + ", city=" + city + ", number=" + number + '}';
  }

}
