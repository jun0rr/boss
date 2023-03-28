/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.config;

import com.jun0rr.binj.BinContext;
import com.jun0rr.binj.mapping.CombinedStrategy;
import com.jun0rr.binj.mapping.ConstructFunction;
import com.jun0rr.binj.mapping.ExtractFunction;
import com.jun0rr.binj.mapping.InjectFunction;
import com.jun0rr.binj.mapping.InvokeStrategy;
import com.jun0rr.binj.mapping.NoArgsConstructStrategy;
import com.jun0rr.indexed.Indexed;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author F6036477
 */
public record MapperConfig(
    InvokeStrategy<ConstructFunction> construct, 
    InvokeStrategy<ExtractFunction> extract, 
    InvokeStrategy<InjectFunction> inject,
    List<CodecConfig> codecs
) {
  
  public static Class ofClassName(String name) {
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
    CombinedStrategy<ConstructFunction> construct = CombinedStrategy.newStrategy();
    cs.stream()
        .map(MapperConfig::ofClassName)
        .map(nc::invokers)
        .flatMap(List::stream)
        .map(ConstructFunction::create)
        .map(f->(InvokeStrategy<ConstructFunction>)f)
        .map(Indexed.indexed())
        .forEach(i->construct.put(i.index(), i.value()));
    CombinedStrategy<ExtractFunction> extract = CombinedStrategy.newStrategy();
    es.stream()
        .map(MapperConfig::ofClassName)
        .map(nc::invokers)
        .flatMap(List::stream)
        .map(ConstructFunction::create)
        .map(f->(InvokeStrategy<ExtractFunction>)f)
        .map(Indexed.indexed())
        .forEach(i->extract.put(i.index(), i.value()));
    CombinedStrategy<InjectFunction> inject = CombinedStrategy.newStrategy();
    ((List<String>)(is == null || is.isEmpty() ? Collections.EMPTY_LIST : is)).stream()
        .map(MapperConfig::ofClassName)
        .map(nc::invokers)
        .flatMap(List::stream)
        .map(ConstructFunction::create)
        .map(f->(InvokeStrategy<InjectFunction>)f)
        .map(Indexed.indexed())
        .forEach(i->inject.put(i.index(), i.value()));
    List<Map> ts = (List) map.get("codecs");
    List<CodecConfig> codecs = ts == null ? Collections.EMPTY_LIST : ts.stream()
        .map(CodecConfig::from)
        .collect(Collectors.toList());
    return new MapperConfig(construct, extract, inject, codecs);
  }
  
  public BinContext createBinContext() {
    BinContext ctx = BinContext.newContext();
    ctx.mapper().constructStrategies().add(construct);
    ctx.mapper().extractStrategies().add(extract);
    ctx.mapper().injectStrategies().add(inject);
    codecs.stream()
        .map(c->c.createCodec(ctx))
        .forEach(c->ctx.codecs().put(c.bintype(), c));
    return ctx;
  }
  
}
