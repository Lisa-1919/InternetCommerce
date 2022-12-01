package com.example.internetcommerce.server;

import com.example.internetcommerce.models.User;
import com.example.internetcommerce.password.PasswordService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

import static com.example.internetcommerce.server.ServerHandler.*;

public class AdminTask {
    protected static void managerRegistration() throws SQLException, IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
        System.out.println("try to add new manager");
        User user = (User) inputStream.readObject();
        ResultSet resultSet = dataBase.select("SELECT * FROM users WHERE e_mail = '" + user.getEmail() + "'");
        resultSet.beforeFirst();
        int counter = 0;
        while (resultSet.next()) {
            counter++;
        }
        resultSet.first();
        if (counter == 0) {
            PasswordService passwordService = new PasswordService();
            String password = passwordService.generatePassword();
            byte[] salt = passwordService.generateSalt();
            String sqlString = "INSERT INTO users (first_name, last_name, e_mail, phone_number, password, salt, role_id) VALUES ('"
                    + user.getFirstName() + "','" + user.getLastName() + "','" + user.getEmail() + "','" + user.getPhoneNumber() + "','"
                    + passwordService.getEncryptedPassword(password, salt) + "', '" + Base64.getEncoder().encodeToString(salt) + "'," + 2 + ")";
            dataBase.insert(sqlString);
            outputStream.flush();
            Writer writer = new FileWriter("D:/Курсовая (5 семестр)/InternetCommerce/src/main/resources/"+user.getFirstName() + "_" + user.getLastName() +".txt", true);
            writer.write(user.getEmail() + " " + password + "\n");
            writer.flush();
            writer.close();
        } else {
            outputStream.writeObject("error");
            outputStream.flush();
        }
    }


}
