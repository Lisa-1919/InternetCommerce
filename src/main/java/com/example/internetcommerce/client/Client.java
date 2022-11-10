package com.example.internetcommerce.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Application {

    private Socket socket;
    protected static ClientSocket clientSocket;

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/internetcommerce/authorisation.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();

        socket = new Socket(InetAddress.getLocalHost(), 1024);
        clientSocket = new ClientSocket(new BufferedReader(new InputStreamReader(socket.getInputStream())),
                new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
    }

    public static void main(String[] args) {
        launch();
    }

}