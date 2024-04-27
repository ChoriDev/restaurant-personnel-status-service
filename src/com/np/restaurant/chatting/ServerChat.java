package com.np.restaurant.chatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.np.restaurant.ServerApp;
import com.np.restaurant.user.User;

public class ServerChat {
    private ObjectInputStream objectInputStream;

    public ServerChat(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
    }

    public void start() {
        Message message;
        try {
            while ((message = (Message) objectInputStream.readObject()) != null) {
                String content = message.getContent();
                if (content.equals("quit")) {
                    break;
                }
                System.out.println("클라이언트가 보낸 메시지: " + content);
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
}