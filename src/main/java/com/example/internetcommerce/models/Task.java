package com.example.internetcommerce.models;

import java.io.Serializable;

public enum Task implements Serializable {

    //common

    AUTHORISATION,
    EXIT,
    PASSWORD_CHANGE,

    //admin

    GET_MANAGERS_LIST,
    ADD_MANAGER,
    DELETE_MANAGER,

    //manager

    ADD_PRODUCT,
    DELETE_PRODUCT,
    EDIT_PRODUCT,
    GET_SALES_LIST,
    BUILD_GRAPH,
    GENERATE_REPORT,

    //manager and user

    GET_PRODUCTS_LIST,
    APP_PRICE_FILTER,

    //user

    REGISTRATION,
    GET_BASKET_LIST,
    ADD_TO_BASKET,
    DELETE_FROM_BASKET,
    CREATE_ORDER,
    EDIT_PRODUCT_IN_BASKET,
    CONFIRM_RECEIPT,
    GET_ORDERS_LIST,
    GET_ORDER_DETAILS_LIST
}
