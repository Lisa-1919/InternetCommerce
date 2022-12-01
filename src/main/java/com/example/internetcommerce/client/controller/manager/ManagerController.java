package com.example.internetcommerce.client.controller.manager;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
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

public class ManagerController implements ControllerInterface {

    private ObjectInputStream inputStream = null;
    private ObjectOutputStream outputStream = null;

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

    @FXML
    private Button btOpenListProduct;
    @FXML
    private Button btOpenFormToAddNewProduct;

    @FXML
    void openFormAddNewProduct(ActionEvent event) {
        btOpenFormToAddNewProduct.getScene().getWindow().hide();
        changeScene("/com/example/internetcommerce/addNewProduct.fxml");
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

    @FXML
    void openListProduct(ActionEvent event) {
        btOpenListProduct.getScene().getWindow().hide();
        changeScene("/com/example/internetcommerce/productCatalogManager.fxml");
    }


}
