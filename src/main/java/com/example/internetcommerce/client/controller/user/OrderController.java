package com.example.internetcommerce.client.controller.user;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.Product;
import com.example.internetcommerce.models.ProductInOrder;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static com.example.internetcommerce.client.Client.clientSocket;
import static com.example.internetcommerce.client.controller.user.UserController.order;

public class OrderController implements ControllerInterface, Initializable {

    @FXML
    private TableColumn<Product, Integer> amountColumn;

    @FXML
    private Button btnBack;

    @FXML
    private Label createDate;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private Label orderCost;

    @FXML
    private Label paymentMethod;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableView<Product> productsInOrderTable;

    @FXML
    private Label receiptDate;

    @FXML
    private Label shippingAddress;

    @FXML
    private Label shippingMethod;
    private ObservableList<Product> productInOrders = FXCollections.observableArrayList();

    @FXML
    void back(ActionEvent event) {
        btnBack.getScene().getWindow().hide();
        changeScene("/com/example/internetcommerce/userHome.fxml");
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
        createDate.setText(order.getCreationDate().toString());
        if (order.getReceiptionDate() == null) {
            receiptDate.setText("Заказ еще не получен");
        } else {
            receiptDate.setText(order.getReceiptionDate().toString());
        }
        orderCost.setText(order.getOrderPrice() + " руб.");
        paymentMethod.setText(order.getPayment());
        shippingMethod.setText(order.getShipping());
        shippingAddress.setText(order.getAddress());
        nameColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("amount"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
        getProductsInOrderList();
    }

    private void getProductsInOrderList(){
        clientSocket.writeObject(Task.GET_ORDER_DETAILS_LIST);
        clientSocket.writeObject(order);
        ArrayList<Product> products = (ArrayList<Product>) clientSocket.readObject();
        productInOrders.setAll(products);
        productsInOrderTable.setItems(productInOrders);
    }
}
