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
    i.putOffset(123L, 456L);
    i.putOffset(135L, 246L);
    i.putOffset("foo", 123L);
    i.putOffset("foo", 456L);
    i.putOffset("bar", 135L);
    i.putOffset("bar", 246L);
    i.putOffset("foo", "hello", "world", 123L);
    i.putOffset("foo", "hello", "baz", 135L);
    i.putOffset("bar", "world", "foo", 456L);
    i.putOffset("bar", "world", "bar", 246L);
    System.out.printf("=> %s%n", i);
    i.findOffsetByCollection("foo").forEach(l->System.out.printf("=> indexByCollection(foo): %d%n", l));
    i.findOffsetByCollection("bar").forEach(l->System.out.printf("=> indexByCollection(bar): %d%n", l));
    i.findOffsetByValue("foo", "hello", "world").forEach(l->System.out.printf("=> indexByValue(foo, hello, world): %d%n", l));
    i.findOffsetByValue("bar", "world", "foo").forEach(l->System.out.printf("=> indexByValue(bar, world, foo): %d%n", l));
    System.out.printf("=> toJson: %s%n", i.toJson());
    System.out.printf("=> fromJson: %s%n", new JsonIndex().fromJson(i.toJson()));
    System.out.printf("=> fromJson.equals: %s%n", new JsonIndex().fromJson(i.toJson()).equals(i));
  }
  
}
