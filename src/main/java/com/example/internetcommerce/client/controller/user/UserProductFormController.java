package com.example.internetcommerce.client.controller.user;

import com.example.internetcommerce.client.controller.ControllerInterface;
import javafx.event.ActionEvent;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.example.internetcommerce.client.Client.*;
import static com.example.internetcommerce.client.controller.common.AuthorisationController.user;
import static com.example.internetcommerce.client.controller.user.UserController.product;

public class UserProductFormController implements Initializable, ControllerInterface {

    @FXML
    private Spinner<Integer> amountSpinner;
    @FXML
    private Button btnAddToBaket;
    @FXML
    private Button btnBack;

    @FXML
    private Label categoryBox;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ImageView imageField;

    @FXML
    private Label nameField;

    @FXML
    private Label priceField;

    @FXML
    void back(ActionEvent event) {
        btnBack.getScene().getWindow().hide();
        changeScene("/com/example/internetcommerce/userHome.fxml");
    }

    @FXML
    public void addToBasket(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        outputStream.writeInt(7);
        outputStream.flush();
        outputStream.writeLong(user.getId());
        outputStream.flush();
        outputStream.writeLong(product.getId());
        outputStream.flush();
        outputStream.writeInt(1);
        outputStream.flush();
        String result = (String) inputStream.readObject();
        String message = result.equals("successful") ? "Товар добавлен в корзину" : "Такой товар уже есть в вашей корзине";
        showMessage(null, message);
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
        nameField.setText(product.getName());
        priceField.setText(product.getPrice() + " руб.");
        descriptionField.setText(product.getDescription());
        imageField.setImage(new Image(product.getImageName()));
        SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        amountSpinner.setValueFactory(spinnerValueFactory);
    }
}
