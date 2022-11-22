package com.example.internetcommerce.client.controller.admin;


import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.User;
import com.example.internetcommerce.validation.Validator;
import com.example.internetcommerce.validation.ValidatorType;
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
import java.security.NoSuchAlgorithmException;

import static com.example.internetcommerce.client.Client.inputStream;
import static com.example.internetcommerce.client.Client.outputStream;

public class HomeAdminController implements ControllerInterface {

    @FXML
    private Button btnAddNewManager;

    @FXML
    private Button btAddNewManager;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    void openFormToAddNewManager(ActionEvent event) throws IOException {
        btnAddNewManager.getScene().getWindow().hide();
        changeScene("/com/example/internetcommerce/addNewManager.fxml");
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
    public void addNewManager(ActionEvent actionEvent) throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        Validator validator = new Validator();
        outputStream.writeInt(2);
        outputStream.flush();
        User user = new User();
        user.setFirstName(firstNameField.getText());
        user.setLastName(lastNameField.getText());

        String email = emailField.getText();
        String phoneNumber = phoneNumberField.getText();
        if (!validator.validate(email, ValidatorType.EMAIL)) {
            showMessage("Ошибка", "Неверный адрес электронной почты");
            emailField.clear();
            email = emailField.getText();
        }
        if (!validator.validate(phoneNumber, ValidatorType.PHONE_NUMBER)) {
            showMessage("Ошибка", "Неверный номер телефона");
            phoneNumberField.clear();
            phoneNumber = phoneNumberField.getText();
        }
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        outputStream.writeObject(user);
        outputStream.flush();
        if (inputStream.readObject().equals("error"))
            showMessage("Ошибка", "Пользователь с таким адресом электронной почты уже существует");
        else {
            btAddNewManager.getScene().getWindow().hide();
            changeScene("/com/example/internetcommerce/homeAdmin.fxml");
        }
    }
}
