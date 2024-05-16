package com.np.restaurant.ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginScreen extends JFrame implements ActionListener {
    private final JTextField usernameField;

    public LoginScreen() {
        setTitle("가는중");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));

        usernameField = new JTextField();
        panel.add(usernameField);

        JButton loginButton = new JButton("로그인");
        loginButton.addActionListener(this);
        panel.add(loginButton);

        add(panel);

        setVisible(true);
    }

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