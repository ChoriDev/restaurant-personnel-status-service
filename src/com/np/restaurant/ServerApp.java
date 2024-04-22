package com.np.restaurant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.np.restaurant.user.User;

public class ServerApp {
    public static void main(String[] args) throws Exception {
        ServerApp serverApp = new ServerApp();
        serverApp.startApp();
    }

    private void startApp() {
        try {
            ServerSocket server = new ServerSocket(10001);
            System.out.println("접속을 기다립니다.");
            while (true) {
                Socket sock = server.accept();
                UserThread userThread = new UserThread(sock);
                userThread.start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

class UserThread extends Thread {
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;

    public UserThread(Socket socket) {
        this.socket = socket;

        try {
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @Override
    public void run() {
        System.out.println("새로운 사용자가 로그인을 시도합니다.");
        login();
    }

    public void login() {
        String name = null;
        try {
            name = br.readLine();
        } catch (IOException e) {
            System.err.println(e);
        }
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.err.println(e);
        }
        User user = new User(name);
        try {
            oos.writeObject(user);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}