/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.query;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 * @author F6036477
 */
public record Either<A,B>(A a, B b, boolean passed) {

  public static <X,Y> Either<X,Y> of(X x, Y y, boolean passed) {
    return new Either(x, y, passed);
  }
  
  public static <X,Y> Either<X,Y> of(X x, Y y) {
    return new Either(x, y, false);
  }
  
  public static <X> Either<X,X> of(X x) {
    return new Either(x, x, false);
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
  
  public boolean and(Predicate<A> p) {
    return passed && p.test(a);
  }
  
  public boolean or(Predicate<A> p) {
    return passed || p.test(a);
  }
  
  public Either<A,B> ifIs(Predicate<A> p) {
    return of(a, b, p.test(a));
  }
  
  public <C> Either<C,B> ifIs(Class<C> c) {
    return of(a, b, is(c)).thenMap(c);
  }
  
  public Either<A,B> ifIsNotNull() {
    return of(a, b, isNotNull());
  }
  
  public Either<A,B> andIs(Predicate<A> p) {
    return of(a, b, and(p));
  }
  
  public Either<A,B> orIs(Predicate<A> p) {
    return of(a, b, or(p));
  }
  
  public <C> Either<C,B> andIs(Class<C> c) {
    return of(a, b, and(x->c.isAssignableFrom(x.getClass()))).thenMap(c);
  }
  
  public <C> Either<C,B> orIs(Class<C> c) {
    return of(a, b, or(x->c.isAssignableFrom(x.getClass()))).thenMap(c);
  }
  
  public <C> Either<C,B> thenMap(Function<A,C> f) {
    C c = passed ? f.apply(a) : null;
    return of(c, b, passed);
  }
  
  public <C> Either<C,B> thenMap(Class<C> c) {
    C x = passed ? c.cast(a) : null;
    return of(x, b, passed);
  }
  
  public <C> Either<C,A> elseMap(Function<B,C> f) {
    C c = !passed ? f.apply(b) : null;
    return of(c, a, !passed);
  }
  
  public <C> Either<C,A> elseMap(Class<C> c) {
    C x = !passed ? c.cast(b) : null;
    return of(x, a, !passed);
  }
  
  public Either<B,A> elseIf(Predicate<B> p) {
    return of(b, a, !passed && p.test(b));
  }
  
  public <C> Either<C,A> elseIs(Class<C> c) {
    boolean p = !passed && c.isAssignableFrom(b.getClass());
    return of(p ? c.cast(b) : null, a, p);
  }
  
  public Either<B,A> elseNot() {
    return of(b, a, !passed);
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
  
  public Either<A,B> not() {
    return of(a, b, !passed);
  }
  
  public Either<A,B> peek(Consumer<Either<A,B>> c) {
    c.accept(this);
    return this;
  }
  
}