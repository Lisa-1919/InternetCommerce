package com.example.internetcommerce.server;

import com.example.internetcommerce.database.StoreDataBase;
import com.example.internetcommerce.models.Message;
import com.example.internetcommerce.models.User;
import com.example.internetcommerce.password.PasswordService;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AdminTask {
    protected static void managerRegistration(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws SQLException, IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
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
            Writer writer = new FileWriter("D:/Курсовая (5 семестр)/InternetCommerce/src/main/resources/"+user.getFirstName() + "_" + user.getLastName() +".txt", true);
            writer.write(user.getEmail() + " " + password + "\n");
            writer.flush();
            writer.close();
            outputStream.writeObject(Message.SUCCESSFUL);
        } else {
            outputStream.writeObject(Message.ERROR);
            outputStream.flush();
        }
    }

    public static void getManagersList(ObjectInputStream inputStream, ObjectOutputStream outputStream,StoreDataBase dataBase) throws SQLException, IOException {
        ResultSet resultSet = dataBase.select("SELECT * FROM users WHERE role_id = " + 2);
        List<User> managers = new ArrayList<>();
        resultSet.beforeFirst();
        while (resultSet.next()) {
            LocalDate birthday = resultSet.getDate("birthday").toLocalDate();
            managers.add(new User(resultSet.getLong("id"), resultSet.getString("first_name"), resultSet.getString("last_name"), resultSet.getString("e_mail"), resultSet.getString("phone_number"), resultSet.getString("country"), birthday));
        }
        resultSet.first();
        outputStream.writeObject(managers);
        outputStream.flush();
    }

    public static void deleteManager(ObjectInputStream inputStream, ObjectOutputStream outputStream,StoreDataBase dataBase) throws IOException, ClassNotFoundException {
        User manager = (User) inputStream.readObject();
        dataBase.delete("DELETE FROM users WHERE id =" + manager.getId());
    }
}
