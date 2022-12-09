package com.example.internetcommerce.client.controller.manager;


import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.ProductInOrder;
import com.example.internetcommerce.models.Sale;
import com.example.internetcommerce.models.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
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
import java.util.*;

import static com.example.internetcommerce.client.Client.*;

public class BuildGraphForm implements Initializable, ControllerInterface {

    @FXML
    private DatePicker fromDate;

    @FXML
    private ComboBox<String> graphTypeBox;
    private ObservableList<String> graphTypes = FXCollections.observableArrayList("Уровень продаж за выбранный период", "Круговой график (по категориям)", "Категория-сумма");
    @FXML
    private DatePicker toDate;

    @FXML
    private Button btnViewGraph;

    private ObservableList<ProductInOrder> sales = FXCollections.observableArrayList();

    @FXML
    void viewGraph(ActionEvent event) throws IOException {
        clientSocket.writeObject(Task.BUILD_GRAPH);
        clientSocket.writeObject(fromDate.getValue());
        clientSocket.writeObject(toDate.getValue());
        switch (graphTypeBox.getSelectionModel().getSelectedItem()) {
            case "Уровень продаж за выбранный период" -> {
                setGraphType(0);
                graphDateSum();
            }
            case "Круговой график (по категориям)" -> {
                setGraphType(1);
                graphCategoryAmount();
            }
            case "Категория-сумма" -> {
                setGraphType(2);
                graphCategorySum();
            }
        }
        btnViewGraph.getScene().getWindow().hide();
    }

    private void graphDateSum() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Дата");
        yAxis.setLabel("Сумма");
        final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);
        lineChart.setTitle("");
        XYChart.Series series = new XYChart.Series();
        series.setName("");
        Sale sale;
        sale = (Sale) clientSocket.readObject();
        sale.getSales().forEach((date, sum) -> {
            series.getData().add(new XYChart.Data<>(date.toString(), sum));
        });
        Stage stage = new Stage();
        Scene scene = new Scene(lineChart, 800, 600);
        lineChart.getData().add(series);
        stage.setScene(scene);
        stage.show();

    }

    private void graphCategoryAmount() {
        Sale sale;
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        sale = (Sale) clientSocket.readObject();
        sale.getSales().forEach((category, sum) -> {
            pieChartData.add(new PieChart.Data((String) category, (int) sum));
        });

        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle("");
        Stage stage = new Stage();
        Scene scene = new Scene(new Group(), 800, 600);
        ((Group) scene.getRoot()).getChildren().add(chart);
        stage.setScene(scene);
        stage.show();
    }

    private void graphCategorySum() {
        Sale sale;
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> bc = new BarChart<String, Number>(xAxis, yAxis);
        bc.setTitle("");
        xAxis.setLabel("Категория");
        yAxis.setLabel("Сумма");
        XYChart.Series series = new XYChart.Series();
        sale = (Sale) clientSocket.readObject();

        sale.getSales().forEach((category, sum) -> {
            series.getData().add(new XYChart.Data<>(category, sum));
        });
        Stage stage = new Stage();
        Scene scene = new Scene(bc, 800, 600);
        bc.getData().addAll(series);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void choiceGraphType(ActionEvent event) throws IOException {

    }

    private void setGraphType(int graphType) throws IOException {
        clientSocket.writeObject(graphType);
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

