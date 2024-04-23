package com.np.restaurant;

import java.io.*;
import java.net.*;
import java.util.*;

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
    private ObjectOutputStream objectOutputStream;

    public ClientThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("클라이언트 핸들러 초기화 오류: " + e.getMessage());
        }
    }

    /*
     * @Override
     * public void run() {
     * try {
     * String username = reader.readLine();
     * System.out.println("클라이언트에서 받은 사용자 이름: " + username);
     * User user = new User(username);
     * ServerApp.addUser(user); // 사용자 추가
     * sendUserObject(user);
     * } catch (IOException e) {
     * System.err.println("클라이언트와의 통신 오류: " + e.getMessage());
     * } finally {
     * try {
     * clientSocket.close();
     * } catch (IOException e) {
     * System.err.println("클라이언트 소켓 종료 오류: " + e.getMessage());
     * }
     * }
     * }
     */

    @Override
    public void run() {
        try {
            int command = Integer.parseInt(reader.readLine());
            while (command != -1) {
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
                        break;
                    case -1:
                        // 종료
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("클라이언트와의 통신 오류: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("클라이언트 소켓 종료 오류: " + e.getMessage());
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
        } catch (IOException e) {
            System.err.println("사용자 객체 전송 오류: " + e.getMessage());
        }
    }
}
