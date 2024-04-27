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
    private static HashMap<User, PrintWriter> chattingUsers = new HashMap<User, PrintWriter>();
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

    public static List<Restaurant> getRestaurants() {
        return restaurants;
    }
}

class ClientThread extends Thread {
    private Socket clientSocket;
    private InputStream in;
    private OutputStream out;
    private BufferedReader reader;
    private PrintWriter writer;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private User user;

    public ClientThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            writer = new PrintWriter(new OutputStreamWriter(out));
            objectInputStream = new ObjectInputStream(in);
            objectOutputStream = new ObjectOutputStream(out);
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
                        // 로그아웃
                        logout();
                        break;
                    case 2:
                        // 음식점 조회
                        showRestaurants();
                        break;
                    case 3:
                        // 채팅
                        chat();
                        break;
                    case 4:
                        // 음식점 선택
                        changeTargetRestaurant();
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
                writer.close();
                reader.close();
                objectInputStream.close();
                objectOutputStream.close();
                in.close();
                out.close();
                clientSocket.close();
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

    private void logout() {
        try {
            ServerApp.removeUser(user);
            user = null;
            writer.println("success");
            writer.flush();
        } catch (Exception e) {
            System.err.println("로그아웃 오류: " + e.getMessage());
        }
    }

    private void chat() {
        ServerApp.addChattingUser(user, writer);
        ServerChat serverChat = new ServerChat(user, reader);
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

    // TODO 만드는 중
    private void changeTargetRestaurant() {
        String oldTargetRestaurant = user.getTargetRestaurant();
        String oldDiningAt = user.getDiningAt();
        User newUser;
        try {
            newUser = (User) objectInputStream.readObject();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void terminateApp() {
        try {
            writer.close();
            reader.close();
            objectOutputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("애플리케이션 종료 오류: " + e.getMessage());
        }
        System.out.println(clientSocket.getInetAddress().getHostAddress() + "에서 클라이언트가 떠났습니다.");
    }
}
