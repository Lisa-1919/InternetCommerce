package com.example.internetcommerce.server;

import com.example.internetcommerce.database.StoreDataBase;
import com.example.internetcommerce.models.*;
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
import java.util.ArrayList;
import java.util.List;

import static com.example.internetcommerce.server.AdminTask.*;
import static com.example.internetcommerce.server.ManagerTask.*;
import static com.example.internetcommerce.server.UserTask.*;

public class ServerHandler implements Runnable {
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private StoreDataBase dataBase;

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
                Task task = (Task) inputStream.readObject();
                System.out.println(LocalDateTime.now() + "   Пользователь " + socket.getInetAddress().toString() + " выполняет задачу №" + task.getTaskMessage());
                if (socket.isClosed()) {
                    System.out.println(LocalDateTime.now() + "   Пользователь " + socket.getInetAddress().toString() + " отключился");
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

    private void setTask(Task task) throws IOException, SQLException, ClassNotFoundException {
        switch (task) {
            case AUTHORISATION: {
                try {
                    userAuthenticate();
                    break;
                } catch (NoSuchAlgorithmException | InvalidKeySpecException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
            case REGISTRATION: {
                try {
                    User user = (User) inputStream.readObject();
                    outputStream.writeObject(userRegistration(user));
                    outputStream.flush();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case ADD_MANAGER: {
                try {
                    managerRegistration(inputStream, outputStream, dataBase);
                } catch (ClassNotFoundException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case ADD_PRODUCT: {
                try {
                    addNewProduct(inputStream, outputStream, dataBase);
                    break;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            case GET_PRODUCTS_LIST: {
                viewAllProducts();
                break;
            }
            case APP_PRICE_FILTER: {
                try {
                    getPriceFilter();
                    break;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
            case GET_BASKET_LIST: {
                try {
                    getBasketListProduct(inputStream, outputStream, dataBase);
                    break;
                } catch (SQLException | IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            case ADD_TO_BASKET: {
                addToBasket(inputStream, outputStream, dataBase);
                break;
            }

            case DELETE_FROM_BASKET: {
                deleteProductFromBasket(inputStream, outputStream, dataBase);
                break;
            }
            case DELETE_PRODUCT: {
                deleteProduct(inputStream, outputStream, dataBase);
                break;
            }
            case EDIT_PRODUCT: {
                try {
                    editProduct(inputStream, outputStream, dataBase);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case EDIT_PRODUCT_IN_BASKET: {
                editBasketProduct(inputStream, outputStream, dataBase);
                break;
            }
            case CREATE_ORDER: {
                try {
                    createNewOrder(inputStream, outputStream, dataBase);
                    break;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            case GET_ORDERS_LIST: {
                allOrdersView(inputStream, outputStream, dataBase);
                break;
            }
            case CONFIRM_RECEIPT: {
                try {
                    confirmReceipt(inputStream, outputStream, dataBase);
                    break;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            case PASSWORD_CHANGE: {
                try {
                    editPassword();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case GET_MANAGERS_LIST: {
                getManagersList(inputStream, outputStream, dataBase);
                break;
            }
            case DELETE_MANAGER: {
                deleteManager(inputStream, outputStream, dataBase);
                break;
            }
            case APP_CATEGORY_FILTER: {
                try {
                    filterProductByCategory();
                    break;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            case GET_SALES_LIST: {
                getSalesList(inputStream, outputStream, dataBase);
                break;
            }
            case BUILD_GRAPH: {
                createGraph(inputStream, outputStream, dataBase);
                break;
            }
            case GENERATE_REPORT: {
                createReport(inputStream, outputStream, dataBase);
                break;
            }
            case GET_ORDER_DETAILS_LIST:{
                getOrderDetailsList(inputStream, outputStream, dataBase);
                break;
            }
        }
    }

    private void filterProductByCategory() throws IOException, ClassNotFoundException, SQLException {
        String filterValue = (String) inputStream.readObject();
        ResultSet resultSet = dataBase.select("SELECT * FROM products WHERE category = '" + filterValue + "'");
        List<Product> filterProductList = new ArrayList<>();
        resultSet.beforeFirst();
        while (resultSet.next()) {
            Product product = new Product(resultSet.getLong("id"), resultSet.getString("category"), resultSet.getString("name"), resultSet.getDouble("price"), resultSet.getString("description"),
                    resultSet.getString(4));
            filterProductList.add(product);
        }
        outputStream.writeObject(filterProductList);
        outputStream.flush();
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
            outputStream.writeObject(Message.ERROR);
            outputStream.flush();
        } else {
            outputStream.writeObject(Message.SUCCESSFUL);
            outputStream.flush();
            user.setSalt((String) resultSet.getObject("salt"));
            outputStream.writeObject(user);
            outputStream.flush();
            user = (User) inputStream.readObject();
            dataBase.update("UPDATE users SET password = '" + user.getPassword() + "' WHERE e_mail = '" + user.getEmail() + "' AND phone_number = '" + user.getPhoneNumber() + "'");
        }

    }

    private void getPriceFilter() throws IOException, ClassNotFoundException, SQLException {
        double[] values = (double[]) inputStream.readObject();
        ResultSet resultSet = dataBase.select("SELECT * FROM products WHERE price BETWEEN " + values[0] + " AND " + values[1]);
        List<Product> products = new ArrayList<>();
        while (resultSet.next()) {
            products.add(new Product(resultSet.getLong(1), resultSet.getString("category"), resultSet.getString(2), resultSet.getDouble(5), resultSet.getString(3),
                    resultSet.getString(4)));
        }
        outputStream.writeObject(products);
        outputStream.flush();

    }

    private void viewAllProducts() throws IOException, SQLException {
        ResultSet resultSet = dataBase.select("SELECT * FROM products");
        List<Product> productList = new ArrayList<>();
        resultSet.beforeFirst();
        while (resultSet.next()) {
            Product product = new Product(resultSet.getLong("id"), resultSet.getString("category"), resultSet.getString("name"), resultSet.getDouble("price"), resultSet.getString("description"),
                    resultSet.getString(4));
            productList.add(product);
        }
        outputStream.writeObject(productList);
        outputStream.flush();
    }


    public Message userRegistration(User user) throws IOException, SQLException, ClassNotFoundException {
        //User user = (User) inputStream.readObject();
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
//            outputStream.writeObject(Message.SUCCESSFUL);
//            outputStream.flush();
            return Message.SUCCESSFUL;
        } else {
            return Message.ERROR;
//            outputStream.writeObject(Message.ERROR);
//            outputStream.flush();
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
            outputStream.writeObject(Message.ERROR);
            outputStream.flush();
        } else {
            String passwordFromDB = resultSet.getString("password");
            String salt = resultSet.getString("salt");
            boolean flag = encryptionService.authenticate(user.getPassword(), Base64.decode(passwordFromDB), Base64.decode(salt));
            if (flag) {
                outputStream.writeObject(Message.SUCCESSFUL);
                outputStream.flush();
                user.setId(resultSet.getLong("id"));
                user.setFirstName(resultSet.getString("first_name"));
                user.setLastName(resultSet.getString("last_name"));
                user.setRoleId(resultSet.getLong("role_id"));
                user.setBirthday(resultSet.getObject("birthday", LocalDate.class));
                if (user.getRoleId() == 1) {
                    ResultSet resultSet1 = dataBase.select("SELECT * FROM baskets WHERE user_id = " + user.getId());
                    Basket basket = new Basket(resultSet1.getLong(1));
                    user.setBasket(basket);
                }
                outputStream.writeObject(user);
                outputStream.flush();
            } else {
                outputStream.writeObject(Message.ERROR);
                outputStream.flush();
            }

        }

    }


}
