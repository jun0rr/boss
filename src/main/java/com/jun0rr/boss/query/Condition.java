/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss.query;

import io.vertx.core.json.JsonObject;
import java.util.function.BiFunction;
import java.util.function.Predicate;


/**
 *
 * @author f6036477
 */
public interface Condition<T> extends Predicate<JsonObject> {
  
  public String field();
  
  public T expected();
  
  public static <U> Condition<U> of(String field, U expected, BiFunction<U,JsonObject,Boolean> fn) {
    return new Condition() {
      private final String _field = field;
      private final U _expected = expected;
      public String field() { return _field; }
      public U expected() { return _expected; }
      public boolean test(JsonObject o) {
        return fn.apply(_expected, o);
      }

      @Override
      public boolean test(JsonObject t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
      }
    };
  }
  
  /*
  
  {
    collection: <name>,
    fields: [<field0>, <field1.nestedField>, ... <fiendN>],
    sort: [<field0>, <field1.nestedField>, ... <fiendN>],
    test:
    {
      field: <field0>,
      equals: <value>,
      and:
      {
        field: <field1.nestedField>,
        greater: <value>,
        or: 
        {
          field: <field1.nestedField>,
          between: [<value0>, <value1>]
        }
      }
    }
  }
  Possible tests:
    - equals (eq);
    - nequals (ne): Not equals;
    - greater (gt): Greater then;
    - ngreater (ng): Not greater then;
    - greatereq (ge): Greater or equals;
    - lesser (lt): Lesser then;
    - nlesser (nl): Not lesser then;
    - lessereq (le): Lesser or equals;
    - between (bt): Between 2 values (start inclusive, end exclusive);
    - nbetween (nb): Not between 2 values, exclusive;
    - equalsi (ei): Equals ignore case;
    - regex (rx): Regex expression;
    - in (in): One of many;
    - nin (nin): Not in one of many;
    - isnull (nu): Is a null value (not present);
    - notnull (nn): Not a null value (is present).
  */
  
}
