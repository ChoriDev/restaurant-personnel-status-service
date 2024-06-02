package com.np.restaurant.ui;

import com.np.restaurant.ClientApp;
import com.np.restaurant.restaurants.Restaurant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class MainScreen extends JFrame {
    private final ClientApp clientApp;
    private JPanel restaurantPanel;
    private JTextField searchField;

    public MainScreen(ClientApp clientApp) {
        this.clientApp = clientApp;

        setTitle("Restaurant List");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(700, 700);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        restaurantPanel = new JPanel();
        mainPanel.add(new JScrollPane(restaurantPanel), BorderLayout.CENTER);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(4, 1));

        // 가장 위에 현재 유저의 상태를 나타냄
        JLabel userState = new JLabel(
                getUserStateText(clientApp.getUser().getStatus(), clientApp.getUser().getRestaurant()));
        userState.setHorizontalAlignment(SwingConstants.CENTER);
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        topPanel.add(userState);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(refreshButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JButton chattingButton = new JButton("Chatting Room");
        mainPanel.add(chattingButton, BorderLayout.SOUTH);
        chattingButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame chattingScreen = new ChattingScreen(clientApp, MainScreen.this);
                clientApp.chat();
                chattingScreen.setVisible(true);
                MainScreen.this.setVisible(false);
            }
        });

        getContentPane().add(mainPanel);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshRestaurants();
                searchField.setText("");
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchRestaurants();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                terminateApp();
            }
        });

        // user의 상태나, 현재 가는 음식점이 바뀔 경우 ui를 실시간으로 바꾸기 위한 리스너
        clientApp.getUser().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("status".equals(evt.getPropertyName())) {
                    userState.setText(getUserStateText((int) evt.getNewValue(), clientApp.getUser().getRestaurant()));
                }
                if ("restaurant".equals(evt.getPropertyName())) {
                    userState.setText(getUserStateText(clientApp.getUser().getStatus(), (String) evt.getNewValue()));
                }
            }
        });

        refreshRestaurants();
        setVisible(true);
    }

    // 유저의 상태별로 어떤 상태인지 나타낼 문자열 생성
    private String getUserStateText(int status, String restaurant) {
        String userName = clientApp.getUser().getName();
        switch (status) {
            case 0:
                return userName + " wondering where to go";
            case 1:
                return userName + " is on the way to " + restaurant;
            case 2:
                return userName + " is having a meal at " + restaurant;
            default:
                return "";
        }
    }

    public void refreshRestaurants() {
        List<Restaurant> restaurants = clientApp.fetchRestaurants();
        if (restaurants == null) {
            restaurants = List.of();
        }

        updateRestaurantPanel(restaurants);
    }

    private void searchRestaurants() {
        String searchWord = searchField.getText();
        List<Restaurant> restaurants = clientApp.searchRestaurant(searchWord);
        if (restaurants == null) {
            restaurants = List.of();
        }

        updateRestaurantPanel(restaurants);
    }

    private void updateRestaurantPanel(List<Restaurant> restaurants) {
        restaurantPanel.removeAll();
        restaurantPanel.setLayout(new GridLayout(restaurants.size() * 3, 1));

        for (Restaurant restaurant : restaurants) {

            JPanel tempPanel = new JPanel(new BorderLayout());
            JLabel nameLabel = new JLabel(restaurant.getName());
            tempPanel.add(nameLabel, BorderLayout.WEST);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

            JLabel goingNum = new JLabel("Going : " + restaurant.getGoingPeopleCount());
            JLabel eatingNum = new JLabel(
                    "Eating : " + restaurant.getEatingPeopleCount() + " / " + restaurant.getSeatNum());
            JLabel recommendNum = new JLabel("Recommend : " + restaurant.getInterestCount());

            buttonPanel.add(Box.createHorizontalGlue());
            buttonPanel.add(goingNum);
            buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
            buttonPanel.add(eatingNum);
            buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
            buttonPanel.add(recommendNum);
            buttonPanel.add(Box.createHorizontalGlue());

            // restaurant.addPropertyChangeListener(new PropertyChangeListener() {
            // @Override
            // public void propertyChange(PropertyChangeEvent evt) {
            // System.out.println(evt);
            // if ("interestCount".equals(evt.getPropertyName())) {
            // int newInterestCount = (Integer) evt.getNewValue();
            // recommendNum.setText("Recommend : " + String.valueOf(newInterestCount));
            // }
            // }
            // });

            tempPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JFrame detailScreen = new DetailScreen(clientApp, restaurant, MainScreen.this);
                    detailScreen.setVisible(true);
                }
            });

            tempPanel.add(buttonPanel, BorderLayout.EAST);
            restaurantPanel.add(Box.createVerticalStrut(10));
            restaurantPanel.add(tempPanel);
            restaurantPanel.add(Box.createVerticalGlue());
        }

        restaurantPanel.revalidate();
        restaurantPanel.repaint();
    }

    private void terminateApp() {
        int response = JOptionPane.showConfirmDialog(this, "Do you really want to exit?", "Confirming",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            clientApp.logout();
            System.exit(0);
        }
    }
}
