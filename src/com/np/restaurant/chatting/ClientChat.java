package com.np.restaurant.chatting;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class ClientChat {
    private BufferedReader reader;
    private PrintWriter writer;
    private BufferedReader keyboard;

    public ClientChat(BufferedReader reader, PrintWriter writer, BufferedReader keyboard) {
        this.reader = reader;
        this.writer = writer;
        this.keyboard = keyboard;
    }

    public void start() {
        System.out.println("다른 사람에게 메시지를 보냅니다.");
        try {
            String line = null;
            String echo = null;
            while ((line = keyboard.readLine()) != null) {
                writer.println(line);
                writer.flush();
                if (line.equals("quit")) {
                    break;
                }
                echo = reader.readLine();
                System.out.println("받은 메시지: " + echo);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}