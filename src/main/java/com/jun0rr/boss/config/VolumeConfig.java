/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.config;

import com.jun0rr.boss.Volume;
import com.jun0rr.boss.volume.DefaultVolume;
import com.jun0rr.boss.volume.FileVolume;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 *
 * @author F6036477
 */
public record VolumeConfig(String id, BufferConfig buffer, Path storePath) {
  
  public static VolumeConfig from(Map map) {
    String id = (String) map.get("id");
    if(id == null) {
      throw new BossConfigException("Bad null VolumeConfig.id");
    }
    String sp = (String) map.get("store_path");
    Map bc = (Map)map.get("buffer");
    if(bc == null) {
      throw new BossConfigException("Bad null VolumeConfig.buffer");
    }
    return new VolumeConfig(id, BufferConfig.from(bc), (sp != null ? Paths.get(sp) : null));
  }
  
  public Volume createVolume() {
    return storePath != null 
        ? new FileVolume(this) 
        : new DefaultVolume(this);
  }
  
}
