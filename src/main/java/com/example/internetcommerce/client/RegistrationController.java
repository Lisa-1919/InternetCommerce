package com.example.internetcommerce.client;

import com.example.internetcommerce.password.PasswordEncryptionService;
import com.example.internetcommerce.validation.EmailValidator;
import com.example.internetcommerce.validation.PhoneNumberValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

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
        if(!emailValidator.validateEmail(e_mail)){
            System.out.println("Неверный адрес электронной почты");
        }
        if(!phoneNumberValidator.validatePhone(phoneNumber)){
            System.out.println("неверный номер телефона");
        }

        LocalDate userBirthday = birthday.getValue();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        while(!checkPasswords(password, confirmPassword)){
            passwordField.clear();
            confirmPasswordField.clear();
            password = passwordField.getText();
            confirmPassword = confirmPasswordField.getText();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Пароли не совпадают");
            alert.showAndWait();
        }
        clientSocket.sendString(firstName+"\n");
        clientSocket.sendString(lastName + "\n");
        clientSocket.sendString(e_mail + "\n");
        clientSocket.sendString(phoneNumber + "\n");
        //clientSocket.sendString(birthday.toString() + "\n");
        byte[] salt = encryptionService.generateSalt();
        byte[] encryptionPassword = encryptionService.getEncryptedPassword(password, salt);
        clientSocket.sendString(Base64.getEncoder().encodeToString(encryptionPassword) +"\n");
        clientSocket.sendString(Base64.getEncoder().encodeToString(salt) + "\n");
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
