package com.example.internetcommerce.client.controller.admin;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static com.example.internetcommerce.client.Client.*;
import static com.example.internetcommerce.client.controller.common.AuthorisationController.user;

public class AdminHomeController implements Initializable, ControllerInterface {
    @FXML
    public Button btnOpenFormToAddManager;
    @FXML
    private AnchorPane accountPage;
    @FXML
    private Tab accountTab;
    @FXML
    private TableColumn<User, LocalDate> birthdayColumn;
    @FXML
    private Button btnDeleteManager;
    @FXML
    private Button btnEditPassword;
    @FXML
    private Button btnExit;
    @FXML
    private TableColumn<User, String> countryColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> firstNameColumn;
    @FXML
    private TableColumn<User, Long> idColumn;
    @FXML
    private TableColumn<User, String> lastNameColumn;
    @FXML
    private TableView<User> managersTable;
    @FXML
    private Tab managersTab;
    @FXML
    private TableColumn<User, String> phoneNumberColumn;
    @FXML
    private Label userBirthday;
    @FXML
    private Label userEmail;

    @FXML
    private Label userFirstName;

    @FXML
    private Label userLastName;

    private ObservableList<User> managersList = FXCollections.observableArrayList();

    @FXML
    void deleteManager(ActionEvent event) throws IOException {
        outputStream.writeInt(17);
        outputStream.flush();
        long managerId = managersTable.getSelectionModel().getSelectedItem().getId();
        outputStream.writeLong(managerId);
        outputStream.flush();
        managersList.remove(managersTable.getSelectionModel().getSelectedIndex());
        managersTable.refresh();
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
        idColumn.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<User, String>("phoneNumber"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<User, String>("country"));
        birthdayColumn.setCellValueFactory(new PropertyValueFactory<User, LocalDate>("birthday"));
        getManagersList();
    }

    @FXML
    public void openFormToAddManager(ActionEvent actionEvent) {
        btnOpenFormToAddManager.getScene().getWindow().hide();
        changeScene("/com/example/internetcommerce/addNewManager.fxml");
    }

    private void getManagersList() {
        try {
            outputStream.writeInt(16);
            outputStream.flush();
            int count = 0;
            int size = inputStream.readInt();
            while (count < size) {
                User manager = (User) inputStream.readObject();
                managersList.add(manager);
                count++;
            }
            managersTable.setItems(managersList);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void initializeManagersPage(Event event) {
        managersTable.getItems().clear();
        getManagersList();
    }

}
