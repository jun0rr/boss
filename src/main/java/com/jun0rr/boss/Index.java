/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss;

import com.jun0rr.jbom.BinType;
import java.util.Map;

/**
 *
 * @author F6036477
 */
public interface Index {
  
  public BinType type();
  
  public String key();
  
  public Map<Object,Address> cache();
  
}
