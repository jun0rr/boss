/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.config;

/**
 *
 * @author F6036477
 */
public class BossConfigException extends RuntimeException {
  
  public BossConfigException(String msg) {
    super(msg);
  }
  
  public BossConfigException(String msg, Object... args) {
    super(String.format(msg, args));
  }
  
  public BossConfigException(Throwable cause) {
    super(cause);
  }
  
  public BossConfigException(Throwable cause, String msg) {
    super(msg, cause);
  }
  
  public BossConfigException(Throwable cause, String msg, Object... args) {
    super(String.format(msg, args), cause);
  }
  
}
