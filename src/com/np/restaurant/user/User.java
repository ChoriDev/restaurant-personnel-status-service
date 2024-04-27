package com.np.restaurant.user;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String targetRestaurant; // 또는 intendedRestaurant
    private String diningAt; // 또는 currentRestaurant

    public User(String name) {
        this.name = name;
        this.targetRestaurant = null;
        this.diningAt = null;
    }

    public String getName() {
        return name;
    }

    public String getTargetRestaurant() {
        return targetRestaurant;
    }

    public String getDiningAt() {
        return diningAt;
    }

    public void setTargetRestaurant(String restaurant) {
        this.targetRestaurant = restaurant;
    }

    public void setDiningAt(String restaurant) {
        this.diningAt = restaurant;
    }
}