/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.boss.ObjectStore;
import com.jun0rr.boss.store.Stored;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
 *
 * @author F6036477
 */
public class TestObjectStore {
  
  @Test
  public void test() throws IOException {
    Properties props = new Properties();
    try {
    props.load(TestObjectStore.class.getResourceAsStream("/boss.properties"));
    System.out.println(props);
    ObjectStore os = ObjectStore.builder().load(props).build();
    List<Person> ps = new LinkedList<>();
    for(int i = 0; i < 10; i++) {
      ps.add(new Person("Hello" + i, "World" + i, LocalDate.of(1980, i+1, i+10), new Address("Street" + i, "SomeCity", 10+i), new long[]{9999999L}));
    }
    if(os.indexStore().findByType(Person.class).count() == 0) {
      ps.forEach(os::store);
      os.close();
      os = ObjectStore.builder().load(props).build();
    }
    else {
      os.find(Person.class, p->true)
          .map(Stored::object)
          .map(o->(Person)o)
          .forEach(ps::add);
    }
    // Test get
    List<Stored<Person>> sps = os.find(Person.class, p->true).collect(Collectors.toList());
    for(int i = 0; i < 10; i++) {
      Stored<Person> s = sps.get(i);
      Assertions.assertTrue(os.indexStore().classIndex().entrySet()
          .stream()
          .filter(e->e.getKey().isTypeOf(Person.class))
          .map(Entry::getValue)
          .flatMap(List::stream)
          .anyMatch(x->x == s.index())
      );
      Assertions.assertTrue(os.indexStore().idIndex().entrySet()
          .stream()
          .filter(e->e.getKey() == s.id())
          .map(Entry::getValue)
          .anyMatch(x->x == s.index())
      );
      Assertions.assertEquals(ps.get(i), s.object());
    }
    // Test indexes
    os.createIndex(Person.class, "name", p->p.name());
    os.createIndex(Person.class, "birth", p->p.birth());
    for(int i = 0; i < 10; i++) {
      int x = i;
      Stored<Person> a = os.find(Person.class, "name", s->"Hello".concat(String.valueOf(x)).equals(s)).findFirst().get();
      Assertions.assertTrue(os.indexStore().classIndex().entrySet()
          .stream()
          .filter(e->e.getKey().isTypeOf(Person.class))
          .map(Entry::getValue)
          .flatMap(List::stream)
          .anyMatch(j->j == a.index())
      );
      Assertions.assertTrue(os.indexStore().idIndex().entrySet()
          .stream()
          .filter(e->e.getKey() == a.id())
          .map(Entry::getValue)
          .anyMatch(j->j == a.index())
      );
      Assertions.assertEquals(ps.get(i), a.object());
      Stored<Person> b = os.find(Person.class, "birth", d->LocalDate.of(1980, x+1, x+10).equals(d)).findFirst().get();
      Assertions.assertTrue(os.indexStore().classIndex().entrySet()
          .stream()
          .filter(e->e.getKey().isTypeOf(Person.class))
          .map(Entry::getValue)
          .flatMap(List::stream)
          .anyMatch(j->j == b.index())
      );
      Assertions.assertTrue(os.indexStore().idIndex().entrySet()
          .stream()
          .filter(e->e.getKey() == b.id())
          .map(Entry::getValue)
          .anyMatch(j->j == b.index())
      );
      Assertions.assertEquals(ps.get(i), b.object());
    }
    // Test find predicate
    for(int i = 0; i < 10; i++) {
      int x = i;
      Stored<Person> a = os.find(Person.class, p->"Hello".concat(String.valueOf(x)).equals(p.name())).findFirst().get();
      Assertions.assertTrue(os.indexStore().classIndex().entrySet()
          .stream()
          .filter(e->e.getKey().isTypeOf(Person.class))
          .map(Entry::getValue)
          .flatMap(List::stream)
          .anyMatch(j->j == a.index())
      );
      Assertions.assertTrue(os.indexStore().idIndex().entrySet()
          .stream()
          .filter(e->e.getKey() == a.id())
          .map(Entry::getValue)
          .anyMatch(j->j == a.index())
      );
      Assertions.assertEquals(ps.get(i), a.object());
      Stored<Person> b = os.find(Person.class, p->LocalDate.of(1980, x+1, x+10).equals(p.birth())).findFirst().get();
      Assertions.assertTrue(os.indexStore().classIndex().entrySet()
          .stream()
          .filter(e->e.getKey().isTypeOf(Person.class))
          .map(Entry::getValue)
          .flatMap(List::stream)
          .anyMatch(j->j == b.index())
      );
      Assertions.assertTrue(os.indexStore().idIndex().entrySet()
          .stream()
          .filter(e->e.getKey() == b.id())
          .map(Entry::getValue)
          .anyMatch(j->j == b.index())
      );
      Assertions.assertEquals(ps.get(i), b.object());
    }
    os.close();
    } catch(Throwable e) { 
      //removeFiles(props);
      e.printStackTrace(); 
      throw e; 
    }
  }
  
  private void removeFiles(Properties props) {
    try {
      Predicate<String> pd = Pattern.compile(String.format("%s\\.\\w{1,3}", props.getProperty("boss.volume.id"))).asMatchPredicate();
      Path path = Paths.get(props.getProperty("boss.volume.storePath"));
      Files.list(path).peek(System.out::println).map(Path::getFileName).filter(p->pd.test(p.toString())).forEach(p->{
        try {
          Files.delete(p);
        }
        catch(Exception e) {
          e.printStackTrace();
          throw new RuntimeException(e);
        }
      });
    }
    catch(Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
}
