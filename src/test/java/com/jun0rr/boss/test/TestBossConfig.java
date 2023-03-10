/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.esotericsoftware.yamlbeans.YamlException;
import com.jun0rr.binj.mapping.AnnotationConstructStrategy;
import com.jun0rr.binj.mapping.AnnotationExtractStrategy;
import com.jun0rr.binj.mapping.AnnotationInjectStrategy;
import com.jun0rr.binj.mapping.DefaultConstructStrategy;
import com.jun0rr.binj.mapping.FieldGetterStrategy;
import com.jun0rr.binj.mapping.FieldSetterStrategy;
import com.jun0rr.binj.mapping.SetterStrategy;
import com.jun0rr.boss.config.BossConfig;
import com.jun0rr.boss.config.BufferConfig;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

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
    Assertions.assertEquals(Paths.get("./"), bc.volume().store().path());
    Assertions.assertEquals(true, bc.volume().store().sync());
    List<Class> cl = List.of(DefaultConstructStrategy.class, AnnotationConstructStrategy.class);
    Assertions.assertTrue(bc.mapper().construct().stream().map(Object::getClass).allMatch(c->cl.stream().anyMatch(d->c==d)));
    List<Class> el = List.of(FieldGetterStrategy.class, AnnotationExtractStrategy.class);
    Assertions.assertTrue(bc.mapper().extract().stream().map(Object::getClass).allMatch(c->el.stream().anyMatch(d->c==d)));
    List<Class> il = List.of(AnnotationInjectStrategy.class, SetterStrategy.class, FieldSetterStrategy.class);
    Assertions.assertTrue(bc.mapper().inject().stream().map(Object::getClass).allMatch(c->il.stream().anyMatch(d->c==d)));
  }
  
}
