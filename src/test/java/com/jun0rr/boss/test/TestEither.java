/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.boss.query.Either;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
 *
 * @author F6036477
 */
public class TestEither {
  
  @Test public void test() {
    System.out.printf("------ test ------%n");
    Object o = "Hello";
    //Object o = 50;
    //Object o = false;
    Either.of(o)
        .peek(e->System.out.printf("=> %s%n", e))
        .ifNotNull()
        .peek(e->System.out.printf("=> if is not null: %s%n", e))
        .andIs(String.class)
        .peek(e->System.out.printf("=> and is a String: %s%n", e))
        .thenAccept(x->System.out.printf("=> '%s' is a String!%n", x))
        .peek(e->System.out.printf("=> then accept: %s%n", e))
        .elseMap(Number.class)
        .peek(e->System.out.printf("=> else map to Number: %s%n", e))
        .thenAccept(x->System.out.printf("=> '%s' is a Number!%n", x))
        .peek(e->System.out.printf("=> then accept: %s%n", e))
        .onFail(t->System.out.printf("[WARN] %s%n", t))
        .peek(e->System.out.printf("=> on fail: %s%n", e))
        .printFail(3)
        ;
  }
  
  @Test public void testNull() {
    System.out.printf("------ testNull ------%n");
    //Object o = "Hello";
    Object o = null;
    Either.of(o)
        .peek(e->System.out.printf("=> %s%n", e))
        .ifNotNull()
        .peek(e->System.out.printf("=> if is not null: %s%n", e))
        .andIs(String.class)
        .peek(e->System.out.printf("=> and is a String: %s%n", e))
        .thenAccept(x->System.out.printf("=> '%s' is a String!%n", x))
        .peek(e->System.out.printf("=> then accept: %s%n", e))
        .elseNot()
        .peek(e->System.out.printf("=> else not: %s%n", e))
        .ifNotNull()
        .peek(e->System.out.printf("=> if is not null: %s%n", e))
        .andIs(Number.class)
        .peek(e->System.out.printf("=> and is a Number: %s%n", e))
        .thenAccept(x->System.out.printf("=> '%s' is a Number!%n", x))
        .peek(e->System.out.printf("=> then accept: %s%n", e))
        .elseAccept(x->System.out.printf("=> '%s' is NOT a String or a Number!%n", x))
        .peek(e->System.out.printf("=> else accept: %s%n", e))
        ;
  }
  
  @Test public void testFail() {
    System.out.printf("------ testFail ------%n");
    Object o = "Hello";
    //Object o = 50;
    Assertions.assertThrows(IllegalArgumentException.class, ()->
        Either.of(o)
        .peek(e->System.out.printf("=> %s%n", e))
        .ifNotNull()
        .peek(e->System.out.printf("=> if is not null: %s%n", e))
        .andIs(Number.class)
        .peek(e->System.out.printf("=> and is a Number: %s%n", e))
        .thenAccept(x->System.out.printf("=> '%s' is a Number!%n", x))
        .peek(e->System.out.printf("=> then accept: %s%n", e))
        .peek(e->System.out.printf("=> else fail: %s%n", e))
        .elseFail(x->new IllegalArgumentException(String.format("'%s' is NOT a Number!", x)))
    )
    ;
  }
  
  @Test public void testMultiClass() {
    System.out.printf("------ testMultiClass ------%n");
    Object a = 5;  //expected
    Object b = 10; //extracted
    Boolean r = Either.of(b)
        .ifIs(o->o == null)
        .peek(e->System.out.printf("=> if is null: %s%n", e))
        .orIs(o->o.getClass() != a.getClass())
        .peek(e->System.out.printf("=> or classes is different: %s%n", e))
        .thenMap(o->false)
        .peek(e->System.out.printf("=> then map to false: %s%n", e))
        .elseIs(Comparable.class)
        .peek(e->System.out.printf("=> else is Comparable: %s%n", e))
        .thenMap(o->o.compareTo(a) > 0)
        .peek(e->System.out.printf("=> then compare: %s%n", e))
        .elseIs(JsonArray.class)
        .peek(e->System.out.printf("=> else is JsonArray: %s%n", e))
        .thenMap(o->Integer.compare(o.size(), ((JsonArray)a).size()) > 0)
        .peek(e->System.out.printf("=> then compare: %s%n", e))
        .elseIs(JsonObject.class)
        .peek(e->System.out.printf("=> else is JsonObject: %s%n", e))
        .thenMap(o->Integer.compare(o.size(), ((JsonObject)a).size()) > 0)
        .peek(e->System.out.printf("=> then compare: %s%n", e))
        .mapped()
        ;
    System.out.printf("=> result: %s%n", r);
  }
  
}
