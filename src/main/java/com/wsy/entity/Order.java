package com.wsy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "tb_order")
public class Order implements Serializable {
    @TableId(value = "orderId", type = IdType.INPUT)
    private int orderId;
    private String bookISBN;
    private Timestamp date;
    private int number;
    private int operator;
    private int checkAndAccept;
    private float discount;
}