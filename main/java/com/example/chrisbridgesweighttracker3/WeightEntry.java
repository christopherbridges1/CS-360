package com.example.chrisbridgesweighttracker3;

// Weight entry
public class WeightEntry {
    // Idnetifier for entry
    private final long id;
    // Date of weight entry
    private final String date;
    // Weight in entry
    private final String weight;

    //Creates weight entry object
    public WeightEntry(long id, String date, String weight) {
        this.id = id;
        this.date = date;
        this.weight = weight;
    }

    // Getters
    public long getId() { return id; }
    public String getDate() { return date; }
    public String getWeight() { return weight; }
}
