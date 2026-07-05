package com.wsy.entity;

import java.io.Serializable;
import java.sql.Date;

public class BookInfo implements Serializable {
    private String bookISBN;
    private String category;
    private String bookname;
    private String writer;
    private String publisher;
    private String translator;
    private Date date;
    private double price;

    public BookInfo() {
    }

    public BookInfo(String bookISBN, String category, String bookname, String writer, String publisher,
                    String translator, Date date, double price) {
        this.bookISBN = bookISBN;
        this.category = category;
        this.bookname = bookname;
        this.writer = writer;
        this.publisher = publisher;
        this.translator = translator;
        this.date = date;
        this.price = price;
    }

    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}