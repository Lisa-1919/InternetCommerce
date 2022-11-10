package com.example.internetcommerce.client;

import com.example.internetcommerce.password.PasswordEncryptionService;
import com.example.internetcommerce.validation.EmailValidator;
import com.example.internetcommerce.validation.PhoneNumberValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.Base64;

import static com.example.internetcommerce.client.Client.clientSocket;

public class RegistrationController {

    @FXML
    private TextField NameField;

    @FXML
    private DatePicker birthday;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ChoiceBox<?> country;

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

    @FXML
    void selectBirthday(ActionEvent event) {

    }

    @FXML
    void signUp(MouseEvent event) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        PasswordEncryptionService encryptionService = new PasswordEncryptionService();
        EmailValidator emailValidator = new EmailValidator();
        PhoneNumberValidator phoneNumberValidator = new PhoneNumberValidator();
        clientSocket.sendInt(1);
        String firstName = NameField.getText();
        String lastName = lastNameField.getText();
        String e_mail = email.getText();
        String phoneNumber = phone.getText();
        while (!emailValidator.validateEmail(e_mail)) {
            System.out.println("Неверный адрес электронной почты");
            email.clear();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Неверный адрес электронной почты");
            alert.showAndWait();
        }
        while (!phoneNumberValidator.validatePhone(phoneNumber)) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ошибка");
            alert.setHeaderText("неверный номер телефона");
            alert.showAndWait();
            phone.clear();
            phoneNumber = phone.getText();
        }

        LocalDate userBirthday = birthday.getValue();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        while (!checkPasswords(password, confirmPassword)) {
            passwordField.clear();
            confirmPasswordField.clear();
            password = passwordField.getText();
            confirmPassword = confirmPasswordField.getText();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Пароли не совпадают");
            alert.showAndWait();
        }
        clientSocket.sendString(firstName + "\n");
        clientSocket.sendString(lastName + "\n");
        clientSocket.sendString(e_mail + "\n");
        clientSocket.sendString(phoneNumber + "\n");
        //clientSocket.sendString(birthday.toString() + "\n");
        byte[] salt = encryptionService.generateSalt();
        byte[] encryptionPassword = encryptionService.getEncryptedPassword(password, salt);
        clientSocket.sendString(Base64.getEncoder().encodeToString(encryptionPassword) + "\n");
        clientSocket.sendString(Base64.getEncoder().encodeToString(salt) + "\n");
        String result = clientSocket.getString();
        if (result.equals("error")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Аккаунт с такой электронной почтой уже существует");
            alert.showAndWait();
            email.clear();
            e_mail = email.getText();
        } else {
            sgnIn.getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/internetcommerce/authorisation.fxml"));
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
    }

    public boolean checkPasswords(String password, String confirmPassword){
        if(password.equals(confirmPassword)){
            return true;
        }
        else{
            return false;
        }
    }
}
