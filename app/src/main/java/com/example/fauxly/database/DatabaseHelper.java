package com.example.fauxly.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
                "last_claim_date TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES user(user_id)" +
                ");");

        // Create flashcard table
        db.execSQL("CREATE TABLE flashcard (" +
                "flashcard_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "user_id INTEGER, " +
                "FOREIGN KEY(user_id) REFERENCES user(user_id));");

        // Create flashcard_content table
        db.execSQL("CREATE TABLE flashcard_content (" +
                "card_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "word TEXT, " +
                "pronunciation TEXT, " +
                "translation TEXT, " +
                "path TEXT, " +
                "flashcard_id INTEGER, " +
                "FOREIGN KEY(flashcard_id) REFERENCES flashcard(flashcard_id));");

        // language table
        db.execSQL("CREATE TABLE language (" +
                "language_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT" +
                ");");

        // user_language table
        db.execSQL("CREATE TABLE user_language (" +
                "user_id INTEGER, " +
                "language_id INTEGER, " +
                "proficiency_level TEXT, " +  // B, I, A
                "FOREIGN KEY(user_id) REFERENCES user(user_id), " +
                "FOREIGN KEY(language_id) REFERENCES language(language_id), " +
                "UNIQUE(user_id, language_id) ON CONFLICT REPLACE" + // Prevent duplicates, replace if duplicate
                ");");

        // word bank table
        db.execSQL("CREATE TABLE word_bank (" +
                "word_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "word TEXT NOT NULL, " +
                "pronunciation TEXT, " +
                "translation TEXT, " +
                "proficiency_level TEXT CHECK (proficiency_level IN ('B', 'I', 'A')), " +
                "audio_path TEXT, " +
                "language_id INTEGER, " +
                "FOREIGN KEY(language_id) REFERENCES language(language_id)" +
                ");");

        // user word table
        db.execSQL("CREATE TABLE user_word (" +
                "user_id INTEGER, " +
                "word_id INTEGER, " +
                "is_learned INTEGER DEFAULT 0, " +
                "date_learned DATE, " +
                "FOREIGN KEY(user_id) REFERENCES user(user_id), " +
                "FOREIGN KEY(word_id) REFERENCES word_bank(word_id), " +
                "PRIMARY KEY(user_id, word_id)" +
                ");");

        // Daily Word Table
        db.execSQL("CREATE TABLE daily_word (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "word_id INTEGER, " +
                "date DATE, " +
                "FOREIGN KEY(user_id) REFERENCES user(user_id), " +
                "FOREIGN KEY(word_id) REFERENCES word_bank(word_id)" +
                ");");

        Log.d("DatabaseHelper", "daily_word table created.");

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
                "date_achieved TEXT, " +
                "is_achieved INTEGER DEFAULT 0, " +
                "FOREIGN KEY(user_id) REFERENCES user(user_id), " +
                "FOREIGN KEY(achievement_id) REFERENCES achievement(achievement_id)," +
                "PRIMARY KEY(user_id, achievement_id)" +
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
                ");");      // lesson_content table


        // user_lesson table
        db.execSQL("CREATE TABLE user_lesson (" +
                "user_id INTEGER, " +
                "lesson_id TEXT, " +
                "isComplete INTEGER, " +
                "FOREIGN KEY(user_id) REFERENCES user(user_id), " +
                "FOREIGN KEY(lesson_id) REFERENCES lesson(lesson_id)" +
                ");");

        // Quiz Table
        db.execSQL("CREATE TABLE quiz (" +
                "quiz_id TEXT PRIMARY KEY, " +
                "language_id INTEGER, " +
                "level TEXT, " +  // Level (B, I, A)
                "quiz_title TEXT, " +
                "FOREIGN KEY(language_id) REFERENCES language(language_id)" +
                ");");

        // Quiz Content Table
        db.execSQL("CREATE TABLE quiz_content (" +
                "content_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "quiz_id TEXT, " +
                "sequence INTEGER, " +
                "content_type TEXT DEFAULT 'question', " +  // Question, image, video, etc.
                "title TEXT, " +  // The question title
                "FOREIGN KEY(quiz_id) REFERENCES quiz(quiz_id)" +
                ");");

        // Option Table
        db.execSQL("CREATE TABLE option (" +
                "option_id INTEGER PRIMARY KEY AUTOINCREMENT, " +  // Auto-incrementing ID for each option
                "content_id INTEGER, " +
                "option_text TEXT, " +
                "is_correct INTEGER DEFAULT 0, " +  // Whether the option is correct (1 = true, 0 = false)
                "FOREIGN KEY(content_id) REFERENCES quiz_content(content_id)" +
                ");");

        // User Quiz Table
        db.execSQL("CREATE TABLE user_quiz (" +
                "user_id INTEGER, " +
                "quiz_id TEXT, " +
                "isComplete INTEGER DEFAULT 0, " +  // 0 = not complete, 1 = complete
                "FOREIGN KEY(user_id) REFERENCES user(user_id), " +
                "FOREIGN KEY(quiz_id) REFERENCES quiz(quiz_id), " +
                "PRIMARY KEY(user_id, quiz_id)" +
                ");");

        android.util.Log.d("DatabaseHelper", "All tables created successfully.");

        insertDefaultTestUser(db);
        insertDefaultLanguages(db);
        insertDefaultLessons(db);
        insertDefaultLessonContent(db);
        insertDefaultFlashCards(db);
        insertDefaultQuiz(db);
        insertDefaultQuizContent(db);
        insertDefaultQuizOption(db);
        insertDefaultAchievementData(db);
        insertDefaultWordBank(db);
    }

    private void insertDefaultFlashCards(SQLiteDatabase db) {
        // Insert default flashcards
        db.execSQL("INSERT INTO flashcard (name, user_id) VALUES ('Basic Greetings', 100);");
        db.execSQL("INSERT INTO flashcard (name, user_id) VALUES ('Furniture', 100);");

        // Insert default flashcard contents
        db.execSQL("INSERT INTO flashcard_content (word, pronunciation, translation, path, flashcard_id) VALUES ('안녕하세요', 'Annyeonghaseyo', 'Hello', NULL, 1);");
        db.execSQL("INSERT INTO flashcard_content (word, pronunciation, translation, path, flashcard_id) VALUES ('감사합니다', 'Gamsahamnida', 'Thank you', NULL, 1);");
        db.execSQL("INSERT INTO flashcard_content (word, pronunciation, translation, path, flashcard_id) VALUES ('의자', 'Uija', 'Chair', NULL, 2);");
        db.execSQL("INSERT INTO flashcard_content (word, pronunciation, translation, path, flashcard_id) VALUES ('책상', 'Chaeksang', 'Desk', NULL, 2);");

        android.util.Log.d("DatabaseHelper", "Default flashcards and contents inserted.");
    }

    private void insertDefaultTestUser(SQLiteDatabase db) {
        // Insert default test user with user_id set to 100
        db.execSQL("INSERT INTO user (user_id, name, email, password) VALUES (100, 'Test User', 'test@example.com', '12345');");

        // Insert default test user's stats
        db.execSQL("INSERT INTO user_stats (user_id, current_level, total_xp, current_xp, level_up_xp, words_learned, total_login_streak, five_day_login_streak, last_claim_date) " +
                "VALUES (100, 1, 0, 0, 1000, 0, 0, 0, NULL);");

        // Insert default test user's language with proficiency level "Beginner" (level "B")
        db.execSQL("INSERT INTO user_language (user_id, language_id, proficiency_level) " +
                "VALUES (100, 1, 'B');");

        android.util.Log.d("DatabaseHelper", "Default test user and stats inserted.");
    }

    private void insertDefaultLanguages(SQLiteDatabase db) {
        db.execSQL("INSERT INTO language (name) VALUES ('Japanese');");
        db.execSQL("INSERT INTO language (name) VALUES ('Korean');");
        android.util.Log.d("DatabaseHelper", "Default language inserted.");
    }

    private void insertDefaultLessons(SQLiteDatabase db) {
        // Insert lessons into the lesson table
        db.execSQL("INSERT INTO lesson (lesson_id, level, lesson_number, language_id, lesson_title) " +
                "VALUES ('BL1', 'B', 1, 1, 'Japanese Writing Systems');");

        db.execSQL("INSERT INTO lesson (lesson_id, level, lesson_number, language_id, lesson_title) " +
                "VALUES ('BL2', 'B', 2, 1, 'Basic Greetings');");

        db.execSQL("INSERT INTO lesson (lesson_id, level, lesson_number, language_id, lesson_title) " +
                "VALUES ('BL3', 'B', 3, 1, 'Numbers and Time');");

        db.execSQL("INSERT INTO lesson (lesson_id, level, lesson_number, language_id, lesson_title) " +
                "VALUES ('BL4', 'B', 4, 1, 'Self-Introduction');");

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

    private void insertDefaultQuiz(SQLiteDatabase db) {
        // Insert default quizzes for Japanese (language_id = 1)
        db.execSQL("INSERT INTO quiz (quiz_id, language_id, level, quiz_title) VALUES " +
                "('BQ1', 1, 'B', 'Japanese Beginner Quiz 1'), " +
                "('IQ1', 1, 'I', 'Japanese Intermediate Quiz 1'), " +
                "('AQ1', 1, 'A', 'Japanese Advanced Quiz 1');");
    }

    private void insertDefaultQuizContent(SQLiteDatabase db) {
        // Insert default quiz content for Japanese Beginner Quiz 1 (quiz_id = 'BQ1')
        db.execSQL("INSERT INTO quiz_content (content_id, quiz_id, sequence, content_type, title) VALUES " +
                "(1, 'BQ1', 1, 'question', 'What does “こんにちは” mean?'), " +
                "(2, 'BQ1', 2, 'question', 'What is the correct pronunciation of “ありがとう”?');");
    }

    private void insertDefaultQuizOption(SQLiteDatabase db) {
        // Insert default options for content_id 1 (Quiz Question 1)
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(1, 'Good Morning', 0), " +
                "(1, 'Good Afternoon', 1), " +
                "(1, 'Good Evening', 0), " +
                "(1, 'Hello', 0);");

        // Insert default options for content_id 2 (Quiz Question 2)
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(2, 'Arigatou', 1), " +
                "(2, 'Aregato', 0), " +
                "(2, 'Arugato', 0), " +
                "(2, 'Arikato', 0);");
    }

    private void insertDefaultAchievementData(SQLiteDatabase db) {
        // Insert default achievements
        db.execSQL("INSERT INTO achievement (title, description) VALUES " +
                "('First Lesson', 'Complete your first lesson'), " +
                "('Quiz Master', 'Complete your first quiz'), " +
                "('Daily Devotee', 'Claim your first daily login reward'), " +
                "('Level Up!', 'Reach Level 2'), " +
                "('Steady Learner', 'Complete 5 lessons');");

        android.util.Log.d("DatabaseHelper", "Default achievements inserted.");
    }

    private void insertDefaultWordBank(SQLiteDatabase db) {
        // japanese beginner
        db.execSQL("INSERT INTO word_bank (word, pronunciation, translation, proficiency_level, audio_path, language_id) VALUES " +
                "('猫', 'neko', 'Cat', 'B', 'jp_cat.mp3', 1), " +
                "('犬', 'inu', 'Dog', 'B', 'jp_dog.mp3', 1), " +
                "('本', 'hon', 'Book', 'B', 'jp_book.mp3', 1), " +
                "('水', 'mizu', 'Water', 'B', 'jp_water.mp3', 1), " +
                "('家', 'ie', 'House', 'B', 'jp_house.mp3', 1), " +
                "('車', 'kuruma', 'Car', 'B', 'jp_car.mp3', 1), " +
                "('駅', 'eki', 'Train Station', 'B', 'jp_train_station.mp3', 1), " +
                "('友達', 'tomodachi', 'Friend', 'B', 'jp_friend.mp3', 1), " +
                "('学校', 'gakkou', 'School', 'B', 'jp_school.mp3', 1), " +
                "('先生', 'sensei', 'Teacher', 'B', 'jp_teacher.mp3', 1);");

        android.util.Log.d("DatabaseHelper", "Default Japanese words inserted.");
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
        db.execSQL("DROP TABLE IF EXISTS quiz;");
        db.execSQL("DROP TABLE IF EXISTS quiz_content;");
        db.execSQL("DROP TABLE IF EXISTS option;");
        db.execSQL("DROP TABLE IF EXISTS user_quiz;");
        db.execSQL("DROP TABLE IF EXISTS word_bank;");
        db.execSQL("DROP TABLE IF EXISTS user_word;");
        db.execSQL("DROP TABLE IF EXISTS daily_word;");
        onCreate(db);
    }
}
