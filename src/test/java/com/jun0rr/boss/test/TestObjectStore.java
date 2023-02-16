/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.boss.ObjectStore;
import com.jun0rr.boss.store.Stored;
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
      ObjectStore store = ObjectStore.builder().load(props).build();
      
      System.out.println(store);
      List<Person> ps = new LinkedList<>();
      IntStream.range(0, 10).mapToObj(i->new Person(
          "Hello" + i, "World" + i, LocalDate.of(1980 + i, i + 1, i + 10), 
          new Address("Street" + i, "City" + i, i + 100), new long[]{299L + i})
      ).forEach(ps::add);
      System.out.println(ps);
      if(!store.isLoaded()) {
        ps.forEach(store::store);
      }
      store.delete(Person.class, p->p.name().equals("Hello5"))
          .findFirst()
          .ifPresent(System.out::println);
      ps.remove(5);
      ps.remove(5);
      for(Person p : ps) {
        assertEquals(p, store.find(Person.class, q->q.name().equals(p.name())).findFirst().get().object());
      }
      store.createIndex(Person.class, "birth", Person::birth);
      store.find(Person.class, "birth", LocalDate.of(1986, 7, 16))
          .peek(s->System.out.println(store.get(s.id())))
          .findFirst()
          .ifPresent(s->store.delete(s.id()));
      store.close();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
}
