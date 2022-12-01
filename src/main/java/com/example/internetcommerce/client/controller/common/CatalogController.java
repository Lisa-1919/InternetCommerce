package com.example.internetcommerce.client.controller.common;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.example.internetcommerce.client.Client.*;

public class CatalogController implements Initializable, ControllerInterface {

    private ObservableList<Product> products = FXCollections.observableArrayList();

    @FXML
    private Button btnAddToBasket;
    @FXML
    private Button btnAppFilter;

    @FXML
    private Button btnSearch;

    @FXML
    private TextField fromField;

    @FXML
    private ChoiceBox<?> searchField;

    @FXML
    private TextField toField;

    @FXML
    private TextField valueSearchFiled;

    @FXML
    private TableColumn<Product, String> descriptionColumn;

    @FXML
    private TableColumn<Product, Long> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableView<Product> productTable;

    @FXML
    private Button btnRefresh;


    @FXML
    private Spinner<Integer> amountField = new Spinner<Integer>();
    @FXML
    void onRefresh(ActionEvent event) {
        productTable.refresh();
    }


    @FXML
    void appFilter(ActionEvent event) throws IOException, ClassNotFoundException {
        outputStream.writeInt(5);
        outputStream.flush();
        double[] numbers = new double[2];
        numbers[0] = Double.parseDouble(fromField.getText());
        numbers[1] = Double.parseDouble(toField.getText());
        outputStream.writeObject(numbers);
        outputStream.flush();
        int count = 0;
        int size = inputStream.readInt();
        while (count < size){
            products.add((Product) inputStream.readObject());
            count++;
        }
        productTable.refresh();
    }

    @FXML
    void onSearch(ActionEvent event) {

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

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(new PropertyValueFactory<Product, Long>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("description"));
        SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10 ,1);
        amountField.setValueFactory(spinnerValueFactory);
        getProductsList();

    }

    private void getProductsList(){
        try {
            outputStream.writeInt(4);
            outputStream.flush();
            int count = 0;
            int size = inputStream.readInt();
            while (count < size){
                products.add((Product) inputStream.readObject());
                count++;
            }
            productTable.setItems(products);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    void addToBasket(ActionEvent event) throws IOException, ClassNotFoundException {
        Product product = productTable.getSelectionModel().getSelectedItem();
        outputStream.writeInt(7);
        outputStream.flush();
        outputStream.writeLong(user.getId());
        outputStream.flush();
        outputStream.writeLong(product.getId());
        outputStream.flush();
        int amount = amountField.getValue();
        outputStream.writeInt(amount);
        outputStream.flush();
        String result = (String) inputStream.readObject();
        String message = result.equals("successful") ? "Товар добавлен в корзину" : "Такой товар уже есть в вашей корзине";
        showMessage(null, message);
    }

}
