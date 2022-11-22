package com.example.internetcommerce.server;

import com.example.internetcommerce.database.StoreDataBase;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Server {

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        ServerSocket sock = new ServerSocket(1024);
        StoreDataBase dataBase = new StoreDataBase();
        while (true) {
            Socket client = sock.accept();
            System.out.println("=======================================");
            System.out.println("Client connected");

            new Thread(new ServerHandler(client, dataBase)).start();
        }
    }
}
