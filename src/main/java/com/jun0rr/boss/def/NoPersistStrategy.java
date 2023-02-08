/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.def;

import com.jun0rr.boss.MetaPersistStrategy;
import com.jun0rr.boss.Volume;

/**
 *
 * @author F6036477
 */
public class NoPersistStrategy implements MetaPersistStrategy {

  @Override public void save(Volume v) {}
  
  @Override public void load(Volume v) {}
  
}
