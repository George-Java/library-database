package com.wsy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Borrow implements Serializable {
    private int borrowId;
    private String readerNumber;
    private String bookISBN;
    private int operatorId;
    private int isBack;
    private Timestamp borrowDate;
    private Timestamp backDate;
}