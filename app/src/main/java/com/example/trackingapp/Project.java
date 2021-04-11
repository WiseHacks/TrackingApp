package com.example.trackingapp;

public class Project {
   public String name,description,developer,date;//,id;

//    public Project(String name, String description, String developer, String date, String id) {
//        this.name = name;
//        this.description = description;
//        this.developer = developer;
//        this.date = date;
//        this.id = id;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

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

    public Project(String name, String description, String developer, String date) {
        this.name = name;
        this.description = description;
        this.developer = developer;
        this.date = date;
    }

    public Project() {
    }
}
