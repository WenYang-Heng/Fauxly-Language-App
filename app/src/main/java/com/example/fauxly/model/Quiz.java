package com.example.fauxly.model;

public class Quiz {
    private String quizId;
    private String title;
    private int languageId;
    private String level;
    private boolean isComplete;

    public Quiz(String quizId, String title, int languageId, String level, boolean isComplete) {
        this.quizId = quizId;
        this.title = title;
        this.languageId = languageId;
        this.level = level;
        this.isComplete = isComplete;
    }

    // Getters and Setters
    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}
