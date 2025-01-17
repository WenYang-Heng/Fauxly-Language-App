package com.example.fauxly.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fauxly.model.Achievement;
import com.example.fauxly.model.DailyWord;
import com.example.fauxly.model.FlashCard;
import com.example.fauxly.model.FlashCardItem;
import com.example.fauxly.model.Lesson;
import com.example.fauxly.model.LessonContent;
import com.example.fauxly.model.Option;
import com.example.fauxly.model.Quiz;
import com.example.fauxly.model.QuizContent;
import com.example.fauxly.model.User;
import com.example.fauxly.model.UserLanguage;
import com.example.fauxly.model.UserStats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    public long insertUser(String name, String email, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long userId = -1;

        db.beginTransaction();
        try {
            ContentValues userValues = new ContentValues();
            userValues.put("name", name);
            userValues.put("email", email);
            userValues.put("password", password);
            userId = db.insert("user", null, userValues);

            if (userId > 0) {
                // Insert default user stats for the new user
                ContentValues statsValues = new ContentValues();
                statsValues.put("user_id", userId);
                statsValues.put("current_level", 1);
                statsValues.put("total_xp", 0);
                statsValues.put("current_xp", 0);
                statsValues.put("level_up_xp", 1000);
                statsValues.put("words_learned", 0);
                statsValues.put("total_login_streak", 0);
                statsValues.put("five_day_login_streak", 0);
                statsValues.put("last_claim_date", (String) null);

                db.insert("user_stats", null, statsValues);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            userId = -1; // Reset userId if any error occurs
        } finally {
            db.endTransaction();
        }

        return userId;
    }

    public String getUserIdByEmailAndPassword(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT user_id FROM user WHERE email = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});

        String userId = null;
        if (cursor.moveToFirst()) {
            userId = cursor.getString(0); // Fetch the user ID
        }

        cursor.close();
        db.close();

        return userId; // Returns null if no match is found
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void insertUserLanguage(int userId, int languageId, String proficiencyLevel) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Check if the user already has a language entry
        Cursor cursor = db.rawQuery(
                "SELECT * FROM user_language WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("language_id", languageId);
        values.put("proficiency_level", proficiencyLevel);

        if (cursor.moveToFirst()) {
            // Update the existing entry
            db.update("user_language", values, "user_id = ?", new String[]{String.valueOf(userId)});
        } else {
            // Insert a new entry
            db.insert("user_language", null, values);
        }

        cursor.close();
        db.close();
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
//    public Lesson getLesson(String lessonId) {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Lesson lesson = null;
//
//        Cursor cursor = db.rawQuery("SELECT * FROM lesson WHERE lesson_id = ?", new String[]{lessonId});
//        if (cursor.moveToFirst()) {
//            lesson = new Lesson(
//                    cursor.getString(cursor.getColumnIndexOrThrow("lesson_id")),
//                    cursor.getString(cursor.getColumnIndexOrThrow("level")),
//                    cursor.getInt(cursor.getColumnIndexOrThrow("lesson_number")),
//                    cursor.getInt(cursor.getColumnIndexOrThrow("language_id")),
//                    cursor.getString(cursor.getColumnIndexOrThrow("lesson_title"))
//            );
//        }
//        cursor.close();
//        return lesson;
//    }

//    public List<Lesson> getLessonsByProficiencyAndLanguage(String proficiencyLevel, int languageId) {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        List<Lesson> lessons = new ArrayList<>();
//
//        // Build the query
//        String query = "SELECT * FROM lesson WHERE level = ? AND language_id = ?";
//        Cursor cursor = db.rawQuery(query, new String[]{proficiencyLevel, String.valueOf(languageId)});
//
//        // Iterate through the results and populate the list
//        if (cursor.moveToFirst()) {
//            do {
//                lessons.add(new Lesson(
//                        cursor.getString(cursor.getColumnIndexOrThrow("lesson_id")),
//                        cursor.getString(cursor.getColumnIndexOrThrow("level")),
//                        cursor.getInt(cursor.getColumnIndexOrThrow("lesson_number")),
//                        cursor.getInt(cursor.getColumnIndexOrThrow("language_id")),
//                        cursor.getString(cursor.getColumnIndexOrThrow("lesson_title"))
//                ));
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return lessons;
//    }

    public List<Lesson> getLessonsWithCompletionStatus(int userId, String proficiencyLevel, int languageId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Lesson> lessons = new ArrayList<>();

        String query = "SELECT l.lesson_id, l.level, l.lesson_number, l.language_id, l.lesson_title, " +
                "COALESCE(ul.isComplete, 0) AS isComplete " +
                "FROM lesson l " +
                "LEFT JOIN user_lesson ul ON l.lesson_id = ul.lesson_id AND ul.user_id = ? " +
                "WHERE l.level = ? AND l.language_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), proficiencyLevel, String.valueOf(languageId)});

        if (cursor.moveToFirst()) {
            do {
                lessons.add(new Lesson(
                        cursor.getString(cursor.getColumnIndexOrThrow("lesson_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("level")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("lesson_number")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("language_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("lesson_title")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("isComplete")) == 1
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lessons;
    }

    public int getCompletedLessonsCount(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;

        String query = "SELECT COUNT(*) FROM user_lesson WHERE user_id = ? AND isComplete = 1";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return count;
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
                    cursor.getString(cursor.getColumnIndexOrThrow("pronunciation")),
                    cursor.getString(cursor.getColumnIndexOrThrow("translation"))
            ));
        }
        cursor.close();
        return contents;
    }

    public boolean isUserLessonExists(int userId, String lessonId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "user_lesson",
                new String[]{"user_id", "lesson_id"},
                "user_id = ? AND lesson_id = ?",
                new String[]{String.valueOf(userId), lessonId},
                null, null, null
        );

        boolean exists = cursor.getCount() > 0; // Check if the cursor has results
        cursor.close();
        return exists;
    }

    public void insertUserLesson(int userId, String lessonId, int isComplete) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("lesson_id", lessonId);
        values.put("isComplete", isComplete);

        db.insert("user_lesson", null, values);
    }

    public void updateUserLessonCompletion(int userId, String lessonId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("isComplete", 1); // Set isComplete to true (1)

        db.update("user_lesson", values, "user_id = ? AND lesson_id = ?", new String[]{String.valueOf(userId), lessonId});
        db.close();
    }

    public boolean isLessonCompleted(int userId, String lessonId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT isComplete FROM user_lesson WHERE user_id = ? AND lesson_id = ?",
                new String[]{String.valueOf(userId), lessonId});

        boolean isCompleted = false;
        if (cursor.moveToFirst()) {
            isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("isComplete")) == 1;
        }
        cursor.close();
        return isCompleted;
    }

    public boolean isQuizCompleted(int userId, String quizId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT isComplete FROM user_quiz WHERE user_id = ? AND quiz_id = ?",
                new String[]{String.valueOf(userId), quizId});

        boolean isCompleted = false;
        if (cursor.moveToFirst()) {
            isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("isComplete")) == 1;
        }
        cursor.close();
        return isCompleted;
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

    public String addWordToFlashcard(int userId, String word, String pronunciation, String translation, String audioPath) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the current language for the user
        LanguageInfo languageInfo = getUserLanguageInfo(userId, db);
        if (languageInfo == null) {
            return "No language set for the user.";
        }

        // Create the flashcard for the language
        int flashcardId = getOrCreateFlashcard(userId, languageInfo.languageName);

        // Check if the word already exists in the flashcard
        if (isWordInFlashcard(flashcardId, word, db)) {
            return "The word is already in your flashcard.";
        }

        // Add the word to the flashcard content
        insertFlashCardContent(flashcardId, word, pronunciation, translation, audioPath);
        return "Word added successfully to your flashcard.";
    }

    private LanguageInfo getUserLanguageInfo(int userId, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery(
                "SELECT ul.language_id, l.name " +
                        "FROM user_language ul " +
                        "JOIN language l ON ul.language_id = l.language_id " +
                        "WHERE ul.user_id = ?",
                new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            int languageId = cursor.getInt(cursor.getColumnIndexOrThrow("language_id"));
            String languageName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            cursor.close();
            return new LanguageInfo(languageId, languageName);
        }

        if (cursor != null) {
            cursor.close();
        }
        return null; // No language found
    }

    private int getOrCreateFlashcard(int userId, String languageName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Check if the flashcard already exists
        Cursor cursor = db.rawQuery(
                "SELECT flashcard_id FROM flashcard WHERE user_id = ? AND name = ?",
                new String[]{String.valueOf(userId), languageName});

        if (cursor.moveToFirst()) {
            int flashcardId = cursor.getInt(cursor.getColumnIndexOrThrow("flashcard_id"));
            cursor.close();
            return flashcardId;
        }
        cursor.close();

        // Flashcard does not exist, create a new flashcard
        insertFlashCard(languageName, userId);

        // Fetch the new flashcard ID
        cursor = db.rawQuery(
                "SELECT flashcard_id FROM flashcard WHERE user_id = ? AND name = ?",
                new String[]{String.valueOf(userId), languageName});

        if (cursor.moveToFirst()) {
            int newFlashcardId = cursor.getInt(cursor.getColumnIndexOrThrow("flashcard_id"));
            cursor.close();
            return newFlashcardId;
        }

        if (cursor != null) {
            cursor.close();
        }
        throw new RuntimeException("Failed to create or retrieve the flashcard.");
    }

    private boolean isWordInFlashcard(int flashcardId, String word, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM flashcard_content WHERE flashcard_id = ? AND word = ?",
                new String[]{String.valueOf(flashcardId), word});

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    private static class LanguageInfo {
        int languageId;
        String languageName;

        LanguageInfo(int languageId, String languageName) {
            this.languageId = languageId;
            this.languageName = languageName;
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

    // In DatabaseRepository.java
    public List<Quiz> getQuizzesWithCompletionStatus(int userId, String level, int languageId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Quiz> quizzes = new ArrayList<>();

        String query = "SELECT q.quiz_id, q.quiz_title, q.language_id, q.level, " +
                "CASE WHEN uq.isComplete = 1 THEN 1 ELSE 0 END AS isComplete " +
                "FROM quiz q " +
                "LEFT JOIN user_quiz uq ON q.quiz_id = uq.quiz_id AND uq.user_id = ? " +
                "WHERE q.level = ? AND q.language_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), level, String.valueOf(languageId)});

        if (cursor.moveToFirst()) {
            do {
                String quizId = cursor.getString(cursor.getColumnIndexOrThrow("quiz_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("quiz_title")); // Updated to quiz_title
                int langId = cursor.getInt(cursor.getColumnIndexOrThrow("language_id"));
                String quizLevel = cursor.getString(cursor.getColumnIndexOrThrow("level"));
                boolean isComplete = cursor.getInt(cursor.getColumnIndexOrThrow("isComplete")) == 1;

                quizzes.add(new Quiz(quizId, title, langId, quizLevel, isComplete));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return quizzes;
    }

    public List<QuizContent> getQuizContents(String quizId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<QuizContent> contents = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM quiz_content WHERE quiz_id = ? ORDER BY sequence", new String[]{quizId});
        while (cursor.moveToNext()) {
            contents.add(new QuizContent(
                    cursor.getInt(cursor.getColumnIndexOrThrow("content_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("quiz_id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("sequence")),
                    cursor.getString(cursor.getColumnIndexOrThrow("content_type")),
                    cursor.getString(cursor.getColumnIndexOrThrow("title"))
            ));
        }
        cursor.close();
        return contents;
    }

    public List<Option> getOptionsByContentId(int contentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Option> options = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM option WHERE content_id = ?", new String[]{String.valueOf(contentId)});
        while (cursor.moveToNext()) {
            options.add(new Option(
                    cursor.getInt(cursor.getColumnIndexOrThrow("option_id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("content_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("option_text")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("is_correct")) == 1
            ));
        }
        cursor.close();
        return options;
    }

    public boolean isCorrectOption(int optionId, int contentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT is_correct FROM option WHERE option_id = ? AND content_id = ?",
                new String[]{String.valueOf(optionId), String.valueOf(contentId)});

        boolean isCorrect = false;
        if (cursor.moveToFirst()) {
            isCorrect = cursor.getInt(cursor.getColumnIndexOrThrow("is_correct")) == 1;
        }
        cursor.close();
        return isCorrect;
    }

    // Check if the user has already attempted the quiz
    public boolean isUserQuizExists(int userId, String quizId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM user_quiz WHERE user_id = ? AND quiz_id = ?",
                new String[]{String.valueOf(userId), quizId}
        );

        boolean exists = cursor.moveToFirst(); // Check if the query returns any result
        cursor.close();
        return exists;
    }

    // Insert a new quiz entry for the user
    public void insertUserQuiz(int userId, String quizId, int isComplete) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("quiz_id", quizId);
        values.put("isComplete", isComplete);

        long result = db.insert("user_quiz", null, values);

        if (result != -1) {
            android.util.Log.d("DatabaseRepository", "User quiz inserted successfully for user ID " + userId + " and quiz ID " + quizId);
        } else {
            android.util.Log.e("DatabaseRepository", "Failed to insert user quiz for user ID " + userId + " and quiz ID " + quizId);
        }
    }

    public String getCorrectOptionText(int contentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String correctAnswer = null;

        String query = "SELECT option_text FROM option WHERE content_id = ? AND is_correct = 1";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(contentId)});

        if (cursor.moveToFirst()) {
            correctAnswer = cursor.getString(cursor.getColumnIndexOrThrow("option_text"));
        }
        cursor.close();
        return correctAnswer;
    }

    public void updateUserQuizCompletion(int userId, String quizId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("isComplete", 1); // Set isComplete to true (1)

        db.update("user_quiz", values, "user_id = ? AND quiz_id = ?", new String[]{String.valueOf(userId), quizId});
        db.close();
    }

    public int getCompletedQuizzesCount(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM user_quiz WHERE user_id = ? AND isComplete = 1";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();

        return count;
    }

    public List<Achievement> getAllAchievementsWithUserStatus(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Achievement> achievements = new ArrayList<>();

        String query = "SELECT a.achievement_id, a.title, a.description, " +
                "CASE WHEN ua.date_achieved IS NOT NULL THEN 1 ELSE 0 END AS isAchieved, " +
                "ua.date_achieved " +
                "FROM achievement a " +
                "LEFT JOIN user_achievement ua " +
                "ON a.achievement_id = ua.achievement_id AND ua.user_id = ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{userId})) {
            if (cursor.moveToFirst()) {
                do {
                    int achievementId = cursor.getInt(cursor.getColumnIndexOrThrow("achievement_id"));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    boolean isAchieved = cursor.getInt(cursor.getColumnIndexOrThrow("isAchieved")) == 1;
                    String dateAchieved = cursor.getString(cursor.getColumnIndexOrThrow("date_achieved"));

                    Achievement achievement = new Achievement(achievementId, title, description, isAchieved, dateAchieved);
                    achievements.add(achievement);
                } while (cursor.moveToNext());
            }
        } finally {
            db.close();
        }

        return achievements;
    }

    public void resetUserStreak(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("five_day_login_streak", 0);
        values.put("total_login_streak", 0); // Reset total login streak
        values.put("last_claim_date", (String) null); // Reset last claim date

        db.update("user_stats", values, "user_id = ?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public void updateUserStreakAndDate(int userId, int claimedDay, String currentDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Fetch current total login streak to increment it
        Cursor cursor = db.query(
                "user_stats",
                new String[]{"total_login_streak"},
                "user_id = ?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        int totalLoginStreak = 0;
        if (cursor != null && cursor.moveToFirst()) {
            totalLoginStreak = cursor.getInt(cursor.getColumnIndexOrThrow("total_login_streak"));
            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put("five_day_login_streak", claimedDay);
        values.put("total_login_streak", totalLoginStreak + 1); // Increment total login streak
        values.put("last_claim_date", currentDate); // Update last claim date

        db.update("user_stats", values, "user_id = ?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public void updateUserStatsXpAndLevel(int userId, int currentXp, int currentLevel, int totalXp) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("current_xp", currentXp);
        values.put("current_level", currentLevel);
        values.put("total_xp", totalXp);

        db.update("user_stats", values, "user_id = ?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public DailyWord getTodaysWord(int userId, int languageId, String proficiencyLevel) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String today = getCurrentDate();

        // Check if a record for today's date exists
        Cursor cursor = db.rawQuery(
                "SELECT wb.word_id, wb.word, wb.pronunciation, wb.translation, wb.audio_path, wb.language_id, wb.proficiency_level, dw.date " +
                        "FROM daily_word dw " +
                        "JOIN word_bank wb ON dw.word_id = wb.word_id " +
                        "WHERE dw.user_id = ? AND wb.language_id = ? AND wb.proficiency_level = ?",
                new String[]{String.valueOf(userId), String.valueOf(languageId), proficiencyLevel});

        if (cursor.moveToFirst()) {
            String recordDate = cursor.getString(cursor.getColumnIndexOrThrow("date"));

            if (today.equals(recordDate)) {
                // If the date matches today, return the existing word
                int wordId = cursor.getInt(cursor.getColumnIndexOrThrow("word_id"));
                String word = cursor.getString(cursor.getColumnIndexOrThrow("word"));
                String pronunciation = cursor.getString(cursor.getColumnIndexOrThrow("pronunciation"));
                String translation = cursor.getString(cursor.getColumnIndexOrThrow("translation"));
                String audioPath = cursor.getString(cursor.getColumnIndexOrThrow("audio_path"));
                cursor.close();

                return new DailyWord(wordId, word, pronunciation, translation, audioPath, languageId, proficiencyLevel);
            } else {
                // If the date is outdated, delete the old record
                db.delete("daily_word", "user_id = ? AND date = ?", new String[]{String.valueOf(userId), recordDate});
            }
        }
        cursor.close();

        // Select a new word for today if none exists
        cursor = db.rawQuery(
                "SELECT word_id, word, pronunciation, translation, audio_path, language_id, proficiency_level FROM word_bank " +
                        "WHERE language_id = ? AND proficiency_level = ? " +
                        "AND word_id NOT IN (SELECT word_id FROM user_word WHERE user_id = ? AND is_learned = 1) " +
                        "ORDER BY RANDOM() LIMIT 1",
                new String[]{String.valueOf(languageId), proficiencyLevel, String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            int wordId = cursor.getInt(cursor.getColumnIndexOrThrow("word_id"));
            String word = cursor.getString(cursor.getColumnIndexOrThrow("word"));
            String pronunciation = cursor.getString(cursor.getColumnIndexOrThrow("pronunciation"));
            String translation = cursor.getString(cursor.getColumnIndexOrThrow("translation"));
            String audioPath = cursor.getString(cursor.getColumnIndexOrThrow("audio_path"));

            // Insert today's word into the daily_word table
            ContentValues values = new ContentValues();
            values.put("word_id", wordId);
            values.put("date", today);
            values.put("user_id", userId);
            db.insertWithOnConflict("daily_word", null, values, SQLiteDatabase.CONFLICT_REPLACE);

            cursor.close();
            return new DailyWord(wordId, word, pronunciation, translation, audioPath, languageId, proficiencyLevel);
        }
        cursor.close();

        return null; // No words available
    }

    public void insertUserWord(int userId, int wordId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Check if the word is already in the user_word table
        Cursor cursor = db.rawQuery("SELECT * FROM user_word WHERE user_id = ? AND word_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(wordId)});
        if (cursor.moveToFirst()) {
            // Update the existing entry to mark it as learned
            ContentValues values = new ContentValues();
            values.put("is_learned", 1);
            values.put("date_learned", getCurrentDate());

            db.update("user_word", values, "user_id = ? AND word_id = ?",
                    new String[]{String.valueOf(userId), String.valueOf(wordId)});
        } else {
            // Insert a new entry
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("word_id", wordId);
            values.put("is_learned", 1);
            values.put("date_learned", getCurrentDate());

            db.insert("user_word", null, values);
        }
        cursor.close();
        db.close();
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private boolean isWordLearned(int userId, int wordId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT is_learned FROM user_word WHERE user_id = ? AND word_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(wordId)});
        boolean isLearned = false;
        if (cursor.moveToFirst()) {
            isLearned = cursor.getInt(0) == 1; // Check if is_learned = 1
        }
        cursor.close();
        db.close();
        return isLearned;
    }

    public int getLearnedWordCount(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM user_word WHERE user_id = ? AND is_learned = 1",
                new String[]{String.valueOf(userId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public boolean isAchievementUnlocked(int userId, int achievementId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT is_achieved FROM user_achievement WHERE user_id = ? AND achievement_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(achievementId)}
        );

        boolean isUnlocked = false;
        if (cursor.moveToFirst()) {
            isUnlocked = cursor.getInt(cursor.getColumnIndexOrThrow("is_achieved")) == 1;
        }
        cursor.close();
        db.close();
        return isUnlocked;
    }


    public void unlockAchievement(int userId, int achievementId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Check if the achievement is already unlocked
        Cursor cursor = db.rawQuery(
                "SELECT is_achieved FROM user_achievement WHERE user_id = ? AND achievement_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(achievementId)}
        );

        if (cursor.moveToFirst()) {
            int isAchieved = cursor.getInt(cursor.getColumnIndexOrThrow("is_achieved"));
            if (isAchieved == 1) {
                cursor.close();
                db.close();
                return; // Achievement is already unlocked
            }
        }
        cursor.close();

        // Unlock the achievement
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("achievement_id", achievementId);
        values.put("date_achieved", getCurrentDate());
        values.put("is_achieved", 1);

        // Insert or update the record
        db.insertWithOnConflict("user_achievement", null, values, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();
    }

    public String getAchievementTitle(int achievementId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT title FROM achievement WHERE achievement_id = ?",
                new String[]{String.valueOf(achievementId)}
        );

        String title = null;
        if (cursor.moveToFirst()) {
            title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        }
        cursor.close();
        db.close();
        return title;
    }

    public void resetFiveDayStreak(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("five_day_login_streak", 0); // Reset only the five-day streak
        db.update("user_stats", values, "user_id = ?", new String[]{String.valueOf(userId)});
        db.close();
    }



}
