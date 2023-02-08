/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.def;

import com.jun0rr.boss.VolumeMetadata;
import com.jun0rr.jbom.mapping.MapConstructor;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class DefaultVolumeMetadata implements VolumeMetadata {
  
  private final String id;
  
  private final int blockSize;
  
  private final int woffset;
  
  private final int[] freebufs;
  
  @MapConstructor({"id", "blockSize", "woffset", "freebufs"})
  public DefaultVolumeMetadata(String id, int blockSize, int woffset, int[] freebufs) {
    this.id = Objects.requireNonNull(id);
    this.blockSize = blockSize;
    this.woffset = woffset;
    this.freebufs = Objects.requireNonNull(freebufs);
  }

  @Override
  public String id() {
    return id;
  }
  
  @Override
  public int blockSize() {
    return blockSize;
  }
  
  @Override
  public int writeOffset() {
    return woffset;
  }
  
  @Override
  public int[] freeOffsets() {
    return freebufs;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 89 * hash + Objects.hashCode(this.id);
    hash = 89 * hash + this.blockSize;
    hash = 89 * hash + this.woffset;
    hash = 89 * hash + Arrays.hashCode(this.freebufs);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final DefaultVolumeMetadata other = (DefaultVolumeMetadata) obj;
    if (this.blockSize != other.blockSize) {
      return false;
    }
    if (this.woffset != other.woffset) {
      return false;
    }
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    return Arrays.equals(this.freebufs, other.freebufs);
  }

  @Override
  public String toString() {
    return "VolumeMetadata{" + "id=" + id + ", blockSize=" + blockSize + ", woffset=" + woffset + ", freebufs=" + Arrays.toString(freebufs) + '}';
  }

}
