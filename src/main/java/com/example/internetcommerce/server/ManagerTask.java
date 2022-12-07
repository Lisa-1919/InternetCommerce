package com.example.internetcommerce.server;

import com.example.internetcommerce.database.StoreDataBase;
import com.example.internetcommerce.models.Order;
import com.example.internetcommerce.models.Product;
import com.example.internetcommerce.models.ProductInOrder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.example.internetcommerce.server.ServerHandler.*;

public class ManagerTask {
    public static void addNewProduct(ObjectInputStream inputStream, ObjectOutputStream outputStream,StoreDataBase dataBase) throws IOException, ClassNotFoundException {
        try {
            Product product = (Product) inputStream.readObject();
            String sqlString = "INSERT INTO products (name, description, image, price, category) VALUES ('"
                    + product.getName() + "','" + product.getDescription() + "','" + product.getImageName() + "'," + product.getPrice() + ",'" + product.getCategory() + "')";
            dataBase.insert(sqlString);
            outputStream.writeObject("add to bd");
            outputStream.flush();
        }catch (Exception e){
            outputStream.writeObject("error");
            outputStream.flush();
        }
    }

    public static void deleteProduct(ObjectInputStream inputStream, ObjectOutputStream outputStream,StoreDataBase dataBase) throws IOException {
        long productId = inputStream.readLong();
        dataBase.delete("DELETE FROM basket_products WHERE product_id = " + productId);
        dataBase.delete("DELETE FROM products WHERE id = " + productId);
    }

    public static void editProduct(ObjectInputStream inputStream, ObjectOutputStream outputStream,StoreDataBase dataBase) throws IOException, ClassNotFoundException {
        Product product = (Product) inputStream.readObject();
        dataBase.update("UPDATE products SET name = '" + product.getName() + "', description = '" + product.getDescription() +
                "', price = " + product.getPrice() + " WHERE id = " + product.getId());
    }

    public static void getSalesList(ObjectInputStream inputStream, ObjectOutputStream outputStream,StoreDataBase dataBase) throws SQLException, IOException {
        List<ProductInOrder> productInOrderList = new ArrayList<>();
        ResultSet orderSet = dataBase.select("SELECT * FROM orders");

        int counter = 0;
        List<Order> orders = new ArrayList<>();
        orderSet.beforeFirst();
        while (orderSet.next()){
            Order order = new Order(orderSet.getLong("id"), new Date(orderSet.getDate("creation_date").getTime()), orderSet.getDate("receiption_date"));
            orders.add(order);
        }
        orderSet.first();
        for(Order order: orders) {
            ResultSet orderDetailsSet = dataBase.select("SELECT * FROM order_details WHERE order_id = " + order.getId());
            orderDetailsSet.beforeFirst();
            while (orderDetailsSet.next()){
                ProductInOrder product = new ProductInOrder();
                product.setCreateOrderDate(order.getCreationDate());
                product.setReceiptOrderDate(order.getReceiptionDate());
                product.setOrderCost(orderDetailsSet.getDouble("cost"));
                product.setAmount(orderDetailsSet.getInt("count"));
                product.setProductId(orderDetailsSet.getLong("product_id"));
                product.setId(orderDetailsSet.getLong("id"));
                productInOrderList.add(product);
                counter++;
            }
            orderDetailsSet.first();
        }
        outputStream.writeInt(counter);
        outputStream.flush();
        for(ProductInOrder product: productInOrderList){
            ResultSet productSet = dataBase.select("SELECT * FROM products WHERE id = " + product.getProductId());
            productSet.beforeFirst();
            while(productSet.next()){
                product.setName(productSet.getString("name"));
                product.setCategory(productSet.getString("category"));
            }
            productSet.first();
            outputStream.writeObject(product);
            outputStream.flush();
        }
    }
    public static void createGraph(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws IOException, ClassNotFoundException, SQLException {
        int graphType = inputStream.readInt();
        switch (graphType){
            case 0:{
                Date fromDate = (Date) inputStream.readObject();
                Date toDate = (Date) inputStream.readObject();
                ResultSet orderSet = dataBase.select("SELECT * FROM orders WHERE creation_date BETWEEN '" + fromDate + "' AND '" + toDate + "'");

                break;
            }
            case 1:{
                Map<String, Double> categoriesCost = new HashMap<>();
                List<ProductInOrder> productInOrderList = new ArrayList<>();
                ResultSet orderSet = dataBase.select("SELECT * FROM orders");
                int counter = 0;
                List<Order> orders = new ArrayList<>();
                orderSet.beforeFirst();
                while (orderSet.next()){
                    Date receiptDate = null;
                    try{
                        receiptDate = new Date(orderSet.getDate("receiption_date").getTime());
                    } catch (RuntimeException e){
                        receiptDate = new Date(0000,00,00);
                    }
                    Order order = new Order(orderSet.getLong("id"), new Date(orderSet.getDate("creation_date").getTime()), new Date(orderSet.getDate("receiption_date").getTime()));
                    orders.add(order);
                }
                orderSet.first();
                for(Order order: orders) {
                    ResultSet orderDetailsSet = dataBase.select("SELECT * FROM order_details WHERE order_id = " + order.getId());
                    orderDetailsSet.beforeFirst();
                    while (orderDetailsSet.next()){
                        ProductInOrder product = new ProductInOrder();
                        product.setCreateOrderDate(order.getCreationDate());
                        product.setReceiptOrderDate(order.getReceiptionDate());
                        product.setOrderCost(orderDetailsSet.getDouble("cost"));
                        product.setAmount(orderDetailsSet.getInt("count"));
                        product.setProductId(orderDetailsSet.getLong("product_id"));
                        product.setId(orderDetailsSet.getLong("id"));
                        productInOrderList.add(product);
                        counter++;
                    }
                    orderDetailsSet.first();
                }
                for(ProductInOrder product: productInOrderList) {
                    ResultSet productSet = dataBase.select("SELECT * FROM products WHERE id = " + product.getProductId());
                    productSet.beforeFirst();
                    while (productSet.next()) {
                        product.setName(productSet.getString("name"));
                        product.setCategory(productSet.getString("category"));
                    }
                }

                break;
            }
        }
    }
}
