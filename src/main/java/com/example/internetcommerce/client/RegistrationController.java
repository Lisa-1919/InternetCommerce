package com.example.internetcommerce.client;

import com.example.internetcommerce.password.PasswordEncryptionService;
import com.example.internetcommerce.validation.Validator;
import com.example.internetcommerce.validation.ValidatorType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.Base64;

import static com.example.internetcommerce.client.Client.socket;

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

    private BufferedReader reader = null;
    private BufferedWriter writer= null;

    @FXML
    void selectBirthday(ActionEvent event) {

    }

    @FXML
    void signUp(MouseEvent event) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        PasswordEncryptionService encryptionService = new PasswordEncryptionService();
        Validator validator = new Validator();
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(1); writer.flush();
        String firstName = NameField.getText();
        String lastName = lastNameField.getText();
        String e_mail = email.getText();
        String phoneNumber = phone.getText();
        if (!validator.validate(e_mail, ValidatorType.EMAIL)) {
            System.out.println("Неверный адрес электронной почты");
            email.clear();
            showMessage("Ошибка", "Неверный адрес электронной почты");

        }
        if (!validator.validate(phoneNumber, ValidatorType.PHONE_NUMBER)) {
            phone.clear();
            phoneNumber = phone.getText();
            showMessage("Ошибка", "Неверный номер телефона");
        }

        LocalDate userBirthday = birthday.getValue();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        while (!checkPasswords(password, confirmPassword)) {
            passwordField.clear();
            confirmPasswordField.clear();
            password = passwordField.getText();
            confirmPassword = confirmPasswordField.getText();
            showMessage("Ошибка", "Пароли не совпадают");
        }
        sendString(firstName);
        sendString(lastName);
        sendString(e_mail);
        sendString(phoneNumber);
        byte[] salt = encryptionService.generateSalt();
        byte[] encryptionPassword = encryptionService.getEncryptedPassword(password, salt);
        sendString(Base64.getEncoder().encodeToString(encryptionPassword));
        sendString(Base64.getEncoder().encodeToString(salt));
        String result = reader.readLine();
        if (result.equals("error")) {
            showMessage("Ошибка", "Аккаунт с такой электронной почтой уже существует");
            email.clear();
            e_mail = email.getText();
        } else {
            reader.close();
            writer.close();
            sgnIn.getScene().getWindow().hide();
            changeScene("/com/example/internetcommerce/home.fxml");
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
