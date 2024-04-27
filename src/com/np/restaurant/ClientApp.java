package com.np.restaurant;

import java.io.*;
import java.net.*;
import java.util.List;

import com.np.restaurant.chatting.ClientChat;
import com.np.restaurant.restaurants.Restaurant;
import com.np.restaurant.user.User;

public class ClientApp {
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private BufferedReader reader;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private BufferedReader keyboard;
    private User user;

    public ClientApp() {
        try {
            socket = new Socket("localhost", 10001);
            in = socket.getInputStream();
            out = socket.getOutputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            objectOutputStream = new ObjectOutputStream(out);
            objectInputStream = new ObjectInputStream(in);
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
                    sendCommand(selectedCommand);
                    login();
                } else {
                    System.out.println("이미 로그인되어 있습니다.");
                }
                break;
            case "로그아웃":
                if (user != null) {
                    sendCommand(selectedCommand);
                    logout();
                } else {
                    System.out.println("로그인 후 이용할 수 있습니다.");
                }
                break;
            case "음식점 조회":
                if (user != null) {
                    sendCommand(selectedCommand);
                    showRestaurants();
                } else {
                    System.out.println("로그인 후 이용할 수 있습니다.");
                }
                break;
            case "채팅":
                if (user != null) {
                    sendCommand(selectedCommand);
                    chat();
                } else {
                    System.out.println("로그인 후 이용할 수 있습니다.");
                }
                break;
            case "종료":
                sendCommand(selectedCommand);
                terminateApp();
                break;
            default:
                System.out.println("없는 기능입니다.");
                break;
        }
    }

    private void sendCommand(String inputCommand) {
        Command command = new Command(inputCommand);
        try {
            objectOutputStream.writeObject(command);
            objectOutputStream.flush();
            objectOutputStream.reset();
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
        SuccessFlag successFlag;
        while (name.isEmpty()) {
            try {
                name = keyboard.readLine();
            } catch (IOException e) {
                System.err.println(e);
            }
            if (name.trim().isEmpty()) {
                System.out.print("이름을 다시 입력해주세요: ");
            }
        }
        User user = new User(name);
        try {
            objectOutputStream.writeObject(user);
            objectOutputStream.flush();
            objectOutputStream.reset();
        } catch (Exception e) {
            System.err.println(e);
        }
        try {
            successFlag = (SuccessFlag) objectInputStream.readObject();
            if (successFlag.getFlag()) {
                this.user = user;
                System.out.println(user.getName() + "님 반갑습니다.");
            } else {
                System.out.println("중복되는 아이디로 인해 로그인에 실패했습니다.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("사용자 객체 수신 오류: " + e.getMessage());
        }
    }

    public void logout() {
        SuccessFlag successFlag;
        try {
            successFlag = (SuccessFlag) objectInputStream.readObject();
            if (successFlag.getFlag()) {
                user = null;
                System.out.println("로그아웃되었습니다.");
            }
        } catch (Exception e) {
            System.err.println("로그아웃 오류: " + e.getMessage());
        }
    }

    // TODO 서버의 첫 메시지에 이상한 문자가 붙는 이슈 발생
    private void chat() {
        ClientChat clientChat = new ClientChat(user, objectInputStream, objectOutputStream, keyboard);
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
            in.close();
            out.close();
            reader.close();
            objectInputStream.close();
            objectOutputStream.close();
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
