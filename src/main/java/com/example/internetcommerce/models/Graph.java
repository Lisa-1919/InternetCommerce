package com.example.internetcommerce.models;

import java.io.Serializable;
import java.time.LocalDate;

public class Graph implements Serializable {

    private int type;
    private LocalDate fromDate;
    private LocalDate toDate;

    public Graph() {
    }

    public Graph(int type, LocalDate fromDate, LocalDate toDate) {
        this.type = type;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }
}
