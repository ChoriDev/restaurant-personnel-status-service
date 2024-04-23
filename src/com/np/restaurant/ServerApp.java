package com.np.restaurant;

import java.io.*;
import java.net.*;
import java.util.*;

import com.np.restaurant.chatting.ServerChat;
import com.np.restaurant.user.User;

public class ServerApp {
    private static List<User> loggedInUsers = new ArrayList<User>();
    private static HashMap<User, PrintWriter> chattingUsers = new HashMap<User, PrintWriter>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(10001)) {
            System.out.println("서버가 시작되었습니다. 클라이언트의 접속을 기다립니다...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                InetAddress inetAddress = clientSocket.getInetAddress();
                System.out.println(inetAddress.getHostAddress() + "에서 클라이언트가 접속했습니다.");
                new ClientThread(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("서버 오류: " + e.getMessage());
        }
    }

    public static synchronized void addUser(User user) {
        loggedInUsers.add(user);
        System.out.println("새로운 사용자가 로그인했습니다: " + user.getName());
    }

    public static synchronized void removeUser(User user) {
        loggedInUsers.remove(user);
        System.out.println("사용자가 로그아웃했습니다: " + user.getName());
    }

    public static synchronized List<User> getLoggedInUsers() {
        return new ArrayList<User>(loggedInUsers);
    }

    public static synchronized void addChattingUser(User user, PrintWriter writer) {
        chattingUsers.put(user, writer);
        System.out.println("새로운 사용자가 채팅방에 참여합니다: " + user.getName());
    }

    public static synchronized void removeChattingUser(User user) {
        chattingUsers.remove(user);
        System.out.println("사용자가 채팅방을 나갔습니다: " + user.getName());
    }

    public static synchronized HashMap<User, PrintWriter> getChattingUsers() {
        return new HashMap<User, PrintWriter>(chattingUsers);
    }
}

class ClientThread extends Thread {
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private ObjectOutputStream objectOutputStream;
    private User user;

    public ClientThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("클라이언트 초기화 오류: " + e.getMessage());
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
            this.user = new User(username);
            ServerApp.addUser(user); // 사용자 추가
            objectOutputStream.writeObject(user);
            objectOutputStream.flush();
            objectOutputStream.reset();
        } catch (IOException e) {
            System.err.println("사용자 객체 전송 오류: " + e.getMessage());
        }
    }

    private void chat() {
        ServerApp.addChattingUser(user, writer);
        ServerChat serverChat = new ServerChat(user, reader);
        serverChat.start();
        ServerApp.removeChattingUser(user);
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
        System.out.println(clientSocket.getInetAddress().getHostAddress() + "에서 클라이언트가 떠났습니다.");
    }
}
