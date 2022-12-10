package com.example.internetcommerce.client.controller.manager;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.Message;
import com.example.internetcommerce.models.Report;
import com.example.internetcommerce.models.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

import static com.example.internetcommerce.client.Client.clientSocket;
import static com.example.internetcommerce.client.controller.common.AuthorisationController.user;


public class ReportController implements Initializable, ControllerInterface {

    @FXML
    private Button btnCreateReport;

    @FXML
    private DatePicker fromDate;

    @FXML
    private DatePicker toDate;

    @FXML
    private ComboBox<String> reportType;

    private ObservableList<String> reportTypes = FXCollections.observableArrayList("Текстовый документ", "Excel");

    @FXML
    void createReport(ActionEvent event) throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выбрать папку");
        File file = directoryChooser.showDialog(new Stage());
        if(fromDate.getValue().isBefore(toDate.getValue()) || fromDate.getValue().isEqual(toDate.getValue())) {
            String type =  reportType.getSelectionModel().getSelectedItem().equals("Excel")?"xlsx":"doc";
            Report report = new Report(fromDate.getValue(), toDate.getValue(), new Date() , user, file, type);
            clientSocket.writeObject(Task.GENERATE_REPORT);
            clientSocket.writeObject(report);
            if (clientSocket.readObject().equals(Message.ERROR))
                showMessage("", "Нет данных, соответствующих данным условиям");
            else
                btnCreateReport.getScene().getWindow().hide();
        }else
            showMessage("Ошибка","Дата начала анализа должна быть раньше даты окончания");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reportType.setItems(reportTypes);
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
}
