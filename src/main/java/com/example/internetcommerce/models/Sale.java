package com.example.internetcommerce.models;

import java.io.Serializable;
import java.util.Map;

public class Sale implements Serializable {

    Map<?, ?> sales;

    public Sale() {
    }

    public Sale(Map<?, ?> sales) {
        this.sales = sales;
    }

    public Map<?, ?> getSales() {
        return sales;
    }

    public void setSales(Map<?, ?> sales) {
        this.sales = sales;
    }
}
