package com.example.internetcommerce.client;

import com.example.internetcommerce.password.PasswordEncryptionService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;

import static com.example.internetcommerce.client.Client.socket;

public class AuthorisationController{

    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button signIn;

    @FXML
    private Button signUp;

    private BufferedReader reader = null;

    private BufferedWriter writer = null;
    @FXML
    void OnClickSignIn(ActionEvent event) throws IOException {
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        System.out.println("create reader and writer");
        writer.write(0); writer.flush();
        String login = loginField.getText();
        String password = passwordField.getText();
        sendString(login);
        sendString(password);
//        writer.write(login + "\n"); writer.flush();
//        writer.write(password + "\n"); writer.flush();
        String result = reader.readLine();
        if (result.equals("error") || result.equals("false")) {
            showMessage("Ошибка", "Аккаунт с такой электронной почтой уже существует");
        } else {
            reader.close();
            writer.close();
            signIn.getScene().getWindow().hide();
            changeScene("/com/example/internetcommerce/home.fxml");
        }

    }

    @FXML
    void OnClickSignUp(ActionEvent event) throws IOException {
        signUp.getScene().getWindow().hide();
        changeScene("/com/example/internetcommerce/registration.fxml");
    }

    private void showMessage(String title, String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.showAndWait();
    }

    private void changeScene(String resourceAddress){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(resourceAddress));
        try{
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void sendString(String string){
        try {
            writer.write(string + "\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
