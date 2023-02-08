/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss;

import java.nio.file.Path;

/**
 *
 * @author F6036477
 */
public interface VolumeConfig {
  
  public int bufferSize();
  
  public int blockSize();
  
  public Path storagePath();
  
  public MetaPersistStrategy metaPersistStrategy();
  
}
