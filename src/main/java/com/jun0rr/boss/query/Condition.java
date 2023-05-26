/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss.query;

import com.google.gson.JsonObject;
import java.util.function.Predicate;

/**
 *
 * @author f6036477
 */
public interface Condition<T> {
  
  public String field();
  
  public boolean test(T expected, JsonObject obj);
  
  public default Condition<T> and(Condition<T> and) {
    return null;
  }
  
  public default Condition<T> or(Condition<T> or) {
    return null;
  }
  
  public default Condition<T> not() {
    return null;
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
    - between (bt): Between 2 values, exclusive;
    - nbetween (nb): Not between 2 values, exclusive;
    - betweeni (bi): Between 2 values, inclusive;
    - equalsi (ei): Equals ignore case;
    - regex (rx): Regex expression;
    - in (in): One of many;
    - isnull (nu): Is a null value (not present);
    - notnull (nn): Not a null value (is present).
  */
  
}
