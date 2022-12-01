package com.example.internetcommerce.client.controller.user;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.User;
import com.example.internetcommerce.password.PasswordService;
import com.example.internetcommerce.validation.Validator;
import com.example.internetcommerce.validation.ValidatorType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.ResourceBundle;

import static com.example.internetcommerce.client.Client.*;

public class RegistrationController implements Initializable, ControllerInterface {

    @FXML
    private TextField NameField;

    @FXML
    private DatePicker birthday;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ComboBox<String> country;

    @FXML
    private TextField email;

    @FXML
    private TextField lastNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField phone;

    @FXML
    private Button sgnIn;

    private ObservableList<String> countries = FXCollections.observableArrayList("Беларусь", "Украина", "Польша");

    @FXML
    void selectBirthday(ActionEvent event) {

    }

    @FXML
    void signUp(MouseEvent event) throws IOException, InvalidKeySpecException, ClassNotFoundException, NoSuchAlgorithmException {
        PasswordService encryptionService = new PasswordService();
        Validator validator = new Validator();
        outputStream.writeInt(1);
        outputStream.flush();
        String firstName = NameField.getText();
        String lastName = lastNameField.getText();
        String e_mail = email.getText();
        String phoneNumber = phone.getText();
        if (!validator.validate(e_mail, ValidatorType.EMAIL)) {
            System.out.println("Неверный адрес электронной почты");
            email.clear();
            e_mail = email.getText();
            showMessage("Ошибка", "Неверный адрес электронной почты");

        }
        if (!validator.validate(phoneNumber, ValidatorType.PHONE_NUMBER)) {
            phone.clear();
            phoneNumber = phone.getText();
            showMessage("Ошибка", "Неверный номер телефона");
        }
        String userCountry = country.getValue();
        LocalDate userBirthday = birthday.getValue();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        while (!encryptionService.checkPasswords(password, confirmPassword)) {
            passwordField.clear();
            confirmPasswordField.clear();
            password = passwordField.getText();
            confirmPassword = confirmPasswordField.getText();
            showMessage("Ошибка", "Пароли не совпадают");
        }
        byte[] salt = encryptionService.generateSalt();
        byte[] encryptionPassword = encryptionService.getEncryptedPassword(password, salt);
        User user = new User(firstName, lastName, e_mail, phoneNumber, userCountry, userBirthday, Base64.getEncoder().encodeToString(encryptionPassword), Base64.getEncoder().encodeToString(salt));
        outputStream.writeObject(user);
        outputStream.flush();
        String result = (String) inputStream.readObject();
        if (result.equals("error")) {
            showMessage("Ошибка", "Аккаунт с такой электронной почтой или номером телефона уже существует");
            email.clear();
        } else {
            sgnIn.getScene().getWindow().hide();
            changeScene("/com/example/internetcommerce/authorisation.fxml");
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
    public void changeScene(String resourceAddress) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(resourceAddress));
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        country.setItems(countries);
    }

}
