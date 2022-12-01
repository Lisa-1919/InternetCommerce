package com.example.internetcommerce.client.controller.user;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.Order;
import com.example.internetcommerce.models.Product;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static com.example.internetcommerce.client.Client.*;
import static javafx.scene.control.cell.TextFieldTableCell.forTableColumn;

public class UserController implements Initializable, ControllerInterface {
    @FXML
    private AnchorPane accountPage;

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
    private Button btnSearch;

    @FXML
    private AnchorPane catalogPage;

    @FXML
    private Tab catalogTab;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private ChoiceBox<String> categotyFilter;

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

    private ObservableList<Product> catalogList = FXCollections.observableArrayList();
    private ObservableList<Product> basketList = FXCollections.observableArrayList();

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

    @FXML
    void addToBasket(ActionEvent event) throws IOException, ClassNotFoundException {
        Product product = productCatalogTable.getSelectionModel().getSelectedItem();
        outputStream.writeInt(7);
        outputStream.flush();
        outputStream.writeLong(user.getId());
        outputStream.flush();
        outputStream.writeLong(product.getId());
        outputStream.flush();
        int amount = amountSpinner.getValue();
        outputStream.writeInt(amount);
        outputStream.flush();
        String result = (String) inputStream.readObject();
        String message = result.equals("successful") ? "Товар добавлен в корзину" : "Такой товар уже есть в вашей корзине";
        showMessage(null, message);
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
        if (size == 0) {
            showMessage("", "Таких товаров нет");
        }
        while (count < size) {
            catalogList.add((Product) inputStream.readObject());
            count++;
        }
        productCatalogTable.refresh();
    }

    @FXML
    void search(ActionEvent event) {

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
        SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        amountSpinner.setValueFactory(spinnerValueFactory);
        getProductCatalogList();

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
        try {
            outputStream.writeInt(4);
            outputStream.flush();
            int count = 0;
            int size = inputStream.readInt();
            while (count < size) {
                catalogList.add((Product) inputStream.readObject());
                count++;
            }
            productCatalogTable.setItems(catalogList);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private void getProductBasketListProduct() {
        try {
            outputStream.writeInt(6);
            outputStream.flush();
            outputStream.writeLong(user.getId());
            outputStream.flush();
            int count = 0;
            int size = inputStream.readInt();
            while (count < size) {
                Product product = (Product) inputStream.readObject();
                product.setSelect(false);
                basketList.add(product);
                count++;
            }
            productBasketTable.setItems(basketList);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void deleteProductFromBasket(ActionEvent actionEvent) throws IOException {
        outputStream.writeInt(8);
        outputStream.flush();
        outputStream.writeLong(user.getId());
        outputStream.flush();
        Product product = productBasketTable.getSelectionModel().getSelectedItem();
        outputStream.writeLong(product.getId());
        outputStream.flush();
        basketList.remove(productBasketTable.getSelectionModel().getSelectedItem());
        productBasketTable.refresh();
    }

    @FXML
    public void editAmount(TableColumn.CellEditEvent<Product, Integer> productIntegerCellEditEvent) throws IOException {
        Product selectedProduct = productBasketTable.getSelectionModel().getSelectedItem();
        selectedProduct.setAmount(productIntegerCellEditEvent.getNewValue());
        outputStream.writeInt(11);
        outputStream.flush();
        outputStream.writeLong(user.getBasket().getId());
        outputStream.flush();
        outputStream.writeLong(selectedProduct.getId());
        outputStream.flush();
        outputStream.writeInt(productIntegerCellEditEvent.getNewValue());
    }

    public void exit(ActionEvent actionEvent) {
    }

    @FXML
    public void createNewOrder(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        outputStream.writeInt(12);
        outputStream.flush();
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
        outputStream.writeLong(user.getBasket().getId());
        outputStream.flush();
        outputStream.writeObject(order);
        outputStream.flush();
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
        productBasketTable.getItems().clear();
        getProductBasketListProduct();
    }

    @FXML
    void confirmReceipt(ActionEvent event) throws IOException {
        if(ordersTable.getSelectionModel().getSelectedItem().getReceiptionDate() == null) {
            outputStream.writeInt(14);
            outputStream.flush();
            Order order = ordersTable.getSelectionModel().getSelectedItem();
            order.setReceiptionDate(new Date());
            outputStream.writeObject(order);
            ordersTable.refresh();
        }
    }

    private void getOrderList() {
        try {
            outputStream.writeInt(13);
            outputStream.flush();
            outputStream.writeLong(user.getId());
            outputStream.flush();
            int count = 0;
            int size = inputStream.readInt();
            while (count < size) {
                Order order = (Order) inputStream.readObject();
                ordersList.add(order);
                count++;
            }
            ordersTable.setItems(ordersList);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void catalogInitialize(Event event) {
        productCatalogTable.getItems().clear();
        getProductCatalogList();
    }

    @FXML
    public void orderInitialize(Event event) {
        ordersTable.getItems().clear();
        getOrderList();
    }
}
