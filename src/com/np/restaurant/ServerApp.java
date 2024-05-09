package com.np.restaurant;

import java.io.*;
import java.net.*;
import java.util.*;

import com.np.restaurant.chatting.ServerChat;
import com.np.restaurant.restaurants.Restaurants;
import com.np.restaurant.restaurants.Restaurant;
import com.np.restaurant.user.User;

public class ServerApp {
    private static List<User> loggedInUsers = new ArrayList<User>();
    private static HashMap<User, ObjectOutputStream> chattingUsers = new HashMap<User, ObjectOutputStream>();
    private static List<Restaurant> restaurants = new Restaurants().getRestaurants();

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

    public static synchronized void addChattingUser(User user, ObjectOutputStream objectOutputStream) {
        chattingUsers.put(user, objectOutputStream);
        System.out.println("새로운 사용자가 채팅방에 참여합니다: " + user.getName());
    }

    public static synchronized void removeChattingUser(User user) {
        chattingUsers.remove(user);
        System.out.println("사용자가 채팅방을 나갔습니다: " + user.getName());
    }

    public static synchronized HashMap<User, ObjectOutputStream> getChattingUsers() {
        return new HashMap<User, ObjectOutputStream>(chattingUsers);
    }

    public static synchronized List<Restaurant> getRestaurants() {
        return new ArrayList<Restaurant>(restaurants);
    }
}

class ClientThread extends Thread {
    private Socket clientSocket;
    private BufferedReader reader;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private User user;

    public ClientThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("클라이언트 초기화 오류: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                String command = (String) objectInputStream.readObject();
                switch (command) {
                    case "로그인":
                        login();
                        break;
                    case "로그아웃":
                        logout();
                        break;
                    case "음식점 조회":
                        showRestaurants();
                        break;
                    case "채팅":
                        chat();
                        break;
                    case "종료":
                        terminateApp();
                        return; // 스레드 종료
                    default:
                        break;
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    private void login() {
        SuccessFlag successFlag;
        try {
            User user = (User) objectInputStream.readObject();
            System.out.println("클라이언트에서 받은 사용자 이름: " + user.getName());
            List<User> loggedInUsers = ServerApp.getLoggedInUsers();
            for (User loggedInUser : loggedInUsers) {
                String loggedInUsername = loggedInUser.getName();
                if (loggedInUsername.equals(user.getName())) {
                    successFlag = new SuccessFlag(false);
                    objectOutputStream.writeObject(successFlag);
                    objectOutputStream.flush();
                    objectOutputStream.reset();
                    return;
                }
            }
            successFlag = new SuccessFlag(true);
            objectOutputStream.writeObject(successFlag);
            objectOutputStream.flush();
            objectOutputStream.reset();
            this.user = user;
            ServerApp.addUser(user); // 사용자 추가

        } catch (Exception e) {
            System.err.println("사용자 객체 전송 오류: " + e.getMessage());
        }
    }

    private void logout() {
        try {
            ServerApp.removeUser(user);
            user = null;
            SuccessFlag successFlag = new SuccessFlag(true);
            objectOutputStream.writeObject(successFlag);
            objectOutputStream.flush();
            objectOutputStream.reset();
        } catch (Exception e) {
            System.err.println("로그아웃 오류: " + e.getMessage());
        }
    }

    private void chat() {
        ServerApp.addChattingUser(user, objectOutputStream);
        ServerChat serverChat = new ServerChat(objectInputStream, objectOutputStream);
        serverChat.start();
        ServerApp.removeChattingUser(user);
    }

    private void showRestaurants() {
        try {
            objectOutputStream.writeObject(ServerApp.getRestaurants());
            objectOutputStream.flush();
            objectOutputStream.reset();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void terminateApp() {
        try {
            clientSocket.close();
            reader.close();
            objectInputStream.close();
            objectOutputStream.close();
        } catch (IOException e) {
            System.err.println("애플리케이션 종료 오류: " + e.getMessage());
        }
        System.out.println(clientSocket.getInetAddress().getHostAddress() + "에서 클라이언트가 떠났습니다.");
    }
}
