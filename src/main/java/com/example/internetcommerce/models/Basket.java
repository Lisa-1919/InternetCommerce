package com.example.internetcommerce.models;

import java.io.Serializable;

public class Basket implements Serializable {

    private long id;
    private double sum = 0;

    public Basket(long id) {
        this.id = id;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }
}
