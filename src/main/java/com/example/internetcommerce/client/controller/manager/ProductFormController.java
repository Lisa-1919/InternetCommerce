package com.example.internetcommerce.client.controller.manager;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.Product;
import com.example.internetcommerce.models.Task;
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
import java.util.Optional;
import java.util.ResourceBundle;

import static com.example.internetcommerce.client.Client.clientSocket;
import static com.example.internetcommerce.client.controller.manager.ManagerHomeController.product;

public class ProductFormController implements Initializable, ControllerInterface {

    @FXML
    private Button btnBack;
    @FXML
    private Button btnDeleteProduct;

    @FXML
    private Button btnEditImg;

    @FXML
    private Button btnSave;

    @FXML
    private ComboBox<String> categoryBox;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ImageView imageField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    private ObservableList<String> categories = FXCollections.observableArrayList("Не выбрано", "Одежда", "Для дома");

    @FXML
    void deleteProduct(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Вы действительно хотите удалить данный товар?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) {
            clientSocket.writeObject(Task.DELETE_PRODUCT);
            clientSocket.writeObject(product);
            btnDeleteProduct.getScene().getWindow().hide();
            changeScene("/com/example/internetcommerce/managerHome.fxml");
        }
    }

    @FXML
    void editImg(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбрать изображение");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Картинки", "*.jpg", "*.png", "*.bmp");
        File file = fileChooser.showOpenDialog(new Stage());
        Image img = new Image(file.toURI().toString());
        imageField.setImage(img);
    }

    @FXML
    void save(ActionEvent event) {
        clientSocket.writeObject(Task.EDIT_PRODUCT);
        clientSocket.writeObject(product);
        btnSave.getScene().getWindow().hide();
        changeScene("/com/example/internetcommerce/managerHome.fxml");
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
        categoryBox.setItems(categories);
        priceField.setText(String.valueOf(product.getPrice()));
        descriptionField.setText(product.getDescription());
        imageField.setImage(new Image(product.getImageName()));
    }

    @FXML
    public void back(ActionEvent actionEvent) {
        btnBack.getScene().getWindow().hide();
        changeScene("/com/example/internetcommerce/managerHome.fxml");
    }
}
