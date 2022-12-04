package com.example.internetcommerce.client.controller.admin;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.User;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static com.example.internetcommerce.client.Client.inputStream;
import static com.example.internetcommerce.client.Client.outputStream;

public class AddManagerController implements Initializable, ControllerInterface {
    @FXML
    private DatePicker birthday;

    @FXML
    private Button btAddNewManager;

    @FXML
    private ComboBox<String> country;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField phoneNumberField;

    private ObservableList<String> countries = FXCollections.observableArrayList("Беларусь", "Украина", "Польша");

    @FXML
    void addNewManager(ActionEvent event) throws IOException, ClassNotFoundException {
        Validator validator = new Validator();
        outputStream.writeInt(2);
        outputStream.flush();
        User user = new User();
        user.setFirstName(firstNameField.getText());
        user.setLastName(lastNameField.getText());
        String email = emailField.getText();
        String phoneNumber = phoneNumberField.getText();
        String userCountry = country.getValue();
        LocalDate userBirthday = birthday.getValue();
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
        user.setBirthday(userBirthday);
        user.setCountry(userCountry);
        outputStream.writeObject(user);
        outputStream.flush();
        if (inputStream.readObject().equals("error"))
            showMessage("Ошибка", "Пользователь с таким адресом электронной почты уже существует");
        else {
            btAddNewManager.getScene().getWindow().hide();
            changeScene("/com/example/internetcommerce/adminHome.fxml");
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        country.setItems(countries);
    }
}
