package com.np.restaurant.restaurants;

import java.io.Serializable;

public class Restaurant implements Serializable {
    private String category;
    private String name;
    private String day;
    private String operationTime;
    private String breakTime;
    private String seatNum;
    private int goingPeopleCount;
    private int eatingPeopleCount;

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

    public void changePeopleInfo(int goingDelta, int eatingDelta) {
        this.goingPeopleCount += goingDelta;
        this.eatingPeopleCount += eatingDelta;
    }
}