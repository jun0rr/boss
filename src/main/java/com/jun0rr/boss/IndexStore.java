/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss;

import com.jun0rr.jbom.BinType;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author F6036477
 */
public interface IndexStore {
  
  public Map<BinType,List<Integer>> classIndex();
  
  public Map<Long,Integer> idIndex();
  
  public Map<IndexType,IndexValue> valueIndex();
  
  public <T> Stream<Integer> find(Class c, String name, Predicate<T> p);
  
}
