package com.example.internetcommerce.models;

import java.io.Serializable;
import java.util.List;

public class CustomList implements Serializable {

    private List<?> list;

    public CustomList() {
    }

    public CustomList(List<?> list) {
        this.list = list;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }
}
