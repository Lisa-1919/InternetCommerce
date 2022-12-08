package com.example.internetcommerce.server;

import com.example.internetcommerce.database.StoreDataBase;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Server {

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        ServerSocket sock = new ServerSocket(1024);
        while (true) {
            Socket client = sock.accept();
            System.out.println(LocalDateTime.now() + "   Клиент " + client.getInetAddress().toString() + " подключился");

            new Thread(new ServerHandler(client, StoreDataBase.getInstance())).start();
        }
    }
}
