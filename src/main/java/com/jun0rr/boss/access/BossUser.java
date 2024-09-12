/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.access;

import com.jun0rr.uncheck.Uncheck;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 *
 * @author F6036477
 */
public record BossUser(String name, long salt, byte[] hash, List<BossGroup> groups) implements Subject {
  
  public BossUser {
    Objects.requireNonNull(name, "Name cannot be null");
    if(salt == 0) {
      throw new IllegalArgumentException("Salt cannot be zero");
    }
    Objects.requireNonNull(hash, "Hash cannot be null");
    Objects.requireNonNull(groups, "Groups cannot be null");
  }

  public BossUser(String name, long salt, byte[] hash) {
    this(name, salt, hash, new LinkedList<>());
  }
  
  public BossUser add(BossGroup g) {
    if(g != null && !groups.contains(g)) {
      groups.add(g);
    }
    return this;
  }
  
  public boolean auth(String password) {
    ByteBuffer buf = ByteBuffer.allocate(Long.BYTES);
    buf.putLong(salt);
    MessageDigest md = Uncheck.call(()->MessageDigest.getInstance("SHA-1"));
    md.update(buf.flip());
    md.update(StandardCharsets.UTF_8.encode(password));
    return Arrays.equals(hash, md.digest());
  }
  
  public static BossUser createUser(String name, String password) {
    ByteBuffer buf = ByteBuffer.allocate(Long.BYTES);
    long salt = new Random().nextLong();
    buf.putLong(salt);
    MessageDigest md = Uncheck.call(()->MessageDigest.getInstance("SHA-1"));
    md.update(buf.flip());
    md.update(StandardCharsets.UTF_8.encode(password));
    return new BossUser(name, salt, md.digest());
  }
  
}
