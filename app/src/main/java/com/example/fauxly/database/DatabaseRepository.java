package com.example.fauxly.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fauxly.model.FlashCard;
import com.example.fauxly.model.FlashCardItem;
import com.example.fauxly.model.Lesson;
import com.example.fauxly.model.LessonContent;
import com.example.fauxly.model.User;
import com.example.fauxly.model.UserLanguage;
import com.example.fauxly.model.UserStats;

import java.util.ArrayList;
import java.util.List;

public class DatabaseRepository {

    private DatabaseHelper dbHelper;

    public DatabaseRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Insert into language table
    public void insertLanguage(String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);

        long newRowId = db.insert("language", null, values);
        if (newRowId != -1) {
            android.util.Log.d("DatabaseRepository", "Language inserted successfully with ID: " + newRowId);
        } else {
            android.util.Log.e("DatabaseRepository", "Error inserting language.");
        }
    }

    // Insert into user table
    public void insertUser(String name, String email, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);

        long newRowId = db.insert("user", null, values);
        if (newRowId != -1) {
            android.util.Log.d("DatabaseRepository", "User inserted successfully with ID: " + newRowId);
        } else {
            android.util.Log.e("DatabaseRepository", "Error inserting user.");
        }
    }

    // Insert into question table
    public void insertQuestion(String questionId, String title, int sequence, int languageId, String lessonId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("question_id", questionId);
        values.put("title", title);
        values.put("sequence", sequence);
        values.put("language_id", languageId);
        values.put("lesson_id", lessonId);

        long newRowId = db.insert("question", null, values);
        if (newRowId != -1) {
            android.util.Log.d("DatabaseRepository", "Question inserted successfully.");
        } else {
            android.util.Log.e("DatabaseRepository", "Error inserting question.");
        }
    }

    // Add similar methods for other tables...
    public Lesson getLesson(String lessonId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Lesson lesson = null;

        Cursor cursor = db.rawQuery("SELECT * FROM lesson WHERE lesson_id = ?", new String[]{lessonId});
        if (cursor.moveToFirst()) {
            lesson = new Lesson(
                    cursor.getString(cursor.getColumnIndexOrThrow("lesson_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("level")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("lesson_number")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("language_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("lesson_title"))
            );
        }
        cursor.close();
        return lesson;
    }

    public List<Lesson> getLessonsByProficiencyAndLanguage(String proficiencyLevel, int languageId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Lesson> lessons = new ArrayList<>();

        // Build the query
        String query = "SELECT * FROM lesson WHERE level = ? AND language_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{proficiencyLevel, String.valueOf(languageId)});

        // Iterate through the results and populate the list
        if (cursor.moveToFirst()) {
            do {
                lessons.add(new Lesson(
                        cursor.getString(cursor.getColumnIndexOrThrow("lesson_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("level")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("lesson_number")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("language_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("lesson_title"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lessons;
    }


    public List<LessonContent> getLessonContents(String lessonId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<LessonContent> contents = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM lesson_content WHERE lesson_id = ? ORDER BY content_order", new String[]{lessonId});
        while (cursor.moveToNext()) {
            contents.add(new LessonContent(
                    cursor.getInt(cursor.getColumnIndexOrThrow("content_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("lesson_id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("content_order")),
                    cursor.getString(cursor.getColumnIndexOrThrow("content_type")),
                    cursor.getString(cursor.getColumnIndexOrThrow("content_data")),
                    cursor.getString(cursor.getColumnIndexOrThrow("path")),
                    cursor.getString(cursor.getColumnIndexOrThrow("word")),
                    cursor.getString(cursor.getColumnIndexOrThrow("pronunciation"))
            ));
        }
        cursor.close();
        return contents;
    }

    // Get User by ID
    public User getUserById(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;

        Cursor cursor = db.query(
                "user",               // Table name
                new String[]{"user_id", "name", "email", "password"}, // Columns to return
                "user_id = ?",        // WHERE clause
                new String[]{String.valueOf(userId)}, // WHERE arguments
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("password"))
            );
            cursor.close();
        }

        return user;
    }

    // Get UserStats by User ID
    public UserStats getUserStatsById(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        UserStats stats = null;

        Cursor cursor = db.query(
                "user_stats",         // Table name
                new String[]{"user_id", "current_level", "total_xp", "current_xp", "level_up_xp", "words_learned", "total_login_streak", "five_day_login_streak", "last_claim_date"}, // Columns to return
                "user_id = ?",        // WHERE clause
                new String[]{String.valueOf(userId)}, // WHERE arguments
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            stats = new UserStats(
                    cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("current_level")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("total_xp")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("current_xp")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("level_up_xp")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("words_learned")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("total_login_streak")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("five_day_login_streak")),
                    cursor.getString(cursor.getColumnIndexOrThrow("last_claim_date"))
            );
            cursor.close();
        }

        return stats;
    }

    public UserLanguage getUserLanguageByUserId(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        UserLanguage userLanguage = null;

        String query = "SELECT ul.user_id, ul.language_id, l.name AS language_name, ul.proficiency_level " +
                "FROM user_language ul " +
                "JOIN language l ON ul.language_id = l.language_id " +
                "WHERE ul.user_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            userLanguage = new UserLanguage(
                    cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("language_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("language_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("proficiency_level"))
            );
            cursor.close();
        }

        return userLanguage;
    }

    public List<FlashCard> getFlashCardsByUserId(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<FlashCard> flashCards = new ArrayList<>();

        // Query to fetch flashcards for the given user
        String query = "SELECT flashcard_id, name FROM flashcard WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                int flashcardId = cursor.getInt(cursor.getColumnIndexOrThrow("flashcard_id"));
                String flashcardName = cursor.getString(cursor.getColumnIndexOrThrow("name"));

                // Count the number of items in each flashcard
                int itemCount = getFlashCardItemCount(flashcardId);

                // Add the flashcard to the list
                flashCards.add(new FlashCard(flashcardId, flashcardName, itemCount));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return flashCards;
    }

    // Helper method to count the number of items in a flashcard
    private int getFlashCardItemCount(int flashcardId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) AS itemCount FROM flashcard_content WHERE flashcard_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(flashcardId)});

        int itemCount = 0;
        if (cursor.moveToFirst()) {
            itemCount = cursor.getInt(cursor.getColumnIndexOrThrow("itemCount"));
        }
        cursor.close();

        return itemCount;
    }

    public List<FlashCardItem> getFlashCardItemsByFlashCardId(int flashCardId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<FlashCardItem> flashCardItems = new ArrayList<>();

        // Query to fetch flashcard items for the given flashCardId
        String query = "SELECT card_id, word, pronunciation, translation, path, flashcard_id FROM flashcard_content WHERE flashcard_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(flashCardId)});

        if (cursor.moveToFirst()) {
            do {
                flashCardItems.add(new FlashCardItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow("card_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("word")),
                        cursor.getString(cursor.getColumnIndexOrThrow("pronunciation")),
                        cursor.getString(cursor.getColumnIndexOrThrow("translation")),
                        cursor.getString(cursor.getColumnIndexOrThrow("path")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("flashcard_id"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return flashCardItems;
    }

    public void insertFlashCard(String name, int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("user_id", userId);

        long newRowId = db.insert("flashcard", null, values);
        if (newRowId != -1) {
            android.util.Log.d("DatabaseRepository", "Flashcard inserted successfully with ID: " + newRowId);
        } else {
            android.util.Log.e("DatabaseRepository", "Error inserting flashcard.");
        }
    }

    public void insertFlashCardContent(int flashCardId, String word, String pronunciation, String translation, String path) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("word", word);
        values.put("pronunciation", pronunciation);
        values.put("translation", translation);
        values.put("path", path);
        values.put("flashcard_id", flashCardId);

        long newRowId = db.insert("flashcard_content", null, values);
        if (newRowId != -1) {
            android.util.Log.d("DatabaseRepository", "Flashcard content inserted successfully for FlashCard ID: " + flashCardId);
        } else {
            android.util.Log.e("DatabaseRepository", "Error inserting flashcard content.");
        }
    }


    public void deleteFlashCardById(int flashCardId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Delete flashcard content first
        db.delete("flashcard_content", "flashcard_id = ?", new String[]{String.valueOf(flashCardId)});

        // Delete the flashcard
        db.delete("flashcard", "flashcard_id = ?", new String[]{String.valueOf(flashCardId)});

        android.util.Log.d("DatabaseRepository", "Flashcard deleted successfully with ID: " + flashCardId);
    }


    public void resetUserStreak(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("five_day_login_streak", 0);
        values.put("last_claim_date", (String) null); // Reset last claim date

        db.update("user_stats", values, "user_id = ?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public void updateUserStreakAndDate(int userId, int claimedDay, String currentDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("five_day_login_streak", claimedDay);
        values.put("last_claim_date", currentDate); // Update last claim date

        db.update("user_stats", values, "user_id = ?", new String[]{String.valueOf(userId)});
        db.close();
    }



}
