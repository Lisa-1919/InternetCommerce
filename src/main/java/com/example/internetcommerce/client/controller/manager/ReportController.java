package com.example.internetcommerce.client.controller.manager;

import com.example.internetcommerce.client.controller.ControllerInterface;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

import static com.example.internetcommerce.client.Client.inputStream;
import static com.example.internetcommerce.client.Client.outputStream;

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
        outputStream.writeInt(21);
        outputStream.flush();
        LocalDate[] dates = new LocalDate[2];
        dates[0] = fromDate.getValue();
        dates[1] = toDate.getValue();
        outputStream.writeObject(dates);
        outputStream.flush();
        if(reportType.getSelectionModel().getSelectedItem().equals("text")){
            outputStream.writeInt(0);
            outputStream.flush();
        } else if (reportType.getSelectionModel().getSelectedItem().equals("exel")) {
            outputStream.writeInt(1);
            outputStream.flush();
        }
        int flag = inputStream.readInt();
        if(flag == 0)
            showMessage("", "Нет данных, соответствующих данным условиям");
        else
            btnCreateReport.getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reportType.setItems(reportTypes);
    }

    @Override
    public void showMessage(String title, String text) {

    }

    @Override
    public void changeScene(String sceneAddress) {

    }
}
