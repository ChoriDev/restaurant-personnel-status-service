package com.np.restaurant.ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginScreen extends JFrame implements ActionListener {
    // UI 요소들을 선언합니다.
    private JTextField usernameField;
    private JButton loginButton;

    public LoginScreen() {
        // JFrame 설정
        setTitle("가는중");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙에 배치

        // JPanel 생성
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));

        // 사용자 닉네임 입력 필드
        usernameField = new JTextField();
        panel.add(usernameField);

        // 로그인 버튼
        loginButton = new JButton("로그인");
        loginButton.addActionListener(this); // ActionListener 등록
        panel.add(loginButton);

        add(panel);

        setVisible(true);
    }

    // 로그인 버튼 클릭 이벤트 처리
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "사용자 닉네임을 입력하세요.");
        } else {
            JOptionPane.showMessageDialog(this, "사용자 '" + username + "'으로 로그인되었습니다.");
        }
    }

    public static void main(String[] args) {
        new LoginScreen();
    }
}