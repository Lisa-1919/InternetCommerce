package com.example.internetcommerce.models;

import java.io.Serializable;

public class Product implements Serializable {

    private long id;
    private String name;
    private double price;
    private String description;
    private int numberOfViews;
    private String imageName;

    public Product() {
    }

    public Product(String name, double price, String description, int numberOfViews, String imageName) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.numberOfViews = numberOfViews;
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumberOfViews() {
        return numberOfViews;
    }

    public void setNumberOfViews(int numberOfViews) {
        this.numberOfViews = numberOfViews;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
