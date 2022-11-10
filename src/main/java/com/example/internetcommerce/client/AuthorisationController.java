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

import java.io.IOException;

import static com.example.internetcommerce.client.Client.clientSocket;

public class AuthorisationController{

    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button signIn;

    @FXML
    private Button signUp;

    @FXML
    void OnClickSignIn(ActionEvent event) throws IOException {
        clientSocket.sendInt(0);
        String login = loginField.getText();
        String password = passwordField.getText();
        clientSocket.sendString(login + "\n");
        clientSocket.sendString(password + "\n");
        String result = clientSocket.getString();
        if (result.equals("error") || result.equals("false")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Аккаунт с такой электронной почтой уже существует");
            alert.showAndWait();
        } else {
            signIn.getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/internetcommerce/home.fxml"));
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

    }

    @FXML
    void OnClickSignUp(ActionEvent event) throws IOException {
        signUp.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/internetcommerce/registration.fxml"));
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

}
