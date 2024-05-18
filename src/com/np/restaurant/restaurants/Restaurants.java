package com.np.restaurant.restaurants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Restaurants implements Serializable {
    public static List<Restaurant> getRestaurants() {
        List<Restaurant> restaurants = new ArrayList<Restaurant>();
        // TODO 상대 경로로 변경하기
        File rawCsv = new File(
                "/home/chori/workspace/project/restaurant-personnel-status-service/src/com/np/restaurant/restaurants/restaurants.csv");
        BufferedReader reader = null;
        String line;

        try {
            reader = new BufferedReader(new FileReader(rawCsv));
            while ((line = reader.readLine()) != null) {
                String[] tokenArray = line.split(",");
                Restaurant restaurant = new Restaurant(
                        tokenArray[0], // 카테고리
                        tokenArray[1], // 음식점명
                        tokenArray[2], // 운영 요일
                        tokenArray[3], // 운영 시간
                        tokenArray[4]); // 브레이크 타임
                restaurants.add(restaurant);
            }
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        return restaurants;
    }
}