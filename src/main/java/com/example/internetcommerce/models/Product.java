package com.example.internetcommerce.models;

import javafx.scene.control.CheckBox;

import java.io.Serializable;

public class Product implements Serializable {

    private long id;
    private String name;
    private double price;
    private String description;
    private int numberOfViews;
    private String imageName;
    private int amount;
    private String category;
    private Boolean select;

    public Product() {
    }

    public Product(String name, double price, String description, int numberOfViews, String imageName, String category) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.numberOfViews = numberOfViews;
        this.imageName = imageName;
        this.category = category;
    }

    public Product(long id, String category, String name, double price, String description, int numberOfViews, String imageName) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.price = price;
        this.description = description;
        this.numberOfViews = numberOfViews;
        this.imageName = imageName;
    }

    public Product(long id, String name, double price, int amount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
    }

    public Product(long id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    public Product(long id, String name, int amount, String category){
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getSelect() {
        return select;
    }

    public void setSelect(Boolean select) {
        this.select = select;
    }
}
