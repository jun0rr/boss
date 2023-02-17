/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss.store;

import com.jun0rr.boss.store.DefaultStored;

/**
 *
 * @author F6036477
 */
public interface Stored<T> {
  
  public long id();
  
  public int index();
  
  public T object();
  
  
  public static <U> Stored<U> of(long id, int idx, U obj) {
    return new DefaultStored(id, idx, obj);
  }
  
}
