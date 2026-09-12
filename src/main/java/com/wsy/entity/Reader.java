package com.wsy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_reader")
public class Reader implements Serializable {
    @TableId(value = "barcode", type = IdType.INPUT)
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