package com.example.fauxly.model;

public class UserLanguage {
    private int userId;
    private int languageId;
    private String languageName;
    private String proficiencyLevel;

    public UserLanguage(int userId, int languageId, String languageName, String proficiencyLevel) {
        this.userId = userId;
        this.languageId = languageId;
        this.languageName = languageName;
        this.proficiencyLevel = proficiencyLevel;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(String proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }
}
