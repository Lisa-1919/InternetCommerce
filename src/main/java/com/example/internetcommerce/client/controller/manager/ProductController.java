package com.example.internetcommerce.client.controller.manager;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.example.internetcommerce.client.Client.*;

public class ProductController implements ControllerInterface, Initializable {
    @FXML
    private Button btAddProduct;

    @FXML
    private Button btChooseImg;

    @FXML
    private ChoiceBox<String> categoryChoice;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ImageView imgView;

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    private ObservableList<String> categories = FXCollections.observableArrayList("Одежда", "Для дома", "Книги");

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
    public void addNewProduct(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        outputStream.writeInt(3);
        outputStream.flush();
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
        String category = categoryChoice.getValue();
        Product product = new Product(name, price, description, 0, imgView.getImage().getUrl(), category);
        outputStream.writeObject(product);
        outputStream.flush();
        String result = (String) inputStream.readObject();
        if(result.equals("error")){
            showMessage("Что-то пошло не так...", "Ошибка при добавлении товара.\nПопробуйте ёще раз");
        }else{
            btAddProduct.getScene().getWindow().hide();
            changeScene("/com/example/internetcommerce/managerHome.fxml");
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

    public void deleteProduct(){

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryChoice.setItems(categories);
    }
}
