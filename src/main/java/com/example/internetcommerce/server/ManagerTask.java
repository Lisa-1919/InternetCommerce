package com.example.internetcommerce.server;

import com.example.internetcommerce.database.StoreDataBase;
import com.example.internetcommerce.models.*;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class ManagerTask {
    public static void addNewProduct(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws IOException, ClassNotFoundException {
        try {
            Product product = (Product) inputStream.readObject();
            String sqlString = "INSERT INTO products (name, description, image, price, category) VALUES ('"
                    + product.getName() + "','" + product.getDescription() + "','" + product.getImageName() + "'," + product.getPrice() + ",'" + product.getCategory() + "')";
            dataBase.insert(sqlString);
            outputStream.writeObject(Message.SUCCESSFUL);
            outputStream.flush();
        } catch (Exception e) {
            outputStream.writeObject(Message.ERROR);
            outputStream.flush();
        }
    }

    public static void deleteProduct(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws IOException, ClassNotFoundException {
        Product product = (Product) inputStream.readObject();
        dataBase.delete("DELETE FROM basket_products WHERE product_id = " + product.getId());
        dataBase.delete("DELETE FROM products WHERE id = " + product.getId());
    }

    public static void editProduct(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws IOException, ClassNotFoundException {
        Product product = (Product) inputStream.readObject();
        dataBase.update("UPDATE products SET name = '" + product.getName() + "', description = '" + product.getDescription() +
                "', price = " + product.getPrice() + " WHERE id = " + product.getId());
    }

    public static void getSalesList(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws SQLException, IOException {
        List<ProductInOrder> productInOrderList = new ArrayList<>();
        ResultSet orderSet = dataBase.select("SELECT * FROM orders");
        List<Order> orders = new ArrayList<>();
        orderSet.beforeFirst();
        while (orderSet.next()) {
            Date receiptDate = null;
            if (orderSet.getDate("receiption_date") == null) {
                receiptDate = orderSet.getDate("receiption_date");
            } else {
                receiptDate = new Date(orderSet.getDate("receiption_date").getTime());
            }
            Order order = new Order(orderSet.getLong("id"), new Date(orderSet.getDate("creation_date").getTime()), receiptDate);
            orders.add(order);
        }
        orderSet.first();
        for (Order order : orders) {
            ResultSet orderDetailsSet = dataBase.select("SELECT * FROM order_details WHERE order_id = " + order.getId());
            orderDetailsSet.beforeFirst();
            while (orderDetailsSet.next()) {
                ProductInOrder product = new ProductInOrder();
                product.setCreateOrderDate(order.getCreationDate());
                product.setReceiptOrderDate(order.getReceiptionDate());
                product.setOrderCost(orderDetailsSet.getDouble("cost"));
                product.setAmount(orderDetailsSet.getInt("count"));
                product.setProductId(orderDetailsSet.getLong("product_id"));
                product.setId(orderDetailsSet.getLong("id"));
                productInOrderList.add(product);
            }
            orderDetailsSet.first();
        }
        for (ProductInOrder product : productInOrderList) {
            ResultSet productSet = dataBase.select("SELECT * FROM products WHERE id = " + product.getProductId());
            productSet.beforeFirst();
            while (productSet.next()) {
                product.setName(productSet.getString("name"));
                product.setCategory(productSet.getString("category"));
            }
            productSet.first();
        }
        outputStream.writeObject(new CustomList(productInOrderList));
    }

    public static void createGraph(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws IOException, ClassNotFoundException, SQLException {
        LocalDate fromDate = (LocalDate) inputStream.readObject();
        LocalDate toDate = (LocalDate) inputStream.readObject();
        int graphType = (int) inputStream.readObject();
        List<ProductInOrder> sales = new ArrayList<>();
        ResultSet orderSet = dataBase.select("SELECT * FROM orders WHERE creation_date BETWEEN '" + fromDate + "' AND '" + toDate + "'");

        int counter = 0;
        List<Order> orders = new ArrayList<>();
        orderSet.beforeFirst();
        while (orderSet.next()) {
            Order order = new Order();
            order.setId(orderSet.getLong("id"));
            order.setCreationDate(new Date(orderSet.getDate("creation_date").getTime()));
            orders.add(order);
        }
        orderSet.first();
        for (Order order : orders) {
            ResultSet orderDetailsSet = dataBase.select("SELECT * FROM order_details WHERE order_id = " + order.getId());
            orderDetailsSet.beforeFirst();
            while (orderDetailsSet.next()) {
                ProductInOrder product = new ProductInOrder();
                product.setCreateOrderDate(order.getCreationDate());
                product.setReceiptOrderDate(order.getReceiptionDate());
                product.setOrderCost(orderDetailsSet.getDouble("cost"));
                product.setAmount(orderDetailsSet.getInt("count"));
                product.setProductId(orderDetailsSet.getLong("product_id"));
                product.setId(orderDetailsSet.getLong("id"));
                sales.add(product);
                counter++;
            }
        }

        if (counter != 0) {
            for (ProductInOrder product : sales) {
                ResultSet productSet = dataBase.select("SELECT * FROM products WHERE id = " + product.getProductId());
                productSet.beforeFirst();
                while (productSet.next()) {
                    product.setName(productSet.getString("name"));
                    product.setCategory(productSet.getString("category"));
                }
            }
            switch (graphType) {
                case 0: {
                    HashMap<LocalDate, Double> salesMap = new HashMap<>();
                    for (ProductInOrder product : sales) {
                        salesMap.merge(product.getCreateOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), product.getOrderCost() * product.getAmount(), Double::sum);
                    }
                    Sale sale = new Sale(salesMap);
                    outputStream.writeObject(sale);
                    outputStream.flush();
                    break;
                }
                case 1: {
                    HashMap<String, Integer> categorySales = new HashMap<>();
                    for (ProductInOrder product : sales) {
                        categorySales.merge(product.getCategory(), product.getAmount(), Integer::sum);
                    }
                    Sale sale = new Sale(categorySales);
                    outputStream.writeObject(sale);
                    outputStream.flush();
                    break;
                }
                case 2: {
                    HashMap<String, Double> categorySales = new HashMap<>();
                    for (ProductInOrder product : sales) {
                        categorySales.merge(product.getCategory(), product.getOrderCost() * product.getAmount(), Double::sum);
                    }
                    Sale sale = new Sale(categorySales);
                    outputStream.writeObject(sale);
                    outputStream.flush();
                    break;
                }
            }
        } else
            outputStream.writeObject(Message.ERROR);outputStream.flush();
    }

    public static void createReport(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws IOException, ClassNotFoundException, SQLException {
        LocalDate[] dates = (LocalDate[]) inputStream.readObject();
        LocalDate from = dates[0];
        LocalDate to = dates[1];
        int reportType = inputStream.readInt();
        List<ProductInOrder> sales = new ArrayList<>();
        ResultSet orderSet = dataBase.select("SELECT * FROM orders WHERE creation_date BETWEEN '" + from + "' AND '" + to + "'");

        int counter = 0;
        List<Order> orders = new ArrayList<>();
        orderSet.beforeFirst();
        while (orderSet.next()) {
            Date receiptDate = null;
            if (orderSet.getDate("receiption_date") == null) {
                receiptDate = orderSet.getDate("receiption_date");
            } else {
                receiptDate = new Date(orderSet.getDate("receiption_date").getTime());
            }
            Order order = new Order(orderSet.getLong("id"), new Date(orderSet.getDate("creation_date").getTime()), receiptDate);
            orders.add(order);
        }
        orderSet.first();
        for (Order order : orders) {
            ResultSet orderDetailsSet = dataBase.select("SELECT * FROM order_details WHERE order_id = " + order.getId());
            orderDetailsSet.beforeFirst();
            while (orderDetailsSet.next()) {
                ProductInOrder product = new ProductInOrder();
                product.setCreateOrderDate(order.getCreationDate());
                product.setReceiptOrderDate(order.getReceiptionDate());
                product.setOrderCost(orderDetailsSet.getDouble("cost"));
                product.setAmount(orderDetailsSet.getInt("count"));
                product.setProductId(orderDetailsSet.getLong("product_id"));
                product.setId(orderDetailsSet.getLong("id"));
                sales.add(product);
                counter++;
            }
        }
        if (counter != 0) {
            String type = "";
            if (reportType == 0)
                type = "txt";
            else
                type = "exel";
            Writer writer = new FileWriter("D:/Курсовая (5 семестр)/Отчеты/" + from.toString() + "." + type, true);
            for (ProductInOrder product : sales) {
                ResultSet productSet = dataBase.select("SELECT * FROM products WHERE id = " + product.getProductId());
                productSet.beforeFirst();
                while (productSet.next()) {
                    product.setName(productSet.getString("name"));
                    product.setCategory(productSet.getString("category"));
                }
                writer.write(product.toString() + "\n");
            }
            writer.close();
        }
    }
}
