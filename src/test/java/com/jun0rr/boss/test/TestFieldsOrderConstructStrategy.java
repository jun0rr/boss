/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.binj.BinContext;
import com.jun0rr.binj.buffer.BinBuffer;
import com.jun0rr.binj.mapping.FieldMethodGetStrategy;
import com.jun0rr.binj.mapping.FieldsOrderConstructStrategy;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestFieldsOrderConstructStrategy {
  
  @Test public void test() {
    BinContext ctx = BinContext.newContext();
    ctx.mapper().constructStrategies().put(0, new FieldsOrderConstructStrategy());
    ctx.mapper().extractStrategies().put(0, new FieldMethodGetStrategy());
    Person p = new Person("John", "Doe", LocalDate.now(), new Address("Foo Street", "Bar City", 4004), new long[]{0, 1});
    System.out.printf("* person: %s%n", p);
    BinBuffer buf = BinBuffer.ofDirectAllocator(64);
    System.out.printf("* calcSise: %d%n", ctx.calcSize(p));
    ctx.write(buf, p);
    System.out.printf("* buffer: %s%n", buf);
    p = ctx.read(buf.flip());
    System.out.printf("* person: %s%n", p);
  }
  
}
