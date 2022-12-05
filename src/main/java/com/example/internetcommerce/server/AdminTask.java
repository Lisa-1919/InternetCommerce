package com.example.internetcommerce.server;

import com.example.internetcommerce.models.Order;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

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
            String sqlString = "INSERT INTO users (first_name, last_name, e_mail, phone_number, password, salt, role_id, country, birthday) VALUES ('"
                    + user.getFirstName() + "','" + user.getLastName() + "','" + user.getEmail() + "','" + user.getPhoneNumber() + "','"
                    + Base64.getEncoder().encodeToString(passwordService.getEncryptedPassword(password, salt)) + "', '" + Base64.getEncoder().encodeToString(salt) + "'," + 2 + ",'" + user.getCountry() + "','" + user.getBirthday() +"')";
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

    public static void getManagersList() throws SQLException, IOException {
        ResultSet resultSet = dataBase.select("SELECT * FROM users WHERE role_id = " + 2);
        List<User> managers = new ArrayList<>();
        resultSet.beforeFirst();
        int counter = 0;
        while (resultSet.next()) {
            counter++;
            LocalDate birthday = resultSet.getDate("birthday").toLocalDate();
            managers.add(new User(resultSet.getLong("id"), resultSet.getString("first_name"), resultSet.getString("last_name"), resultSet.getString("e_mail"), resultSet.getString("phone_number"), resultSet.getString("country"), birthday));
        }
        resultSet.first();
        outputStream.writeInt(counter);
        outputStream.flush();
        for (User manager: managers) {
            outputStream.writeObject(manager);
            outputStream.flush();
        }
    }

    public static void deleteManager() throws IOException {
        long managerId = inputStream.readLong();
        dataBase.delete("DELETE FROM users WHERE id =" + managerId);
    }
}
