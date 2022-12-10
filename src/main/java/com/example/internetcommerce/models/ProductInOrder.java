package com.example.internetcommerce.models;

import java.io.Serializable;
import java.util.Date;

public class ProductInOrder extends Product implements Serializable {

    private Date createOrderDate;
    private Date receiptOrderDate;
    private double orderCost;
    private long productId;

    public ProductInOrder() {
    }

    public ProductInOrder(long id, String name, int amount, String category) {
        super(id, name, amount, category);
    }

    public Date getCreateOrderDate() {
        return createOrderDate;
    }

    public void setCreateOrderDate(Date createOrderDate) {
        this.createOrderDate = createOrderDate;
    }

    public Date getReceiptOrderDate() {
        return receiptOrderDate;
    }

    public void setReceiptOrderDate(Date receiptOrderDate) {
        this.receiptOrderDate = receiptOrderDate;
    }

    public double getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(double orderCost) {
        this.orderCost = orderCost;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return super.getId() + ". " + super.getName() + " " + super.getAmount() + " " + orderCost + " руб. " + createOrderDate + " " + receiptOrderDate;
    }
}
