/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.binj.mapping.Binary;
import com.jun0rr.boss.ObjectStore;
import com.jun0rr.boss.store.Stored;
import com.jun0rr.boss.test.TestObjectStore.Person;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
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
    //ObjectMapper mp = new ObjectMapper();
    //mp.constructStrategy().add(new DefaultConstructStrategy());
    //mp.extractStrategy().add(new AnnotationExtractStrategy());
    //BinContext ctx = BinContext.of(mp);
    //Volume vol = new DefaultVolume("TestObjectStore", 128, BufferAllocator.directAllocator(256), new NoPersistStrategy());
    //ObjectStore os = new DefaultObjectStore(vol, ctx);
    //ObjectStore os = ObjectStore.builder()
        //.setBlockSize(128)
        //.setBufferAllocatorType(ObjectStore.Builder.AllocatorType.DIRECT)
        //.setBufferSize(256)
        //.setConstructStrategy(new DefaultConstructStrategy())
        //.setExtractStrategy(new AnnotationExtractStrategy())
        //.setVolumeId("TestObjectStore")
        //.build();
    try {
    Properties props = new Properties();
    props.load(TestObjectStore.class.getResourceAsStream("/boss.properties"));
    System.out.println(props);
    ObjectStore os = ObjectStore.builder().load(props).build();
    List<Person> ps = new LinkedList<>();
    if(os.indexStore().findByType(Person.class).count() == 0) {
      for(int i = 0; i < 10; i++) {
        ps.add(new Person("Hello" + i, "World" + i, LocalDate.of(1980, i+1, i+10), new Address("Street" + i, "SomeCity", 10+i), new long[]{(long)(Math.random() * Long.MAX_VALUE)}));
        Stored<Person> s = os.store(ps.get(i));
        System.out.println(s);
      }
      os.close();
      os = ObjectStore.builder().load(props).build();
    }
    else {
      os.find(Person.class, p->true)
          .map(Stored::object)
          .map(o->(Person)o)
          .forEach(ps::add);
    }
    System.out.println(ps);
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
    } catch(Throwable e) { e.printStackTrace(); throw e; }
  }
  
  
  
  public static class Person {
    
    private final String name;
    
    private final String last;
    
    private final LocalDate birth;
    
    private final Address address;
    
    private final long[] ids;
    
    //@MapConstructor({"name", "last", "birth", "address", "ids"})
    public Person(String name, String last, LocalDate birth, Address address, long[] ids) {
      this.name = Objects.requireNonNull(name);
      this.last = Objects.requireNonNull(last);
      this.birth = Objects.requireNonNull(birth);
      this.address = Objects.requireNonNull(address);
      this.ids = ids;
    }

    @Binary
    public String name() {
      return name;
    }
    
    @Binary
    public String last() {
      return last;
    }

    @Binary
    public LocalDate birth() {
      return birth;
    }

    @Binary
    public long[] ids() {
      return ids;
    }

    @Binary
    public Address address() {
      return address;
    }

    @Override
    public int hashCode() {
      int hash = 7;
      hash = 89 * hash + Objects.hashCode(this.name);
      hash = 89 * hash + Objects.hashCode(this.last);
      hash = 89 * hash + Objects.hashCode(this.birth);
      hash = 89 * hash + Objects.hashCode(this.address);
      hash = 89 * hash + Arrays.hashCode(this.ids);
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final Person other = (Person) obj;
      if (!Objects.equals(this.name, other.name)) {
        return false;
      }
      if (!Objects.equals(this.last, other.last)) {
        return false;
      }
      if (!Objects.equals(this.birth, other.birth)) {
        return false;
      }
      if (!Objects.equals(this.address, other.address)) {
        return false;
      }
      return Arrays.equals(this.ids, other.ids);
    }
    
    @Override
    public String toString() {
      return "Person{" + "name=" + name + ", last=" + last + ", birth=" + birth + ", address=" + address + ", ids=" + Arrays.toString(ids) + '}';
    }
    
  }
  
  
  public static class Address {
    
    private final String street;
    
    private final String city;
    
    private final int number;
    
    //@MapConstructor({"street", "city", "number"})
    public Address(String street, String city, int number) {
      this.street = Objects.requireNonNull(street);
      this.city = Objects.requireNonNull(city);
      this.number = number;
    }
    
    @Binary
    public String street() {
      return street;
    }
    
    @Binary
    public String city() {
      return city;
    }
    
    @Binary
    public int number() {
      return number;
    }

    @Override
    public int hashCode() {
      int hash = 7;
      hash = 29 * hash + Objects.hashCode(this.street);
      hash = 29 * hash + Objects.hashCode(this.city);
      hash = 29 * hash + this.number;
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final Address other = (Address) obj;
      if (this.number != other.number) {
        return false;
      }
      if (!Objects.equals(this.street, other.street)) {
        return false;
      }
      return Objects.equals(this.city, other.city);
    }

    @Override
    public String toString() {
      return "Address{" + "street=" + street + ", city=" + city + ", number=" + number + '}';
    }
    
  }
  
}
