package com.example.internetcommerce.server;

import com.example.internetcommerce.database.StoreDataBase;
import com.example.internetcommerce.password.PasswordEncryptionService;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerHandler {

    private BufferedReader reader;
    private BufferedWriter writer;
    private StoreDataBase dataBase;

    public ServerHandler(BufferedReader reader, BufferedWriter writer, StoreDataBase dataBase) {
        this.reader = reader;
        this.writer = writer;
        this.dataBase = dataBase;
    }

    public void setTask(int taskIndex) throws IOException, SQLException {
        switch (taskIndex) {
            case 0: {
                try {
                    userAuthenticate();
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeySpecException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case 1: {
                userRegistration();
                break;
            }

        }
    }

    private void userRegistration() throws IOException, SQLException {

        String firstName = reader.readLine();
        String password = reader.readLine();
        String salt = reader.readLine();

        int numberOfUsers = dataBase.select("select count(*) from users").getInt(1);
        String sqlString = "INSERT INTO users (first_name, password, salt, role_id) VALUES ('"
                + firstName +"','"  + password +"', '" + salt + "'," + 1 + ")";
        dataBase.insert(sqlString);
        writer.write("add to bd\n");
    }

    private void userAuthenticate() throws IOException, SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        PasswordEncryptionService encryptionService = new PasswordEncryptionService();

        String firstName = reader.readLine();
        String password = reader.readLine();
        String sqlString = "SELECT * FROM users WHERE first_name = '" + firstName + "'";
        ResultSet resultSet = dataBase.select(sqlString);
        String passwordFromDB = resultSet.getString("password");
        String salt = resultSet.getString("salt");
        boolean flag = encryptionService.authenticate(password, Base64.decode(passwordFromDB), Base64.decode(salt));
        System.out.println(flag);
    }
}
