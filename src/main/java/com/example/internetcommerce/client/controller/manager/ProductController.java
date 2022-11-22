package com.example.internetcommerce.client.controller.manager;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static com.example.internetcommerce.client.Client.socket;

public class ProductController implements ControllerInterface {
    @FXML
    private Button btAddProduct;

    @FXML
    private Button btChooseImg;

    @FXML
    private ChoiceBox<?> categoryChoice;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ImageView imgView;

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;
    private ObjectInputStream inputStream = null;
    private ObjectOutputStream outputStream = null;

    @FXML
    public void chooseImg(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбрать изображение");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Картинки", "*.jpg", "*.png", "*.bmp");
        File file = fileChooser.showOpenDialog(new Stage());
        Image img = new Image(file.toURI().toString());
        imgView.setImage(img);
    }

    @FXML
    public void addNewProduct(ActionEvent actionEvent) throws IOException {
        inputStream = new ObjectInputStream(socket.getInputStream());
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        String name = nameField.getText();
        double price = 0;
        try {
            price = Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            showMessage("Ошибка", "Поле \"Цена\" может содержать только числа!");
        }
        if (price <= 0) {
            showMessage("Ошибка", "Цена должна быть больше 0");
        }
        String description = descriptionField.getText();
        String imgAddress = imgView.getImage().getUrl();
        Product product = new Product();

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
}
