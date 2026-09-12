package com.wsy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stockpile implements Serializable {
    private String bookISBN;
    private int stockQuantity;
}