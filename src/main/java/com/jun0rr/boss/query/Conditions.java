/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss.query;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 *
 * @author f6036477
 */
public abstract class Conditions {
  
  public static Condition eq(String field, Object value) {
    return new Condition(field, value, (a,b)->Objects.equals(a, b));
  }
  
  public static Condition ne(String field, Object value) {
    return new Condition(field, value, (a,b)->!Objects.equals(a, b));
  }
  
  public static Condition gt(String field, Object value) {
    return new Condition(field, value, (a,b)->{
      if(a.getClass() != b.getClass()) {
        return false;
      }
      if(Comparable.class.isAssignableFrom(a.getClass())) {
        Comparable c = (Comparable) a;
        Comparable d = (Comparable) b;
        return d.compareTo(c) > 0;
      }
      else if(JsonArray.class.isAssignableFrom(a.getClass())) {
        JsonArray j = (JsonArray) a;
        JsonArray k = (JsonArray) b;
        return Integer.compare(k.size(), j.size()) > 0;
      }
      else if(JsonObject.class.isAssignableFrom(a.getClass())) {
        JsonObject j = (JsonObject) a;
        JsonObject k = (JsonObject) b;
        return Integer.compare(k.size(), j.size()) > 0;
      }
      return false;
    });
  }
  
  public static Condition ge(String field, Object value) {
    return new Condition(field, value, (a,b)->{
      if(a.getClass() != b.getClass()) {
        return false;
      }
      if(Comparable.class.isAssignableFrom(a.getClass())) {
        Comparable c = (Comparable) a;
        Comparable d = (Comparable) b;
        return d.compareTo(c) >= 0;
      }
      else if(JsonArray.class.isAssignableFrom(a.getClass())) {
        JsonArray j = (JsonArray) a;
        JsonArray k = (JsonArray) b;
        return Integer.compare(k.size(), j.size()) >= 0;
      }
      else if(JsonObject.class.isAssignableFrom(a.getClass())) {
        JsonObject j = (JsonObject) a;
        JsonObject k = (JsonObject) b;
        return Integer.compare(k.size(), j.size()) >= 0;
      }
      return false;
    });
  }
  
  public static Condition lt(String field, Object value) {
    return new Condition(field, value, (a,b)->{
      if(a.getClass() != b.getClass()) {
        return false;
      }
      if(Comparable.class.isAssignableFrom(a.getClass())) {
        Comparable c = (Comparable) a;
        Comparable d = (Comparable) b;
        return d.compareTo(c) < 0;
      }
      else if(JsonArray.class.isAssignableFrom(a.getClass())) {
        JsonArray j = (JsonArray) a;
        JsonArray k = (JsonArray) b;
        return Integer.compare(k.size(), j.size()) < 0;
      }
      else if(JsonObject.class.isAssignableFrom(a.getClass())) {
        JsonObject j = (JsonObject) a;
        JsonObject k = (JsonObject) b;
        return Integer.compare(k.size(), j.size()) < 0;
      }
      return false;
    });
  }
  
  public static Condition le(String field, Object value) {
    return new Condition(field, value, (a,b)->{
      if(a.getClass() != b.getClass()) {
        return false;
      }
      if(Comparable.class.isAssignableFrom(a.getClass())) {
        Comparable c = (Comparable) a;
        Comparable d = (Comparable) b;
        return d.compareTo(c) <= 0;
      }
      else if(JsonArray.class.isAssignableFrom(a.getClass())) {
        JsonArray j = (JsonArray) a;
        JsonArray k = (JsonArray) b;
        return Integer.compare(k.size(), j.size()) <= 0;
      }
      else if(JsonObject.class.isAssignableFrom(a.getClass())) {
        JsonObject j = (JsonObject) a;
        JsonObject k = (JsonObject) b;
        return Integer.compare(k.size(), j.size()) <= 0;
      }
      return false;
    });
  }
  
  public static Condition bt(String field, Object value) {
    return new Condition(field, value, (a,b)->{
      if(!JsonArray.class.isAssignableFrom(a.getClass()) 
          || !Comparable.class.isAssignableFrom(b.getClass()) 
          || !Comparable.class.isAssignableFrom(((JsonArray)a).getValue(0).getClass())) {
        return false;
      }
      JsonArray va = (JsonArray) a;
      Comparable x = (Comparable) va.getValue(0);
      Comparable y = (Comparable) va.getValue(1);
      Comparable z = (Comparable) b;
      return z.compareTo(x) >= 0 && z.compareTo(y) < 0;
    });
  }
  
  public static Condition nb(String field, Object value) {
    return new Condition(field, value, (a,b)->{
      if(!JsonArray.class.isAssignableFrom(a.getClass()) 
          || !Comparable.class.isAssignableFrom(b.getClass()) 
          || !Comparable.class.isAssignableFrom(((JsonArray)a).getValue(0).getClass())) {
        return false;
      }
      JsonArray va = (JsonArray) a;
      Comparable x = (Comparable) va.getValue(0);
      Comparable y = (Comparable) va.getValue(1);
      Comparable z = (Comparable) b;
      return z.compareTo(x) < 0 || z.compareTo(y) >= 0;
    });
  }
  
  public static Condition ct(String field, Object value) {
    return new Condition(field, value, (a,b)->{
      if(String.class.isAssignableFrom(a.getClass()) 
          && String.class.isAssignableFrom(b.getClass())) {
        String x = (String) a;
        String y = (String) b;
        return y.contains(x);
      }
      else if(JsonArray.class.isAssignableFrom(b.getClass())) {
        return ((JsonArray)b).contains(a);
      }
      else return false;
    });
  }
  
  public static Condition ew(String field, Object value) {
    return new Condition(field, value, (a,b)->{
      if(String.class.isAssignableFrom(a.getClass()) 
          && String.class.isAssignableFrom(b.getClass())) {
        String x = (String) a;
        String y = (String) b;
        return y.endsWith(x);
      }
      else if(JsonArray.class.isAssignableFrom(b.getClass())) {
        JsonArray y = (JsonArray) b;
        return Objects.equals(y.getValue(y.size() -1), a);
      }
      else return false;
    });
  }
  
  public static Condition sw(String field, Object value) {
    return new Condition(field, value, (a,b)->{
      if(String.class.isAssignableFrom(a.getClass()) 
          && String.class.isAssignableFrom(b.getClass())) {
        String x = (String) a;
        String y = (String) b;
        return y.startsWith(x);
      }
      else if(JsonArray.class.isAssignableFrom(b.getClass())) {
        JsonArray y = (JsonArray) b;
        return Objects.equals(y.getValue(0), a);
      }
      else return false;
    });
  }
  
  public static Condition eqi(String field, Object value) {
    return new Condition(field, value, (a,b)->{
      if(!String.class.isAssignableFrom(a.getClass()) 
          || !String.class.isAssignableFrom(b.getClass())) {
        return false;
      }
      String x = (String) a;
      String y = (String) b;
      return x.equalsIgnoreCase(y);
    });
  }
  
  public static Condition rx(String field, Object value) {
    return new Condition(field, value, (a,b)->{
      if(!String.class.isAssignableFrom(a.getClass()) 
          || !String.class.isAssignableFrom(b.getClass())) {
        return false;
      }
      String x = (String) a;
      String y = (String) b;
      return Pattern.compile(x)
          .asMatchPredicate()
          .test(y);
    });
  }
  
  public static Condition in(String field, Object value) {
    return new Condition(field, value, (a,b)->{
      if(!JsonArray.class.isAssignableFrom(a.getClass())) {
        return false;
      }
      JsonArray va = (JsonArray) a;
      if(JsonArray.class.isAssignableFrom(b.getClass())) {
        JsonArray vb = (JsonArray) b;
        return vb.stream()
            .allMatch(o->va.stream()
                .anyMatch(p->Objects.equals(o, p)));
      }
      else {
        return va.stream()
            .anyMatch(o->Objects.equals(b, o));
      }
    });
  }
  
  public static Condition ni(String field, Object value) {
    return new Condition(field, value, (a,b)->{
      if(!JsonArray.class.isAssignableFrom(a.getClass())) {
        return false;
      }
      JsonArray va = (JsonArray) a;
      if(JsonArray.class.isAssignableFrom(b.getClass())) {
        JsonArray vb = (JsonArray) b;
        return vb.stream()
            .allMatch(o->va.stream()
                .noneMatch(p->Objects.equals(o, p)));
      }
      else {
        return va.stream()
            .noneMatch(o->Objects.equals(b, o));
      }
    });
  }
  
}
