package com.np.restaurant.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChattingScreen extends JFrame {
    private final JTextArea chatArea;
    private final JTextField inputField;

    public ChattingScreen() {
        setTitle("채팅방");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        inputField = new JTextField();
        JButton sendButton = new JButton("전송");

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        setVisible(true);
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.trim().isEmpty()) {
            chatArea.append("나: " + message + "\n");
            inputField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChattingScreen::new);
    }
}
