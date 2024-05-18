package com.np.restaurant.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChattingScreen extends JFrame {
    private final JTextArea chatArea;
    private final JTextField inputField;

    public ChattingScreen() {
        setTitle("chatting room");
        setSize(500, 500);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        inputField = new JTextField();
        JButton sendButton = new JButton("send");

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() { // 전송 버튼 클릭시 메세지 보냄
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        setVisible(true);
    }

    private void sendMessage() { // 내가 보내는 메세지 그냥 보여주기용으로 짬.
        String message = inputField.getText();
        if (!message.trim().isEmpty()) {
            chatArea.append("me: " + message + "\n");
            inputField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChattingScreen::new);
    }
}