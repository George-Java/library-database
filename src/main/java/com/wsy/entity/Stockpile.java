package com.wsy.entity;

import java.io.Serializable;

public class Stockpile implements Serializable {
    private String bookISBN;
    private int stockQuantity;

    public Stockpile() {
    }

    public Stockpile(String bookISBN, int stockQuantity) {
        this.bookISBN = bookISBN;
        this.stockQuantity = stockQuantity;
    }

    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}