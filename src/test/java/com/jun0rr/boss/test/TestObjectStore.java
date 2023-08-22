/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.binj.mapping.AnnotationConstructStrategy;
import com.jun0rr.binj.mapping.AnnotationExtractStrategy;
import com.jun0rr.binj.mapping.AnnotationInjectStrategy;
import com.jun0rr.binj.mapping.CombinedStrategy;
import com.jun0rr.binj.mapping.ConstructFunction;
import com.jun0rr.binj.mapping.DefaultConstructStrategy;
import com.jun0rr.binj.mapping.ExtractFunction;
import com.jun0rr.binj.mapping.FieldGetterStrategy;
import com.jun0rr.binj.mapping.FieldSetterStrategy;
import com.jun0rr.binj.mapping.FieldsOrderConstructStrategy;
import com.jun0rr.binj.mapping.GetterStrategy;
import com.jun0rr.binj.mapping.InjectFunction;
import com.jun0rr.binj.mapping.SetterStrategy;
import com.jun0rr.boss.ObjectStore;
import com.jun0rr.boss.config.BossConfig;
import com.jun0rr.boss.config.BufferConfig;
import com.jun0rr.boss.store.DefaultObjectStore;
import com.jun0rr.boss.test.record.Address;
import com.jun0rr.boss.test.record.Person;
import com.jun0rr.uncheck.Uncheck;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Juno
 */
public class TestObjectStore {
  
  @Test
  public void testFileStore() {
    System.out.printf("---> testFileStore() <---");
    try {
      Path path = Paths.get("./TestObjectStore.bin");
      Uncheck.call(()->Files.deleteIfExists(path));
      BossConfig config = BossConfig.from(TestObjectStore.class.getResourceAsStream("/boss.yml"));
      ObjectStore store = new DefaultObjectStore(config);
      System.out.println(store);
      for(int i = 0; i < 10; i++) {
        Person p = new Person("John-" + i, "Doe-" +i, LocalDate.now(), new Address("Street-" + (i + 1), "City-" + (i + 1), i + 100));
        System.out.println(store.store(p));
      }
      store.createIndex(Person.class, "name", Person::name);
      store.createIndex(Person.class, "address.number", p->p.address().number());
      store.find(Person.class, p->p.name().equals("John-5")).forEach(System.out::println);
      store.close();
      
      store = new DefaultObjectStore(config);
      for(int i = 0; i < 10; i++) {
        Person p = new Person("John-" + i, "Doe-" +i, LocalDate.now(), new Address("Street-" + (i + 1), "City-" + (i + 1), i + 100));
        Assertions.assertEquals(p, store.find(Person.class, "name", p.name()).findFirst().get().object());
      }
      store.close();
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
  @Test
  public void testMemStore() {
    System.out.printf("---> testMemStore() <---");
    try {
      Path path = Paths.get("./TestObjectStore.bin");
      Uncheck.call(()->Files.deleteIfExists(path));
      BossConfig config = BossConfig.from(TestObjectStore.class.getResourceAsStream("/boss.yml"));
      BossConfig config = BossConfig.builder()
          .setBufferSize(1024)
          .setBufferType(BufferConfig.Type.DIRECT)
          .setMappingConstructStrategy(new CombinedStrategy<ConstructFunction>()
              .put(0, new FieldsOrderConstructStrategy())
              .put(1, new DefaultConstructStrategy())
              .put(2, new AnnotationConstructStrategy()))
          .setMappingExtractStrategy(new CombinedStrategy<ExtractFunction>()
              .put(0, new GetterStrategy())
              .put(1, new FieldGetterStrategy())
              .put(2, new AnnotationExtractStrategy()))
          .setMappingInjectStrategy(new CombinedStrategy<InjectFunction>()
              .put(0, new SetterStrategy())
              .put(1, new FieldSetterStrategy())
              .put(2, new AnnotationInjectStrategy()))
          .setMaxCacheSize(4*1024*1024)
          .setVolumeId("memVolume")
          ;
      ObjectStore store = new DefaultObjectStore(config);
      System.out.println(store);
      for(int i = 0; i < 10; i++) {
        Person p = new Person("John-" + i, "Doe-" +i, LocalDate.now(), new Address("Street-" + (i + 1), "City-" + (i + 1), i + 100));
        System.out.println(store.store(p));
      }
      store.createIndex(Person.class, "name", Person::name);
      store.createIndex(Person.class, "address.number", p->p.address().number());
      store.find(Person.class, p->p.name().equals("John-5")).forEach(System.out::println);
      store.close();
      
      store = new DefaultObjectStore(config);
      for(int i = 0; i < 10; i++) {
        Person p = new Person("John-" + i, "Doe-" +i, LocalDate.now(), new Address("Street-" + (i + 1), "City-" + (i + 1), i + 100));
        Assertions.assertEquals(p, store.find(Person.class, "name", p.name()).findFirst().get().object());
      }
      store.close();
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
}
