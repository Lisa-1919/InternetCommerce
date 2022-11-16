package com.example.internetcommerce.server;

import com.example.internetcommerce.database.StoreDataBase;
import com.example.internetcommerce.password.PasswordEncryptionService;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64;

import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerHandler implements Runnable{

    private Socket socket;
    private ObjectInputStream reader;
    private ObjectOutputStream writer;
    private StoreDataBase dataBase;

    public ServerHandler(Socket socket, StoreDataBase dataBase) {
        try {
            this.socket = socket;
            this.reader = new ObjectInputStream(socket.getInputStream());
            this.writer = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.dataBase = dataBase;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int task = reader.readInt();
                if(task == 100){
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
            reader.close();
            writer.close();
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
                userRegistration();
                break;
            }

        }
    }

    private void userRegistration() throws IOException, SQLException {

        String firstName = reader.readLine();
        String lastName = reader.readLine();
        String email = reader.readLine();
        String phoneNumber = reader.readLine();
        String password = reader.readLine();
        String salt = reader.readLine();
        int numberOfUsers = dataBase.select("select count(*) from users").getInt(1);
        ResultSet resultSet = dataBase.select("SELECT * FROM users WHERE e_mail = '" + email + "'");
        resultSet.beforeFirst();
        int counter = 0;
        while(resultSet.next()){
            counter++;
        }
        resultSet.first();
        if(counter == 0){
            String sqlString = "INSERT INTO users (first_name, last_name, e_mail, phone_number, password, salt, role_id) VALUES ('"
                    + firstName +"','" + lastName + "','" + email +"','" + phoneNumber +"','"  + password +"', '" + salt + "'," + 1 + ")";
            dataBase.insert(sqlString);
            writer.writeUTF("add to bd");
            writer.flush();
        } else {
            writer.writeUTF("error"); writer.flush();
        }

    }

    private void userAuthenticate() throws IOException, SQLException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException {
        PasswordEncryptionService encryptionService = new PasswordEncryptionService();

        String email = (String) reader.readObject();
        String password = (String) reader.readObject();
        String sqlString = "SELECT * FROM users WHERE e_mail = '" + email + "'";
        ResultSet resultSet = dataBase.select(sqlString);
        resultSet.beforeFirst();
        int counter = 0;
        while(resultSet.next()){
            counter++;
        }
        resultSet.first();
        if(counter == 0){
            writer.writeObject("error");
            writer.flush();
        } else{
            String passwordFromDB = resultSet.getString("password");
            String salt = resultSet.getString("salt");
            boolean flag = encryptionService.authenticate(password, Base64.decode(passwordFromDB), Base64.decode(salt));
            if(flag){
                writer.writeObject("true");
                writer.flush();
            }else{
                writer.writeObject("false");
                writer.flush();
            }
            System.out.println(flag);
        }

    }


}
