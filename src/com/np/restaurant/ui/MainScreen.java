package com.np.restaurant.ui;

import com.np.restaurant.restaurants.Restaurant;
import com.np.restaurant.restaurants.Restaurants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MainScreen extends JFrame {

    public MainScreen() {
        setTitle("restaurant list");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);

        List<Restaurant> restaurants = Restaurants.getRestaurants();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // 레스토랑 리스트들을 담아 둘 panel
        JPanel restaurantPanel = new JPanel();
        restaurantPanel.setLayout(new GridLayout(restaurants.size(), 1));

        // 검색창 + 새로고침 창
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayout(3, 1));

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("search");
        JButton refreshButton = new JButton("refresh");

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        // 레스토랑당 한 줄 씩 표시할 UI
        for (Restaurant restaurant : restaurants) {
            JPanel tempPanel = new JPanel(new BorderLayout());
            JLabel nameLabel = new JLabel(restaurant.getName());
            tempPanel.add(nameLabel, BorderLayout.WEST);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            // 가는중 버튼
            JButton goingButton = new JButton("going");
            buttonPanel.add(new JLabel("    "));
            buttonPanel.add(goingButton);
            goingButton.addActionListener(new ButtonListener());

            // 식사중 버튼
            JButton eatingButton = new JButton("eating");
            buttonPanel.add(new JLabel("    "));
            buttonPanel.add(eatingButton);
            eatingButton.addActionListener(new ButtonListener());

            // 추천수 버튼
            JButton recommendButton = new JButton("recommend count");
            buttonPanel.add(new JLabel("    "));
            buttonPanel.add(recommendButton);
            recommendButton.addActionListener(new ButtonListener());

            tempPanel.addMouseListener(new MouseAdapter() { // 상세 페이지로 이동
                @Override
                public void mouseClicked(MouseEvent e) {
                    JFrame detailScreen = new DetailScreen(restaurant);
                    detailScreen.setVisible(true);
                }
            });

            tempPanel.add(buttonPanel, BorderLayout.EAST);

            restaurantPanel.add(tempPanel);
        }

        JScrollPane scrollPane = new JScrollPane(restaurantPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // 채팅방 버튼 맨 밑에 고정
        JButton chattingButton = new JButton("chatting room");
        mainPanel.add(chattingButton, BorderLayout.SOUTH);
        chattingButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { // 채팅버튼 클릭시 채팅방으로 이동
                JFrame chattingScreen = new ChattingScreen();
                chattingScreen.setVisible(true);
            }
        });

        getContentPane().add(mainPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainScreen::new);
    }

    private static class ButtonListener implements ActionListener { // 버튼이 클릭 되었을 때 취할 행동. 버튼 클릭시 중복되는 기능이 없어 안쓰일듯
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, "Clicked!");
        }
    }
}