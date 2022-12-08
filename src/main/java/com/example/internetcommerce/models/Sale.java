package com.example.internetcommerce.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class Sale implements Serializable {

    HashMap<LocalDate, Double> sales;

    public Sale() {
    }

    public Sale(HashMap<LocalDate, Double> sales) {
        this.sales = sales;
    }

    public HashMap<LocalDate, Double> getSales() {
        return sales;
    }

    public void setSales(HashMap<LocalDate, Double> sales) {
        this.sales = sales;
    }
}
