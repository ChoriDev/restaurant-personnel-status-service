package com.np.restaurant.restaurants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Restaurants implements Serializable {
    private List<List<String>> restaurants;

    public Restaurants() {
        List<List<String>> restaurants = new ArrayList<List<String>>();
        // TODO 상대 경로로 변경하기
        File restaurantsCsv = new File(
                "/home/chori/workspace/project/restaurant-personnel-status-service/src/com/np/restaurant/restaurants/restaurants.csv");
        BufferedReader reader = null;
        String line;

        try {
            reader = new BufferedReader(new FileReader(restaurantsCsv));
            while ((line = reader.readLine()) != null) {
                List<String> restaurantInfo = new ArrayList<String>();
                String[] lineArray = line.split(",");
                restaurantInfo = Arrays.asList(lineArray);
                restaurants.add(restaurantInfo);
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
        this.restaurants = restaurants;
    }

    public List<List<String>> getRestaurants() {
        return restaurants;
    }
}
