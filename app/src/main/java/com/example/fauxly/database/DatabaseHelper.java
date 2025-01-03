package com.example.fauxly.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MAD2007.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // user table
        db.execSQL("CREATE TABLE user (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "email TEXT, " +
                "password TEXT" +
                ");");

        // user_stats table
        db.execSQL("CREATE TABLE user_stats (" +
                "user_id INTEGER, " +
                "current_level INTEGER, " +
                "total_xp INTEGER, " +
                "current_xp INTEGER, " +
                "level_up_xp INTEGER DEFAULT 1000, " +
                "words_learned INTEGER, " +
                "total_login_streak INTEGER, " +
                "five_day_login_streak INTEGER, " +
                "FOREIGN KEY(user_id) REFERENCES user(user_id)" +
                ");");

        // language table
        db.execSQL("CREATE TABLE language (" +
                "language_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT" +
                ");");

        // user_language table
        db.execSQL("CREATE TABLE user_language (" +
                "user_id INTEGER, " +
                "language_id INTEGER, " +
                "proficiency_level TEXT, " +  //  Beginner, Intermediate, Advanced
                "FOREIGN KEY(user_id) REFERENCES user(user_id), " +
                "FOREIGN KEY(language_id) REFERENCES language(language_id)" +
                ");");

        // achievement table
        db.execSQL("CREATE TABLE achievement (" +
                "achievement_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "description TEXT" +
                ");");

        // user_achievement table
        db.execSQL("CREATE TABLE user_achievement (" +
                "user_id INTEGER, " +
                "achievement_id INTEGER, " +
                "date_achieved DATE, " +
                "FOREIGN KEY(user_id) REFERENCES user(user_id), " +
                "FOREIGN KEY(achievement_id) REFERENCES achievement(achievement_id)" +
                ");");

        // lesson table
        db.execSQL("CREATE TABLE lesson (" +
                "lesson_id TEXT PRIMARY KEY, " +  // BL1 for Beginner Lesson 1
                "level TEXT, " +
                "lesson_number INTEGER, " +
                "language_id INTEGER, " +
                "lesson_title TEXT, " +
                "FOREIGN KEY(language_id) REFERENCES language(language_id)" +
                ");");

        // lesson_content table
        db.execSQL("CREATE TABLE lesson_content (" +
                "content_id INTEGER PRIMARY KEY AUTOINCREMENT, " +  // Auto-incrementing ID for each content piece
                "lesson_id TEXT, " +
                "content_order INTEGER, " +  // order of lesson
                "content_type TEXT, " +  // Text, Image, Video, etc.
                "content_data TEXT, " +
                "path TEXT, " +  // File path or URL for multimedia content
                "FOREIGN KEY(lesson_id) REFERENCES lesson(lesson_id)" +  // Links to lesson_id in lesson table
                ");");

        // user_lesson table
        db.execSQL("CREATE TABLE user_lesson (" +
                "user_id INTEGER, " +
                "lesson_id TEXT, " +
                "isComplete INTEGER, " +
                "FOREIGN KEY(user_id) REFERENCES user(user_id), " +
                "FOREIGN KEY(lesson_id) REFERENCES lesson(lesson_id)" +
                ");");


        // Create the question table
        db.execSQL("CREATE TABLE question (" +
                "question_id TEXT PRIMARY KEY, " +  //  BL1Q1 for Beginner Lesson 1 Question 1
                "title TEXT, " +
                "sequence INTEGER, " +
                "language_id INTEGER, " +
                "lesson_id TEXT, " +
                "FOREIGN KEY(language_id) REFERENCES language(language_id), " +
                "FOREIGN KEY(lesson_id) REFERENCES lesson(lesson_id)" +
                ");");


        // question_option table
        db.execSQL("CREATE TABLE question_option (" +
                "option_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "question_id TEXT, " +
                "option_text TEXT, " +
                "isCorrect INTEGER, " +
                "FOREIGN KEY(question_id) REFERENCES question(question_id)" +
                ");");

        android.util.Log.d("DatabaseHelper", "All tables created successfully.");

        insertDefaultLanguages(db);
        insertDefaultLessons(db);
    }

    private void insertDefaultLanguages(SQLiteDatabase db) {
        db.execSQL("INSERT INTO language (name) VALUES ('Japanese');");
        android.util.Log.d("DatabaseHelper", "Default language inserted.");
    }

    private void insertDefaultLessons(SQLiteDatabase db) {
        // Insert lessons into the lesson table
        db.execSQL("INSERT INTO lesson (lesson_id, level, lesson_number, language_id, lesson_title) " +
                "VALUES ('BL1', 'Beginner', 1, 1, 'Japanese Writing Systems');");

        db.execSQL("INSERT INTO lesson (lesson_id, level, lesson_number, language_id, lesson_title) " +
                "VALUES ('BL2', 'Beginner', 2, 1, 'Basic Greetings');");

        db.execSQL("INSERT INTO lesson (lesson_id, level, lesson_number, language_id, lesson_title) " +
                "VALUES ('BL3', 'Beginner', 3, 1, 'Numbers and Time');");

        db.execSQL("INSERT INTO lesson (lesson_id, level, lesson_number, language_id, lesson_title) " +
                "VALUES ('BL4', 'Beginner', 4, 1, 'Self-Introduction');");

        android.util.Log.d("DatabaseHelper", "Default lessons inserted.");
    }

    private void insertDefaultLessonContent(SQLiteDatabase db) {
        // Lesson 1 Content
        db.execSQL("INSERT INTO lesson_content (lesson_id, content_order, content_type, content_data, path) VALUES " +
                "('BL1', 1, 'Text', 'Japanese uses three writing systems: Hiragana, Katakana, and Kanji.', NULL), " +
                "('BL1', 2, 'Text', 'Hiragana is phonetic and used for native Japanese words.', NULL), " +
                "('BL1', 3, 'Text', 'The first five Hiragana characters are あ, い, う, え, お.', NULL), " +
                "('BL1', 4, 'Image', 'Hiragana chart for reference.', '/path/to/hiragana_chart.png');");

        // Lesson 2 Content
        db.execSQL("INSERT INTO lesson_content (lesson_id, content_order, content_type, content_data, path) VALUES " +
                "('BL2', 1, 'Text', 'Learn basic greetings in Japanese.', NULL), " +
                "('BL2', 2, 'Text', 'Hello: こんにちは (Konnichiwa)', NULL), " +
                "('BL2', 3, 'Text', 'Good morning: おはようございます (Ohayou gozaimasu)', NULL), " +
                "('BL2', 4, 'Text', 'Good evening: こんばんは (Konbanwa)', NULL), " +
                "('BL2', 5, 'Text', 'Thank you: ありがとう (Arigatou)', NULL);");

        // Lesson 3 Content
        db.execSQL("INSERT INTO lesson_content (lesson_id, content_order, content_type, content_data, path) VALUES " +
                "('BL3', 1, 'Text', 'Learn numbers 1-10 in Japanese.', NULL), " +
                "('BL3', 2, 'Text', '1: いち (Ichi), 2: に (Ni), 3: さん (San), 4: よん (Yon), 5: ご (Go)', NULL), " +
                "('BL3', 3, 'Text', '6: ろく (Roku), 7: なな (Nana), 8: はち (Hachi), 9: きゅう (Kyuu), 10: じゅう (Juu)', NULL), " +
                "('BL3', 4, 'Text', 'Asking time: いま なんじ ですか？ (Ima nanji desu ka?)', NULL), " +
                "('BL3', 5, 'Text', 'Example: It’s 2 o’clock: にじです (Ni-ji desu).', NULL);");

        // Log the completion of data insertion
        android.util.Log.d("DatabaseHelper", "Default lesson content inserted.");
    }



    private void insertDefaultAchievementData(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables if they exist and recreate them
        db.execSQL("DROP TABLE IF EXISTS user;");
        db.execSQL("DROP TABLE IF EXISTS user_stats;");
        db.execSQL("DROP TABLE IF EXISTS achievement;");
        db.execSQL("DROP TABLE IF EXISTS user_achievement;");
        db.execSQL("DROP TABLE IF EXISTS lesson;");
        db.execSQL("DROP TABLE IF EXISTS user_lesson;");
        db.execSQL("DROP TABLE IF EXISTS question;");
        db.execSQL("DROP TABLE IF EXISTS question_option;");
        db.execSQL("DROP TABLE IF EXISTS language;");
        db.execSQL("DROP TABLE IF EXISTS user_language;");
        onCreate(db);
    }
}
