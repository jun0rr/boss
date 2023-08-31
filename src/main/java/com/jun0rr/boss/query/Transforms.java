/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.query;

import io.vertx.core.json.JsonArray;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public abstract class Transforms {
  
  public static Transform add(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!Number.class.isAssignableFrom(a.getClass()) 
          || !Number.class.isAssignableFrom(b.getClass())) {
        return Double.NaN;
      }
      if(Double.class.isAssignableFrom(a.getClass())
          || Double.class.isAssignableFrom(b.getClass())) {
        Double x = (Double) a;
        Double y = (Double) b;
        return x.doubleValue() + y.doubleValue();
      }
      else if(Long.class.isAssignableFrom(a.getClass())
          || Long.class.isAssignableFrom(b.getClass())) {
        Long x = (Long) a;
        Long y = (Long) b;
        return x.longValue() + y.longValue();
      }
      else if(Integer.class.isAssignableFrom(a.getClass())
          || Integer.class.isAssignableFrom(b.getClass())) {
        Integer x = (Integer) a;
        Integer y = (Integer) b;
        return x.intValue() + y.intValue();
      }
      return Double.NaN;
    });
  }
  
  public static Transform sub(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!Number.class.isAssignableFrom(a.getClass()) 
          || !Number.class.isAssignableFrom(b.getClass())) {
        return Double.NaN;
      }
      if(Double.class.isAssignableFrom(a.getClass())
          || Double.class.isAssignableFrom(b.getClass())) {
        Double x = (Double) a;
        Double y = (Double) b;
        return y.doubleValue() - x.doubleValue();
      }
      else if(Long.class.isAssignableFrom(a.getClass())
          || Long.class.isAssignableFrom(b.getClass())) {
        Long x = (Long) a;
        Long y = (Long) b;
        return y.longValue() - x.longValue();
      }
      else if(Integer.class.isAssignableFrom(a.getClass())
          || Integer.class.isAssignableFrom(b.getClass())) {
        Integer x = (Integer) a;
        Integer y = (Integer) b;
        return y.intValue() - x.intValue();
      }
      return Double.NaN;
    });
  }
  
  public static Transform div(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!Number.class.isAssignableFrom(a.getClass()) 
          || !Number.class.isAssignableFrom(b.getClass())) {
        return Double.NaN;
      }
      if(Double.class.isAssignableFrom(a.getClass())
          || Double.class.isAssignableFrom(b.getClass())) {
        Double x = (Double) a;
        Double y = (Double) b;
        return y.doubleValue() / x.doubleValue();
      }
      else if(Long.class.isAssignableFrom(a.getClass())
          || Long.class.isAssignableFrom(b.getClass())) {
        Long x = (Long) a;
        Long y = (Long) b;
        return y.longValue() / x.longValue();
      }
      else if(Integer.class.isAssignableFrom(a.getClass())
          || Integer.class.isAssignableFrom(b.getClass())) {
        Integer x = (Integer) a;
        Integer y = (Integer) b;
        return y.intValue() / x.intValue();
      }
      return Double.NaN;
    });
  }
  
  public static Transform x(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!Number.class.isAssignableFrom(a.getClass()) 
          || !Number.class.isAssignableFrom(b.getClass())) {
        return Double.NaN;
      }
      if(Double.class.isAssignableFrom(a.getClass())
          || Double.class.isAssignableFrom(b.getClass())) {
        Double x = (Double) a;
        Double y = (Double) b;
        return y.doubleValue() * x.doubleValue();
      }
      else if(Long.class.isAssignableFrom(a.getClass())
          || Long.class.isAssignableFrom(b.getClass())) {
        Long x = (Long) a;
        Long y = (Long) b;
        return y.longValue() * x.longValue();
      }
      else if(Integer.class.isAssignableFrom(a.getClass())
          || Integer.class.isAssignableFrom(b.getClass())) {
        Integer x = (Integer) a;
        Integer y = (Integer) b;
        return y.intValue() * x.intValue();
      }
      return Double.NaN;
    });
  }
  
  public static Transform pow(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!Number.class.isAssignableFrom(a.getClass()) 
          || !Number.class.isAssignableFrom(b.getClass())) {
        return Double.NaN;
      }
      Number x = (Number) a;
      Number y = (Number) b;
      return Math.pow(y.doubleValue(), x.doubleValue());
    });
  }
  
  public static Transform sq(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!Number.class.isAssignableFrom(b.getClass())) {
        return Double.NaN;
      }
      Number y = (Number) b;
      return Math.sqrt(y.doubleValue());
    });
  }
  
  public static Transform max(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!Number.class.isAssignableFrom(a.getClass()) 
          || !Number.class.isAssignableFrom(b.getClass())) {
        return Double.NaN;
      }
      if(Double.class.isAssignableFrom(a.getClass())
          || Double.class.isAssignableFrom(b.getClass())) {
        Double x = (Double) a;
        Double y = (Double) b;
        return Math.max(y.doubleValue(), x.doubleValue());
      }
      else if(Long.class.isAssignableFrom(a.getClass())
          || Long.class.isAssignableFrom(b.getClass())) {
        Long x = (Long) a;
        Long y = (Long) b;
        return Math.max(y.longValue(), x.longValue());
      }
      else if(Integer.class.isAssignableFrom(a.getClass())
          || Integer.class.isAssignableFrom(b.getClass())) {
        Integer x = (Integer) a;
        Integer y = (Integer) b;
        return Math.max(y.intValue(), x.intValue());
      }
      return Double.NaN;
    });
  }
  
  public static Transform min(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!Number.class.isAssignableFrom(a.getClass()) 
          || !Number.class.isAssignableFrom(b.getClass())) {
        return Double.NaN;
      }
      if(Double.class.isAssignableFrom(a.getClass())
          || Double.class.isAssignableFrom(b.getClass())) {
        Double x = (Double) a;
        Double y = (Double) b;
        return Math.min(y.doubleValue(), x.doubleValue());
      }
      else if(Long.class.isAssignableFrom(a.getClass())
          || Long.class.isAssignableFrom(b.getClass())) {
        Long x = (Long) a;
        Long y = (Long) b;
        return Math.min(y.longValue(), x.longValue());
      }
      else if(Integer.class.isAssignableFrom(a.getClass())
          || Integer.class.isAssignableFrom(b.getClass())) {
        Integer x = (Integer) a;
        Integer y = (Integer) b;
        return Math.min(y.intValue(), x.intValue());
      }
      return Double.NaN;
    });
  }
  
  public static Transform ceil(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!Number.class.isAssignableFrom(b.getClass())) {
        return Double.NaN;
      }
      if(Double.class.isAssignableFrom(b.getClass())) {
        Double y = (Double) b;
        return Math.ceil(y.doubleValue());
      }
      else if(Long.class.isAssignableFrom(b.getClass())) {
        Long y = (Long) b;
        return Math.ceil(y.longValue());
      }
      else if(Integer.class.isAssignableFrom(b.getClass())) {
        Integer y = (Integer) b;
        return Math.ceil(y.intValue());
      }
      return Double.NaN;
    });
  }
  
  public static Transform floor(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!Number.class.isAssignableFrom(b.getClass())) {
        return Double.NaN;
      }
      if(Double.class.isAssignableFrom(b.getClass())) {
        Double y = (Double) b;
        return Math.floor(y.doubleValue());
      }
      else if(Long.class.isAssignableFrom(b.getClass())) {
        Long y = (Long) b;
        return Math.floor(y.longValue());
      }
      else if(Integer.class.isAssignableFrom(b.getClass())) {
        Integer y = (Integer) b;
        return Math.floor(y.intValue());
      }
      return Double.NaN;
    });
  }
  
  public static Transform substring(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!JsonArray.class.isAssignableFrom(a.getClass()) 
          || !String.class.isAssignableFrom(b.getClass())) {
        return false;
      }
      JsonArray x = (JsonArray) a;
      String y = (String) b;
      return y.substring(x.getInteger(0), x.getInteger(1));
    });
  }
  
  public static Transform replace(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!JsonArray.class.isAssignableFrom(a.getClass()) 
          || !String.class.isAssignableFrom(b.getClass())) {
        return false;
      }
      JsonArray x = (JsonArray) a;
      String y = (String) b;
      return y.replace(x.getString(0), x.getString(1));
    });
  }
  
  public static Transform concat(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!String.class.isAssignableFrom(a.getClass()) 
          || !String.class.isAssignableFrom(b.getClass())) {
        return false;
      }
      String x = (String) a;
      String y = (String) b;
      return y.concat(x);
    });
  }
  
  public static Transform size(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(String.class.isAssignableFrom(b.getClass())) {
        String y = (String) b;
        return y.length();
      }
      else if(JsonArray.class.isAssignableFrom(b.getClass())) {
        JsonArray y = (JsonArray) b;
        return y.size();
      }
      return Double.NaN;
    });
  }
  
  public static Transform strip(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!String.class.isAssignableFrom(b.getClass())) {
        return false;
      }
      String y = (String) b;
      return y.strip();
    });
  }
  
  public static Transform upper(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!String.class.isAssignableFrom(b.getClass())) {
        return false;
      }
      String y = (String) b;
      return y.toUpperCase();
    });
  }
  
  public static Transform lower(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!String.class.isAssignableFrom(b.getClass())) {
        return false;
      }
      String y = (String) b;
      return y.toLowerCase();
    });
  }
  
  public static Transform split(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!String.class.isAssignableFrom(a.getClass())
          || !String.class.isAssignableFrom(b.getClass())) {
        return false;
      }
      String x = (String) a;
      String y = (String) b;
      return y.split(x);
    });
  }
  
}
