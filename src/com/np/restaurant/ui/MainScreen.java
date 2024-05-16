package com.np.restaurant.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainScreen extends JFrame {

    public MainScreen() {
        setTitle("음식점 리스트");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);

        // 음식점 데이터 (이름, 버튼1 레이블, 버튼2 레이블, 버튼3 레이블)
        String[][] restaurants = {
                {"레스토랑 A", "버튼1", "버튼2", "버튼3"},
                {"레스토랑 B", "버튼1", "버튼2", "버튼3"},
                {"레스토랑 C", "버튼1", "버튼2", "버튼3"},
                {"레스토랑 C", "버튼1", "버튼2", "버튼3"},
                {"레스토랑 C", "버튼1", "버튼2", "버튼3"},
                {"레스토랑 C", "버튼1", "버튼2", "버튼3"},
                {"레스토랑 C", "버튼1", "버튼2", "버튼3"},
                {"레스토랑 C", "버튼1", "버튼2", "버튼3"},
                {"레스토랑 C", "버튼1", "버튼2", "버튼3"},
                {"레스토랑 B", "버튼1", "버튼2", "버튼3"},
                {"레스토랑 B", "버튼1", "버튼2", "버튼3"},
                {"레스토랑 B", "버튼1", "버튼2", "버튼3"},
                {"레스토랑 A", "버튼1", "버튼2", "버튼3"},
                {"레스토랑 A", "버튼1", "버튼2", "버튼3"},
                {"레스토랑 A", "버튼1", "버튼2", "버튼3"},
                {"레스토랑 A", "버튼1", "버튼2", "버튼3"},
                {"레스토랑 A", "버튼1", "버튼2", "버튼3"},
        };

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel restaurantPanel = new JPanel();
        restaurantPanel.setLayout(new GridLayout(restaurants.length, 1));

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayout(3, 1));

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("검색");
        JButton refreshButton = new JButton("새로고침");

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        for (String[] restaurant : restaurants) {
            JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel nameLabel = new JLabel(restaurant[0]);
            tempPanel.add(nameLabel);

            for (int i = 1; i < restaurant.length; i++) {
                JButton button = new JButton(restaurant[i]);
                tempPanel.add(new JLabel(""));
                tempPanel.add(button);
                button.addActionListener(new ButtonListener());
            }

            tempPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // 새로운 화면으로 전환
                    JFrame newFrame = new NewScreen(nameLabel.getText());
                    newFrame.setVisible(true);
                    MainScreen.this.setVisible(false);
                }
            });

            restaurantPanel.add(tempPanel);
        }


        JScrollPane scrollPane = new JScrollPane(restaurantPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(searchPanel,BorderLayout.NORTH);

        JButton chattingButton = new JButton("채팅방");
        mainPanel.add(chattingButton, BorderLayout.SOUTH);


        getContentPane().add(mainPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainScreen::new);
    }

    private static class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 버튼 클릭시 동작할 내용을 여기에 작성하세요.
            JOptionPane.showMessageDialog(null, "버튼이 클릭되었습니다.");
        }
    }
    private static class NewScreen extends JFrame {
        public NewScreen(String restaurantName) {
            setTitle("레스토랑: " + restaurantName);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(400, 300);

            JLabel label = new JLabel("Welcome to " + restaurantName);
            add(label, BorderLayout.CENTER);
        }
    }
}
