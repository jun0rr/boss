/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.boss.config.BossConfig;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestConfig {
  
  @Test public void test() {
    Yaml yaml = new Yaml(new Constructor(BossConfig.class));
    
  }
  
}
