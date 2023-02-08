/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.def;

import com.jun0rr.boss.Block;
import com.jun0rr.boss.MetaKey;
import com.jun0rr.boss.MetaPersistStrategy;
import com.jun0rr.boss.Volume;
import com.jun0rr.jbom.BinContext;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class FirstBlockPersistStrategy implements MetaPersistStrategy {
  
  private final BinContext ctx;
  
  public FirstBlockPersistStrategy(BinContext ctx) {
    this.ctx = Objects.requireNonNull(ctx);
  }

  @Override 
  public void save(Volume v) {
    Block b = v.get(0);
    v.release(b);
    ctx.write(b.buffer(), v.metadata());
  }
  
  @Override 
  public void load(Volume v) {
    Block b = v.get(0);
    Map<MetaKey,Object> meta = ctx.read(b.buffer());
    meta.forEach((k,o)->v.metadata().put(k,o));
    v.release(b);
  }
  
}
