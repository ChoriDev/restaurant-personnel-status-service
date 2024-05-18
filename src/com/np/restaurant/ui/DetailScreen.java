package com.np.restaurant.ui;

import com.np.restaurant.restaurants.Restaurant;

import javax.swing.*;
import java.awt.*;

public class DetailScreen extends JFrame {
    public DetailScreen(Restaurant restaurant) { // 각 레스토랑에 해당하는 상세페이지
        setTitle("restaurant: " + restaurant.getName());
        setSize(400, 300);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // 사진이 추가되면 좋을 듯

        JLabel nameLabel = new JLabel(restaurant.getName() + " / " + restaurant.getCategory());
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(nameLabel);

        JLabel hoursLabel = new JLabel("open day : " + restaurant.getDay() + " , " + restaurant.getOperationTime());
        hoursLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(hoursLabel);

        JLabel breakTimeLabel = new JLabel("BreakTime : " + restaurant.getBreakTime());
        breakTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(breakTimeLabel);

        // 객체의 모든 요소를 참조하는 방법
        // for (Field field : restaurant.getClass().getDeclaredFields()) {
        // field.setAccessible(true);
        // Object value = field.get(restaurant);
        //
        // JLabel detailLabel = new JLabel(String.valueOf(value));
        // detailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // mainPanel.add(detailLabel);
        // }

        add(mainPanel, BorderLayout.CENTER);
    }
}