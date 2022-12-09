package com.example.internetcommerce.client.controller.common;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.Message;
import com.example.internetcommerce.models.Task;
import com.example.internetcommerce.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import static com.example.internetcommerce.client.Client.*;

public class AuthorisationController implements ControllerInterface, Initializable {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signIn;

    @FXML
    private Button signUp;

    @FXML
    private Button btnRestorePassword;

    public static User user;

    @FXML
    void OnClickSignIn(ActionEvent event) throws IOException, ClassNotFoundException {
        clientSocket.writeObject(Task.AUTHORISATION);
        User userAuthorise = new User();
        userAuthorise.setEmail(loginField.getText());
        userAuthorise.setPassword(passwordField.getText());

        clientSocket.writeObject(userAuthorise);
        if (clientSocket.readObject().equals(Message.ERROR)) {
            showMessage("Ошибка", "Введен неверный логин или пароль");
            passwordField.clear();
        } else {
            user = (User) clientSocket.readObject();
            signIn.getScene().getWindow().hide();
            if(user.getRoleId() == 1) {
                changeScene("/com/example/internetcommerce/userHome.fxml");
            }
            if (user.getRoleId() == 2) {
                changeScene("/com/example/internetcommerce/managerHome.fxml");
            }
            if(user.getRoleId() == 3){
                changeScene("/com/example/internetcommerce/adminHome.fxml");
            }
        }

    }

    @FXML
    void OnClickSignUp(ActionEvent event) throws IOException {
        signUp.getScene().getWindow().hide();
        changeScene("/com/example/internetcommerce/registration.fxml");
    }

    @Override
    public void changeScene(String resourceAddress){
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

    @Override
    public void showMessage(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.showAndWait();
    }


    @FXML
    public void restorePassword(ActionEvent actionEvent) {
        btnRestorePassword.getScene().getWindow().hide();
        changeScene("/com/example/internetcommerce/editPasswordPage.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        user = new User();
    }
}
