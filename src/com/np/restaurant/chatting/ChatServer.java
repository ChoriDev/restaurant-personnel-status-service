package com.np.restaurant.chatting;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class ChatServer {
    private BufferedReader reader;
    private PrintWriter writer;

    public ChatServer(BufferedReader reader, PrintWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public void start() {
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.equals("quit")) {
                    break;
                }
                System.out.println("클라이언트가 보낸 메시지: " + line);
                writer.println(line);
                writer.flush();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}