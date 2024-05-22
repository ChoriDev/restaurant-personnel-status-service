package com.np.restaurant.ui;

import com.np.restaurant.ClientApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JFrame implements ActionListener {
    private final JTextField usernameField;
    private final ClientApp clientApp;

    public LoginScreen(ClientApp clientApp) {
        this.clientApp = clientApp;

        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));

        usernameField = new JTextField();
        panel.add(usernameField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        panel.add(loginButton);

        add(panel);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter your name.");
        } else {
            boolean loginSuccess = clientApp.login(username);
            if (loginSuccess) {
                JOptionPane.showMessageDialog(this, "User '" + username + "' logged in.");
                JFrame mainScreen = new MainScreen(clientApp);
                mainScreen.setVisible(true);
                this.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Login failed. Try a different username.");
            }
        }
    }

    public static void main(String[] args) {
        ClientApp clientApp = new ClientApp();
        new LoginScreen(clientApp);
    }
}
