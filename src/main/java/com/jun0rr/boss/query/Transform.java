/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.query;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *
 * @author F6036477
 */
public record Transform(String field, Object value, BiFunction<Object,Object,Object> function) implements Function {

  @Override
  public Object apply(Object o) {
    return function.apply(value, JsonExtract.extract(field, o));
  }
  
  /*
  
  {
    collection: <name>,
    fields: [<field0>, <field1.nestedField>, ... <fiendN>],
    sort (sortAsc): [<field0>, <field1.nestedField>, ... <fiendN>],
    sortDesc: [<field0>, <field1.nestedField>, ... <fiendN>],
    filter:
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
    transform:
    [
      {
        field: <field0>,
        add: <value>,
        div: <value>,
        sub: <value>,
        sq: null
      },
      {
        max: <field0>
      },
      {
        field: <field1>,
        todt: null,
        add: [day, 5],
        setdt: [year, 2024],
        getdt: month,
        
      }...
    ],
    stream:
    {
      field: <field0>
      dt: null,
      add: [day, 4],
      gd: day,
      ge: 5,
      pw: 2,
      ts: null
    }
    ]
  }
  Filters:
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
    - contains (ct): String/Array contains
    - endswith (ew): String/Array ends with
    - startswith (sw): String/Array starts with
    - in: One of many;
    - notin (ni): Not in one of many;
    - isnull (nu): Is a null value (not present);
    - notnull (nn): Not a null value (is present).
  
  Transforms:
    - add;  //+
    - sub;  //subtract
    - div;  //divide
    - x;    //multiply
    - pow (pw);
    - *sqrt (sq);
    - *max;
    - *min;
    - *ceil (cl);
    - *floor (fl);
    - substring (ss);
    - concat (cc);
    - replace (rp);
    - size (sz);
    - strip (st);
    - split (sp);
    - upper (up); //upper case
    - lower (lw); //lower case
    - tostr (ts); //to string
    - todt (sd); //string to LocalDateTime
    - tozd (zd); //string to ZonedDateTime
    - told (ld); //string to LocalDate
    - getdt (gd); //get a date field [year, month, day, hour, minute, second, millis]
    - setdt (gd); //set a date field [year, month, day, hour, minute, second, millis]
  */
  
}
