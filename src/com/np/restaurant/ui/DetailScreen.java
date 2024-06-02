package com.np.restaurant.ui;

import com.np.restaurant.ClientApp;
import com.np.restaurant.restaurants.Restaurant;
import com.np.restaurant.user.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DetailScreen extends JFrame {
    public static final int  AVERAGE_EATING_TIME = 30;

    public DetailScreen(ClientApp clientApp, Restaurant restaurant, MainScreen mainScreen) { // 각 레스토랑에 해당하는 상세페이지
        setTitle("Restaurant: " + restaurant.getName());
        setSize(400, 300);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // 사진이 추가되면 좋을 듯

        mainPanel.add(Box.createVerticalStrut(20));

        JLabel nameLabel = new JLabel(restaurant.getName() + " / " + restaurant.getCategory());
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(nameLabel);

        JLabel hoursLabel = new JLabel("Open day: " + restaurant.getDay() + " , " + restaurant.getOperationTime());
        hoursLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(hoursLabel);

        JLabel breakTimeLabel = new JLabel("BreakTime: " + restaurant.getBreakTime());
        breakTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(breakTimeLabel);

        mainPanel.add(Box.createVerticalStrut(10));

        JButton goingButton = new JButton("Going");
        goingButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        goingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientApp.people(restaurant.getName(), User.GOING);
                mainScreen.refreshRestaurants();
            }
        });
        mainPanel.add(goingButton);

        mainPanel.add(Box.createVerticalStrut(10));

        JButton eatingButton = new JButton("Eating");
        eatingButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        eatingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientApp.people(restaurant.getName(), User.EATING);
                mainScreen.refreshRestaurants();
            }
        });
        mainPanel.add(eatingButton);

        mainPanel.add(Box.createVerticalStrut(10));

        JLabel waitingTimeLabel = new JLabel("Estimated wait time: " + estimatedWaitTime(restaurant) + " minute");
        waitingTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(waitingTimeLabel);

        add(mainPanel, BorderLayout.CENTER);
    }

    // 예상 대기시간 로직
    public String estimatedWaitTime(Restaurant restaurant) {
        int interestCount = restaurant.getInterestCount();
        int seatNum = Integer.parseInt(restaurant.getSeatNum().trim());
        int goingCount = restaurant.getGoingPeopleCount();
        int eatingCount = restaurant.getEatingPeopleCount();

        if(eatingCount + goingCount < seatNum){
            return "0";
        } else {
            int turnoverTime = AVERAGE_EATING_TIME / seatNum;
            int estimatedTime = (goingCount + eatingCount - seatNum + interestCount/2) * turnoverTime;
            return String.valueOf(estimatedTime);
        }
    }
}
