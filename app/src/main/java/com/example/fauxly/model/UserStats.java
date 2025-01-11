package com.example.fauxly.model;

public class UserStats {
    private int userId;
    private int currentLevel;
    private int totalXp;
    private int currentXp;           // XP accumulated in the current level
    private int levelUpXp;           // XP required to level up
    private int wordsLearned;
    private int totalLoginStreak;
    private int fiveDayLoginStreak;  // Five-day login streak
    private String lastClaimDate;

    // Constructor
    public UserStats(int userId, int currentLevel, int totalXp, int currentXp, int levelUpXp,
                     int wordsLearned, int totalLoginStreak, int fiveDayLoginStreak, String lastClaimDate) {
        this.userId = userId;
        this.currentLevel = currentLevel;
        this.totalXp = totalXp;
        this.currentXp = currentXp;
        this.levelUpXp = levelUpXp;
        this.wordsLearned = wordsLearned;
        this.totalLoginStreak = totalLoginStreak;
        this.fiveDayLoginStreak = fiveDayLoginStreak;
        this.lastClaimDate = lastClaimDate;
    }

    public String getLastClaimDate() {
        return lastClaimDate;
    }

    public void setLastClaimDate(String lastClaimDate) {
        this.lastClaimDate = lastClaimDate;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(int totalXp) {
        this.totalXp = totalXp;
    }

    public int getCurrentXp() {
        return currentXp;
    }

    public void setCurrentXp(int currentXp) {
        this.currentXp = currentXp;
    }

    public int getLevelUpXp() {
        return levelUpXp;
    }

    public void setLevelUpXp(int levelUpXp) {
        this.levelUpXp = levelUpXp;
    }

    public int getWordsLearned() {
        return wordsLearned;
    }

    public void setWordsLearned(int wordsLearned) {
        this.wordsLearned = wordsLearned;
    }

    public int getTotalLoginStreak() {
        return totalLoginStreak;
    }

    public void setTotalLoginStreak(int totalLoginStreak) {
        this.totalLoginStreak = totalLoginStreak;
    }

    public int getFiveDayLoginStreak() {
        return fiveDayLoginStreak;
    }

    public void setFiveDayLoginStreak(int fiveDayLoginStreak) {
        this.fiveDayLoginStreak = fiveDayLoginStreak;
    }
}

