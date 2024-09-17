/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.binj.BinContext;
import com.jun0rr.boss.Boss;
import com.jun0rr.boss.ObjectStore;
import com.jun0rr.boss.Stored;
import com.jun0rr.boss.config.BossConfig;
import com.jun0rr.boss.store.DefaultObjectStore;
import com.jun0rr.uncheck.Uncheck;
import com.jun0rr.timer.Timer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
      Timer timer = new Timer();
      Path path = Paths.get("./TestObjectStore.bin");
      Uncheck.call(()->Files.deleteIfExists(path));
      BossConfig config = BossConfig.from(TestObjectStore.class.getResourceAsStream("/boss.yml"));
      ObjectStore store = new DefaultObjectStore(config);
      System.out.println(store);
      timer.start();
      for(int i = 0; i < 1000; i++) {
        Person p = new Person("John-" + i, "Doe-" +i, LocalDate.now(), new Address("Street-" + (i + 1), "City-" + (i + 1), i + 100));
        Stored<Person> s = store.store(p);
        //System.out.println(store.store(p));
      }
      timer.lap();
      store.createIndex(Person.class, "name", Person::name);
      //store.createIndex(Person.class, "address.number", p->p.address().number());
      timer.lap();
      store.find(Person.class, p->p.name().equals("John-505")).forEach(System.out::println);
      timer.lap();
      store.close();
      timer.stop();
      
      NumberFormat df = DecimalFormat.getInstance();
      timer.duration().stream()
          .forEach(s->System.out.printf("* %s / %sms%n", Timer.format(s), df.format(s.toMillis())));
      System.out.printf("* store total time: %s / %sms%n", Timer.format(timer.total()), df.format(timer.total().toMillis()));
      
      store = new DefaultObjectStore(config);
      timer.start();
      for(int i = 999; i >= 0; i--) {
        Person p = new Person("John-" + i, "Doe-" +i, LocalDate.now(), new Address("Street-" + (i + 1), "City-" + (i + 1), i + 100));
        Assertions.assertEquals(p, store.find(Person.class, "name", p.name()).findFirst().get().object());
      }
      timer.stop();
      System.out.printf("* find total time: %s / %sms%n", Timer.format(timer.total()), df.format(timer.total().toMillis()));
      
      timer.start();
      for(int i = 999; i >= 0; i--) {
        Person p = new Person("John-" + i, "Doe-" +i, LocalDate.now(), new Address("Street-" + (i + 1), "City-" + (i + 1), i + 100));
        Assertions.assertEquals(p, store.find(Person.class, q->q.name().equals(p.name())).findFirst().get().object());
      }
      timer.stop();
      System.out.printf("* find2 total time: %s / %sms%n", Timer.format(timer.total()), df.format(timer.total().toMillis()));
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
      Timer timer = new Timer();
      ObjectStore store = Boss.memoryObjectStore();
      System.out.println(store);
      BinContext ctx = BinContext.ofCombinedStrategyMapper();
      timer.start();
      for(int i = 0; i < 1000; i++) {
        Person p = new Person("John-" + i, "Doe-" +i, LocalDate.now(), new Address("Street-" + (i + 1), "City-" + (i + 1), i + 100));
        Stored<Person> s = store.store(p);
        //System.out.printf("-> %s, size=%d%n", s, ctx.calcSize(p));
      }
      timer.lap();
      store.createIndex(Person.class, "name", Person::name);
      //store.createIndex(Person.class, "address.number", p->p.address().number());
      timer.lap();
      store.find(Person.class, p->p.name().equals("John-505")).forEach(System.out::println);
      //timer.lap();
      //store.close();
      timer.stop();
      
      NumberFormat df = DecimalFormat.getInstance();
      timer.duration().stream()
          //.map(Timer::format)
          .forEach(s->System.out.printf("* %s / %sms%n", Timer.format(s), df.format(s.toMillis())));
      System.out.printf("* store total time: %s / %sms%n", Timer.format(timer.total()), df.format(timer.total().toMillis()));
      
      timer.start();
      for(int i = 999; i >= 0; i--) {
        Person p = new Person("John-" + i, "Doe-" +i, LocalDate.now(), new Address("Street-" + (i + 1), "City-" + (i + 1), i + 100));
        Assertions.assertEquals(p, store.find(Person.class, "name", p.name()).findFirst().get().object());
      }
      timer.stop();
      System.out.printf("* find total time: %s / %sms%n", Timer.format(timer.total()), df.format(timer.total().toMillis()));
      
      timer.start();
      for(int i = 999; i >= 0; i--) {
        Person p = new Person("John-" + i, "Doe-" +i, LocalDate.now(), new Address("Street-" + (i + 1), "City-" + (i + 1), i + 100));
        Assertions.assertEquals(p, store.find(Person.class, q->q.name().equals(p.name())).findFirst().get().object());
      }
      timer.stop();
      System.out.printf("* find2 total time: %s / %sms%n", Timer.format(timer.total()), df.format(timer.total().toMillis()));
      //store.close();
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
  public static record Person(String name, String last, LocalDate birth, Address address) {}
  
  public static record Address(String street, String city, int number) {}
  
}
