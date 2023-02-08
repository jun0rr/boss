/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss;

import com.jun0rr.jbom.mapping.Binary;

/**
 *
 * @author F6036477
 */
public interface VolumeMetadata {
  
  @Binary public String id();
  
  @Binary public int blockSize();
  
  @Binary public int writeOffset();
  
  @Binary public int[] freeOffsets();
  
}
