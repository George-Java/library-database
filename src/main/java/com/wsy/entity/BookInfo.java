package com.wsy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookInfo implements Serializable {
    private String bookISBN;
    private String category;
    private String bookname;
    private String writer;
    private String publisher;
    private String translator;
    private Date date;
    private double price;
}