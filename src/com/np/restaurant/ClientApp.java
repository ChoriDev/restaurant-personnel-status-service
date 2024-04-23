package com.np.restaurant;

import java.io.*;
import java.net.*;

import com.np.restaurant.chatting.ClientChat;
import com.np.restaurant.user.User;

public class ClientApp {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private BufferedReader keyboard;
    private User user;

    public ClientApp() {
        try {
            socket = new Socket("localhost", 10001);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
                printAllCommands();
                break;
            case "로그인":
                if (user == null) {
                    sendCommand(0);
                    login();
                } else {
                    System.out.println("이미 로그인되어 있습니다.");
                }
                break;
            case "현황 조회":
                sendCommand(1);
                break;
            case "채팅":
                sendCommand(2);
                chat();
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

    private void printAllCommands() {
        System.out.println("사용할 수 있는 기능");
        System.out.println("로그인, 현황 조회, 채팅, 종료");
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
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
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

    private void chat() {
        ClientChat clientChat = new ClientChat(reader, writer, keyboard);
        clientChat.start();
    }

    private void terminateApp() {
        try {
            socket.close();
            writer.close();
            reader.close();
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
