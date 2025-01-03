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
                "word TEXT, " +  // Individual word (if applicable)
                "pronunciation TEXT, " +  // English pronunciation for the word
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
        insertDefaultLessonContent(db);
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
        db.execSQL("INSERT INTO lesson_content (lesson_id, content_order, content_type, content_data, path, word, pronunciation) VALUES " +
                "('BL1', 1, 'Text', 'Japanese uses three writing systems: Hiragana, Katakana, and Kanji.', NULL, NULL, NULL), " +
                "('BL1', 2, 'Text', 'Hiragana is phonetic and used for native Japanese words.', NULL, NULL, NULL), " +
                "('BL1', 3, 'Text', 'The first five Hiragana characters are あ, い, う, え, お.', NULL, NULL, NULL), " +
                "('BL1', 3, 'Word', NULL, 'bl1_ah', 'あ', 'A (ah)'), " +
                "('BL1', 4, 'Word', NULL, 'bl1_ee', 'い', 'I (ee)'), " +
                "('BL1', 5, 'Word', NULL, 'bl1_uu', 'う', 'U (oo)'), " +
                "('BL1', 6, 'Word', NULL, 'bl1_eh', 'え', 'E (eh)'), " +
                "('BL1', 7, 'Word', NULL, 'bl1_oh', 'お', 'O (oh)');");

        // Lesson 2 Content
        db.execSQL("INSERT INTO lesson_content (lesson_id, content_order, content_type, content_data, path, word, pronunciation) VALUES " +
                "('BL2', 1, 'Text', 'Learn basic greetings in Japanese.', NULL, NULL, NULL), " +
                "('BL2', 2, 'Word', 'Hello', 'bl2_konnichiwa', 'こんにちは', 'Konnichiwa (koh-nee-chee-wah)'), " +
                "('BL2', 3, 'Word', 'Good Morning', 'bl2_ohayou_gozaimasu', 'おはようございます', 'Ohayou gozaimasu (oh-ha-yoh goh-zai-mahs)'), " +
                "('BL2', 4, 'Word', 'Good Evening', 'bl2_konbanwa', 'こんばんは', 'Konbanwa (kohn-bahn-wah)'), " +
                "('BL2', 5, 'Word', 'Good Night', 'bl2_oyasuminasai', 'おやすみなさい', 'Oyasuminasai (oh-yah-soo-mee-nah-sai)'), " +
                "('BL2', 6, 'Word', 'Thank you', 'bl2_arigatou', 'ありがとう', 'Arigatou (ah-ree-gah-toh)');");

        // Lesson 3 Content
        db.execSQL("INSERT INTO lesson_content (lesson_id, content_order, content_type, content_data, path, word, pronunciation) VALUES " +
                "('BL3', 1, 'Text', 'Learn numbers 1-10 in Japanese.', NULL, NULL, NULL), " +
                "('BL3', 2, 'Word', 'One', 'bl3_ichi', 'いち', 'Ichi (ee-chee)'), " +
                "('BL3', 3, 'Word', 'Two', 'bl3_ni', 'に', 'Ni (nee)'), " +
                "('BL3', 4, 'Word', 'Three', 'bl3_san', 'さん', 'San (sahn)'), " +
                "('BL3', 5, 'Word', 'Four', 'bl3_yon', 'よん', 'Yon (yohn)'), " +
                "('BL3', 6, 'Word', 'Five', 'bl3_go', 'ご', 'Go (goh)'), " +
                "('BL3', 7, 'Word', 'Six', 'bl3_roku', 'ろく', 'Roku (roh-koo)'), " +
                "('BL3', 8, 'Word', 'Seven', 'bl3_nana', 'なな', 'Nana (nah-nah)'), " +
                "('BL3', 9, 'Word', 'Eight', 'bl3_hachi', 'はち', 'Hachi (hah-chee)'), " +
                "('BL3', 10, 'Word', 'Nine', 'bl3_kyuu', 'きゅう', 'Kyuu (kyoo)'), " +
                "('BL3', 11, 'Word', 'Ten', 'bl3_juu', 'じゅう', 'Juu (joo)'), " +
                "('BL3', 12, 'Word', 'Asking Time', 'bl3_asking_time', 'いま なんじ ですか？', 'Ima nanji desu ka? (What time is it?)');");


        // Log the completion of data insertion
        android.util.Log.d("DatabaseHelper", "Default lesson content with updated words and pronunciation inserted.");
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
