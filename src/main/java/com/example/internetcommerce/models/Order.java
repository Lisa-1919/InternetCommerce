package com.example.internetcommerce.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    private long id;
    private long userId;
    private Date creationDate;
    private Date receiptionDate;
    private List<Product> products;
    private String address;
    private double orderPrice;
    private String shipping;

    private String payment;

    public Order() {
    }

    public Order(long userId, Date creationDate, Date receiptionDate, List<Product> products, String address, double orderPrice, String shipping) {
        this.userId = userId;
        this.creationDate = creationDate;
        this.receiptionDate = receiptionDate;
        this.products = products;
        this.address = address;
        this.orderPrice = orderPrice;
        this.shipping = shipping;
    }

    public Order(long userId, Date creationDate, List<Product> products, String address, double orderPrice, String shipping, String payment) {
        this.userId = userId;
        this.creationDate = creationDate;
        this.products = products;
        this.address = address;
        this.orderPrice = orderPrice;
        this.shipping = shipping;
        this.payment = payment;
    }

    public Order(long id, Date creationDate, Date receiptionDate, String address, double orderPrice) {
        this.id = id;
        this.creationDate = creationDate;
        this.receiptionDate = receiptionDate;
        this.products = products;
        this.address = address;
        this.orderPrice = orderPrice;
    }

    public Order(long id, Date creationDate, Date receiptionDate) {
        this.id = id;
        this.creationDate = creationDate;
        this.receiptionDate = receiptionDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getReceiptionDate() {
        return receiptionDate;
    }

    public void setReceiptionDate(Date receiptionDate) {
        this.receiptionDate = receiptionDate;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public String getShipping() {
        return shipping;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }
}
