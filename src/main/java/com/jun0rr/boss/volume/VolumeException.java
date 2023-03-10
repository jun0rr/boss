/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.volume;

import com.jun0rr.boss.config.*;

/**
 *
 * @author F6036477
 */
public class VolumeException extends RuntimeException {
  
  public VolumeException(String msg) {
    super(msg);
  }
  
  public VolumeException(String msg, Object... args) {
    super(String.format(msg, args));
  }
  
  public VolumeException(Throwable cause) {
    super(cause);
  }
  
  public VolumeException(Throwable cause, String msg) {
    super(msg, cause);
  }
  
  public VolumeException(Throwable cause, String msg, Object... args) {
    super(String.format(msg, args), cause);
  }
  
}
