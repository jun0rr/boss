/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.query;

import io.vertx.core.json.JsonArray;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public abstract class Transforms {
  
  public static Transform add(String field, Object value) {
    return new Transform(field, value, (a,b)->{
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
      else if(JsonArray.class.isAssignableFrom(a.getClass())
          && Temporal.class.isAssignableFrom(b.getClass())) {
        JsonArray x = (JsonArray) a;
        Temporal y = (Temporal) b;
        return y.plus(x.getLong(1), tempUnit(x.getString(0)));
      }
      return Double.NaN;
    });
  }
  
  public static Transform sub(String field, Object value) {
    return new Transform(field, value, (a,b)->{
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
      else if(JsonArray.class.isAssignableFrom(a.getClass())
          && Temporal.class.isAssignableFrom(b.getClass())) {
        JsonArray x = (JsonArray) a;
        Temporal y = (Temporal) b;
        return y.minus(x.getLong(1), tempUnit(x.getString(0)));
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
      if(Comparable.class.isAssignableFrom(a.getClass())
          && Comparable.class.isAssignableFrom(b.getClass())) {
        Comparable x = (Comparable) a;
        Comparable y = (Comparable) b;
        int r = y.compareTo(x);
        return r >= 0 ? y : x;
      }
      return false;
    });
  }
  
  public static Transform min(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(Comparable.class.isAssignableFrom(a.getClass())
          && Comparable.class.isAssignableFrom(b.getClass())) {
        Comparable x = (Comparable) a;
        Comparable y = (Comparable) b;
        int r = y.compareTo(x);
        return r <= 0 ? y : x;
      }
      return false;
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
  
  public static Transform tostr(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      return Objects.toString(b);
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
      return JsonArray.of(y.split(x));
    });
  }
  
  public static Transform todt(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!String.class.isAssignableFrom(b.getClass())) {
        return false;
      }
      String y = (String) b;
      DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      return LocalDateTime.parse(y, fmt);
    });
  }
  
  public static Transform told(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!String.class.isAssignableFrom(b.getClass())) {
        return false;
      }
      String y = (String) b;
      DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      return LocalDate.parse(y, fmt);
    });
  }
  
  public static Transform tozd(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!String.class.isAssignableFrom(b.getClass())) {
        return false;
      }
      String y = (String) b;
      DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      return LocalDateTime.parse(y, fmt).atZone(ZoneId.systemDefault());
    });
  }
  
  private static ChronoField chronoField(String field) {
    return switch(field) {
      case "year" -> ChronoField.YEAR;
      case "day" -> ChronoField.DAY_OF_MONTH;
      case "month" -> ChronoField.MONTH_OF_YEAR;
      case "hour" -> ChronoField.HOUR_OF_DAY;
      case "minute" -> ChronoField.MINUTE_OF_HOUR;
      case "second" -> ChronoField.SECOND_OF_MINUTE;
      case "weekday" -> ChronoField.DAY_OF_WEEK;
      default -> null;
    };
  }
  
  private static ChronoUnit tempUnit(String field) {
    return switch(field) {
      case "year" -> ChronoUnit.YEARS;
      case "day" -> ChronoUnit.DAYS;
      case "month" -> ChronoUnit.MONTHS;
      case "hour" -> ChronoUnit.HOURS;
      case "minute" -> ChronoUnit.MINUTES;
      case "second" -> ChronoUnit.SECONDS;
      default -> null;
    };
  }
  
  public static Transform getdt(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!String.class.isAssignableFrom(a.getClass()) 
          || !TemporalAccessor.class.isAssignableFrom(b.getClass())) {
        return false;
      }
      String x = (String) a;
      TemporalAccessor y = (TemporalAccessor) b;
      return y.get(chronoField(x));
    });
  }
  
  public static Transform setdt(String field, Object value) {
    return new Transform(field, value, (a,b)->{
      if(!JsonArray.class.isAssignableFrom(a.getClass()) 
          && !Temporal.class.isAssignableFrom(b.getClass())) {
        System.out.printf("-> setdt: a.class=%s, b.class=%s%n", a.getClass(), b.getClass());
        return false;
      }
      JsonArray x = (JsonArray) a;
      Temporal y = (Temporal) b;
      return y.with(chronoField(x.getString(0)), x.getLong(1));
    });
  }
  
}
