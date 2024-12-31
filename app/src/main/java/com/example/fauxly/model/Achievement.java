package com.example.fauxly.model;

public class Achievement {
    private String title;
    private String description;

    public Achievement(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
