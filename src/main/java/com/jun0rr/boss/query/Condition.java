/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.query;

import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 *
 * @author F6036477
 */
public record Condition(String field, Object value, BiFunction<Object,Object,Boolean> function) implements Predicate<JsonObject> {

  @Override
  public boolean test(JsonObject o) {
    return function.apply(value, extract(o));
  }
  
  protected Object extract(JsonObject o) {
    List<String> names = List.of(field.split("\\."));
    Object val = o;
    for(String name : names) {
      val = Either.of(val)
          .ifNotNull()
          .andIs(JsonObject.class)
          .andIs(j->j.containsKey(name))
          .thenMap(j->j.getValue(name))
          .a();
    }
    return val;
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
    - greatereq (ge): Greater or equals;
    - lesser (lt): Lesser then;
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
