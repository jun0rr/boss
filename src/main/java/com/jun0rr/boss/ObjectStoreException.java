/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss;

/**
 *
 * @author F6036477
 */
public class ObjectStoreException extends RuntimeException {

  public ObjectStoreException(String msg) {
    super(msg);
  }

  public ObjectStoreException(String msg, Object... args) {
    super(String.format(msg, args));
  }

  public ObjectStoreException(Throwable cause) {
    super(cause);
  }

  public ObjectStoreException(Throwable cause, String msg) {
    super(msg, cause);
  }

  public ObjectStoreException(Throwable cause, String msg, Object... args) {
    super(String.format(msg, args), cause);
  }
  
}
