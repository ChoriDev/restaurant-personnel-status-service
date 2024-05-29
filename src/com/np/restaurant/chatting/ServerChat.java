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

public class ServerChat {
    private final User user;
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream selfObjectOutputStream;
    private final List<Restaurant> restaurants;

    public ServerChat(User user, ObjectInputStream objectInputStream, ObjectOutputStream selfObjectOutputStream, List<Restaurant> restaurants) {
        this.user = user;
        this.objectInputStream = objectInputStream;
        this.selfObjectOutputStream = selfObjectOutputStream;
        this.restaurants = restaurants;
        // 채팅 수신, broadcast 이후 관심도 로직을 실행하기 위하여 restaurants 정보를 ServerChat Class에 추가하였음.
    }

    public void start() {
        try {
            Message message;
            broadcast(new Message(user, null, "joins for chatting."));
            sendUserList();
            while ((message = (Message) objectInputStream.readObject()) != null) {
                String content = message.getContent();
                System.out.println(user.getName() + ": " + content);
                if ("quit".equals(content)) {
                    sendQuit(message);
                    break;
                } else if (content.startsWith("/to")) {
                    unicast(message);
                } else {
                    broadcast(message);
                }
                interest(); /// 채팅메시지 수신 & 송신 이후 관심도 로직 실행
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error in ServerChat: " + e.getMessage());
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
                restaurant.changeInterestCount(1);
                new Thread(() -> {
                    try {
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