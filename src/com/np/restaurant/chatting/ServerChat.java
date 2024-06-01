package com.np.restaurant.chatting;

import com.np.restaurant.Constants;
import com.np.restaurant.ServerApp;
import com.np.restaurant.restaurants.Restaurant;
import com.np.restaurant.user.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ServerChat {
    private final User user;
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream selfObjectOutputStream;
    private List<Restaurant> restaurants;

    public ServerChat(User user, ObjectInputStream objectInputStream, ObjectOutputStream selfObjectOutputStream) {
        this.user = user;
        this.objectInputStream = objectInputStream;
        this.selfObjectOutputStream = selfObjectOutputStream;
        this.restaurants = ServerApp.getRestaurants();
    }

    public void start() {
        try {
            Message message;
            broadcast(new Message(user, null, "joins for chatting."));
            sendUserList();
            while ((message = (Message) objectInputStream.readObject()) != null) {
                String content = message.getContent();
                System.out.println(user.getName() + ": " + content);
                if (content.startsWith("interest:")) {
                    handleInterestMessage(content.substring(9));
                } else if ("quit".equals(content)) {
                    sendQuit(message);
                    break;
                } else if (content.startsWith("/to")) {
                    unicast(message);
                } else {
                    broadcast(message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error in ServerChat: " + e.getMessage());
        }
    }

    private void handleInterestMessage(String restaurantName) {
        for (Restaurant restaurant : restaurants) {
            if (restaurant.getName().equals(restaurantName)) {
                // 관심도 1 증가시키는 코드
                restaurant.incrementInterest();
                System.out.println("레스토랑 관심도 증가: " + restaurantName);

                // 일정 시간 후 관심도 1 감소시키는 타이머 설정
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        restaurant.decrementInterest();
                        System.out.println("레스토랑 관심도 감소: " + restaurantName);
                    }
                }, Constants.INTEREST_MAINTAIN_IN_MILLIS); // 10분 후 실행
            }
        }
    }

    public void broadcast(Message message) {
        HashMap<User, ObjectOutputStream> chattingUsers = ServerApp.getChattingUsers();
        synchronized (chattingUsers) {
            for (ObjectOutputStream objectOutputStream : chattingUsers.values()) {
                try {
                    objectOutputStream.writeObject(message);
                    objectOutputStream.flush();
                } catch (IOException e) {
                    System.err.println("Error broadcasting message: " + e.getMessage());
                }
            }
        }
    }

    // 특정 사용자에게 개인 메시지 보내기
    public void unicast(Message message) {
        String[] tokens = message.getContent().split(" ", 3);
        if (tokens.length != 3) {
            System.err.println("Invalid private message format");
            return;
        }
        String receiverName = tokens[1];
        String privateContent = tokens[2];
        User receiver = null;
        ObjectOutputStream receiverOutputStream = null;
        Message newMessage = null;

        HashMap<User, ObjectOutputStream> chattingUsers = ServerApp.getChattingUsers();

        for (User user : chattingUsers.keySet()) {
            if (user.getName().equals(receiverName)) {
                receiver = user;
                break;
            }
        }

        if (receiver == null) {
            System.err.println("User " + receiverName + " not found");
            newMessage = new Message(user, null, "Uesr " + receiverName + " is not existed");
            try {
                selfObjectOutputStream.writeObject(newMessage);
                selfObjectOutputStream.flush();
            } catch (IOException e) {
                System.err.println("Error sending self message: " + e.getMessage());
            }
            return;
        }

        receiverOutputStream = chattingUsers.get(receiver);

        if (receiverOutputStream == null) {
            System.err.println("Output stream for user " + receiverName + " not found");
            return;
        }

        newMessage = new Message(user, receiver, "(private message) " + privateContent);

        try {
            receiverOutputStream.writeObject(newMessage);
            receiverOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Error sending private message to " + receiverName + ": " + e.getMessage());
        }

        try {
            selfObjectOutputStream.writeObject(newMessage);
            selfObjectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Error sending self message: " + e.getMessage());
        }
    }

    public void sendQuit(Message message) {
        try {
            broadcast(new Message(user, null, "leaves chatting."));
            selfObjectOutputStream.writeObject(message);
            selfObjectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Error sending quit message: " + e.getMessage());
        }
    }

    // 현재 채팅방에 있는 사용자 목록 전송
    public void sendUserList() {
        HashMap<User, ObjectOutputStream> chattingUsers = ServerApp.getChattingUsers();
        synchronized (chattingUsers) {
            StringBuilder userListMessage = new StringBuilder("users in chatting: ");
            for (User user : chattingUsers.keySet()) {
                userListMessage.append(user.getName()).append(", ");
            }
            userListMessage.delete(userListMessage.length() - 2, userListMessage.length()); // 마지막 쉼표 및 공백 제거
            broadcast(new Message(user, null, userListMessage.toString()));
        }
    }
}