package com.example.internetcommerce.server;

import com.example.internetcommerce.database.StoreDataBase;
import com.example.internetcommerce.models.Basket;
import com.example.internetcommerce.models.Product;
import com.example.internetcommerce.models.User;
import com.example.internetcommerce.password.PasswordService;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.internetcommerce.server.AdminTask.*;
import static com.example.internetcommerce.server.ManagerTask.*;
import static com.example.internetcommerce.server.UserTask.*;

public class ServerHandler implements Runnable {
    protected static ObjectOutputStream outputStream;
    protected static ObjectInputStream inputStream;
    protected static StoreDataBase dataBase;

    private Socket socket;

    public ServerHandler(Socket socket, StoreDataBase dataBase) {
        this.socket = socket;
        try {
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.dataBase = dataBase;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int task = inputStream.readInt();
                System.out.println(LocalDateTime.now() + "   Клиент " + socket.getInetAddress().toString() + " выполняет задачу №" + task);
                if (task == 100) {
                    System.out.println(LocalDateTime.now() + "   Клиент " + socket.getInetAddress().toString() + " отключился");
                    break;
                }
                setTask(task);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        this.dataBase.close();
        try {
            this.inputStream.close();
            this.outputStream.close();
            this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setTask(int task) throws IOException, SQLException, ClassNotFoundException {
        switch (task) {
            case 0: {
                try {
                    userAuthenticate();
                    break;
                } catch (NoSuchAlgorithmException | InvalidKeySpecException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
            case 1: {
                try {
                    userRegistration();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case 2: {
                try {
                    managerRegistration();
                } catch (ClassNotFoundException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case 3: {
                try {
                    addNewProduct();
                    break;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            case 4:{
                viewAllProducts();
                break;
            }
            case 5:{
                try {
                    getPriceFilter();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case 6:{
                try {
                    getBasketListProduct();
                    break;
                } catch (SQLException | IOException | ClassNotFoundException e){
                    throw new RuntimeException(e);
                }
            }
            case 7: {
                addToBasket();
                break;
            }

            case 8:{
                deleteProductFromBasket();
                break;
            }
            case 9:{
                deleteProduct();
                break;
            }
            case 10:{
                try {
                    editProduct();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case 11:{
                editBasketProduct();
                break;
            }
            case 12: {
                try {
                    createNewOrder();
                    break;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            case 13:{
                allOrdersView();
                break;
            }
            case 14:{
                try {
                    confirmReceipt();
                    break;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            case 15:{
                try {
                    editPassword();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case 16:{
                getManagersList();
                break;
            }
            case 17:{
                deleteManager();
                break;
            }
            case 18:{
                try {
                    filterProductByCategory();
                    break;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            case 19:{
                getSalesList();
                break;
            }
            case 20:{
                createGraph();
                break;
            }
        }
    }

    private void filterProductByCategory() throws IOException, ClassNotFoundException, SQLException {
        String filterValue = (String) inputStream.readObject();
        ResultSet resultSet = dataBase.select("SELECT * FROM products WHERE category = '" + filterValue + "'");
        Product product = null;
        int rowcount = 0;
        if (resultSet.last()) {
            rowcount = resultSet.getRow();
            resultSet.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
        }
        outputStream.writeInt(rowcount);
        outputStream.flush();
        while (resultSet.next()) {
            product = new Product(resultSet.getLong("id"), resultSet.getString("category"), resultSet.getString("name"), resultSet.getDouble("price"), resultSet.getString("description"),
                    resultSet.getInt(6), resultSet.getString(4));
            outputStream.writeObject(product);
            outputStream.flush();
        }
    }

    private void editPassword() throws IOException, ClassNotFoundException, SQLException {
        User user = (User) inputStream.readObject();
        ResultSet resultSet = dataBase.select("SELECT * FROM users WHERE e_mail = '" + user.getEmail() + "' AND phone_number = '" + user.getPhoneNumber() + "'");
        resultSet.beforeFirst();
        int counter = 0;
        while (resultSet.next()) {
            counter++;
        }
        resultSet.first();
        if (counter == 0) {
            outputStream.writeObject("error");
            outputStream.flush();

        } else {
            outputStream.writeObject("successful");
            outputStream.flush();
            outputStream.writeObject(resultSet.getObject("salt"));
            outputStream.flush();
            String newPassword = (String) inputStream.readObject();
            dataBase.update("UPDATE users SET password = '" + newPassword + "' WHERE e_mail = '" + user.getEmail() + "' AND phone_number = '" + user.getPhoneNumber() + "'");
        }

    }

    private void getPriceFilter() throws IOException, ClassNotFoundException, SQLException {
        double[] values = (double[]) inputStream.readObject();
        ResultSet resultSet = dataBase.select("SELECT * FROM products WHERE price BETWEEN " + values[0] + " AND " + values[1]);
        Product product = null;
        int rowcount = 0;
        if (resultSet.last()) {
            rowcount = resultSet.getRow();
            resultSet.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
        }
        outputStream.writeInt(rowcount);
        outputStream.flush();
        while (resultSet.next()) {
            product = new Product(resultSet.getLong(1),resultSet.getString("category"), resultSet.getString(2), resultSet.getDouble(5), resultSet.getString(3),
                    resultSet.getInt(6), resultSet.getString(4));
            outputStream.writeObject(product);
            outputStream.flush();
        }
    }

    private void viewAllProducts() throws IOException, SQLException {
        ResultSet resultSet = dataBase.select("SELECT * FROM products");
        Product product = null;
        int rowcount = 0;
        if (resultSet.last()) {
            rowcount = resultSet.getRow();
            resultSet.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
        }
        outputStream.writeInt(rowcount);
        outputStream.flush();
        while (resultSet.next()) {
            product = new Product(resultSet.getLong("id"), resultSet.getString("category"), resultSet.getString("name"), resultSet.getDouble("price"), resultSet.getString("description"),
                    resultSet.getInt(6), resultSet.getString(4));
            outputStream.writeObject(product);
            outputStream.flush();
        }
    }


    private void userRegistration() throws IOException, SQLException, ClassNotFoundException {
        User user = (User) inputStream.readObject();
        ResultSet resultSet = dataBase.select("SELECT * FROM users WHERE e_mail = '" + user.getEmail() + "' OR phone_number = '" + user.getPhoneNumber() + "'");
        resultSet.beforeFirst();
        int counter = 0;
        while (resultSet.next()) {
            counter++;
        }
        resultSet.first();
        if (counter == 0) {
            String sqlString = "INSERT INTO users (first_name, last_name, e_mail, phone_number, country, birthday, password, salt, role_id) VALUES ('"
                    + user.getFirstName() + "','" + user.getLastName() + "','" + user.getEmail() + "','" + user.getPhoneNumber() + "','" + user.getCountry() +
                    "','" + user.getBirthday() + "','" + user.getPassword() + "', '" + user.getSalt() + "'," + 1 + ")";
            dataBase.insert(sqlString);
            resultSet = dataBase.select("SELECT * FROM users WHERE e_mail = '" + user.getEmail() + "'");
            String sqlBasketString = "INSERT INTO baskets (sum, user_id) VALUES (" + 0 + "," + resultSet.getLong(1) + ")";
            dataBase.insert(sqlBasketString);
            outputStream.writeObject("add to bd");
            outputStream.flush();
        } else {
            outputStream.writeObject("error");
            outputStream.flush();
        }

    }

    private void userAuthenticate() throws IOException, SQLException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException {
        PasswordService encryptionService = new PasswordService();
        User user = (User) inputStream.readObject();
        String sqlString = "SELECT * FROM users WHERE e_mail = '" + user.getEmail() + "'";
        ResultSet resultSet = dataBase.select(sqlString);
        resultSet.beforeFirst();
        int counter = 0;
        while (resultSet.next()) {
            counter++;
        }
        resultSet.first();
        if (counter == 0) {
            outputStream.writeObject("error");
            outputStream.flush();
        } else {
            String passwordFromDB = resultSet.getString("password");
            String salt = resultSet.getString("salt");
            boolean flag = encryptionService.authenticate(user.getPassword(), Base64.decode(passwordFromDB), Base64.decode(salt));
            if (flag) {
                outputStream.writeObject("true");
                outputStream.flush();
            } else {
                outputStream.writeObject("false");
                outputStream.flush();
            }
            user.setId(resultSet.getLong("id"));
            user.setFirstName(resultSet.getString("first_name"));
            user.setLastName(resultSet.getString("last_name"));
            user.setRoleId(resultSet.getLong("role_id"));
            user.setBirthday(resultSet.getObject("birthday", LocalDate.class));
            if(user.getRoleId() == 1){
                ResultSet resultSet1 = dataBase.select("SELECT * FROM baskets WHERE user_id = " + user.getId());
                Basket basket = new Basket(resultSet1.getLong(1));
                user.setBasket(basket);
            }
            outputStream.writeObject(user);
            outputStream.flush();
        }

    }


}
