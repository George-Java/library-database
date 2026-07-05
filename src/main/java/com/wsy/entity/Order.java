package com.wsy.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class Order implements Serializable {
    private int orderId;
    private String bookISBN;
    private Timestamp date;
    private int number;
    private int operator;
    private int checkAndAccept;
    private float discount;

    public Order() {
    }

    public Order(int orderId, String bookISBN, Timestamp date, int number, int operator, int checkAndAccept, float discount) {
        this.orderId = orderId;
        this.bookISBN = bookISBN;
        this.date = date;
        this.number = number;
        this.operator = operator;
        this.checkAndAccept = checkAndAccept;
        this.discount = discount;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getOperator() {
        return operator;
    }

    public void setOperator(int operator) {
        this.operator = operator;
    }

    public int getCheckAndAccept() {
        return checkAndAccept;
    }

    public void setCheckAndAccept(int checkAndAccept) {
        this.checkAndAccept = checkAndAccept;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }
}