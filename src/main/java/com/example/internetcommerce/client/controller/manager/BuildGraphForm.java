package com.example.internetcommerce.client.controller.manager;


import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.ProductInOrder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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

    @FXML
    void viewGraph(ActionEvent event) {
        btnViewGraph.getScene().getWindow().hide();
        changeScene("/com/example/internetcommerce/graph.fxml");
        barChartInitialize();
    }

    private void barChartInitialize(){

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc =
                new BarChart<String,Number>(xAxis,yAxis);
        xAxis.setLabel("Категория");
        yAxis.setLabel("Цена");
        List<XYChart.Series> seriesList = new ArrayList<>();
        for(String category: categories){
            XYChart.Series series = new XYChart.Series();
            series.setName(category);
            seriesList.add(series);

        }
    }
    @FXML
    void choiceGraphType(ActionEvent event) throws IOException {
        switch (graphTypeBox.getSelectionModel().getSelectedItem()) {
            case "Уровень продаж за выбранный период" -> {
                fromDate.setDisable(false);
                toDate.setDisable(false);
                btnViewGraph.setDisable(false);
                setGraphType(0);
                outputStream.writeObject(fromDate);
                outputStream.flush();
                outputStream.writeObject(toDate);
                outputStream.flush();
            }
            case "Круговой график (по категориям)" -> {
                btnViewGraph.setDisable(false);
                setGraphType(1);
            }
        }
    }

    private void setGraphType(int graphType) throws IOException {
        outputStream.writeInt(20);
        outputStream.flush();
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

