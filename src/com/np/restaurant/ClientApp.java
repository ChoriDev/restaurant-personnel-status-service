package com.np.restaurant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.np.restaurant.user.User;

public class ClientApp {
    Socket socket = null;
    // 소켓에 쓰는 PrintWriter
    PrintWriter pw = null;
    // 소켓에서 읽는 BufferedReader
    BufferedReader br = null;
    // 앱 실행 동안 사용할 keyboard 입력
    BufferedReader keyboard = null;

    public ClientApp() {
        try {
            socket = new Socket("localhost", 10001);
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            keyboard = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void main(String[] args) throws Exception {
        ClientApp clientApp = new ClientApp();
        clientApp.startApp();
    }

    private void startApp() {
        System.out.println("음식점 인원 현황 서비스입니다.");
        User user = login();
        if (user != null) {
            System.out.println(user.getName() + "님 반갑습니다.");
            System.out.println("사용할 수 있는 메뉴를 보려면 \'전체 메뉴\'을 입력해주세요.");
            String selectedMenu = null;
            while (true) {
                try {
                    selectedMenu = keyboard.readLine();
                } catch (IOException e) {
                    System.out.println("올바른 메뉴를 입력해주세요.");
                    System.err.println(e);
                }
                switch (selectedMenu) {
                    case "전체 메뉴":
                        printAllMenus();
                        break;
                    case "종료":
                        terminateApp();
                        break;
                    default:
                        System.err.println("없는 메뉴입니다.");
                        break;
                }
            }
        } else {
            terminateApp();
        }
    }

    private User login() {
        String name = null;
        System.out.print("사용자의 이름을 알려주세요: ");
        ObjectInputStream ois = null;
        try {
            while (true) {
                if (!((name = keyboard.readLine()).equals(""))) {
                    break;
                }
                System.out.print("이름을 다시 입력해주세요: ");
            }
            pw.println(name);
            pw.flush();
        } catch (Exception e) {
            System.err.println(e);
        }
        if (name.equals("종료")) {
            return null;
        } else {
            User user = null;
            try {
                ois = new ObjectInputStream(socket.getInputStream());
            } catch (Exception e) {
                System.err.println(e);
            }
            try {
                user = (User) ois.readObject();
            } catch (Exception e) {
                System.err.println(e);
            }
            try {
                ois.close();
            } catch (Exception e) {
                System.err.println(e);
            }
            return user;
        }
    }

    private void printAllMenus() {
        // System.out.println("이용할 수 있는 서비스: ");
    }

    private void terminateApp() {
        try {
            pw.close();
            br.close();
            keyboard.close();
        } catch (Exception e) {
            System.err.println(e);
        }
        System.out.println("이용해주셔서 고맙습니다.");
        System.exit(0);
    }
}