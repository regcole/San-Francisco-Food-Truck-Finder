package com.redfin.foodtruckfinder.domain;


import lombok.Data;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


@Data
public class FoodTruck implements Serializable {

    private String name;

    private LocalTime startTime;

    private LocalTime endTime;

    private String location;

    public FoodTruck(JSONObject foodTruckJSON) {
        name = foodTruckJSON.getString("applicant");
        String startTimeString = foodTruckJSON.getString("start24");
        startTime = LocalTime.parse(startTimeString, DateTimeFormatter.ofPattern("HH:mm"));
        String endTimeString = foodTruckJSON.getString("end24");
        endTime = LocalTime.parse(endTimeString, DateTimeFormatter.ofPattern("HH:mm"));
        location = foodTruckJSON.getString("location");
    }

    public void print() {
        System.out.format("%10s%10s\n", "Name:" + getName(), " || Location: " + getLocation());
//        System.out.println("Name:"+getName()+" Location:"+getLocation());
    }

}
