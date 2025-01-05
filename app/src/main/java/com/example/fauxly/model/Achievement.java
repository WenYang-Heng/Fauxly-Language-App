package com.example.fauxly.model;

public class Achievement {

    private final int id;
    private final String title;
    private final String description;
    private final boolean isAchieved;
    private final String dateAchieved;

    public Achievement(int id, String title, String description, boolean isAchieved, String dateAchieved) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.isAchieved = isAchieved;
        this.dateAchieved = dateAchieved;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAchieved() {
        return isAchieved;
    }

    public String getDateAchieved() {
        return dateAchieved;
    }
}
