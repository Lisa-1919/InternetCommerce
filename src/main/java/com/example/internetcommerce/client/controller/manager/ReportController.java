package com.example.internetcommerce.client.controller.manager;

import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.Message;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

import static com.example.internetcommerce.client.Client.clientSocket;


public class ReportController implements Initializable, ControllerInterface {

    @FXML
    private Button btnCreateReport;

    @FXML
    private DatePicker fromDate;

    @FXML
    private DatePicker toDate;

    @FXML
    private ComboBox<String> reportType;

    private ObservableList<String> reportTypes = FXCollections.observableArrayList("text", "exel");

    @FXML
    void createReport(ActionEvent event) throws IOException {
        LocalDate[] dates = new LocalDate[2];
        dates[0] = fromDate.getValue();
        dates[1] = toDate.getValue();
        if(dates[0].isBefore(dates[1]) || dates[0].isEqual(dates[1])) {
            clientSocket.writeObject(Task.GENERATE_REPORT);
            clientSocket.writeObject(dates);
            if (reportType.getSelectionModel().getSelectedItem().equals("text")) {
                clientSocket.writeObject(0);
            } else if (reportType.getSelectionModel().getSelectedItem().equals("exel")) {
                clientSocket.writeObject(1);
            }
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
