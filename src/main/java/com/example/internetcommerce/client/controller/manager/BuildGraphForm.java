package com.example.internetcommerce.client.controller.manager;


import com.example.internetcommerce.client.controller.ControllerInterface;
import com.example.internetcommerce.models.*;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

import static com.example.internetcommerce.client.Client.*;
import static com.example.internetcommerce.client.controller.common.AuthorisationController.user;

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
        if (fromDate.getValue().isBefore(toDate.getValue()) || fromDate.getValue().isEqual(toDate.getValue())) {
            Graph graph = new Graph();
            graph.setFromDate(fromDate.getValue());
            graph.setToDate(toDate.getValue());
            switch (graphTypeBox.getSelectionModel().getSelectedItem()) {
                case "Уровень продаж за выбранный период" -> {
                    graph.setType(0);
                    clientSocket.writeObject(graph);
                    graphDateSum();
                }
                case "Круговой график (по категориям)" -> {
                    graph.setType(1);
                    clientSocket.writeObject(graph);
                    graphCategoryAmount();
                }
                case "Категория-сумма" -> {
                    graph.setType(2);
                    clientSocket.writeObject(graph);
                    graphCategorySum();
                }
            }

        } else
            showMessage("Ошибка", "Дата начала анализа должна быть раньше даты окончания");
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
        if (clientSocket.readObject().equals(Message.ERROR))
            showMessage("", "Нет данных, соответствующих данным условиям");
        else {
            btnViewGraph.getScene().getWindow().hide();
            Sale sale;
            sale = (Sale) clientSocket.readObject();
            DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            sale.getSales().forEach((date, sum) -> {
                series.getData().add(new XYChart.Data<>(formatters.format((TemporalAccessor) date), sum));
            });
            Stage stage = new Stage();
            Scene scene = new Scene(lineChart, 800, 600);
            lineChart.getData().add(series);
            stage.setScene(scene);
            stage.show();
        }
    }

    private void graphCategoryAmount() {
        if (clientSocket.readObject().equals(Message.ERROR))
            showMessage("", "Нет данных, соответствующих данным условиям");
        else {
            btnViewGraph.getScene().getWindow().hide();
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
    }

    private void graphCategorySum() {
        if (clientSocket.readObject().equals(Message.ERROR))
            showMessage("", "Нет данных, соответствующих данным условиям");
        else {
            btnViewGraph.getScene().getWindow().hide();
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
    }

    @FXML
    void choiceGraphType(ActionEvent event) throws IOException {

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

