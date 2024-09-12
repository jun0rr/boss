/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.boss.access.BossUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Juno
 */
public class TestBossUser {
  
  @Test
  public void test() {
    BossUser usr = BossUser.createUser("juno", "superPassword123");
    System.out.printf("* user = %s%n", usr);
    Assertions.assertTrue(usr.auth("superPassword123"));
  }
  
}
