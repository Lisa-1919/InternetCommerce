package com.example.internetcommerce.client.controller.manager;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.CustomList;
import com.example.internetcommerce.models.Product;
import com.example.internetcommerce.models.ProductInOrder;
import com.example.internetcommerce.models.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import static com.example.internetcommerce.client.Client.*;
import static com.example.internetcommerce.client.controller.common.AuthorisationController.user;
import static javafx.scene.control.cell.TextFieldTableCell.forTableColumn;

public class ManagerHomeController implements ControllerInterface, Initializable {
    @FXML
    private Tab accountTab;

    @FXML
    private Button btnDeleteProduct;

    @FXML
    private Button btnEditProduct;

    @FXML
    private Button btnEditPassword;

    @FXML
    private Button btnExit;

    @FXML
    private Button btnOpenFormToAddProduct;

    @FXML
    private ComboBox<String> categoryBox;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private TableColumn<Product, String> descriptionColumn;

    @FXML
    private Tab graphicsTab;

    @FXML
    private TableColumn<Product, Long> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private Tab productsTab;

    @FXML
    private TableView<Product> productsTable;

    @FXML
    private TextField searchField;

    @FXML
    private Label userBirthday;

    @FXML
    private Label userEmail;

    @FXML
    private Label userFirstName;

    @FXML
    private Label userLastName;

    @FXML
    private Button btnOpenFormToBuildGraph;

    @FXML
    private Button btnOpenFormToCreateReport;

    @FXML
    private TableColumn<ProductInOrder, Double> orderCostColumn;
    @FXML
    private TableColumn<ProductInOrder, Date> receiptDate;
    @FXML
    private TableColumn<ProductInOrder, Date> createDate;
    @FXML
    private TableColumn<ProductInOrder, String> saleCategoryColumn;

    @FXML
    private TableColumn<ProductInOrder, Long> saleIdColumn;

    @FXML
    private TableView<ProductInOrder> salesTable;

    @FXML
    private TableColumn<ProductInOrder, String> productNameColumn;

    @FXML
    private TableColumn<ProductInOrder, Integer> amountColumn;

    protected static Product product;
    private ObservableList<Product> productList = FXCollections.observableArrayList();
    protected static ObservableList<String> categories = FXCollections.observableArrayList("Не выбрано", "Одежда", "Для дома");
    private ObservableList<ProductInOrder> salesList = FXCollections.observableArrayList();
    @FXML
    void deleteProduct(ActionEvent event) throws IOException {
        clientSocket.writeObject(Task.DELETE_PRODUCT);
        Product product = productsTable.getSelectionModel().getSelectedItem();
        clientSocket.writeObject(product);
        productList.remove(product);
        productsTable.refresh();
    }

    @FXML
    void editProduct(ActionEvent event) throws IOException {
        clientSocket.writeObject(Task.EDIT_PRODUCT);
        Product product = productsTable.getSelectionModel().getSelectedItem();
        clientSocket.writeObject(product);
        productList.set(productsTable.getSelectionModel().getFocusedIndex(), product);
        productsTable.refresh();
    }

    @FXML
    void editProductDescription(TableColumn.CellEditEvent<Product, String> event) {
        Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        selectedProduct.setDescription(event.getNewValue());
    }

    @FXML
    void editProductName(TableColumn.CellEditEvent<Product, String> event) {
        Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        selectedProduct.setName(event.getNewValue());
    }

    @FXML
    void editProductPrice(TableColumn.CellEditEvent<Product, Double> event) {
        Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        selectedProduct.setPrice(event.getNewValue());
    }

    @FXML
    void editPassword(ActionEvent event) {
        btnEditPassword.getScene().getWindow().hide();
        changeScene("/com/example/internetcommerce/editPasswordPage.fxml");
    }

    @FXML
    void exit(ActionEvent event) {
        btnExit.getScene().getWindow().hide();
        changeScene("/com/example/internetcommerce/authorisation.fxml");
    }

    @FXML
    void openFormToAddProduct(ActionEvent event) {
        btnOpenFormToAddProduct.getScene().getWindow().hide();
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
        userFirstName.setText(user.getFirstName());
        userLastName.setText(user.getLastName());
        userEmail.setText(user.getEmail());
        if (user.getBirthday() == null) {
            userBirthday.setText("День рождения не выбран");
        } else {
            userBirthday.setText(user.getBirthday().getDayOfMonth() + "." + user.getBirthday().getMonthValue() + "." + user.getBirthday().getYear());
        }

        idColumn.setCellValueFactory(new PropertyValueFactory<Product, Long>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("description"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("category"));
        getProductList();
        categoryBox.setItems(categories);
        productsTable.setRowFactory( tv -> {
            TableRow<Product> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    product = row.getItem();
                    row.getScene().getWindow().hide();
                    changeScene("/com/example/internetcommerce/managerProductForm.fxml");
                }
            });
            return row;
        });

        nameColumn.setCellFactory(forTableColumn());
        descriptionColumn.setCellFactory(forTableColumn());
        priceColumn.setCellFactory(forTableColumn(new DoubleStringConverter()));

        FilteredList<Product> productFilteredList = new FilteredList<>(productList, b->true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            productFilteredList.setPredicate(product -> {
                if(newValue.isEmpty() || newValue.isBlank() || newValue == null){
                    return true;
                }
                String searchValue = newValue.toLowerCase();

                if(product.getName().toLowerCase().indexOf(searchValue) > -1){
                    return true;
                } else if(product.getCategory().toLowerCase().indexOf(searchValue) > -1){
                    return true;
                }else if(product.getDescription().toLowerCase().indexOf(searchValue) > -1){
                    return true;
                } else
                    return false;
            });
        });

        SortedList<Product> productSortedList = new SortedList<>(productFilteredList);
        productSortedList.comparatorProperty().bind(productsTable.comparatorProperty());
        productsTable.setItems(productSortedList);

        saleIdColumn.setCellValueFactory(new PropertyValueFactory<ProductInOrder, Long>("id"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<ProductInOrder, String>("name"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<ProductInOrder, Integer>("amount"));
        saleCategoryColumn.setCellValueFactory(new PropertyValueFactory<ProductInOrder, String>("category"));
        createDate.setCellValueFactory(new PropertyValueFactory<ProductInOrder, Date>("createOrderDate"));
        receiptDate.setCellValueFactory(new PropertyValueFactory<ProductInOrder, Date>("receiptOrderDate"));
        orderCostColumn.setCellValueFactory(new PropertyValueFactory<ProductInOrder, Double>("orderCost"));
        getSalesList();
    }

    private void getProductList(){
            clientSocket.writeObject(Task.GET_PRODUCTS_LIST);
            productList = (ObservableList<Product>) clientSocket.readObject();
            productsTable.getItems().clear();
            productsTable.setItems(productList);
    }

    private void getSalesList(){
            clientSocket.writeObject(Task.GET_SALES_LIST);
            CustomList list = (CustomList) clientSocket.readObject();
            salesList = (ObservableList<ProductInOrder>) list.getList();
            salesTable.getItems().clear();
            salesTable.setItems(salesList);
    }

    @FXML
    public void categoryFilter(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        switch (categoryBox.getSelectionModel().getSelectedItem()) {
            case "Одежда" -> {
                setFilteredValue("Одежда");
            }
            case "Для дома" -> {
                setFilteredValue("Для дома");
            }
            case "Не выбрано" -> {
                productsTable.getItems().clear();
                getProductList();
            }
        }
    }

    private void setFilteredValue(String filteredValue) throws IOException, ClassNotFoundException {
        clientSocket.writeObject(Task.APP_PRICE_FILTER);
        clientSocket.writeObject(filteredValue);
        ObservableList<Product> filteredProductList = FXCollections.observableArrayList();
        productsTable.getItems().clear();
        filteredProductList = (ObservableList<Product>) clientSocket.readObject();
        productsTable.setItems(filteredProductList);
    }

    @FXML
    void openFormToBuildGraph(ActionEvent event) {
        changeScene("/com/example/internetcommerce/buildGraphForm.fxml");
    }

    @FXML
    void openFormToCreateReport(ActionEvent event) {
        changeScene("/com/example/internetcommerce/formToCreateReport.fxml");
    }

}
