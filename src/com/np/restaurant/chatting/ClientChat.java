package com.np.restaurant.chatting;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import com.np.restaurant.user.User;

public class ClientChat {
    private User user;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private BufferedReader keyboard;

    public ClientChat(
            User user,
            ObjectInputStream objectInputStream,
            ObjectOutputStream objectOutputStream,
            BufferedReader keyboard) {
        this.user = user;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
        this.keyboard = keyboard;
    }

    public void start() {
        System.out.println("다른 사람에게 메시지를 보냅니다.");
        Message message = new Message(user, null);
        try {
            String line = null;
            InputThread inputThread = new InputThread(objectInputStream);
            inputThread.start();
            while ((line = keyboard.readLine()) != null) {
                message.setContent(line);
                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
                objectOutputStream.reset();
                if (line.equals("quit")) {
                    inputThread.interrupt();
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

class InputThread extends Thread {
    private ObjectInputStream objectInputStream = null;

    public InputThread(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // 직렬화된 객체를 블로킹 모드로 읽기
                if (objectInputStream != null) {
                    Message message = (Message) objectInputStream.readObject(); // 여기서 블로킹 발생
                    String content = message.getContent();
                    if (content != null) {
                        System.out.println(content);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}