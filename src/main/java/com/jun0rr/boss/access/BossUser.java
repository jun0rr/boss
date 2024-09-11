/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.access;

import java.util.List;

/**
 *
 * @author F6036477
 */
public record BossUser(String name, long salt, byte[] hash, List<BossGroup> groups) implements Subject {

}
