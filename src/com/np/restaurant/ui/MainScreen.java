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
        setSize(700, 500);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        restaurantPanel = new JPanel();
        mainPanel.add(new JScrollPane(restaurantPanel), BorderLayout.CENTER);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(4, 1));


        JLabel userState = new JLabel(getUserStateText(clientApp.getUser().getStatus(), clientApp.getUser().getRestaurant()));
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

        // Add property change listener to the user
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

    private String getUserStateText(int status, String restaurant) {
        String userName = clientApp.getUser().getName();
        switch (status) {
            case 0:
                return userName + "님 어디로 갈지 고민중이시군요.";
            case 1:
                return userName + "님은 " + restaurant + "으로 가는중";
            case 2:
                return userName + "님은 " + restaurant + "에서 식사중";
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
            JLabel recommendNum = new JLabel("Recommend : recommend Count");

            buttonPanel.add(Box.createHorizontalGlue());
            buttonPanel.add(goingNum);
            buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
            buttonPanel.add(eatingNum);
            buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
            buttonPanel.add(recommendNum);
            buttonPanel.add(Box.createHorizontalGlue());

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
