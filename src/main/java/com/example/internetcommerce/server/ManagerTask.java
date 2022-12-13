package com.example.internetcommerce.server;

import com.example.internetcommerce.database.StoreDataBase;
import com.example.internetcommerce.models.*;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class ManagerTask {
    public static void addNewProduct(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws IOException, ClassNotFoundException {
        try {
            Product product = (Product) inputStream.readObject();
            String sqlString = "INSERT INTO products (name, description, image, price, category) VALUES ('"
                    + product.getName() + "','" + product.getDescription() + "','" + product.getImageName() + "'," + product.getPrice() + ",'" + product.getCategory() + "')";
            dataBase.insert(sqlString);
            outputStream.writeObject(Message.SUCCESSFUL);
            outputStream.flush();
        } catch (Exception e) {
            outputStream.writeObject(Message.ERROR);
            outputStream.flush();
        }
    }

    public static void deleteProduct(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws IOException, ClassNotFoundException {
        Product product = (Product) inputStream.readObject();
        dataBase.delete("DELETE FROM basket_products WHERE product_id = " + product.getId());
        dataBase.delete("DELETE FROM products WHERE id = " + product.getId());
    }

    public static void editProduct(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws IOException, ClassNotFoundException {
        Product product = (Product) inputStream.readObject();
        dataBase.update("UPDATE products SET name = '" + product.getName() + "', description = '" + product.getDescription() +
                "', price = " + product.getPrice() + ", image = '" + product.getImageName() + "' WHERE id = " + product.getId());
    }

    public static void getSalesList(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws SQLException, IOException {
        List<ProductInOrder> productInOrderList = new ArrayList<>();
        ResultSet orderSet = dataBase.select("SELECT * FROM orders");
        List<Order> orders = new ArrayList<>();
        orderSet.beforeFirst();
        while (orderSet.next()) {
            Date receiptDate = null;
            if (orderSet.getDate("receiption_date") == null) {
                receiptDate = orderSet.getDate("receiption_date");
            } else {
                receiptDate = new Date(orderSet.getDate("receiption_date").getTime());
            }
            Order order = new Order(orderSet.getLong("id"), new Date(orderSet.getDate("creation_date").getTime()), receiptDate);
            orders.add(order);
        }
        orderSet.first();
        for (Order order : orders) {
            ResultSet orderDetailsSet = dataBase.select("SELECT * FROM order_details WHERE order_id = " + order.getId());
            orderDetailsSet.beforeFirst();
            while (orderDetailsSet.next()) {
                ProductInOrder product = new ProductInOrder();
                product.setCreateOrderDate(order.getCreationDate());
                product.setReceiptOrderDate(order.getReceiptionDate());
                product.setOrderCost(orderDetailsSet.getDouble("cost"));
                product.setAmount(orderDetailsSet.getInt("count"));
                product.setProductId(orderDetailsSet.getLong("product_id"));
                product.setId(orderDetailsSet.getLong("id"));
                productInOrderList.add(product);
            }
            orderDetailsSet.first();
        }
        for (ProductInOrder product : productInOrderList) {
            ResultSet productSet = dataBase.select("SELECT * FROM products WHERE id = " + product.getProductId());
            productSet.beforeFirst();
            while (productSet.next()) {
                product.setName(productSet.getString("name"));
                product.setCategory(productSet.getString("category"));
            }
            productSet.first();
        }
        outputStream.writeObject(productInOrderList);
        outputStream.flush();
    }

    public static void createGraph(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws IOException, ClassNotFoundException, SQLException {
        Graph graph = (Graph) inputStream.readObject();
        List<ProductInOrder> sales = new ArrayList<>();
        ResultSet orderSet = dataBase.select("SELECT * FROM orders WHERE creation_date BETWEEN '" + graph.getFromDate() + "' AND '" + graph.getToDate() + "'");

        int counter = 0;
        List<Order> orders = new ArrayList<>();
        orderSet.beforeFirst();
        while (orderSet.next()) {
            Order order = new Order();
            order.setId(orderSet.getLong("id"));
            order.setCreationDate(new Date(orderSet.getDate("creation_date").getTime()));
            orders.add(order);
        }
        orderSet.first();
        for (Order order : orders) {
            ResultSet orderDetailsSet = dataBase.select("SELECT * FROM order_details WHERE order_id = " + order.getId());
            orderDetailsSet.beforeFirst();
            while (orderDetailsSet.next()) {
                ProductInOrder product = new ProductInOrder();
                product.setCreateOrderDate(order.getCreationDate());
                product.setReceiptOrderDate(order.getReceiptionDate());
                product.setOrderCost(orderDetailsSet.getDouble("cost"));
                product.setAmount(orderDetailsSet.getInt("count"));
                product.setProductId(orderDetailsSet.getLong("product_id"));
                product.setId(orderDetailsSet.getLong("id"));
                sales.add(product);
                counter++;
            }
        }

        if (counter != 0) {
            outputStream.writeObject(Message.SUCCESSFUL);
            outputStream.flush();
            for (ProductInOrder product : sales) {
                ResultSet productSet = dataBase.select("SELECT * FROM products WHERE id = " + product.getProductId());
                productSet.beforeFirst();
                while (productSet.next()) {
                    product.setName(productSet.getString("name"));
                    product.setCategory(productSet.getString("category"));
                }
            }
            switch (graph.getType()) {
                case 0: {
                    Map<LocalDate, Double> salesMap = new TreeMap<>();
                    for (ProductInOrder product : sales) {
                        salesMap.merge(product.getCreateOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), product.getOrderCost() * product.getAmount(), Double::sum);
                    }
                    salesMap.entrySet();
                    Sale sale = new Sale(salesMap);
                    outputStream.writeObject(sale);
                    outputStream.flush();
                    break;
                }
                case 1: {
                    HashMap<String, Integer> categorySales = new HashMap<>();
                    for (ProductInOrder product : sales) {
                        categorySales.merge(product.getCategory(), product.getAmount(), Integer::sum);
                    }
                    Sale sale = new Sale(categorySales);
                    outputStream.writeObject(sale);
                    outputStream.flush();
                    break;
                }
                case 2: {
                    HashMap<String, Double> categorySales = new HashMap<>();
                    for (ProductInOrder product : sales) {
                        categorySales.merge(product.getCategory(), product.getOrderCost() * product.getAmount(), Double::sum);
                    }
                    Sale sale = new Sale(categorySales);
                    outputStream.writeObject(sale);
                    outputStream.flush();
                    break;
                }
            }
        } else {
            outputStream.writeObject(Message.ERROR);
            outputStream.flush();
        }
    }

    public static void createReport(ObjectInputStream inputStream, ObjectOutputStream outputStream, StoreDataBase dataBase) throws IOException, ClassNotFoundException, SQLException {
        Report report = (Report) inputStream.readObject();
        List<ProductInOrder> sales = new ArrayList<>();
        ResultSet orderSet = dataBase.select("SELECT * FROM orders WHERE creation_date BETWEEN '" + report.getFromDate() + "' AND '" + report.getToDate() + "'");
        int counter = 0;
        List<Order> orders = new ArrayList<>();
        orderSet.beforeFirst();
        while (orderSet.next()) {
            Date receiptDate = null;
            if (orderSet.getDate("receiption_date") == null) {
                receiptDate = orderSet.getDate("receiption_date");
            } else {
                receiptDate = new Date(orderSet.getDate("receiption_date").getTime());
            }
            Order order = new Order(orderSet.getLong("id"), new Date(orderSet.getDate("creation_date").getTime()), receiptDate);
            orders.add(order);
        }
        orderSet.first();
        for (Order order : orders) {
            ResultSet orderDetailsSet = dataBase.select("SELECT * FROM order_details WHERE order_id = " + order.getId());
            orderDetailsSet.beforeFirst();
            while (orderDetailsSet.next()) {
                ProductInOrder product = new ProductInOrder();
                product.setCreateOrderDate(order.getCreationDate());
                product.setReceiptOrderDate(order.getReceiptionDate());
                product.setOrderCost(orderDetailsSet.getDouble("cost"));
                product.setAmount(orderDetailsSet.getInt("count"));
                product.setProductId(orderDetailsSet.getLong("product_id"));
                product.setId(orderDetailsSet.getLong("id"));
                sales.add(product);
                counter++;
            }
        }
        if (counter != 0) {
            outputStream.writeObject(Message.SUCCESSFUL);
            outputStream.flush();
            for (ProductInOrder product : sales) {
                ResultSet productSet = dataBase.select("SELECT * FROM products WHERE id = " + product.getProductId());
                productSet.beforeFirst();
                while (productSet.next()) {
                    product.setName(productSet.getString("name"));
                    product.setCategory(productSet.getString("category"));
                }
            }
            switch (report.getFileType()) {
                case "doc": {
                    createTextReport(report, sales);
                    break;
                }
                case "xlsx": {
                    createExcelReport(report, sales);
                    break;
                }
            }

        } else {
            outputStream.writeObject(Message.ERROR);
            outputStream.flush();
        }
    }

    private static void createTextReport(Report report, List<ProductInOrder> listForReport) {

        try (Writer writer = new FileWriter(report.getDirectory() + UUID.randomUUID().toString() + "." + report.getFileType(), StandardCharsets.UTF_8, true)) {
            writer.write(report.getReportCreateDate() + "\n" +
                    report.getManager().getFirstName() + " " + report.getManager().getLastName() + "\n");
            for (ProductInOrder product : listForReport) {
                writer.write(product.toString() + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createExcelReport(Report report, List<ProductInOrder> listForReport) {
        try {
            File file = new File(report.getDirectory() + "/" + UUID.randomUUID().toString() + ".xls");
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet();
            Row row = sheet.createRow(0);
            sheet.createRow(0).createCell(0).setCellValue("№");
            sheet.createRow(0).createCell(1).setCellValue("Название");
            sheet.createRow(0).createCell(2).setCellValue("Количество");
            sheet.createRow(0).createCell(3).setCellValue("Дата заказа");
            sheet.createRow(0).createCell(4).setCellValue("Категория");
            workbook.write(new FileOutputStream(file));
            for (int i = 0; i < listForReport.size(); i++) {
                Row row1 = sheet.createRow(i+1);
                row1.createCell(0).setCellValue(listForReport.get(i).getProductId());
                row1.createCell(1).setCellValue(listForReport.get(i).getName());
                row1.createCell(2).setCellValue(listForReport.get(i).getAmount());
                row1.createCell(3).setCellValue(listForReport.get(i).getCreateOrderDate());
                row1.createCell(4).setCellValue(listForReport.get(i).getCategory());
                workbook.write(new FileOutputStream(file));
            }
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
