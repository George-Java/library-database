package com.wsy.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class Borrow implements Serializable {
    private int borrowId;
    private String readerNumber;
    private String bookISBN;
    private int operatorId;
    private int isBack;
    private Timestamp borrowDate;
    private Timestamp backDate;

    public Borrow() {
    }

    public Borrow(int borrowId, String readerNumber, String bookISBN, int operatorId, int isBack, Timestamp borrowDate, Timestamp backDate) {
        this.borrowId = borrowId;
        this.readerNumber = readerNumber;
        this.bookISBN = bookISBN;
        this.operatorId = operatorId;
        this.isBack = isBack;
        this.borrowDate = borrowDate;
        this.backDate = backDate;
    }

    public int getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(int borrowId) {
        this.borrowId = borrowId;
    }

    public String getReaderNumber() {
        return readerNumber;
    }

    public void setReaderNumber(String readerNumber) {
        this.readerNumber = readerNumber;
    }

    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    public int getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(int operatorId) {
        this.operatorId = operatorId;
    }

    public int getIsBack() {
        return isBack;
    }

    public void setIsBack(int isBack) {
        this.isBack = isBack;
    }

    public Timestamp getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Timestamp borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Timestamp getBackDate() {
        return backDate;
    }

    public void setBackDate(Timestamp backDate) {
        this.backDate = backDate;
    }
}