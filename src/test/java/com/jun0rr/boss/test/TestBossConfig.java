/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.esotericsoftware.yamlbeans.YamlException;
import com.jun0rr.boss.config.BossConfig;
import com.jun0rr.boss.config.BufferConfig;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestBossConfig {
  
  @Test public void test() throws YamlException {
    BossConfig bc = BossConfig.from(getClass().getResourceAsStream("/boss.yml"));
    System.out.println(bc);
    Assertions.assertEquals("TestObjectStore", bc.volume().id());
    Assertions.assertEquals(1024, bc.volume().buffer().size());
    Assertions.assertEquals(4 * Math.round(Math.pow(1024, 3)), bc.volume().buffer().maxCacheSize());
    Assertions.assertEquals(BufferConfig.Type.DIRECT, bc.volume().buffer().type());
    Assertions.assertEquals(Paths.get("./TestObjectStore.bin"), bc.volume().storePath());
    bc.mapper().context().codecs()
        .values()
        .forEach(System.out::println);
  }
  
}
