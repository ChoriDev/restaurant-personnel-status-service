package com.np.restaurant.ui;

import com.np.restaurant.restaurants.Restaurant;

import javax.swing.*;
import java.awt.*;

public class DetailScreen extends  JFrame{
    public DetailScreen(Restaurant restaurant){
        setTitle("레스토랑: " + restaurant.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel("Welcome to " + restaurant.getName());
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(welcomeLabel);

        JLabel nameLabel = new JLabel(restaurant.getName() + " / " + restaurant.getCategory());
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(nameLabel);

        JLabel hoursLabel = new JLabel("운영일 : " + restaurant.getDay() + " , " + restaurant.getOperationTime());
        hoursLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(hoursLabel);

        JLabel breakTimeLabel = new JLabel("BreakTime : " + restaurant.getBreakTime());
        breakTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(breakTimeLabel);

//              객체의 모든 요소를 참조하는 방법
//            for (Field field : restaurant.getClass().getDeclaredFields()) {
//                field.setAccessible(true);
//                Object value = field.get(restaurant);
//
//                JLabel detailLabel = new JLabel(String.valueOf(value));
//                detailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//                mainPanel.add(detailLabel);
//            }

        add(mainPanel, BorderLayout.CENTER);
    }
}
