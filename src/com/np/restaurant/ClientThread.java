package com.np.restaurant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.np.restaurant.chatting.ServerChat;
import com.np.restaurant.restaurants.Restaurant;
import com.np.restaurant.user.PeopleDelta;
import com.np.restaurant.user.User;

class ClientThread extends Thread {
    private static final String LOGIN = "로그인";
    private static final String LOGOUT = "로그아웃";
    private static final String FETCH_RESTAURANTS = "음식점 조회";
    private static final String SEARCH_RESTAURANT = "음식점 검색";
    private static final String CHAT = "채팅";
    private static final String PEOPLE = "인원";
    private static final String INTEREST = "관심도";
    private static final String TERMINATE = "종료";

    private final Socket clientSocket;
    private final BufferedReader reader;
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;
    private User user;
    private final List<Restaurant> restaurants;

    public ClientThread(Socket clientSocket, List<Restaurant> restaurants) throws IOException {
        this.clientSocket = clientSocket;
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        this.objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        this.restaurants = restaurants;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String command = (String) objectInputStream.readObject();
                switch (command) {
                    case LOGIN:
                        login();
                        break;
                    case LOGOUT:
                        logout();
                        break;
                    case FETCH_RESTAURANTS:
                        fetchRestaurants();
                        break;
                    case SEARCH_RESTAURANT:
                        searchRestaurant();
                        break;
                    case CHAT:
                        chat();
                        break;
                    case PEOPLE:
                        people();
                        break;
                    case INTEREST:
                        interest();
                        break;
                    case TERMINATE:
                        terminateApp();
                        return;
                    default:
                        System.err.println("알 수 없는 명령어: " + command);
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("명령어 수신 오류: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void login() {
        try {
            User user = (User) objectInputStream.readObject();
            System.out.println("클라이언트에서 받은 사용자 이름: " + user.getName());
            if (ServerApp.getLoggedInUsers().stream().anyMatch(u -> u.getName().equals(user.getName()))) {
                sendSuccessFlag(false);
            } else {
                sendSuccessFlag(true);
                this.user = user;
                ServerApp.addUser(user);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("로그인 오류: " + e.getMessage());
        }
    }

    private void logout() {
        try {
            ServerApp.removeUser(user);
            user = null;
            sendSuccessFlag(true);
        } catch (IOException e) {
            System.err.println("로그아웃 오류: " + e.getMessage());
        }
    }

    private void chat() {
        try {
            ServerApp.addChattingUser(user, objectOutputStream);
            ServerChat serverChat = new ServerChat(user, objectInputStream, objectOutputStream);
            serverChat.start();
        } finally {
            ServerApp.removeChattingUser(user);
        }
    }

    private void people() {
        List<Restaurant> restaurants = ServerApp.getRestaurants();
        Boolean successFlag = false;
        String restaurantName = null;
        String status = null;
        Restaurant restaurantObject = null;
        PeopleDelta peopleDelta = null;
        // 음식점명 수신 및 확인
        try {
            restaurantName = (String) objectInputStream.readObject();
            System.out.println("음식점명 수신: " + restaurantName);
            for (Restaurant restaurant : restaurants) {
                if (restaurant.getName().contains(restaurantName)) {
                    successFlag = true;
                    restaurantObject = restaurant;
                    break;
                }
            }
            objectOutputStream.writeObject(successFlag);
            objectOutputStream.flush();
            objectOutputStream.reset();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("음식점명 확인 오류: " + e.getMessage());
        }
        // 인원 변경
        try {
            peopleDelta = (PeopleDelta) objectInputStream.readObject();
            System.out.println("Delta: " + peopleDelta.getGoingPeopleDelta() + ", " + peopleDelta.getEatingPeopleDelta());
            Objects.requireNonNull(restaurantObject).changePeopleInfo(peopleDelta.getGoingPeopleDelta(), peopleDelta.getEatingPeopleDelta());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("인원 변경 오류: " + e.getMessage());
        }
    }

    private void fetchRestaurants() {
        try {
            sendObject(ServerApp.getRestaurants());
        } catch (IOException e) {
            System.err.println("음식점 목록 전송 오류: " + e.getMessage());
        }
    }

    private void searchRestaurant() {
        try {
            String searchWord = (String) objectInputStream.readObject();
            List<Restaurant> result = new ArrayList<>();
            for (Restaurant restaurant : ServerApp.getRestaurants()) {
                if (restaurant.getName().contains(searchWord)) {
                    result.add(restaurant);
                }
            }
            sendObject(result);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("음식점 검색 오류: " + e.getMessage());
        }
    }

    private void terminateApp() {
        cleanup();
        System.out.println(clientSocket.getInetAddress().getHostAddress() + "에서 클라이언트가 떠났습니다.");
    }

    private void sendSuccessFlag(boolean flag) throws IOException {
        objectOutputStream.writeObject(new SuccessFlag(flag));
        objectOutputStream.flush();
        objectOutputStream.reset();
    }

    private void sendObject(Object obj) throws IOException {
        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        objectOutputStream.reset();
    }

    private void cleanup() {
        try {
            if (reader != null)
                reader.close();
            if (objectInputStream != null)
                objectInputStream.close();
            if (objectOutputStream != null)
                objectOutputStream.close();
            if (clientSocket != null)
                clientSocket.close();
        } catch (IOException e) {
            System.err.println("리소스 정리 오류: " + e.getMessage());
        }
    }

    private void interest() {
        String restaurantName = null;
        try {
            restaurantName = (String) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
        }
        if (restaurantName == null) {
            return;
        }
        for (Restaurant restaurant : restaurants) {
            if (restaurantName.equals(restaurant.getName())) {
                new Thread(() -> {
                    try {
                        restaurant.changeInterestCount(1);
                        Thread.sleep(Constants.INTEREST_MAINTAIN_IN_MILLIS);
                        restaurant.changeInterestCount(-1);
                    } catch (InterruptedException e) {
                        System.err.println(e);
                    }
                }).start();
                break;
            }
        }
    }
}