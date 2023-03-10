/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 *
 * @author F6036477
 */
public record StoreConfig(Path path, boolean sync) {

  public static StoreConfig from(Map map) {
    String sp = (String) map.get("path");
    if(sp == null) {
      throw new BossConfigException("Bad null StoreConfig.path");
    }
    String ss = (String)map.get("sync");
    if(ss == null) {
      throw new BossConfigException("Bad null StoreConfig.sync");
    }
    return new StoreConfig(Paths.get(sp), Boolean.parseBoolean(ss));
  }

}
