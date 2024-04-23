package com.np.restaurant.chatting;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.np.restaurant.ServerApp;
import com.np.restaurant.user.User;

public class ServerChat {
    private User user;
    private BufferedReader reader;

    public ServerChat(User user, BufferedReader reader) {
        this.user = user;
        this.reader = reader;
    }

    public void start() {
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.equals("quit")) {
                    break;
                }
                System.out.println("클라이언트가 보낸 메시지: " + line);
                broadcast(user.getName() + ": " + line);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void broadcast(String msg) {
        HashMap<User, PrintWriter> chattingUsers = ServerApp.getChattingUsers();
        synchronized (chattingUsers) {
            Collection collection = chattingUsers.values();
            Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                PrintWriter pw = (PrintWriter) iterator.next();
                pw.println(msg);
                pw.flush();
            }
        }
    }
}