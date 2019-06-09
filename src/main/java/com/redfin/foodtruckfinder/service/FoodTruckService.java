package com.redfin.foodtruckfinder.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redfin.foodtruckfinder.domain.FoodTruck;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service("FoodTruckService")
public class FoodTruckService {

    @Value("${food-truck-url}")
    private String baseFoodTruckURL;


    /**
     * Get current available food trucks
     *
     * @return
     */
    public List<FoodTruck> getCurrentlyAvailableFoodTrucks() {
        return getAllAvailableFoodTrucks(LocalDate.now(), LocalTime.now());
    }

    /**
     * Get available food trucks for specified date and time
     *
     * @param targetDate
     * @param targetTime
     * @return
     */
    public List<FoodTruck> getAllAvailableFoodTrucks(LocalDate targetDate, LocalTime targetTime) {
        List<FoodTruck> foodTrucks = new ArrayList<FoodTruck>();
        try {
            int targetDay = targetDate.getDayOfWeek().getValue();
            String jsonResponse = fetchJSONResponse(targetDay);
            JSONArray foodTrucksJson = new JSONArray(jsonResponse);
            foodTrucks = parseFoodTruckJsonResponse(foodTrucksJson);
        } catch (Exception error) {
            error.printStackTrace();
        }

        // Return a sorted list of food trucks and filter out the ones that are not open
        return foodTrucks
                .stream()
                .filter(foodTruck -> foodTruck.getStartTime().isBefore(targetTime) &&
                        foodTruck.getEndTime().isAfter(targetTime))
                .sorted(Comparator.comparing(FoodTruck::getName))
                .collect(Collectors.toList());

    }

    /**
     * @param foodTrucksJson
     * @return
     */
    private List<FoodTruck> parseFoodTruckJsonResponse(JSONArray foodTrucksJson) {
        List<FoodTruck> results = new ArrayList<>();
        Iterator<Object> itr = foodTrucksJson.iterator();
        while ((itr.hasNext())) {
            JSONObject foodTruckJSON = (JSONObject) itr.next();
            FoodTruck foodTruck = new FoodTruck(foodTruckJSON);
            results.add(foodTruck);
        }
        return results;
    }

    /**
     * @param day
     * @return
     */
    private String fetchJSONResponse(int day) {
        String reponse = "[]";
        try {
            StringBuilder result = new StringBuilder();
            String urlString = buildUrlForDay(day);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            reponse = result.toString();
        } catch (Exception error) {
            error.printStackTrace();
        }
        return reponse;
    }

    /**
     * Builds the query string for the day given
     *
     * @param day
     * @return
     */
    private String buildUrlForDay(int day) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(baseFoodTruckURL);
        queryBuilder.append("?dayorder=");
        queryBuilder.append(day);
        return queryBuilder.toString();
    }
}
