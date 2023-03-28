/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package com.jun0rr.boss.test.record;

import java.time.LocalDate;


/**
 *
 * @author Juno
 */
public record Person(String name, String last, LocalDate birth, Address address) {

}
