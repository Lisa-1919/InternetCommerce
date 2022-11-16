package com.example.internetcommerce.server;

import com.example.internetcommerce.database.StoreDataBase;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Server {

    private static ObjectInputStream inputStream = null;
    private static ObjectOutputStream outputStream = null;
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        ServerSocket socket = new ServerSocket(1024);
        StoreDataBase dataBase = new StoreDataBase();
        while(true){
            Socket client = socket.accept();
            System.out.println("=======================================");
            System.out.println("Client connected " + client.getInetAddress());
            new Thread(new ServerHandler(client, dataBase));
        }
    }
}
