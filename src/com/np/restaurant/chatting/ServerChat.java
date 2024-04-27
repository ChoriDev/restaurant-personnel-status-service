package com.np.restaurant.chatting;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.np.restaurant.ServerApp;
import com.np.restaurant.user.User;

public class ServerChat {
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream selfObjectOutputStream;

    public ServerChat(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        this.objectInputStream = objectInputStream;
        this.selfObjectOutputStream = objectOutputStream;
    }

    public void start() {
        Message message;
        try {
            while ((message = (Message) objectInputStream.readObject()) != null) {
                String content = message.getContent();
                System.out.println("클라이언트가 보낸 메시지: " + content);
                if (content.equals("quit")) {
                    sendQuit(message);
                    break;
                }
                broadcast(message);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void broadcast(Message message) {
        // TODO HashMap을 ServerApp에서 직접 받지 않고 사용하는 방법 생각해보기
        HashMap<User, ObjectOutputStream> chattingUsers = ServerApp.getChattingUsers();
        synchronized (chattingUsers) {
            Collection collection = chattingUsers.values();
            Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                ObjectOutputStream objectOutputStream = (ObjectOutputStream) iterator.next();
                try {
                    objectOutputStream.writeObject(message);
                    objectOutputStream.flush();
                    objectOutputStream.reset();
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        }
    }

    public void sendQuit(Message message) {
        try {
            selfObjectOutputStream.writeObject(message);
            selfObjectOutputStream.flush();
            selfObjectOutputStream.reset();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}