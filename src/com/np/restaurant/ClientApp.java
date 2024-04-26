package com.np.restaurant;

import java.io.*;
import java.net.*;
import java.util.List;

import com.np.restaurant.chatting.ClientChat;
import com.np.restaurant.restaurants.Restaurant;
import com.np.restaurant.user.User;

public class ClientApp {
    private Socket socket;
    private BufferedReader reader;
    private ObjectInputStream objectInputStream;
    private PrintWriter writer;
    private BufferedReader keyboard;
    private User user;

    public ClientApp() {
        try {
            socket = new Socket("localhost", 10001);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            keyboard = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            System.err.println("클라이언트 초기화 오류: " + e.getMessage());
        }
    }

    public void start() {
        System.out.println("음식점 인원 현황 서비스입니다.");
        while (true) {
            System.out.println("사용할 수 있는 기능을 보려면 '전체 기능'를 입력해주세요.");
            try {
                String command = keyboard.readLine();
                commandSelection(command);
            } catch (IOException e) {
                System.err.println("입력 오류: " + e.getMessage());
            }
        }
    }

    private void commandSelection(String selectedCommand) {
        switch (selectedCommand) {
            case "전체 기능":
                showAllCommands();
                break;
            case "로그인":
                if (user == null) {
                    sendCommand(0);
                    login();
                } else {
                    System.out.println("이미 로그인되어 있습니다.");
                }
                break;
            case "로그아웃":
                if (user != null) {
                    sendCommand(1);
                    logout();
                } else {
                    System.out.println("로그인 후 이용할 수 있습니다.");
                }
                break;
            case "음식점 조회":
                if (user != null) {
                    sendCommand(2);
                    showRestaurants();
                } else {
                    System.out.println("로그인 후 이용할 수 있습니다.");
                }
                break;
            case "채팅":
                if (user != null) {
                    sendCommand(3);
                    chat();
                } else {
                    System.out.println("로그인 후 이용할 수 있습니다.");
                }
                break;
            case "종료":
                sendCommand(-1);
                terminateApp();
                break;
            default:
                System.out.println("없는 기능입니다.");
                break;
        }
    }

    private void sendCommand(int commandNumber) {
        try {
            writer.println(commandNumber);
            writer.flush();
        } catch (Exception e) {
            System.err.println("명령어 전송 오류: " + e.getMessage());
        }
    }

    private void showAllCommands() {
        System.out.println("사용할 수 있는 기능");
        System.out.println("로그인, 음식점 조회, 채팅, 종료");
    }

    private void login() {
        System.out.print("사용자의 이름을 알려주세요: ");
        String name = "";
        try {
            while (name.isEmpty()) {
                name = keyboard.readLine();
                if (name.trim().isEmpty()) {
                    System.out.print("이름을 다시 입력해주세요: ");
                }
            }
            writer.println(name);
            writer.flush();
            try {
                this.user = (User) objectInputStream.readObject();
                System.out.println(user.getName() + "님 반갑습니다.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("사용자 객체 수신 오류: " + e.getMessage());
                System.out.println("로그인에 실패했습니다.");
            }
        } catch (IOException e) {
            System.err.println("로그인 오류: " + e.getMessage());
            System.out.println("로그인에 실패했습니다.");
        }
    }

    public void logout() {
        try {
            // TODO 서버의 첫 메시지 이슈 해결 후 contains 대신 equals로 바꾸기
            if ((reader.readLine()).contains("success")) {
                user = null;
                System.out.println("로그아웃되었습니다.");
            }
        } catch (Exception e) {
            System.err.println("로그아웃 오류: " + e.getMessage());
        }
    }

    // TODO 서버의 첫 메시지에 이상한 문자가 붙는 이슈 발생
    private void chat() {
        ClientChat clientChat = new ClientChat(reader, writer, keyboard);
        clientChat.start();
    }

    private void showRestaurants() {
        try {
            List<Restaurant> restaurants = (List<Restaurant>) objectInputStream.readObject();
            for (Restaurant restaurant : restaurants) {
                System.out.println(restaurant.toString());
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("음식점 객체 수신 오류: " + e.getMessage());
        }
    }

    private void terminateApp() {
        try {
            socket.close();
            reader.close();
            objectInputStream.close();
            writer.close();
            keyboard.close();
        } catch (IOException e) {
            System.err.println("애플리케이션 종료 오류: " + e.getMessage());
        }
        System.out.println("이용해주셔서 고맙습니다.");
        System.exit(0);
    }

    public static void main(String[] args) {
        ClientApp client = new ClientApp();
        client.start();
    }
}
