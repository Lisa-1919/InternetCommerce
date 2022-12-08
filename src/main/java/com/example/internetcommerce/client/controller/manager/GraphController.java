package com.example.internetcommerce.client.controller.manager;

import com.example.internetcommerce.models.ProductInOrder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.ResourceBundle;

public class GraphController implements Initializable {


    private NumberAxis xAxis;
    private NumberAxis yAxis;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        xAxis.setLabel("Дата");
        yAxis.setLabel("Сумма");
        final LineChart<Number,Number> lineChart =
                new LineChart<Number,Number>(xAxis,yAxis);

        lineChart.setTitle("");
        //defining a series
        XYChart.Series series = new XYChart.Series();
        series.setName("My portfolio");
    }
}
