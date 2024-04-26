package com.np.restaurant.restaurants;

import java.io.Serializable;

public class Restaurant implements Serializable {
    private String category;
    private String name;
    private String day;
    private String operationTime;
    private String breakTime;
    private int movingPeopleCount;
    private int eatingPeopleCount;

    public Restaurant(String category, String name, String day, String operationTime, String breakTime) {
        this.category = category;
        this.name = name;
        this.day = day;
        this.operationTime = operationTime;
        this.breakTime = breakTime;
        this.movingPeopleCount = 0;
        this.eatingPeopleCount = 0;
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

    public int getMovingPeopleCount() {
        return movingPeopleCount;
    }

    public int getEatingPeopleCount() {
        return eatingPeopleCount;
    }

    public String toString() {
        return category + ", "
                + name + ", "
                + day + ", "
                + operationTime + ", "
                + breakTime + ", "
                + movingPeopleCount + ", "
                + eatingPeopleCount;
    }
}
