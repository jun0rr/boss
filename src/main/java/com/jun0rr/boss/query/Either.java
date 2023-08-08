/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.query;

import com.jun0rr.uncheck.Uncheck;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author F6036477
 */
public record Either<A,B>(A a, B b, A mapped, Throwable error, boolean passed) {

  public static <X,Y> Either<X,Y> of(X x, Y y, X mapped, Throwable t, boolean passed) {
    return new Either(x, y, mapped, t, passed);
  }
  
  public static <X,Y> Either<X,Y> of(X x, Y y, boolean passed) {
    return new Either(x, y, null, null, passed);
  }
  
  public static <X,Y> Either<X,Y> of(X x, Y y) {
    return new Either(x, y, null, null, false);
  }
  
  public static <X> Either<X,X> of(X x) {
    return new Either(x, x, null, null, false);
  }
  
  public boolean is(Predicate<A> p) {
    return p.test(a);
  }
  
  public boolean is(Class<?> c) {
    return is(x->c.isAssignableFrom(x.getClass()));
  }
  
  public boolean isNotNull() {
    return a != null;
  }
  
  public boolean isFailed() {
    return error != null;
  }
  
  public boolean and(Predicate<A> p) {
    return passed && p.test(a);
  }
  
  public boolean or(Predicate<A> p) {
    return passed || p.test(a);
  }
  
  public Either<A,B> ifIs(Predicate<A> p) {
    return of(a, b, mapped, error, p.test(a));
  }
  
  public <C> Either<C,B> ifIs(Class<C> c) {
    return of(a, b, mapped, error, is(c)).thenMap(c);
  }
  
  public Either<A,B> ifNotNull() {
    return of(a, b, mapped, error, isNotNull());
  }
  
  public Either<A,B> andIs(Predicate<A> p) {
    return of(a, b, mapped, error, and(p));
  }
  
  public Either<A,B> orIs(Predicate<A> p) {
    return of(a, b, mapped, error, or(p));
  }
  
  public <C> Either<C,B> andIs(Class<C> c) {
    return of(a, b, mapped, error, and(x->c.isAssignableFrom(x.getClass()))).thenMap(c);
  }
  
  public <C> Either<C,B> orIs(Class<C> c) {
    return of(a, b, mapped, error, or(x->c.isAssignableFrom(x.getClass()))).thenMap(c);
  }
  
  public <C> Either<C,B> thenMap(Function<A,C> f) {
    try {
      C c = passed ? f.apply(a) : null;
      return of(c, b, c, error, passed);
    }
    catch(Throwable t) {
      return of(null, b, null, t, false);
    }
  }
  
  public <C> Either<C,B> thenMap(Class<C> c) {
    try {
      C x = passed ? c.cast(a) : null;
      return of(x, b, x, error, passed);
    }
    catch(Throwable t) {
      return of(null, b, null, t, false);
    }
  }
  
  public <C> Either<C,A> elseMap(Function<B,C> f) {
    try {
      C c = !passed ? f.apply(b) : null;
      return of(c, a, c, error, !passed);
    }
    catch(Throwable t) {
      return of(null, a, null, t, false);
    }
  }
  
  public <C> Either<C,A> elseMap(Class<C> c) {
    try {
      C x = !passed ? c.cast(b) : null;
      return of(x, a, x, error, !passed);
    }
    catch(Throwable t) {
      return of(null, a, null, t, false);
    }
  }
  
  public Either<B,A> elseIf(Predicate<B> p) {
    return of(b, a, null, error, !passed && p.test(b));
  }
  
  public <C> Either<C,A> elseIs(Class<C> c) {
    boolean p = !passed && c.isAssignableFrom(b.getClass());
    C x = p ? c.cast(b) : null;
    return of(x, a, x, error, p);
  }
  
  public Either<B,A> elseNot() {
    return of(b, a, null, error, !passed);
  }
  
  public Either<A,B> thenAccept(Consumer<A> c) {
    if(passed) c.accept(a);
    return this;
  }
  
  public Either<A,B> elseAccept(Consumer<B> c) {
    if(!passed) c.accept(b);
    return this;
  }
  
  public <T extends Throwable> Either<A,B> thenFail(Function<A,T> f) throws T {
    if(passed && f != null) throw f.apply(a);
    return this;
  }
  
  public <T extends Throwable> Either<A,B> elseFail(Function<B,T> f) throws T {
    if(!passed && f != null) throw f.apply(b);
    return this;
  }
  
  public Either<A,B> peek(Consumer<Either<A,B>> c) {
    c.accept(this);
    return this;
  }
  
  public Either<A,B> onFail(Consumer<Throwable> c) {
    if(error != null) {
      c.accept(error);
    }
    return this;
  }
  
  public Either<A,B> printFail(int level) {
    if(error != null) {
      System.err.printf("[ERROR] %s%n", error);
      if(error.getCause() != null) {
        System.err.printf("[ERROR] CAUSE: %s%n", error.getCause());
      }
      level = level < 0 ? Integer.MAX_VALUE : level;
      Stream.of(error.getStackTrace())
          .limit(level)
          .map(s->String.format("[ERROR]    at %s.%s(%s:%d)%n", s.getClassName(), s.getMethodName(), s.getFileName(), s.getLineNumber()))
          .forEach(System.out::printf)
          ;
    }
    return this;
  }
  
}
