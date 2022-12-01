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
        try {
            Product product = (Product) inputStream.readObject();
            String sqlString = "INSERT INTO products (name, description, image, price) VALUES ('"
                    + product.getName() + "','" + product.getDescription() + "','" + product.getImageName() + "'," + product.getPrice() + ")";
            dataBase.insert(sqlString);
            outputStream.writeObject("add to bd");
            outputStream.flush();
        }catch (Exception e){
            outputStream.writeObject("error");
            outputStream.flush();
        }
    }

    public static void deleteProduct() throws IOException {
        long productId = inputStream.readLong();
        dataBase.delete("DELETE FROM basket_products WHERE product_id = " + productId);
        dataBase.delete("DELETE FROM products WHERE id = " + productId);
    }

    public static void editProduct() throws IOException, ClassNotFoundException {
        Product product = (Product) inputStream.readObject();
        dataBase.update("UPDATE products SET name = '" + product.getName() + "', description = '" + product.getDescription() +
                "', price = " + product.getPrice() + " WHERE id = " + product.getId());
    }
}
