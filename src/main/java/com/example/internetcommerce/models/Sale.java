package com.example.internetcommerce.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class Sale implements Serializable {

    HashMap<?, ?> sales;

    public Sale() {
    }

    public Sale(HashMap<?, ?> sales) {
        this.sales = sales;
    }

    public HashMap<?, ?> getSales() {
        return sales;
    }

    public void setSales(HashMap<?, ?> sales) {
        this.sales = sales;
    }
}
