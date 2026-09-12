package com.wsy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_operator")
public class Operator {
    @TableId(value = "id", type = IdType.INPUT)
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
