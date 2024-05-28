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
import java.util.List;

public class MainScreen extends JFrame {
    private final ClientApp clientApp;
    private JPanel restaurantPanel;
    private JTextField searchField;

    public MainScreen(ClientApp clientApp) {
        this.clientApp = clientApp;

        setTitle("Restaurant List");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(500, 500);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        restaurantPanel = new JPanel();
        mainPanel.add(new JScrollPane(restaurantPanel), BorderLayout.CENTER);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayout(3, 1));

        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

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

        refreshRestaurants();
        setVisible(true);
    }

    private void refreshRestaurants() {
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
        restaurantPanel.setLayout(new GridLayout(restaurants.size(), 1));

        for (Restaurant restaurant : restaurants) {
            JPanel tempPanel = new JPanel(new BorderLayout());
            JLabel nameLabel = new JLabel(restaurant.getName());
            tempPanel.add(nameLabel, BorderLayout.WEST);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            JButton goingButton = new JButton("Going");
            goingButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clientApp.people(restaurant.getName(), "going");
                }
            });
            buttonPanel.add(goingButton);

            JButton eatingButton = new JButton("Eating");
            eatingButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clientApp.people(restaurant.getName(), "eating");
                }
            });
            buttonPanel.add(eatingButton);

            JButton recommendButton = new JButton("Recommend Count");
            buttonPanel.add(recommendButton);

            tempPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JFrame detailScreen = new DetailScreen(restaurant);
                    detailScreen.setVisible(true);
                }
            });

            tempPanel.add(buttonPanel, BorderLayout.EAST);
            restaurantPanel.add(tempPanel);
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
