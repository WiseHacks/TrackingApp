package com.example.trackingapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Project {
   public String name,description,developer,date;//,id;
//    public ArrayList<String> team = new ArrayList<>();
    public Map<String, String> team = new HashMap<>();
//    public ArrayList<String > teambyname = new ArrayList<>();



    public Map<String, String> getTeam() {
        return team;
    }

    public void setTeam(Map<String, String> team) {
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Project(String name, String description, String developer, String date, Map<String, String> team) {
        this.name = name;
        this.description = description;
        this.developer = developer;
        this.date = date;
        this.team = team;
    }

    public Project() {
    }
}
