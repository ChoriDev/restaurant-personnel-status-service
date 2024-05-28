package com.np.restaurant.user;

import java.io.Serializable;
import java.util.Vector;

public class User implements Serializable {
    private final String name;
    private String restaurant;
    private String status;
    private final Vector<String> interestedRestaurants;

    public User(String name) {
        this.name = name;
        this.restaurant = null;
        this.status = "default";
        this.interestedRestaurants = new Vector<>();
    }

    public String getName() {
        return name;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public String getStatus() {
        return status;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public void setDiningAt(String status) {
        this.status = status;
    }

    public Vector<String> getInterestedRestaurants() {
        return new Vector<>(interestedRestaurants);
    }

    public synchronized void addInterestedRestaurant(String restaurantName) {
        if (!interestedRestaurants.contains(restaurantName)) {
            interestedRestaurants.add(restaurantName);
        }
    }

    public synchronized void removeInterestedRestaurant(String restaurantName) {
        interestedRestaurants.remove(restaurantName);
    }
}
