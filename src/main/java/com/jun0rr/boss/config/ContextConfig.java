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

/**
 *
 * @author F6036477
 */
public record ContextConfig(BinContext context) {
  
  public static Class ofClassName(String name) {
    try {
      return Class.forName(name);
    }
    catch(ClassNotFoundException | ClassCastException e) {
      throw new BossConfigException(e);
    }
  }
  
  public static ContextConfig from(Map map) {
    List<String> cs = (List) map.get("construct");
    if(cs == null) {
      throw new BossConfigException("Bad null MapperConfig.construct");
    }
    List<String> es = (List)map.get("extract");
    if(es == null) {
      throw new BossConfigException("Bad null MapperConfig.extract");
    }
    BinContext ctx = BinContext.newContext();
    List<String> is = (List)map.get("inject");
    NoArgsConstructStrategy nc = new NoArgsConstructStrategy();
    cs.stream()
        .map(ContextConfig::ofClassName)
        .map(nc::invokers)
        .flatMap(List::stream)
        .map(ConstructFunction::create)
        .map(f->(InvokeStrategy<ConstructFunction>)f)
        .map(Indexed.builder())
        .peek(i->System.out.printf("-> MapperConfig.constructors: %s%n", i))
        .forEach(i->ctx.mapper().constructStrategies().put(i.index(), i.value()));
    CombinedStrategy<ExtractFunction> extract = CombinedStrategy.newStrategy();
    es.stream()
        .map(ContextConfig::ofClassName)
        .map(nc::invokers)
        .flatMap(List::stream)
        .map(ConstructFunction::create)
        .map(f->(InvokeStrategy<ExtractFunction>)f)
        .map(Indexed.builder())
        .peek(i->System.out.printf("-> MapperConfig.extractors: %s%n", i))
        .forEach(i->ctx.mapper().extractStrategies().put(i.index(), i.value()));
    ((List<String>)(is == null || is.isEmpty() ? Collections.EMPTY_LIST : is)).stream()
        .map(ContextConfig::ofClassName)
        .map(nc::invokers)
        .flatMap(List::stream)
        .map(ConstructFunction::create)
        .map(f->(InvokeStrategy<InjectFunction>)f)
        .map(Indexed.builder())
        .peek(i->System.out.printf("-> MapperConfig.injectors: %s%n", i))
        .forEach(i->ctx.mapper().injectStrategies().put(i.index(), i.value()));
    List<Map> ts = (List) map.get("codecs");
    ((List<Map>)(ts == null ? Collections.EMPTY_LIST : ts))
        .stream()
        .map(CodecConfig::from)
        .map(c->c.createCodec(ctx))
        .forEach(c->ctx.putIfAbsent(c.bintype(), c));
    return new ContextConfig(ctx);
  }
  
}
