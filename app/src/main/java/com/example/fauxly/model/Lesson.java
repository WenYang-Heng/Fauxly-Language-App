package com.example.fauxly.model;

public class Lesson {
    private String lessonId;       // Unique identifier for the lesson (e.g., BL1)
    private String level;          // Level of the lesson (e.g., Beginner, Intermediate, Advanced)
    private int lessonNumber;      // Lesson number
    private int languageId;        // Foreign key referencing the language table
    private String lessonTitle;    // Title of the lesson
    private Boolean isComplete;

    // Constructor
    public Lesson(String lessonId, String level, int lessonNumber, int languageId, String lessonTitle, boolean isComplete) {
        this.lessonId = lessonId;
        this.level = level;
        this.lessonNumber = lessonNumber;
        this.languageId = languageId;
        this.lessonTitle = lessonTitle;
        this.isComplete = isComplete;
    }

    // Getters and Setters
    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getLessonNumber() {
        return lessonNumber;
    }

    public void setLessonNumber(int lessonNumber) {
        this.lessonNumber = lessonNumber;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }

    // Add getter and setter for isComplete
    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "lessonId='" + lessonId + '\'' +
                ", level='" + level + '\'' +
                ", lessonNumber=" + lessonNumber +
                ", languageId=" + languageId +
                ", lessonTitle='" + lessonTitle + '\'' +
                '}';
    }
}
