package com.wsy.entity;

import java.sql.Timestamp;

public class Operator {
    private int id;
    private String name;
    private String sex;
    private int age;
    private String phone;
    private String identityCard;
    private Timestamp workDate; // DATETIME→Timestamp
    private boolean admin;
    private String userName;
    private String password;

    public Operator() {
    }

    public Operator(int id, String name, String sex, int age, String phone, String identityCard, Timestamp workDate, boolean admin, String userName, String password) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.phone = phone;
        this.identityCard = identityCard;
        this.workDate = workDate;
        this.admin = admin;
        this.userName = userName;
        this.password = password;
    }

    // Getter & Setter
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public int getAge() {
        return age;
    }

    public String getPhone() {
        return phone;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public Timestamp getWorkDate() {
        return workDate;
    }

    public boolean isAdmin() {
        return admin;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public void setWorkDate(Timestamp workDate) {
        this.workDate = workDate;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
