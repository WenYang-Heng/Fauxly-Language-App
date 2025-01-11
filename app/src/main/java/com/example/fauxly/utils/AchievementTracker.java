package com.example.fauxly.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.fauxly.database.DatabaseRepository;

public class AchievementTracker {

    private final Context context;
    private final DatabaseRepository repository;

    public AchievementTracker(Context context) {
        this.context = context;
        this.repository = new DatabaseRepository(context);
    }

    public void evaluateAchievements(int userId) {
        // Achievement 1: First Lesson
        if (!repository.isAchievementUnlocked(userId, 1) &&
                repository.getCompletedLessonsCount(userId) == 1) {
            unlockAchievement(userId, 1);
        }

        // Achievement 2: Quiz Master
        if (!repository.isAchievementUnlocked(userId, 2) &&
                repository.getCompletedQuizzesCount(userId) == 1) {
            unlockAchievement(userId, 2);
        }

        // Achievement 3: Daily Devotee
        if (!repository.isAchievementUnlocked(userId, 3) &&
                repository.getUserStatsById(userId).getTotalLoginStreak() == 1) { // Check if the user has claimed the daily reward
            unlockAchievement(userId, 3);
        }

        // Achievement 3: Level Up!
        if (!repository.isAchievementUnlocked(userId, 4) &&
                repository.getUserStatsById(userId).getCurrentLevel() == 2) {
            unlockAchievement(userId, 4);
        }

        // Achievement 4: Steady Learner
        if (!repository.isAchievementUnlocked(userId, 5) &&
                repository.getCompletedLessonsCount(userId) == 5) {
            unlockAchievement(userId, 5);
        }
    }

    private void unlockAchievement(int userId, int achievementId) {
        repository.unlockAchievement(userId, achievementId);
        String achievementTitle = repository.getAchievementTitle(achievementId);
        showToast("Achievement Unlocked: " + achievementTitle);
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
