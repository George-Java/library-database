package com.wsy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_bookinfo")
public class BookInfo implements Serializable {
    @TableId(value = "bookISBN", type = IdType.INPUT)
    private String bookISBN;
    private String category;
    private String bookname;
    private String writer;
    private String publisher;
    private String translator;
    private Date date;
    private double price;
}