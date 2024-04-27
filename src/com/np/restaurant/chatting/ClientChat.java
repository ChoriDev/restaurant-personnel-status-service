package com.np.restaurant.chatting;

import java.io.BufferedReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
        InputThread inputThread = new InputThread(objectInputStream);
        inputThread.start();
        Message message = new Message(user, null);
        String line = null;
        try {
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
                if (objectInputStream != null) {
                    Message message = (Message) objectInputStream.readObject(); // 여기서 블로킹 발생
                    User sender = message.getSender();
                    String content = message.getContent();
                    if (content != null) {
                        System.out.println(sender.getName() + ": " + content);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}