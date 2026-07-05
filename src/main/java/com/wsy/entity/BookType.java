package com.wsy.entity;

import java.io.Serializable;

public class BookType implements Serializable {
    private String number;
    private String typeName;
    private int days;
    private float fk;

    public BookType() {
    }

    public BookType(String number, String typeName, int days, float fk) {
        this.number = number;
        this.typeName = typeName;
        this.days = days;
        this.fk = fk;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public float getFk() {
        return fk;
    }

    public void setFk(float fk) {
        this.fk = fk;
    }
}