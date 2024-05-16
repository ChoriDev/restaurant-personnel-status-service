package com.np.restaurant.ui;

import com.np.restaurant.restaurants.Restaurant;
import com.np.restaurant.restaurants.Restaurants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.List;

public class MainScreen extends JFrame {

    public MainScreen() {
        setTitle("음식점 리스트");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);

        List<Restaurant> restaurants = Restaurants.getRestaurants();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel restaurantPanel = new JPanel();
        restaurantPanel.setLayout(new GridLayout(restaurants.size(), 1));

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayout(3, 1));

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("검색");
        JButton refreshButton = new JButton("새로고침");

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        for (Restaurant restaurant : restaurants) {
            JPanel tempPanel = new JPanel(new BorderLayout());
            JLabel nameLabel = new JLabel(restaurant.getName());
            tempPanel.add(nameLabel, BorderLayout.WEST);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            // 가는중 버튼
            JButton goingButton = new JButton("가는중");
            buttonPanel.add(new JLabel("    "));
            buttonPanel.add(goingButton);
            goingButton.addActionListener(new ButtonListener());

            // 식사중 버튼
            JButton eatingButton = new JButton("식사중");
            buttonPanel.add(new JLabel("    "));
            buttonPanel.add(eatingButton);
            eatingButton.addActionListener(new ButtonListener());

            // 추천수 버튼
            JButton recommendButton = new JButton("추천수");
            buttonPanel.add(new JLabel("    "));
            buttonPanel.add(recommendButton);
            recommendButton.addActionListener(new ButtonListener());


            tempPanel.addMouseListener(new MouseAdapter() {     // 상세 페이지로 이동
                @Override
                public void mouseClicked(MouseEvent e) {
                    JFrame newFrame = new DetailScreen(restaurant);
                    newFrame.setVisible(true);
                    MainScreen.this.setVisible(false);
                }
            });

            tempPanel.add(buttonPanel, BorderLayout.EAST);

            restaurantPanel.add(tempPanel);
        }


        JScrollPane scrollPane = new JScrollPane(restaurantPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(searchPanel,BorderLayout.NORTH);

        JButton chattingButton = new JButton("채팅방");
        mainPanel.add(chattingButton, BorderLayout.SOUTH);
        chattingButton.addMouseListener(new MouseAdapter() {
                                       @Override
                                       public void mouseClicked(MouseEvent e) {     // 채팅버튼 클릭시 채팅방으로 이동
                                           JFrame newFrame = new ChattingScreen();
                                           newFrame.setVisible(true);
                                       }
                                   });


        getContentPane().add(mainPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainScreen::new);
    }

    private static class ButtonListener implements ActionListener { // 버튼이 클릭 되었을 때 취할 행동
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, "버튼이 클릭되었습니다.");
        }
    }
}
