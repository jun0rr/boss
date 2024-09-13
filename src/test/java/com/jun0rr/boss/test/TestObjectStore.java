/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.binj.mapping.AnnotationConstructStrategy;
import com.jun0rr.binj.mapping.AnnotationGetStrategy;
import com.jun0rr.binj.mapping.AnnotationSetStrategy;
import com.jun0rr.binj.mapping.DefaultConstructStrategy;
import com.jun0rr.binj.mapping.FieldMethodGetStrategy;
import com.jun0rr.binj.mapping.FieldSetStrategy;
import com.jun0rr.binj.mapping.FieldsOrderConstructStrategy;
import com.jun0rr.binj.mapping.GetterMethodStrategy;
import com.jun0rr.binj.mapping.SetterMethodStrategy;
import com.jun0rr.boss.Boss;
import com.jun0rr.boss.ObjectStore;
import com.jun0rr.boss.config.BossConfig;
import com.jun0rr.boss.config.BufferConfig;
import com.jun0rr.boss.store.DefaultObjectStore;
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
    System.out.printf("---> testFileStore() <---%n");
    try {
      Path path = Paths.get("./TestObjectStore.bin");
      Uncheck.call(()->Files.deleteIfExists(path));
      BossConfig config = BossConfig.from(TestObjectStore.class.getResourceAsStream("/boss.yml"));
      config.mapping().context().mapper().constructStrategies().invokers(Person.class).stream()
          .forEach(x->System.out.printf("* config.mapper.construct.invokers: %s%n", x));
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
    System.out.printf("---> testMemStore() <---%n");
    try {
      BossConfig config = BossConfig.builder()
          .addConstructStrategy(new FieldsOrderConstructStrategy())
          .addConstructStrategy(new DefaultConstructStrategy())
          .addConstructStrategy(new AnnotationConstructStrategy())
          .addExtractStrategy(new FieldMethodGetStrategy())
          .addExtractStrategy(new GetterMethodStrategy())
          .addExtractStrategy(new AnnotationGetStrategy())
          .addInjectStrategy(new SetterMethodStrategy())
          .addInjectStrategy(new FieldSetStrategy())
          .addInjectStrategy(new AnnotationSetStrategy())
          .setBufferSize(1024)
          .setBufferType(BufferConfig.Type.DIRECT)
          .setMaxCacheSize(4*1024*1024*1024)
          .setVolumeId("memVolume")
          .build()
          ;
      System.out.println(config);
      //ObjectStore store = new DefaultObjectStore(config);
      ObjectStore store = Boss.objectStore(config);
      System.out.println(store);
      for(int i = 0; i < 10; i++) {
        Person p = new Person("John-" + i, "Doe-" +i, LocalDate.now(), new Address("Street-" + (i + 1), "City-" + (i + 1), i + 100));
        System.out.println(store.store(p));
      }
      store.createIndex(Person.class, "name", Person::name);
      store.createIndex(Person.class, "address.number", p->p.address().number());
      store.find(Person.class, p->p.name().equals("John-5")).forEach(System.out::println);
      store.close();
      
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
  
  public static record Person(String name, String last, LocalDate birth, Address address) {}
  
  public static record Address(String street, String city, int number) {}
  
}
