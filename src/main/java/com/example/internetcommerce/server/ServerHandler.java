package com.example.internetcommerce.server;

import com.example.internetcommerce.database.StoreDataBase;
import com.example.internetcommerce.models.User;
import com.example.internetcommerce.password.PasswordService;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64;

import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.internetcommerce.server.AdminTask.*;
import static com.example.internetcommerce.server.ManagerTask.*;

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
                System.out.println(task);
                if (task == 100) {
                    System.out.println("Client disconnect");
                    break;
                }
                setTask(task);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        dataBase.close();
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setTask(int task) throws IOException, SQLException {
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
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }


    private void userRegistration() throws IOException, SQLException, ClassNotFoundException {
        User user = (User) inputStream.readObject();
        ResultSet resultSet = dataBase.select("SELECT * FROM users WHERE e_mail = '" + user.getEmail() + "'");
        resultSet.beforeFirst();
        int counter = 0;
        while (resultSet.next()) {
            counter++;
        }
        resultSet.first();
        if (counter == 0) {
            String sqlString = "INSERT INTO users (first_name, last_name, e_mail, phone_number, password, salt, role_id) VALUES ('"
                    + user.getFirstName() + "','" + user.getLastName() + "','" + user.getEmail() + "','" + user.getPhoneNumber() + "','"
                    + user.getPassword() + "', '" + user.getSalt() + "'," + 1 + ")";
            dataBase.insert(sqlString);
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
            outputStream.writeInt(resultSet.getInt(9));
            outputStream.flush();
        }

    }


}
