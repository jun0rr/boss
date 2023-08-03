/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.boss.json.JsonIndex;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestJsonIndex {
  
  @Test public void test() {
    JsonIndex i = new JsonIndex();
    i.putIndex(123L, 456L);
    i.putIndex(135L, 246L);
    i.putIndex("foo", 123L);
    i.putIndex("foo", 456L);
    i.putIndex("bar", 135L);
    i.putIndex("bar", 246L);
    i.putIndex("foo", "hello", "world", 123L);
    i.putIndex("foo", "hello", "baz", 135L);
    i.putIndex("bar", "world", "foo", 456L);
    i.putIndex("bar", "world", "bar", 246L);
    System.out.printf("=> %s%n", i);
    i.findIndexByCollection("foo").forEach(l->System.out.printf("=> indexByCollection(foo): %d%n", l));
    i.findIndexByCollection("bar").forEach(l->System.out.printf("=> indexByCollection(bar): %d%n", l));
    i.findIndexByValue("foo", "hello", "world").forEach(l->System.out.printf("=> indexByValue(foo, hello, world): %d%n", l));
    i.findIndexByValue("bar", "world", "foo").forEach(l->System.out.printf("=> indexByValue(bar, world, foo): %d%n", l));
    System.out.printf("=> toJson: %s%n", i.toJson());
    System.out.printf("=> fromJson: %s%n", new JsonIndex().fromJson(i.toJson()));
    System.out.printf("=> fromJson.equals: %s%n", new JsonIndex().fromJson(i.toJson()).equals(i));
  }
  
}
