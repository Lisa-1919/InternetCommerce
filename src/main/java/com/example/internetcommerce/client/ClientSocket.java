package com.example.internetcommerce.client;

import java.io.*;
import java.net.Socket;

public class ClientSocket {
    private BufferedReader reader;
    private BufferedWriter writer;

    public ClientSocket(BufferedReader reader, BufferedWriter writer) throws IOException {
        this.reader = reader;
        this.writer = writer;
    }

    public void sendString (String str) throws IOException {
        writer.write(str);
        writer.flush();
    }

    public void sendInt (int i) throws IOException {
        writer.write(i);
        writer.flush();
    }

    public String getString () throws IOException {
        return reader.readLine();
    }

    public int getInt () throws IOException{
        return Integer.parseInt(reader.readLine());
    }
}
