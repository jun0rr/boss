/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.config;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 *
 * @author F6036477
 */
public record BossConfig(VolumeConfig volume, MapperConfig mapper) {

  public static BossConfig from(Map map) {
    //System.out.printf("BossConfig.from: %s%n", map);
    Map vc = (Map) map.get("volume");
    if(vc == null) {
      throw new BossConfigException("Bad null BossConfig.volume");
    }
    Map mc = (Map)map.get("mapper");
    if(mc == null) {
      throw new BossConfigException("Bad null BossConfig.mapper");
    }
    return new BossConfig(VolumeConfig.from(vc), MapperConfig.from(mc));
  }
  
  public static BossConfig from(InputStream is) {
    try {
      YamlReader yr = new YamlReader(new InputStreamReader(is));
      return from((Map)yr.read());
    }
    catch(YamlException e) {
      throw new BossConfigException(e);
    }
  }
  
}
