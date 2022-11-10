package com.example.internetcommerce.server;

import com.example.internetcommerce.database.StoreDataBase;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Server {
    private static ServerSocket socket;
    private static BufferedReader reader;
    private static BufferedWriter writer;

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        socket = new ServerSocket(1024);
        Socket client = socket.accept();
        System.out.println("Client connected");
        reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        StoreDataBase dataBase = new StoreDataBase();
        ServerHandler serverHandler = new ServerHandler(reader, writer, dataBase);
        while(true){
            int taskIndex = reader.read();
            serverHandler.setTask(taskIndex);
            System.out.println(taskIndex);
        }
    }
}
