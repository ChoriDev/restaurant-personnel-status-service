package com.np.restaurant.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginScreen extends JFrame implements ActionListener {
    private final JTextField usernameField;

    public LoginScreen() {

        setTitle("going");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));

        usernameField = new JTextField();
        panel.add(usernameField);

        JButton loginButton = new JButton("login");
        loginButton.addActionListener(this);
        panel.add(loginButton);

        add(panel);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) { // 로그인 버튼을 클릭할 때 사용될 actionListener

        String username = usernameField.getText();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter your name.");
        } else {
            JOptionPane.showMessageDialog(this, "user '" + username + "'logins.");

            JFrame mainScreen = new MainScreen();
            mainScreen.setVisible(true);
            LoginScreen.this.setVisible(false);
        }
    }

    public static void main(String[] args) {
        new LoginScreen();
    }
}