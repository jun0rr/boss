/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.config;

import com.jun0rr.binj.BinCodec;
import com.jun0rr.binj.BinContext;
import com.jun0rr.binj.BinType;
import com.jun0rr.uncheck.Uncheck;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 *
 * @author F6036477
 */
public record CodecConfig(Class type, Class codec) {
  
  public BinCodec createCodec(BinContext ctx) {
    BinType bt = BinType.of(type);
    List<Constructor> cts = List.of(codec.getDeclaredConstructors()).stream()
        .filter(c->Modifier.isPublic(c.getModifiers()))
        .toList();
    Optional<Constructor> oct = cts.stream()
        .filter(c->c.getParameterCount() == 0)
        .findAny();
    BinCodec codec = null;
    if(oct.isPresent()) {
      Constructor c = oct.get();
      codec = (BinCodec) Uncheck.call(()->c.newInstance());
    }
    Predicate<Class> pred = c->BinType.class.isAssignableFrom(c) || BinContext.class.isAssignableFrom(c);
    oct = cts.stream()
        .filter(c->c.getParameterCount() == 1)
        .filter(c->pred.test(c.getParameterTypes()[0]))
        .findAny();
    if(codec == null && oct.isPresent()) {
      Constructor c = oct.get();
      if(BinContext.class.isAssignableFrom(c.getParameterTypes()[0])) {
        codec = (BinCodec) Uncheck.call(()->c.newInstance(ctx));
      }
      else {
        codec = (BinCodec) Uncheck.call(()->c.newInstance(bt));
      }
    }
    oct = cts.stream()
        .filter(c->c.getParameterCount() == 2)
        .filter(c->pred.test(c.getParameterTypes()[0]) || pred.test(c.getParameterTypes()[1]))
        .findAny();
    if(codec == null && oct.isPresent()) {
      Constructor c = oct.get();
      if(BinContext.class.isAssignableFrom(c.getParameterTypes()[0])) {
        codec = (BinCodec) Uncheck.call(()->c.newInstance(ctx, bt));
      }
      else {
        codec = (BinCodec) Uncheck.call(()->c.newInstance(bt, ctx));
      }
    }
    if(codec == null) {
      throw new IllegalStateException("Constructor not found for " + type);
    }
    return codec;
  }
  
  public static CodecConfig from(Map map) {
    String st = (String) map.get("type");
    String sc = (String) map.get("codec");
    if(st == null || sc == null) {
      throw new IllegalArgumentException("Required keys ['type', 'codec'] not found in " + map);
    }
    return new CodecConfig(MapperConfig.ofClassName(st), MapperConfig.ofClassName(sc));
  }
  
}
