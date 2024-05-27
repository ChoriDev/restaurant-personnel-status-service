package com.np.restaurant;

import com.np.restaurant.restaurants.Restaurants;

import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.np.restaurant.restaurants.Restaurant;
import com.np.restaurant.user.PeopleDelta;
import com.np.restaurant.user.User;

public class ServerApp {
    private static final List<User> loggedInUsers = new ArrayList<>();
    private static final HashMap<User, ObjectOutputStream> chattingUsers = new HashMap<>();
    private static final List<Restaurant> restaurants = new Restaurants().getRestaurants();

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
        return new ArrayList<>(loggedInUsers);
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
        return new HashMap<>(chattingUsers);
    }

    public static synchronized List<Restaurant> getRestaurants() {
        return new ArrayList<>(restaurants);
    }
}