package com.np.restaurant.user;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String restaurant;
    private String status;

    public User(String name) {
        this.name = name;
        this.restaurant = null;
        this.status = null;
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
}