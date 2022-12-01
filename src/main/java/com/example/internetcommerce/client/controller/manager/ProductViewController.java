package com.example.internetcommerce.client.controller.manager;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.Product;
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

import static com.example.internetcommerce.client.Client.inputStream;
import static com.example.internetcommerce.client.Client.outputStream;
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
        outputStream.writeInt(9);
        outputStream.flush();
        Product product = productTableView.getSelectionModel().getSelectedItem();
        outputStream.writeLong(product.getId());
        outputStream.flush();
        products.remove(product);
        productTableView.refresh();
    }

    @FXML
    void editProduct(ActionEvent event) throws IOException {
        outputStream.writeInt(10);
        outputStream.flush();
        Product product = productTableView.getSelectionModel().getSelectedItem();
        outputStream.writeObject(product);
        products.set(productTableView.getSelectionModel().getFocusedIndex(), product);
        productTableView.refresh();
    }


    private void getProductList(){
        try{
            outputStream.writeInt(4);
            outputStream.flush();
            int count = 0;
            int size = inputStream.readInt();
            while (count < size){
                products.add((Product) inputStream.readObject());
                count++;
            }
            productTableView.setItems(products);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
