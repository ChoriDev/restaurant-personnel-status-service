package com.np.restaurant.user;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String restaurant;
    private int status;
    public static final int DEFAULT = 0;
    public static final int GOING = 1;
    public static final int EATING = 2;

    private PropertyChangeSupport support;


    public User(String name) {
        this.name = name;
        this.restaurant = null;
        this.status = DEFAULT;
        support = new PropertyChangeSupport(this);
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
        String oldRestaurant = this.restaurant;
        this.restaurant = restaurant;
        support.firePropertyChange("restaurant", oldRestaurant, restaurant);
    }

    public void setStatus(int status) {
        int oldStatus = this.status;
        this.status = status;
        support.firePropertyChange("status", oldStatus, status);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }
}