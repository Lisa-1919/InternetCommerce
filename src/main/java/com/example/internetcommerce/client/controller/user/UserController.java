package com.example.internetcommerce.client.controller.user;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static com.example.internetcommerce.client.Client.*;
import static com.example.internetcommerce.client.controller.common.AuthorisationController.user;
import static javafx.scene.control.cell.TextFieldTableCell.forTableColumn;

public class UserController implements Initializable, ControllerInterface {
    @FXML
    private AnchorPane accountPage;
    @FXML
    private Button btnExit;
    @FXML
    private Button btnEditPassword;
    @FXML
    private Tab accountTab;

    @FXML
    private TableColumn<Product, Integer> amountBasketColumn;

    @FXML
    private Spinner<Integer> amountSpinner = new Spinner<Integer>();
    ;

    @FXML
    private AnchorPane basketPage;

    @FXML
    private Tab basketTab;

    @FXML
    private Button btnAddToBasket;

    @FXML
    private Button btnAppFilter;

    @FXML
    private AnchorPane catalogPage;

    @FXML
    private Tab catalogTab;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private ComboBox<String> categotyBox;

    @FXML
    private TableColumn<Product, String> descriptionColumn;

    @FXML
    private TableColumn<Product, String> nameBasketColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private AnchorPane ordersPage;

    @FXML
    private Tab ordersTab;

    @FXML
    private TableColumn<Product, Double> priceBasketColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableView<Product> productBasketTable;

    @FXML
    private TableView<Product> productCatalogTable;

    @FXML
    private TableColumn<Product, CheckBox> flagColumn;
    @FXML
    private TextField searchField;

    @FXML
    private TextField fromField;
    @FXML
    private TextField toField;

    private ObservableList<Product> basketList = FXCollections.observableArrayList();

    private ObservableList<Product> catalogList = FXCollections.observableArrayList();
    @FXML
    private Label userBirthday;

    @FXML
    private Label userEmail;

    @FXML
    private Label userFirstName;

    @FXML
    private Label userLastName;

    @FXML
    private ToggleGroup paymentGroup;

    @FXML
    private ChoiceBox<String> selfDeliveryAddress;
    private ObservableList<String> selfDeliveryAddressList = FXCollections.observableArrayList("Минск", "Борисов", "Гродно");
    @FXML
    private ToggleGroup shippingGroup;
    @FXML
    private TextField courierAddress;
    @FXML
    private RadioButton selfDelivery;
    @FXML
    private RadioButton courier;
    @FXML
    private TableColumn<Order, String> addressColumn;
    @FXML
    private TableColumn<Order, Double> orderPriceColumn;

    @FXML
    private TableView<Order> ordersTable;
    private ObservableList<Order> ordersList = FXCollections.observableArrayList();

    @FXML
    private TableColumn<Order, Date> creationDateColumn;
    @FXML
    private Button btnConfirmReceipt;
    @FXML
    private TableColumn<Order, Date> receiptionDateColumn;
    private ObservableList<String> categories = FXCollections.observableArrayList("Не выбрано", "Одежда", "Для дома", "Книги");
    protected static Product product;

    @FXML
    void addToBasket(ActionEvent event) throws IOException, ClassNotFoundException {
        Product product = productCatalogTable.getSelectionModel().getSelectedItem();
        clientSocket.writeObject(Task.ADD_TO_BASKET);
        clientSocket.writeObject(user);
        int amount = amountSpinner.getValue();
        product.setAmount(amount);
        clientSocket.writeObject(product);
        Message message = (Message) clientSocket.readObject();
        String message1 = message.equals(Message.SUCCESSFUL) ? "Товар добавлен в корзину" : "Такой товар уже есть в вашей корзине";
        showMessage(null, message1);
    }

    @FXML
    void appFilter(ActionEvent event) throws IOException, ClassNotFoundException {
        clientSocket.writeObject(Task.APP_PRICE_FILTER);
        double[] numbers = new double[2];
        numbers[0] = Double.parseDouble(fromField.getText());
        numbers[1] = Double.parseDouble(toField.getText());
        clientSocket.writeObject(numbers);
        ArrayList<Product> products = (ArrayList<Product>) clientSocket.readObject();
        catalogList.setAll(products);
        productCatalogTable.refresh();
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
        nameColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("description"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("category"));
        SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        amountSpinner.setValueFactory(spinnerValueFactory);
        getProductCatalogList();
        categotyBox.setItems(categories);
        productCatalogTable.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    product = row.getItem();
                    row.getScene().getWindow().hide();
                    changeScene("/com/example/internetcommerce/userProductForm.fxml");
                }
            });
            return row;
        });

        FilteredList<Product> productFilteredList = new FilteredList<>(catalogList, b -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            productFilteredList.setPredicate(product -> {
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }
                String searchValue = newValue.toLowerCase();

                if (product.getName().toLowerCase().indexOf(searchValue) > -1) {
                    return true;
                } else if (product.getCategory().toLowerCase().indexOf(searchValue) > -1) {
                    return true;
                } else if (product.getDescription().toLowerCase().indexOf(searchValue) > -1) {
                    return true;
                } else
                    return false;
            });
        });

        SortedList<Product> productSortedList = new SortedList<>(productFilteredList);
        productSortedList.comparatorProperty().bind(productCatalogTable.comparatorProperty());
        productCatalogTable.setItems(productSortedList);

        nameBasketColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        amountBasketColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("amount"));
        priceBasketColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
        flagColumn.setCellValueFactory(arg0 -> {
            Product product = arg0.getValue();
            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().setValue(product.getSelect());
            checkBox.selectedProperty().addListener((ov, old_val, new_val) -> product.setSelect(new_val));
            return new SimpleObjectProperty<>(checkBox);
        });
        getProductBasketListProduct();
        selfDeliveryAddress.setItems(selfDeliveryAddressList);
        amountBasketColumn.setCellFactory(forTableColumn(new IntegerStringConverter()));


        creationDateColumn.setCellValueFactory(new PropertyValueFactory<Order, Date>("creationDate"));
        receiptionDateColumn.setCellValueFactory(new PropertyValueFactory<Order, Date>("receiptionDate"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("address"));
        orderPriceColumn.setCellValueFactory(new PropertyValueFactory<Order, Double>("orderPrice"));
        getOrderList();
    }

    private void getProductCatalogList() {
        clientSocket.writeObject(Task.GET_PRODUCTS_LIST);
        ArrayList<Product> products = (ArrayList<Product>) clientSocket.readObject();
        catalogList.setAll(products);
        productCatalogTable.setItems(catalogList);
    }

    private void getProductBasketListProduct() {
        clientSocket.writeObject(Task.GET_BASKET_LIST);
        clientSocket.writeObject(user);
        ArrayList<Product> products = (ArrayList<Product>) clientSocket.readObject();
        basketList.setAll(products);
        productBasketTable.setItems(basketList);
    }

    @FXML
    public void deleteProductFromBasket(ActionEvent actionEvent) throws IOException {
        clientSocket.writeObject(Task.DELETE_FROM_BASKET);
        clientSocket.writeObject(user);
        Product product = productBasketTable.getSelectionModel().getSelectedItem();
        clientSocket.writeObject(product);
        basketList.remove(productBasketTable.getSelectionModel().getSelectedItem());
        productBasketTable.refresh();
    }

    @FXML
    public void editAmount(TableColumn.CellEditEvent<Product, Integer> productIntegerCellEditEvent) throws IOException {
        Product selectedProduct = productBasketTable.getSelectionModel().getSelectedItem();
        selectedProduct.setAmount(productIntegerCellEditEvent.getNewValue());
        clientSocket.writeObject(Task.EDIT_PRODUCT_IN_BASKET);
        clientSocket.writeObject(user.getBasket());
        selectedProduct.setAmount(productIntegerCellEditEvent.getNewValue());
        clientSocket.writeObject(selectedProduct);
    }

    public void exit(ActionEvent actionEvent) {
        btnExit.getScene().getWindow().hide();
        changeScene("/com/example/internetcommerce/authorisation.fxml");
    }

    @FXML
    public void createNewOrder(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        clientSocket.writeObject(Task.CREATE_ORDER);
        List<Product> selectedProducts = new ArrayList<>();
        double orderPrice = 0;
        for (Product product : basketList) {
            if (product.getSelect()) {
                selectedProducts.add(product);
                orderPrice += product.getPrice() * product.getAmount();
            }
        }
        RadioButton selectedShipping = (RadioButton) shippingGroup.getSelectedToggle();
        String address = "";
        String shipping = "";
        if (selectedShipping.getText().equals("Курьер")) {
            shipping = selectedShipping.getText();
            address = courierAddress.getText();
            orderPrice += 5;
        } else if (selectedShipping.getText().equals("Самовывоз")) {
            address = selfDeliveryAddress.getValue();
            shipping = selectedShipping.getText();
        }
        RadioButton selectedPayment = (RadioButton) paymentGroup.getSelectedToggle();
        String payment = selectedPayment.getText();
        Order order = new Order(user.getId(), new Date(), selectedProducts, address, orderPrice, shipping, payment);
        clientSocket.writeObject(user.getBasket());
        clientSocket.writeObject(order);
        basketList.removeIf(Product::getSelect);
        productBasketTable.refresh();
    }

    @FXML
    public void getCourierAddress(ActionEvent actionEvent) {
        if (courier.isSelected()) {
            courierAddress.setEditable(true);
            selfDeliveryAddress.setDisable(true);
        } else {
            courierAddress.setEditable(false);
            selfDeliveryAddress.setDisable(false);
        }
    }

    @FXML
    public void selectSelfDeliveryAddress(ActionEvent actionEvent) {
        if (selfDelivery.isSelected()) {
            courierAddress.setEditable(false);
            selfDeliveryAddress.setDisable(false);
        } else {
            courierAddress.setEditable(true);
            selfDeliveryAddress.setDisable(true);
        }
    }

    @FXML
    public void basketInitialize(Event event) {
//        productBasketTable.getItems().clear();
//        getProductBasketListProduct();
    }

    @FXML
    void confirmReceipt(ActionEvent event) throws IOException {
        if (ordersTable.getSelectionModel().getSelectedItem().getReceiptionDate() == null) {
            clientSocket.writeObject(Task.CONFIRM_RECEIPT);
            Order order = ordersTable.getSelectionModel().getSelectedItem();
            order.setReceiptionDate(new Date());
            clientSocket.writeObject(order);
            ordersTable.refresh();
        }
    }

    private void getOrderList() {
        clientSocket.writeObject(Task.GET_ORDERS_LIST);
        clientSocket.writeObject(user);
        ArrayList<Order> orders = (ArrayList<Order>) clientSocket.readObject();
        ordersList.setAll(orders);
        ordersTable.setItems(ordersList);
    }

    @FXML
    public void editPassword(ActionEvent actionEvent) {
        btnEditPassword.getScene().getWindow().hide();
        changeScene("/com/example/internetcommerce/editPasswordPage.fxml");
    }

    @FXML
    public void categoryFilter(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        switch (categotyBox.getSelectionModel().getSelectedItem()) {
            case "Одежда" -> {
                setFilteredValue("Одежда");
            }
            case "Для дома" -> {
                setFilteredValue("Для дома");
            }
            case "Книги" -> {
                setFilteredValue("Книги");
            }
            case "Не выбрано" -> {
                productCatalogTable.getItems().clear();
                getProductCatalogList();
            }
        }
    }

    private void setFilteredValue(String filteredValue) throws IOException, ClassNotFoundException {
        clientSocket.writeObject(Task.APP_CATEGORY_FILTER);
        clientSocket.writeObject(filteredValue);
        ObservableList<Product> filteredProductList = FXCollections.observableArrayList();
        ArrayList<Product> products = (ArrayList<Product>) clientSocket.readObject();
        filteredProductList.setAll(products);
        productCatalogTable.setItems(filteredProductList);
    }
}
