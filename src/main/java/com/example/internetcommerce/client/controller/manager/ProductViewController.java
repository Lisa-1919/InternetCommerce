package com.example.internetcommerce.client.controller.manager;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.Product;
import com.example.internetcommerce.models.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.converter.DoubleStringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.example.internetcommerce.client.Client.clientSocket;
import static javafx.scene.control.cell.TextFieldTableCell.forTableColumn;

public class ProductViewController implements ControllerInterface, Initializable {

    ObservableList<Product> products = FXCollections.observableArrayList();
    @FXML
    private Button btnDelete;

    @FXML
    private Button btnEdit;

    @FXML
    private TableColumn<Product, String> descriptionColumn;

    @FXML
    private TableColumn<Product, Long> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableView<Product> productTableView;

    @Override
    public void showMessage(String title, String text) {

    }

    @Override
    public void changeScene(String sceneAddress) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(new PropertyValueFactory<Product, Long>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("description"));
        getProductList();

        nameColumn.setCellFactory(forTableColumn());
        descriptionColumn.setCellFactory(forTableColumn());
        priceColumn.setCellFactory(forTableColumn(new DoubleStringConverter()));
    }

    @FXML
    void deleteProduct(ActionEvent event) throws IOException {
        clientSocket.writeObject(Task.DELETE_PRODUCT);
        Product product = productTableView.getSelectionModel().getSelectedItem();
        clientSocket.writeObject(product);
        products.remove(product);
        productTableView.refresh();
    }

    @FXML
    void editProduct(ActionEvent event) throws IOException {
        clientSocket.writeObject(Task.EDIT_PRODUCT);
        Product product = productTableView.getSelectionModel().getSelectedItem();
        clientSocket.writeObject(product);
        products.set(productTableView.getSelectionModel().getFocusedIndex(), product);
        productTableView.refresh();
    }


    private void getProductList(){
            clientSocket.writeObject(Task.GET_PRODUCTS_LIST);
            products = (ObservableList<Product>) clientSocket.readObject();
            productTableView.setItems(products);
    }

    @FXML
    public void descriptionCommit(TableColumn.CellEditEvent<Product, String> productStringCellEditEvent) {
       Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();
       selectedProduct.setDescription(productStringCellEditEvent.getNewValue());
    }

    @FXML
    public void nameEdit(TableColumn.CellEditEvent<Product, String> productStringCellEditEvent) {
        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();
        selectedProduct.setName(productStringCellEditEvent.getNewValue());
    }

    @FXML
    public void priceCommit(TableColumn.CellEditEvent<Product, Double> productDoubleCellEditEvent) {
        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();
        selectedProduct.setPrice(productDoubleCellEditEvent.getNewValue());
    }
}
