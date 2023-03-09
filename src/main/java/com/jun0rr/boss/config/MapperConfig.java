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
import java.util.List;
import java.util.stream.Collectors;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author F6036477
 */
public record MapperConfig(List<String> construct, List<String> extract, List<String> inject) {
  
  public List<InvokeStrategy<ConstructFunction>> constructStrategy() {
    NoArgsConstructStrategy cs = new NoArgsConstructStrategy();
    return construct().stream()
        .map(this::ofClassName)
        .map(cs::invokers)
        .flatMap(List::stream)
        .map(ConstructFunction::create)
        .map(f->(InvokeStrategy<ConstructFunction>)f)
        .collect(Collectors.toList())
        ;
  }
  
  public List<InvokeStrategy<ExtractFunction>> extractStrategy() {
    NoArgsConstructStrategy cs = new NoArgsConstructStrategy();
    return construct().stream()
        .map(this::ofClassName)
        .map(cs::invokers)
        .flatMap(List::stream)
        .map(ConstructFunction::create)
        .map(f->(InvokeStrategy<ExtractFunction>)f)
        .collect(Collectors.toList())
        ;
  }
  
  public List<InvokeStrategy<InjectFunction>> injectStrategy() {
    NoArgsConstructStrategy cs = new NoArgsConstructStrategy();
    return construct().stream()
        .map(this::ofClassName)
        .map(cs::invokers)
        .flatMap(List::stream)
        .map(ConstructFunction::create)
        .map(f->(InvokeStrategy<InjectFunction>)f)
        .collect(Collectors.toList())
        ;
  }
  
  private Class ofClassName(String name) {
    try {
      return Class.forName(name);
    }
    catch(ClassNotFoundException | ClassCastException e) {
      throw new BossConfigException(e);
    }
  }
  
}
