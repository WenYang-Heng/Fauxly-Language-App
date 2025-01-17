package com.example.fauxly.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fauxly.model.UserStats;

public class HomePageRepository {
    private final DatabaseHelper dbHelper;

    public HomePageRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void updateUserStatsXpAndStreak(int userId, int currentXp, int currentLevel, int totalXp, int fiveDayLoginStreak, int totalLoginStreak, String lastClaimDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("current_xp", currentXp);
        values.put("current_level", currentLevel);
        values.put("total_xp", totalXp);
        values.put("five_day_login_streak", fiveDayLoginStreak);
        values.put("total_login_streak", totalLoginStreak);
        values.put("last_claim_date", lastClaimDate);

        db.update("user_stats", values, "user_id = ?", new String[]{String.valueOf(userId)});
        db.close();
    }
}
