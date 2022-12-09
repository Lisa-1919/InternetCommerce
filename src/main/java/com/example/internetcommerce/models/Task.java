package com.example.internetcommerce.models;

import java.io.Serializable;

public enum Task implements Serializable {

    //common

    AUTHORISATION("authorisation"),
    EXIT("exit"),
    PASSWORD_CHANGE("change password"),

    //admin

    GET_MANAGERS_LIST("get managers list"),
    ADD_MANAGER("add new manager"),
    DELETE_MANAGER("delete manager"),

    //manager

    ADD_PRODUCT ("add product"),
    DELETE_PRODUCT("delete product"),
    EDIT_PRODUCT("edit product"),
    GET_SALES_LIST("get sales list"),
    BUILD_GRAPH("build graph"),
    GENERATE_REPORT("generate report"),

    //manager and user

    GET_PRODUCTS_LIST("get product list"),
    APP_PRICE_FILTER("app price filter"),

    APP_CATEGORY_FILTER("app category filter"),

    //user

    REGISTRATION("registration"),
    GET_BASKET_LIST("get basket list"),
    ADD_TO_BASKET("add product to basket"),
    DELETE_FROM_BASKET("delete product from basket"),
    CREATE_ORDER("create order"),
    EDIT_PRODUCT_IN_BASKET("edit product in basket"),
    CONFIRM_RECEIPT("confirm receipt"),
    GET_ORDERS_LIST("get orders list"),
    GET_ORDER_DETAILS_LIST("get order details list");

    private String taskMessage;
    Task(String taskMessage) {
        this.taskMessage = taskMessage;
    }

    public String getTaskMessage() {
        return taskMessage;
    }

    public void setTaskMessage(String taskMessage) {
        this.taskMessage = taskMessage;
    }
}
