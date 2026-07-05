package com.wsy.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class Reader implements Serializable {
    private String barcode;
    private String name;
    private String sex;
    private int age;
    private String profession;
    private String type;
    private String identityCard;
    private int maxNum;
    private Timestamp date; // 会员有效日期
    private String phone;
    private float keepMoney;
    private Date dateOfIssuance;

    public Reader() {
    }

    public Reader(String barcode, String name, String sex, int age, String profession, String type, String identityCard,
                  int maxNum, Timestamp date, String phone, float keepMoney, Date dateOfIssuance) {
        this.barcode = barcode;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.profession = profession;
        this.type = type;
        this.identityCard = identityCard;
        this.maxNum = maxNum;
        this.date = date;
        this.phone = phone;
        this.keepMoney = keepMoney;
        this.dateOfIssuance = dateOfIssuance;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public float getKeepMoney() {
        return keepMoney;
    }

    public void setKeepMoney(float keepMoney) {
        this.keepMoney = keepMoney;
    }

    public Date getDateOfIssuance() {
        return dateOfIssuance;
    }

    public void setDateOfIssuance(Date dateOfIssuance) {
        this.dateOfIssuance = dateOfIssuance;
    }

    // 新增，供 TableView 反射用
    public String getDateString() {
        if (date == null) return "";
        return date.toLocalDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public String getDateOfIssuanceString() {
        if (dateOfIssuance == null) return "";
        return dateOfIssuance.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}