/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.config;

import com.jun0rr.binj.mapping.ConstructFunction;
import com.jun0rr.binj.mapping.ExtractFunction;
import com.jun0rr.binj.mapping.InjectFunction;
import com.jun0rr.binj.mapping.InvokeStrategy;
import com.jun0rr.binj.mapping.NoArgsConstructStrategy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author F6036477
 */
public record MapperConfig(
    List<InvokeStrategy<ConstructFunction>> construct, 
    List<InvokeStrategy<ExtractFunction>> extract, 
    List<InvokeStrategy<InjectFunction>> inject
) {
  
  private static Class ofClassName(String name) {
    try {
      return Class.forName(name);
    }
    catch(ClassNotFoundException | ClassCastException e) {
      throw new BossConfigException(e);
    }
  }
  
  public static MapperConfig from(Map map) {
    List<String> cs = (List) map.get("construct");
    if(cs == null) {
      throw new BossConfigException("Bad null MapperConfig.construct");
    }
    List<String> es = (List)map.get("extract");
    if(es == null) {
      throw new BossConfigException("Bad null MapperConfig.extract");
    }
    List<String> is = (List)map.get("inject");
    NoArgsConstructStrategy nc = new NoArgsConstructStrategy();
    List<InvokeStrategy<ConstructFunction>> construct = cs.stream()
        .map(MapperConfig::ofClassName)
        .map(nc::invokers)
        .flatMap(List::stream)
        .map(ConstructFunction::create)
        .map(f->(InvokeStrategy<ConstructFunction>)f)
        .collect(Collectors.toList());
    List<InvokeStrategy<ExtractFunction>> extract = es.stream()
        .map(MapperConfig::ofClassName)
        .map(nc::invokers)
        .flatMap(List::stream)
        .map(ConstructFunction::create)
        .map(f->(InvokeStrategy<ExtractFunction>)f)
        .collect(Collectors.toList());
    List<InvokeStrategy<InjectFunction>> inject = is == null || is.isEmpty() ? Collections.EMPTY_LIST : is.stream()
        .map(MapperConfig::ofClassName)
        .map(nc::invokers)
        .flatMap(List::stream)
        .map(ConstructFunction::create)
        .map(f->(InvokeStrategy<InjectFunction>)f)
        .collect(Collectors.toList());
    return new MapperConfig(construct, extract, inject);
  }
  
}
