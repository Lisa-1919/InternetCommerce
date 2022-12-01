package com.example.internetcommerce.client;

import com.example.internetcommerce.models.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Application {

    public static Socket socket;
    public static ObjectInputStream inputStream;
    public static ObjectOutputStream outputStream;
    public static User user;
    @Override
    public void start(Stage stage) throws IOException {
        user = new User();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/internetcommerce/authorisation.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();

        socket = new Socket(InetAddress.getLocalHost(), 1024);
        inputStream = new ObjectInputStream(socket.getInputStream());
        outputStream = new ObjectOutputStream(socket.getOutputStream());
    }

    public static void main(String[] args) {
        launch();
    }

}