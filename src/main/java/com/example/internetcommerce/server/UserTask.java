package com.example.internetcommerce.server;

import com.example.internetcommerce.database.StoreDataBase;
import com.example.internetcommerce.models.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserTask {

    protected static void getBasketListProduct(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws SQLException, IOException, ClassNotFoundException {
        User user = (User) inputStream.readObject();
        long basketId = dataBase.select("SELECT * FROM baskets WHERE user_id = " + user.getId()).getLong(1);
        ResultSet resultSet = dataBase.select("SELECT * FROM basket_products WHERE basket_id = " + basketId);
        List<Product> products = new ArrayList<>();
        resultSet.beforeFirst();
        while (resultSet.next()) {
            products.add(new Product(resultSet.getLong(2), resultSet.getInt(4)));
        }
        resultSet.first();
        for (Product product : products) {
            ResultSet resultSet1 = dataBase.select("SELECT * FROM products WHERE id = " + product.getId());
            product.setName(resultSet1.getString("name"));
            product.setPrice(resultSet1.getDouble("price"));
            product.setCategory(resultSet1.getString("category"));
        }
        outputStream.writeObject(products);
        outputStream.flush();
    }

    protected static void addToBasket(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws IOException, SQLException, ClassNotFoundException {
        User user = (User) inputStream.readObject();
        Product product = (Product) inputStream.readObject();
        long basketId = dataBase.select("SELECT * FROM baskets WHERE user_id = " + user.getId()).getLong(1);
        ResultSet resultSet = dataBase.select("SELECT * FROM basket_products WHERE product_id = " + product.getId() +
                " AND basket_id  = " + basketId);
        resultSet.beforeFirst();
        int counter = 0;
        while (resultSet.next()) {
            counter++;
        }
        resultSet.first();
        if (counter == 0) {
            dataBase.insert("INSERT INTO basket_products ( product_id, basket_id, amount) VALUES (" + product.getId() + "," +
                    basketId + "," + product.getAmount() + ")");
            outputStream.writeObject(Message.SUCCESSFUL);
            outputStream.flush();
        } else {
            outputStream.writeObject(Message.ERROR);
            outputStream.flush();
        }
    }

    public static void deleteProductFromBasket(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws IOException, SQLException, ClassNotFoundException {
        User user = (User) inputStream.readObject();
        Product product = (Product) inputStream.readObject();
        long basketId = dataBase.select("SELECT * FROM baskets WHERE user_id = " + user.getId()).getLong(1);
        dataBase.delete("DELETE FROM basket_products WHERE product_id = " + product.getId() + " AND basket_id = " + basketId);
    }

    public static void editBasketProduct(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws IOException, ClassNotFoundException {
        Basket basket = (Basket) inputStream.readObject();
        Product product = (Product) inputStream.readObject();
        dataBase.update("UPDATE basket_products SET amount = " + product.getAmount() + " WHERE basket_id = " + basket.getId() + " AND product_id =" + product.getId());
    }

    public static void createNewOrder(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws IOException, ClassNotFoundException, SQLException {
        Basket basket = (Basket) inputStream.readObject();
        Order order = (Order) inputStream.readObject();
        dataBase.insert("INSERT INTO orders(user_id, address, order_price, creation_date, payment, shipping) VALUES (" +
                order.getUserId() + ", '" + order.getAddress() + "'," + order.getOrderPrice() + ",'" + order.getCreationDate() + "','" +
                order.getPayment() + "','" + order.getShipping() + "')");
        long orderId = dataBase.select("SELECT * FROM orders WHERE user_id = " + order.getUserId() + " AND creation_date ='" + order.getCreationDate() + "'").getLong(1);
        for (Product product : order.getProducts()) {
            dataBase.delete("DELETE FROM basket_products WHERE basket_id = " + basket.getId() + " AND product_id = " + product.getId());
            dataBase.insert("INSERT INTO order_details (order_id, product_id, count, cost) VALUES (" + orderId +
                    "," + product.getId() + "," + product.getAmount() + "," + product.getPrice() + ")");
        }
    }

    public static void allOrdersView(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws IOException, SQLException, ClassNotFoundException {
        User user = (User) inputStream.readObject();
        ResultSet resultSet = dataBase.select("SELECT * FROM orders WHERE user_id = " + user.getId());
        List<Order> orders = new ArrayList<>();
        resultSet.beforeFirst();
        while (resultSet.next()) {
            Order order = new Order(resultSet.getLong("id"), new Date(resultSet.getDate("creation_date").getTime()), resultSet.getDate("receiption_date"), resultSet.getString("address"), resultSet.getDouble("order_price"));
            order.setShipping(resultSet.getString("shipping"));
            order.setPayment(resultSet.getString("payment"));
            orders.add(order);
        }
        resultSet.first();
        outputStream.writeObject(orders);
        outputStream.flush();
    }

    public static void confirmReceipt(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws IOException, ClassNotFoundException {
        Order order = (Order) inputStream.readObject();
        dataBase.update("UPDATE orders SET receiption_date = '" + order.getReceiptionDate() + "' WHERE id = " + order.getId());
    }

    public static void getOrderDetailsList(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws IOException, ClassNotFoundException, SQLException {
        Order order = (Order) inputStream.readObject();
        ResultSet resultSet = dataBase.select("SELECT * FROM order_details WHERE order_id = " + order.getId());
        List<Product> productInOrders = new ArrayList<>();
        resultSet.beforeFirst();
        while (resultSet.next()) {
            Product product = new Product();
            product.setAmount(resultSet.getInt("count"));
            product.setPrice(resultSet.getDouble("cost"));
            product.setId(resultSet.getLong("product_id"));
            productInOrders.add(product);
        }
        for (Product product : productInOrders) {
            ResultSet resultSet1 = dataBase.select("SELECT * FROM products WHERE id =" + product.getId());
            if (resultSet1 == null)
                product.setName("Товар снят с продажи");
            else
                product.setName(resultSet1.getString("name"));
        }
        outputStream.writeObject(productInOrders);
        outputStream.flush();
    }
}
