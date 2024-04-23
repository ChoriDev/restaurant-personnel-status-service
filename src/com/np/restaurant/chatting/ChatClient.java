package com.np.restaurant.chatting;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class ChatClient {
    private BufferedReader reader;
    private PrintWriter writer;
    private BufferedReader keyboard;

    public ChatClient(BufferedReader reader, PrintWriter writer, BufferedReader keyboard) {
        this.reader = reader;
        this.writer = writer;
        this.keyboard = keyboard;
    }

    public void start() {
        try {
            String line = null;
            String echo = null;
            while ((line = keyboard.readLine()) != null) {
                writer.println(line);
                writer.flush();
                echo = reader.readLine();
                System.out.println("서버에게 받은 메시지: " + echo);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}