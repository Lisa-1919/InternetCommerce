package com.example.internetcommerce.client.controller.manager;


import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.ProductInOrder;
import com.example.internetcommerce.models.Sale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import static com.example.internetcommerce.client.Client.inputStream;
import static com.example.internetcommerce.client.Client.outputStream;
import static com.example.internetcommerce.client.controller.manager.ManagerHomeController.categories;

public class BuildGraphForm implements Initializable, ControllerInterface {

    @FXML
    private DatePicker fromDate;

    @FXML
    private ComboBox<String> graphTypeBox;
    private ObservableList<String> graphTypes = FXCollections.observableArrayList("Уровень продаж за выбранный период", "Круговой график (по категориям)");
    @FXML
    private DatePicker toDate;

    @FXML
    private Button btnViewGraph;

    private ObservableList<ProductInOrder> sales = FXCollections.observableArrayList();

    @FXML
    void viewGraph(ActionEvent event) throws IOException {
        outputStream.writeInt(20);
        outputStream.flush();
        outputStream.writeObject(fromDate.getValue());
        outputStream.flush();
        outputStream.writeObject(toDate.getValue());
        outputStream.flush();
        switch (graphTypeBox.getSelectionModel().getSelectedItem()) {
            case "Уровень продаж за выбранный период" -> {
                setGraphType(0);
                viewGraphPage();
            }
//            case "Круговой график (по категориям)" -> {
//                btnViewGraph.setDisable(false);
//                setGraphType(1);
//            }
        }
        btnViewGraph.getScene().getWindow().hide();
//
    }

    private void viewGraphPage() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Дата");
        yAxis.setLabel("Сумма");
        final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);
        lineChart.setTitle("");
        XYChart.Series series = new XYChart.Series();
        series.setName("");
        Sale sale;
        try {
            sale = (Sale) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        sale.getSales().forEach((date, sum) -> {
            series.getData().add(new XYChart.Data<>(date.toString(), sum));
        });
        Stage stage = new Stage();
        Scene scene = new Scene(lineChart, 800, 600);
        lineChart.getData().add(series);
        stage.setScene(scene);
        stage.show();

    }

//    private void barChartInitialize() {
//
//        final CategoryAxis xAxis = new CategoryAxis();
//        final NumberAxis yAxis = new NumberAxis();
//        final BarChart<String, Number> bc =
//                new BarChart<String, Number>(xAxis, yAxis);
//        xAxis.setLabel("Категория");
//        yAxis.setLabel("Цена");
//        List<XYChart.Series> seriesList = new ArrayList<>();
//        for (String category : categories) {
//            XYChart.Series series = new XYChart.Series();
//            series.setName(category);
//            seriesList.add(series);
//
//        }
//    }

    @FXML
    void choiceGraphType(ActionEvent event) throws IOException {

    }

    private void setGraphType(int graphType) throws IOException {
        outputStream.writeInt(graphType);
        outputStream.flush();
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
        graphTypeBox.setItems(graphTypes);
    }


}

