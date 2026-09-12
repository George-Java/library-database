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
@TableName("tb_borrow")
public class Borrow implements Serializable {
    @TableId(value = "borrowId", type = IdType.INPUT)
    private int borrowId;
    private String readerNumber;
    private String bookISBN;
    private int operatorId;
    private int isBack;
    private Timestamp borrowDate;
    private Timestamp backDate;
}