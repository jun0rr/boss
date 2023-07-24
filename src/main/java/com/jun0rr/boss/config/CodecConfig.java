/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.config;

import com.jun0rr.binj.BinCodec;
import com.jun0rr.binj.BinContext;
import com.jun0rr.binj.BinType;
import com.jun0rr.binj.codec.ArrayCodec;
import com.jun0rr.binj.codec.EnumCodec;
import com.jun0rr.binj.codec.ObjectCodec;
import com.jun0rr.binj.mapping.CombinedStrategy;
import com.jun0rr.binj.mapping.ConstructFunction;
import com.jun0rr.binj.mapping.NoArgsConstructStrategy;
import com.jun0rr.binj.mapping.ParamTypesConstructStrategy;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author F6036477
 */
public record CodecConfig(Class type, Class codec) {
  
  public BinCodec createCodec(BinContext ctx) {
    BinType bt = BinType.of(type);
    CombinedStrategy<ConstructFunction> construct = CombinedStrategy.newStrategy();
    construct.put(0, new NoArgsConstructStrategy())
        .put(1, new ParamTypesConstructStrategy());
    Map<String,Object> args = new HashMap();
    args.put(BinContext.class.getName(), ctx);
    args.put(BinType.class.getName(), bt);
    return construct.invokers(codec).stream()
        .findFirst()
        .get().create(args)
        ;
  }
  
  public static CodecConfig from(Map map) {
    String st = (String) map.get("type");
    String sc = (String) map.get("codec");
    if(st == null) {
      throw new IllegalArgumentException("Required key ['type'] not found in " + map);
    }
    Class type = MapperConfig.ofClassName(st);
    Class codec;
    if(sc != null) {
      codec = MapperConfig.ofClassName(sc);
    }
    else {
      if(type.isEnum()) {
        codec = EnumCodec.class;
      }
      else if(type.isArray()) {
        codec = ArrayCodec.class;
      }
      else {
        codec = ObjectCodec.class;
      }
    }
    return new CodecConfig(type, codec);
  }
  
}
