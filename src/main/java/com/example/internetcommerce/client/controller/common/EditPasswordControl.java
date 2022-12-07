package com.example.internetcommerce.client.controller.common;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.password.PasswordService;
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

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static com.example.internetcommerce.client.Client.*;
import static com.example.internetcommerce.client.controller.common.AuthorisationController.user;

public class EditPasswordControl implements ControllerInterface {

    @FXML
    private Button btnEditPassword;

    @FXML
    private Button btnVerify;

    @FXML
    private PasswordField confirmNewPasswordField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private TextField phoneField;

    @FXML
    private Button btnBack;

    @FXML
    void editPassword(ActionEvent event) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        PasswordService passwordService = new PasswordService();
        String newPassword = newPasswordField.getText();
        String confirmNewPassword = confirmNewPasswordField.getText();
        while (!passwordService.checkPasswords(newPassword, confirmNewPassword)) {
            newPasswordField.clear();
            confirmNewPasswordField.clear();
            newPassword = newPasswordField.getText();
            confirmNewPassword = confirmNewPasswordField.getText();
            showMessage("Ошибка", "Пароли не совпадают");
        }
        outputStream.writeObject(Base64.getEncoder().encodeToString(passwordService.getEncryptedPassword(newPassword, Base64.getDecoder().decode(user.getSalt()))));
        outputStream.flush();
        btnEditPassword.getScene().getWindow().hide();
        if(user.getId() == 0) {
            changeScene("/com/example/internetcommerce/authorisation.fxml");
        }else {
            if(user.getRoleId() == 1) {
                changeScene("/com/example/internetcommerce/userHome.fxml");
            } else if (user.getRoleId() == 2){
                changeScene("/com/example/internetcommerce/managerHome.fxml");
            }
            else if(user.getRoleId() == 3){
                changeScene("/com/example/internetcommerce/adminHome.fxml");
            }
        }
    }

    @FXML
    void verify(ActionEvent event) throws IOException, ClassNotFoundException {
        outputStream.writeInt(15);
        outputStream.flush();
        user.setEmail(emailField.getText());
        user.setPhoneNumber(phoneField.getText());
        outputStream.writeObject(user);
        String result = (String) inputStream.readObject();
        if(result.equals("successful")){
            String salt = (String) inputStream.readObject();
            user.setSalt(salt);
            newPasswordField.setEditable(true);
            confirmNewPasswordField.setEditable(true);
            btnEditPassword.setDisable(false);
        }
    }

    @Override
    public void showMessage(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.showAndWait();
    }

    @Override
    public void changeScene(String sceneAddress) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(sceneAddress));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void back(ActionEvent actionEvent) {
        btnBack.getScene().getWindow().hide();
        if(user.getId() == 0) {
            changeScene("/com/example/internetcommerce/authorisation.fxml");
        }else {
            if(user.getRoleId() == 1) {
                changeScene("/com/example/internetcommerce/userHome.fxml");
            } else if (user.getRoleId() == 2){
                changeScene("/com/example/internetcommerce/managerHome.fxml");
            }
            else if(user.getRoleId() == 3){
                changeScene("/com/example/internetcommerce/adminHome.fxml");
            }
        }
    }
}
