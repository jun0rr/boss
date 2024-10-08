/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.access;

import java.util.Objects;

/**
 *
 * @author F6036477
 */
public record BossGroup(String name) implements Subject {
  
  public BossGroup {
    Objects.requireNonNull(name, "Name cannot be null");
  }
  
}
