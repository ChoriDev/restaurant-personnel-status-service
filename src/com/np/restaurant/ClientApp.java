package com.np.restaurant;

import com.np.restaurant.restaurants.Restaurant;
import com.np.restaurant.ui.LoginScreen;
import com.np.restaurant.user.PeopleDelta;
import com.np.restaurant.user.User;
import com.np.restaurant.chatting.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class ClientApp {
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private User user;

    public ClientApp() {
        try {
            socket = new Socket("localhost", 10001);
            in = socket.getInputStream();
            out = socket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(out);
            objectInputStream = new ObjectInputStream(in);
        } catch (IOException e) {
            System.err.println("클라이언트 초기화 오류: " + e.getMessage());
        }
    }

    public boolean login(String name) {
        User user = new User(name);
        try {
            sendCommand("로그인");
            objectOutputStream.writeObject(user);
            objectOutputStream.flush();
            objectOutputStream.reset();

            SuccessFlag successFlag = (SuccessFlag) objectInputStream.readObject();
            if (successFlag.getFlag()) {
                this.user = user;
                return true;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("로그인 오류: " + e.getMessage());
        }
        return false;
    }

    public void logout() {
        try {
            sendCommand("로그아웃");
            SuccessFlag successFlag = (SuccessFlag) objectInputStream.readObject();
            if (successFlag.getFlag()) {
                user = null;
            }
            sendCommand("종료");
        } catch (Exception e) {
            System.err.println("로그아웃 오류: " + e.getMessage());
        }
    }

    public List<Restaurant> fetchRestaurants() {
        try {
            sendCommand("음식점 조회");
            return (List<Restaurant>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("음식점 조회 오류: " + e.getMessage());
            return null;
        }
    }

    public List<Restaurant> searchRestaurant(String searchWord) {
        try {
            sendCommand("음식점 검색");
            objectOutputStream.writeObject(searchWord);
            objectOutputStream.flush();
            objectOutputStream.reset();

            return (List<Restaurant>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("음식점 검색 오류: " + e.getMessage());
            return null;
        }
    }

    public void chat() {
        sendCommand("채팅");
    }

    public void sendChatMessage(String content) {
        try {
            Message message = new Message(user, null, content);
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            objectOutputStream.reset();
        } catch (IOException e) {
            System.err.println("채팅 메시지 전송 오류: " + e.getMessage());
        }
    }

    public Message receiveChatMessage() throws IOException, ClassNotFoundException {
        return (Message) objectInputStream.readObject();
    }

    private void sendCommand(String command) {
        try {
            objectOutputStream.writeObject(command);
            objectOutputStream.flush();
            objectOutputStream.reset();
        } catch (IOException e) {
            System.err.println("명령어 전송 오류: " + e.getMessage());
        }
    }

    public void people(String newRestaurantName, int newStatus) {
        sendCommand("인원");
        int prevStatus = 0;
        String prevRestaurantName = null;
        Boolean hasRestaurantChanged = false;
        try {
            prevStatus = user.getStatus();
            prevRestaurantName = user.getRestaurant();
            System.out.println("상태: " + prevStatus + " -> " + newStatus);
            System.out.println("음식점: " + prevRestaurantName + " -> " + newRestaurantName);
            
            // 음식점 변경여부 확인
            if (prevStatus == User.DEFAULT || newRestaurantName.equals(prevRestaurantName))
                hasRestaurantChanged = false;
            else
                hasRestaurantChanged = true;
            objectOutputStream.writeObject(hasRestaurantChanged);
            objectOutputStream.flush();
            objectOutputStream.reset();

            if (newRestaurantName.equals(prevRestaurantName) && prevStatus == newStatus) { // 취소하는 경우
                user.setStatus(User.DEFAULT);
                user.setRestaurant(null);
            } else { // 새로운 음식 및 상태 갱신
                user.setStatus(newStatus);
                user.setRestaurant(newRestaurantName);
            }

            if (!hasRestaurantChanged) { // Default 혹은 음식점 변화 X
                if (prevStatus == User.DEFAULT && newStatus == User.GOING) {
                    objectOutputStream.writeObject(new PeopleDelta(newRestaurantName, 1, 0));
                } else if (prevStatus == User.DEFAULT && newStatus == User.EATING) {
                    objectOutputStream.writeObject(new PeopleDelta(newRestaurantName, 0, 1));
                } else if (prevStatus == User.GOING && newStatus == User.GOING) {
                    objectOutputStream.writeObject(new PeopleDelta(newRestaurantName, -1, 0));
                } else if (prevStatus == User.GOING && newStatus == User.EATING) {
                    objectOutputStream.writeObject(new PeopleDelta(newRestaurantName, -1, 1));
                } else if (prevStatus == User.EATING && newStatus == User.GOING) {
                    objectOutputStream.writeObject(new PeopleDelta(newRestaurantName, 1, -1));
                } else if (prevStatus == User.EATING && newStatus == User.EATING) {
                    objectOutputStream.writeObject(new PeopleDelta(newRestaurantName, 0, -1));
                }
            }
            else { // 음식점 변화
                if (prevStatus == User.GOING && newStatus == User.GOING) {
                    objectOutputStream.writeObject(new PeopleDelta(prevRestaurantName, -1, 0));
                    objectOutputStream.writeObject(new PeopleDelta(newRestaurantName, 1, 0));
                } else if (prevStatus == User.GOING && newStatus == User.EATING) {
                    objectOutputStream.writeObject(new PeopleDelta(prevRestaurantName, -1, 0));
                    objectOutputStream.writeObject(new PeopleDelta(newRestaurantName, 0, 1));
                } else if (prevStatus == User.EATING && newStatus == User.GOING) {
                    objectOutputStream.writeObject(new PeopleDelta(prevRestaurantName, 0, -1));
                    objectOutputStream.writeObject(new PeopleDelta(newRestaurantName, 1, 0));
                } else if (prevStatus == User.EATING && newStatus == User.EATING) {
                    objectOutputStream.writeObject(new PeopleDelta(prevRestaurantName, 0, -1));
                    objectOutputStream.writeObject(new PeopleDelta(newRestaurantName, 0, 1));
                }

            }
            objectOutputStream.flush();
            objectOutputStream.reset();
        } catch (IOException | NullPointerException e) {
            System.err.println("음식점명 전송 오류: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new LoginScreen(new ClientApp());
    }
}