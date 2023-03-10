/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.config;

import java.util.Map;

/**
 *
 * @author F6036477
 */
public record VolumeConfig(String id, BufferConfig buffer, StoreConfig store) {
  
  public static VolumeConfig from(Map map) {
    String id = (String) map.get("id");
    if(id == null) {
      throw new BossConfigException("Bad null VolumeConfig.id");
    }
    Map bc = (Map)map.get("buffer");
    if(bc == null) {
      throw new BossConfigException("Bad null VolumeConfig.buffer");
    }
    Map sc = (Map)map.get("store");
    return new VolumeConfig(id, BufferConfig.from(bc), (sc != null ? StoreConfig.from(sc) : null));
  }
  
}
