package com.seamlesstrackers.safarsathi.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document(collection = "Login_details") // Ensure MongoDB collection name is correct
public class Rahasathi {

    @Id
    private String userid;

    private String busNo;
    private Double latitude;
    private Double longitude;
    private Map<String, Integer> ticketsCollected = new HashMap<>(); // Stores ticket count at locations
    private int availableSeats = 40; // Default seats (can be changed)

    public Rahasathi() {}

    public Rahasathi(String userid, String busNo, Double latitude, Double longitude, int availableSeats) {
        this.userid = userid;
        this.busNo = busNo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.availableSeats = availableSeats;
    }

    public String getUserid() { return userid; }
    public void setUserid(String userid) { this.userid = userid; }

    public String getBusNo() { return busNo; }
    public void setBusNo(String busNo) { this.busNo = busNo; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Map<String, Integer> getTicketsCollected() { return ticketsCollected; }
    public void setTicketsCollected(Map<String, Integer> ticketsCollected) { this.ticketsCollected = ticketsCollected; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
}
