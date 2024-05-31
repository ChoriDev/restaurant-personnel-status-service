package com.np.restaurant.user;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String restaurant;
    private int status;
    public static final int DEFAULT = 0;
    public static final int GOING = 1;
    public static final int EATING = 2;


    public User(String name) {
        this.name = name;
        this.restaurant = null;
        this.status = DEFAULT;
    }

    public String getName() {
        return name;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public int getStatus() {
        return status;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}