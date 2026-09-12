package com.wsy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {
    private int orderId;
    private String bookISBN;
    private Timestamp date;
    private int number;
    private int operator;
    private int checkAndAccept;
    private float discount;
}