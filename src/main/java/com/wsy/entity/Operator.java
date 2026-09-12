package com.wsy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operator {
    private int id;
    private String name;
    private String sex;
    private int age;
    private String phone;
    private String identityCard;
    private Timestamp workDate; // DATETIME→Timestamp
    private boolean admin;
    private String userName;
    private String password;
}
