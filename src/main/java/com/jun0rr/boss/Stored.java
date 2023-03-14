/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss;

/**
 *
 * @author F6036477
 */
public record Stored<T>(long id, long offset, T object) {
  
  public static <U> Stored<U> of(long id, long offset, U obj) {
    return new Stored(id, offset, obj);
  }
  
}
