package com.wsy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookType implements Serializable {
    private String number;
    private String typeName;
    private int days;
    private float fk;
}