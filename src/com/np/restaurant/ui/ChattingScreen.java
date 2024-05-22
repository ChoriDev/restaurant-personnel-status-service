package com.np.restaurant.ui;

import com.np.restaurant.ClientApp;
import com.np.restaurant.chatting.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class ChattingScreen extends JFrame {
    private final JTextArea chatArea;
    private final JTextField inputField;
    private final ClientApp clientApp;
    private final JFrame mainScreen;

    public ChattingScreen(ClientApp clientApp, JFrame mainScreen) {
        this.clientApp = clientApp;
        this.mainScreen = mainScreen;

        setTitle("Chatting Room");
        setSize(500, 500);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        inputField = new JTextField();
        JButton sendButton = new JButton("Send");

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // 기본 닫기 동작을 하지 않도록 설정

        // WindowListener 추가하여 닫기 버튼 클릭 시 특정 메서드 호출
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                quitChattingRoom();
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        setVisible(true);

        new Thread(() -> {
            try {
                receiveMessages();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.trim().isEmpty()) {
            clientApp.sendChatMessage(message);
            inputField.setText("");
        }
    }

    private void receiveMessages() throws IOException, ClassNotFoundException {
        while (true) {
            Message message = clientApp.receiveChatMessage();
            String content = message.getContent();
            if (content.equals("quit")) {
                break;
            }
            if (content != null) {
                chatArea.append(message.getSender().getName() + ": " + content + "\n");
            }
        }
    }

    private void sendQuitMessage() {
        String message = "quit";
        if (!message.trim().isEmpty()) {
            clientApp.sendChatMessage(message);
            inputField.setText("");
        }
    }

    private void quitChattingRoom() {
        int response = JOptionPane.showConfirmDialog(this, "Do you really want to leave?", "Confirming",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            sendQuitMessage();
            setVisible(false);
            mainScreen.setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChattingScreen(new ClientApp(), null));
    }
}
