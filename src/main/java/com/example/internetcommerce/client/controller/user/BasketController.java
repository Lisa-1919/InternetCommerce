package com.example.internetcommerce.client.controller.user;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.example.internetcommerce.client.Client.*;

public class BasketController implements ControllerInterface, Initializable {

    @FXML
    private TableView<Product> basketList;
    @FXML
    private TableColumn<Product, String> name;
    @FXML
    private TableColumn<Product, Double> price;
    @FXML
    private TableColumn<Product, Integer> amount;

    private ObservableList<Product> basketProductList = FXCollections.observableArrayList();


    @Override
    public void showMessage(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.showAndWait();
    }

    @Override
    public void changeScene(String sceneAddress) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        name.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        amount.setCellValueFactory(new PropertyValueFactory<Product, Integer>("amount"));
        price.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));

        getBasketListProduct();
    }

    @FXML
    private void getBasketListProduct() {
        try {
            outputStream.writeInt(6);
            outputStream.flush();
            outputStream.writeLong(user.getId());
            outputStream.flush();
            int count = 0;
            int size = inputStream.readInt();
            while (count < size) {
                basketProductList.add((Product) inputStream.readObject());
                count++;
            }
            basketList.setItems(basketProductList);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void deleteProduct(Event event) throws IOException, ClassNotFoundException {
        outputStream.writeInt(8);
        outputStream.flush();
        outputStream.writeLong(user.getId());
        outputStream.flush();
        Product product = basketList.getSelectionModel().getSelectedItem();
        outputStream.writeLong(product.getId());
        outputStream.flush();
        basketProductList.remove(basketList.getSelectionModel().getSelectedItem());
        basketList.refresh();
    }
}