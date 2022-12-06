package com.example.internetcommerce.server;

import com.example.internetcommerce.models.Order;
import com.example.internetcommerce.models.Product;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.internetcommerce.server.ServerHandler.*;

public class UserTask {

    protected static void getBasketListProduct() throws SQLException, IOException, ClassNotFoundException {
        long userId = inputStream.readLong();
        long basketId = dataBase.select("SELECT * FROM baskets WHERE user_id = " + userId).getLong(1);
        ResultSet resultSet = dataBase.select("SELECT * FROM basket_products WHERE basket_id = " + basketId);
        List<Product> products = new ArrayList<>();
        resultSet.beforeFirst();
        int counter = 0;
        while (resultSet.next()) {
            counter++;
            products.add(new Product(resultSet.getLong(2), resultSet.getInt(4)));
        }
        resultSet.first();
        outputStream.writeInt(counter);
        outputStream.flush();
        for (Product product : products) {
            ResultSet resultSet1 = dataBase.select("SELECT * FROM products WHERE id = " + product.getId());
            product.setName(resultSet1.getString("name"));
            product.setPrice(resultSet1.getDouble("price"));
            product.setCategory(resultSet1.getString("category"));
            outputStream.writeObject(product);
            outputStream.flush();
        }
    }

    protected static void addToBasket() throws IOException, SQLException {
        long userId = inputStream.readLong();
        long productId = inputStream.readLong();
        int amount = inputStream.readInt();
        long basketId = dataBase.select("SELECT * FROM baskets WHERE user_id = " + userId).getLong(1);
        ResultSet resultSet = dataBase.select("SELECT * FROM basket_products WHERE product_id = " + productId +
                " AND basket_id  = " + basketId);
        resultSet.beforeFirst();
        int counter = 0;
        while (resultSet.next()) {
            counter++;
        }
        resultSet.first();
        if (counter == 0) {
            dataBase.insert("INSERT INTO basket_products ( product_id, basket_id, amount) VALUES (" + productId + "," +
                    basketId + "," + amount + ")");
            outputStream.writeObject("successful");
            outputStream.flush();
        } else {
            outputStream.writeObject("error");
            outputStream.flush();
        }
    }

    public static void deleteProductFromBasket() throws IOException, SQLException {
        long userId = inputStream.readLong();
        long productId = inputStream.readLong();
        long basketId = dataBase.select("SELECT * FROM baskets WHERE user_id = " + userId).getLong(1);
        dataBase.delete("DELETE FROM basket_products WHERE product_id = " + productId + " AND basket_id = " + basketId);
    }

    public static void editBasketProduct() throws IOException {
        long basketId = inputStream.readLong();
        long productId = inputStream.readLong();
        int amount = inputStream.readInt();
        dataBase.update("UPDATE basket_products SET amount = " + amount + " WHERE basket_id = " + basketId + " AND product_id =" + productId);
    }

    public  static void createNewOrder() throws IOException, ClassNotFoundException, SQLException {
        long basketId = inputStream.readLong();
        Order order = (Order) inputStream.readObject();
        dataBase.insert("INSERT INTO orders(user_id, address, order_price, creation_date, payment, shipping) VALUES (" +
                order.getUserId() + ", '" + order.getAddress() + "'," + order.getOrderPrice() + ",'" + order.getCreationDate() + "','" +
                order.getPayment() +"','" + order.getShipping() +"')");
        long orderId = dataBase.select("SELECT * FROM orders WHERE user_id = " + order.getUserId() + " AND creation_date ='" + order.getCreationDate() + "'").getLong(1);
        for (Product product: order.getProducts()) {
            dataBase.delete("DELETE FROM basket_products WHERE basket_id = " + basketId + " AND product_id = " + product.getId());
            dataBase.insert("INSERT INTO order_details (order_id, product_id, count, cost) VALUES (" + orderId +
                    "," + product.getId() + "," + product.getAmount() + "," + product.getPrice()* product.getAmount()+")");
        }
    }

    public static void allOrdersView() throws IOException, SQLException {
        long userId = inputStream.readLong();
        ResultSet resultSet = dataBase.select("SELECT * FROM orders WHERE user_id = " + userId);
        List<Order> orders = new ArrayList<>();
        resultSet.beforeFirst();
        int counter = 0;
        while (resultSet.next()) {
            counter++;
            orders.add(new Order(resultSet.getLong("id"), new Date(resultSet.getDate("creation_date").getTime()), new Date(resultSet.getDate("receiption_date").getTime()), resultSet.getString("address"), resultSet.getDouble("order_price")));
        }
        resultSet.first();
        outputStream.writeInt(counter);
        outputStream.flush();
        for (Order order: orders) {
            outputStream.writeObject(order);
            outputStream.flush();
        }
    }

    public static void confirmReceipt() throws IOException, ClassNotFoundException {
        Order order = (Order) inputStream.readObject();
        dataBase.update("UPDATE orders SET receiption_date = '" + order.getReceiptionDate() + "' WHERE id = " + order.getId());
    }
}
