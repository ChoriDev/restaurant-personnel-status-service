package com.np.restaurant;

import java.io.*;
import java.net.*;
import java.util.*;

import com.np.restaurant.chatting.ChatServer;
import com.np.restaurant.user.User;

public class ServerApp {
    private static List<User> loggedInUsers = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(10001)) {
            System.out.println("서버가 시작되었습니다. 클라이언트의 접속을 기다립니다...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("새로운 클라이언트가 접속했습니다.");
                new ClientThread(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("서버 오류: " + e.getMessage());
        }
    }

    static synchronized void addUser(User user) {
        loggedInUsers.add(user);
        System.out.println("새로운 사용자가 로그인했습니다: " + user.getName());
    }

    static synchronized void removeUser(User user) {
        loggedInUsers.remove(user);
        System.out.println("사용자가 로그아웃했습니다: " + user.getName());
    }

    static synchronized List<User> getLoggedInUsers() {
        return new ArrayList<>(loggedInUsers);
    }
}

class ClientThread extends Thread {
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private ObjectOutputStream objectOutputStream;

    public ClientThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("클라이언트 핸들러 초기화 오류: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                int command = Integer.parseInt(reader.readLine());
                switch (command) {
                    case 0:
                        // 로그인
                        login();
                        break;
                    case 1:
                        // 현황 조회
                        break;
                    case 2:
                        // 채팅
                        chat();
                        break;
                    case -1:
                        // 종료
                        terminateApp();
                        return; // 스레드 종료
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("클라이언트와의 통신 오류: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                writer.close();
                reader.close();
                objectOutputStream.close();
            } catch (IOException e) {
                System.err.println("애플리케이션 종료 오류: " + e.getMessage());
            }
        }
    }

    private void login() {
        try {
            String username = reader.readLine();
            System.out.println("클라이언트에서 받은 사용자 이름: " + username);
            User user = new User(username);
            ServerApp.addUser(user); // 사용자 추가
            objectOutputStream.writeObject(user);
            objectOutputStream.flush();
            objectOutputStream.reset();
        } catch (IOException e) {
            System.err.println("사용자 객체 전송 오류: " + e.getMessage());
        }
    }

    private void chat() {
        ChatServer chatServer = new ChatServer(reader, writer);
        chatServer.start();
    }

    private void terminateApp() {
        try {
            clientSocket.close();
            writer.close();
            reader.close();
            objectOutputStream.close();
        } catch (IOException e) {
            System.err.println("애플리케이션 종료 오류: " + e.getMessage());
        }
        System.out.println("이용해주셔서 고맙습니다.");
    }
}
