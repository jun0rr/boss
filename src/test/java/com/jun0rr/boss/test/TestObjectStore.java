/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.boss.ObjectStore;
import com.jun0rr.boss.Stored;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestObjectStore {
  
  @Test
  public void test() {
    try {
      Properties props = new Properties();
      props.load(getClass().getResourceAsStream("/boss.properties"));
      ObjectStore store = ObjectStore.builder().set(props).build();
      
      System.out.println(store);
      List<Person> ps = new LinkedList<>();
      IntStream.range(0, 10).mapToObj(i->new Person(
          "Hello" + i, "World" + i, LocalDate.of(1980 + i, i + 1, i + 10), 
          new Address("Street" + i, "City" + i, i + 100), new long[]{299L + i})
      ).forEach(ps::add);
      if(!store.isLoaded()) {
        ps.stream().map(store::store).forEach(System.out::println);
      }
      store.delete(Person.class, p->p.name().equals("Hello5"))
          .findFirst()
          .ifPresent(p->System.out.println("deleted! " + p));
      ps.remove(5);
      ps.remove(5);
      System.out.println("find Hello2");
      Stored<Person> s2 = store.find(Person.class, p->p.name().equals("Hello2")).findFirst().get();
      System.out.println(s2);
      System.out.println("updated! " + store.<Person>update(s2.id(), p->new Person(p.name(), "World2", p.birth(), p.address(), p.ids())));
      for(Person p : ps) {
        assertEquals(p, store.find(Person.class, q->q.name().equals(p.name())).findFirst().get().object());
      }
      System.out.println("create index birth");
      store.createIndex(Person.class, "birth", Person::birth);
      store.find(Person.class, "birth", LocalDate.of(1986, 7, 16))
          .findFirst()
          .ifPresent(s->System.out.println("deleted! " + store.delete(s.id())));
      s2 = store.find(Person.class, p->p.name().equals("Hello2")).findFirst().get();
      s2 = store.<Person>update(s2.id(), p->new Person(p.name(), "XXXX", p.birth(), p.address(), p.ids()));
      System.out.println("updated! " + store.get(s2.id()));
      
      System.out.println("create index address.number");
      store.createIndex(Person.class, "address.number", p->p.address().number());
      System.out.println("find address.number=103! " + store.find(Person.class, "address.number", 103).findAny());
      
      Optional<Stored<Person>> opt = store.find(Person.class, "address.number", 120).findFirst();
      System.out.println("find address.number=120! " + opt);
      if(opt.isEmpty()) {
        s2 = store.store(new Person("Hello10", "World10", LocalDate.of(1990, 11, 20), new Address("Street10", "City10", 120), new long[]{319L}));
        System.out.println("stored! " + s2);
      }
      else {
        System.out.println(opt.get());
      }
      
      store.close();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    finally {
      try { Files.delete(Paths.get("./TestObjectStore.odb0")); }
      catch(IOException e) {
        e.printStackTrace();
      }
    }
  }
  
}
