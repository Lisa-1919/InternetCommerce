package com.example.internetcommerce.server;

import com.example.internetcommerce.database.StoreDataBase;
import com.example.internetcommerce.models.Product;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;

import static com.example.internetcommerce.server.ServerHandler.*;

public class ManagerTask {
    public static void addNewProduct() throws IOException, ClassNotFoundException {
        Product product = (Product) inputStream.readObject();
        String sqlString = "INSERT INTO products (name, description, image, price) VALUES ('"
                + product.getName() + "','" + product.getDescription() + "','" + product.getImageName() + "','" + product.getPrice() + "','"
                 + ")";
        dataBase.insert(sqlString);
        outputStream.writeObject("add to bd");
        outputStream.flush();
    }

    public static void deleteProduct(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase){

    }

}
