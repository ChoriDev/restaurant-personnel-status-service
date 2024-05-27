package com.np.restaurant;

import com.np.restaurant.restaurants.Restaurant;
import com.np.restaurant.ui.LoginScreen;
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

    public static void main(String[] args) {
        new LoginScreen(new ClientApp());
    }
}