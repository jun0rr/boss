/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss;

import com.jun0rr.jbom.BinType;

/**
 *
 * @author F6036477
 */
public interface Address {
  
  public BinType type();
  
  public int offset();
  
  public int size();
  
  public String volumeID();
  
}
