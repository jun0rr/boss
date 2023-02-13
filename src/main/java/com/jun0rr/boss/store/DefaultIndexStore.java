/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.store;

import com.jun0rr.boss.IndexStore;
import com.jun0rr.jbom.BinType;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author F6036477
 */
public class DefaultIndexStore implements IndexStore {
  
  private final Map<BinType, List<Integer>> classIndex;
  
  private final Map<Long, Integer> idIndex;
  
  private final Map<IndexType, List<IndexValue>> valueIndex;
  
  public DefaultIndexStore() {
    this.classIndex = new ConcurrentHashMap<>();
    this.idIndex = new ConcurrentHashMap<>();
    this.valueIndex = new ConcurrentHashMap<>();
  }

  @Override
  public Map<BinType, List<Integer>> classIndex() {
    return classIndex;
  }

  @Override
  public Map<Long, Integer> idIndex() {
    return idIndex;
  }

  @Override
  public Map<IndexType, List<IndexValue>> valueIndex() {
    return valueIndex;
  }
  
}
