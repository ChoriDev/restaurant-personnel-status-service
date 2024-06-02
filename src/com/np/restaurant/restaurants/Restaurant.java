package com.np.restaurant.restaurants;

import com.np.restaurant.user.PeopleDelta;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Restaurant implements Serializable {
    private String category;
    private String name;
    private String day;
    private String operationTime;
    private String breakTime;
    private String seatNum;
    private int goingPeopleCount;
    private int eatingPeopleCount;
    private int interestCount;

    private PropertyChangeSupport support;

    public Restaurant(String category, String name, String day, String operationTime, String breakTime,
            String seatNum) {
        this.category = category;
        this.name = name;
        this.day = day;
        this.operationTime = operationTime;
        this.breakTime = breakTime;
        this.seatNum = seatNum;
        this.goingPeopleCount = 0;
        this.eatingPeopleCount = 0;
        this.interestCount = 0;
        this.support = new PropertyChangeSupport(this);
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getDay() {
        return day;
    }

    public String getOperationTime() {
        return operationTime;
    }

    public String getBreakTime() {
        return breakTime;
    }

    public String getSeatNum() {
        return seatNum;
    }

    public int getGoingPeopleCount() {
        return goingPeopleCount;
    }

    public int getEatingPeopleCount() {
        return eatingPeopleCount;
    }

    public int getInterestCount() {
        return interestCount;
    }

    public String toString() {
        return category + ", "
                + name + ", "
                + day + ", "
                + operationTime + ", "
                + breakTime + ", "
                + seatNum + ", "
                + goingPeopleCount + ", "
                + eatingPeopleCount;
    }

    public void changePeopleInfo(PeopleDelta peopleDelta) {
        this.goingPeopleCount += peopleDelta.getGoingPeopleDelta();
        this.eatingPeopleCount += peopleDelta.getEatingPeopleDelta();
    }

    public static Restaurant findRestaurantByName(List<Restaurant> restaurants, String name) {
        return restaurants.stream()
                    .filter(restaurant -> restaurant.getName().equals(name))
                    .findFirst()
                    .orElse(null);
    }

    public void setInterestCount(int interestCount) {
        int oldInterestCount = this.interestCount;
        this.interestCount = interestCount;
        support.firePropertyChange("interestCount", oldInterestCount, interestCount);
    }

    public void incrementInterest() {
        setInterestCount(this.interestCount + 1);
    }

    public void decrementInterest() {
        if (this.interestCount > 0) {
            setInterestCount(this.interestCount - 1);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}