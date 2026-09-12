package com.wsy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reader implements Serializable {
    private String barcode;
    private String name;
    private String sex;
    private int age;
    private String profession;
    private String type;
    private String identityCard;
    private int maxNum;
    private Timestamp date; // 会员有效日期
    private String phone;
    private float keepMoney;
    private Date dateOfIssuance;
}