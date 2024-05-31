package com.np.restaurant.user;

import java.io.Serializable;

// 음식점 인원 정보 변경을 위한 클래스
public class PeopleDelta implements Serializable {
    public PeopleDelta(String restaurantName, int goingPeopleDelta, int eatingPeopleDelta) {
        this.restaurantName = restaurantName;
        this.goingPeopleDelta = goingPeopleDelta;
        this.eatingPeopleDelta = eatingPeopleDelta;
    }

    public int getGoingPeopleDelta() {
        return goingPeopleDelta;
    }

    public int getEatingPeopleDelta() {
        return eatingPeopleDelta;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    private String restaurantName;
    private int goingPeopleDelta;
    private int eatingPeopleDelta;

}