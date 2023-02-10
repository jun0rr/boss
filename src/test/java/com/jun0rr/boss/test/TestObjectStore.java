/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.boss.ObjectStore;
import com.jun0rr.boss.Volume;
import com.jun0rr.boss.def.DefaultObjectStore;
import com.jun0rr.boss.def.DefaultVolume;
import com.jun0rr.boss.def.NoPersistStrategy;
import com.jun0rr.jbom.BinContext;
import com.jun0rr.jbom.buffer.BufferAllocator;
import com.jun0rr.jbom.mapping.AnnotationExtractStrategy;
import com.jun0rr.jbom.mapping.Binary;
import com.jun0rr.jbom.mapping.DefaultConstructStrategy;
import com.jun0rr.jbom.mapping.ObjectMapper;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestObjectStore {
  
  @Test
  public void test() {
    ObjectMapper mp = new ObjectMapper();
    mp.constructStrategy().add(new DefaultConstructStrategy());
    mp.extractStrategy().add(new AnnotationExtractStrategy());
    BinContext ctx = BinContext.of(mp);
    Volume vol = new DefaultVolume("TestObjectStore", 128, BufferAllocator.directAllocator(256), new NoPersistStrategy());
    ObjectStore os = new DefaultObjectStore(vol, ctx);
    List<Person> ps = new LinkedList<>();
    for(int i = 0; i < 10; i++) {
      ps.add(new Person("Hello" + i, "World" + i, LocalDate.of(1980, i+1, i+10), new Address("Street" + i, "SomeCity", 10+i), new long[]{(long)(Math.random() * Long.MAX_VALUE)}));
    }
    System.out.println(ps);
    ps.forEach(os::store);
    System.out.println("--- classIndex: Person");
    os.index().classIndex().entrySet().stream()
        .filter(e->e.getKey().isTypeOf(Person.class))
        .map(Entry::getValue)
        .flatMap(List::stream)
        .forEach(System.out::println);
    System.out.println("--- classIndex: Address");
    os.index().classIndex().entrySet().stream()
        .filter(e->e.getKey().isTypeOf(Address.class))
        .map(Entry::getValue)
        .flatMap(List::stream)
        .forEach(System.out::println);
    System.out.println("--- volume:");
    System.out.println(vol);
    System.out.println("--- createIndex: name");
    os.createIndex(Person.class, "name", p->p.name());
    os.find(Person.class, "name", s->"Hello7".equals(s)).forEach(System.out::println);
    System.out.println("--- createIndex: birth");
    os.createIndex(Person.class, "birth", p->p.birth());
    System.out.println("--- valueIndex:");
    os.index().valueIndex().entrySet().stream()
        .filter(e->e.getKey().type().isTypeOf(Person.class))
        .peek(e->System.out.println(e.getKey()))
        .map(Entry::getValue)
        .flatMap(List::stream)
        .forEach(System.out::println);
    System.out.println("--- findByField: birth");
    os.find(Person.class, "birth", d->LocalDate.of(1980, 5, 14).equals(d)).forEach(System.out::println);
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
