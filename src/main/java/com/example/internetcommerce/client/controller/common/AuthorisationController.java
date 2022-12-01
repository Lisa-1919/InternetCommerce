package com.example.internetcommerce.client.controller.common;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;

import static com.example.internetcommerce.client.Client.*;

public class AuthorisationController implements ControllerInterface {

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

    @FXML
    void OnClickSignIn(ActionEvent event) throws IOException, ClassNotFoundException {
        outputStream.writeInt(0);
        outputStream.flush();
        user.setEmail(loginField.getText());
        user.setPassword(passwordField.getText());
        outputStream.writeObject(user);
        outputStream.flush();
        String result = (String) inputStream.readObject();

        if (result.equals("error") || result.equals("false")) {
            showMessage("Ошибка", "Аккаунт с такой электронной почтой не существует");
        } else {
            user = (User) inputStream.readObject();
            signIn.getScene().getWindow().hide();
            if(user.getRoleId() == 1) {
                changeScene("/com/example/internetcommerce/userHome.fxml");
            }
            if (user.getRoleId() == 2) {
                changeScene("/com/example/internetcommerce/managerMenu.fxml");
            }

            if(user.getRoleId() == 3){
                changeScene("/com/example/internetcommerce/homeAdmin.fxml");
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
}
