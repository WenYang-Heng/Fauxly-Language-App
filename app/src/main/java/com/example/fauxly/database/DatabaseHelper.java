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
                "translation TEXT, " +
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
        insertDefaultLessonsKorean(db);
        insertLessonContentBL5(db);
        insertLessonContentBL6(db);
        insertDefaultQuizzesKorean(db);
        insertQuizContentBLQ5(db);
        insertQuizContentBLQ6(db);
        insertQuizOptionsBLQ5(db);
        insertQuizOptionsBLQ6(db);
        insertDefaultWordBankKorean(db);
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
        db.execSQL("INSERT INTO lesson_content (lesson_id, content_order, content_type, content_data, path, word, pronunciation, translation) VALUES " +
                "('BL1', 1, 'Text', 'Japanese uses three writing systems: Hiragana, Katakana, and Kanji.', NULL, NULL, NULL, NULL), " +
                "('BL1', 2, 'Text', 'Hiragana is phonetic and used for native Japanese words.', NULL, NULL, NULL, NULL), " +
                "('BL1', 3, 'Text', 'The first five Hiragana characters are あ, い, う, え, お.', NULL, NULL, NULL, NULL), " +
                "('BL1', 3, 'Word', NULL, 'bl1_ah.mp3', 'あ', 'A (ah)', 'Ah'), " +
                "('BL1', 4, 'Word', NULL, 'bl1_ee.mp3', 'い', 'I (ee)', 'Ee'), " +
                "('BL1', 5, 'Word', NULL, 'bl1_uu.mp3', 'う', 'U (oo)', 'Oo'), " +
                "('BL1', 6, 'Word', NULL, 'bl1_eh.mp3', 'え', 'E (eh)', 'Eh'), " +
                "('BL1', 7, 'Word', NULL, 'bl1_oh.mp3', 'お', 'O (oh)', 'Oh');");

// Lesson 2 Content
        db.execSQL("INSERT INTO lesson_content (lesson_id, content_order, content_type, content_data, path, word, pronunciation, translation) VALUES " +
                "('BL2', 1, 'Text', 'Learn basic greetings in Japanese.', NULL, NULL, NULL, NULL), " +
                "('BL2', 2, 'Word', 'Hello', 'bl2_konnichiwa.mp3', 'こんにちは', 'Konnichiwa (koh-nee-chee-wah)', 'Hello'), " +
                "('BL2', 3, 'Word', 'Good Morning', 'bl2_ohayou_gozaimasu.mp3', 'おはようございます', 'Ohayou gozaimasu (oh-ha-yoh goh-zai-mahs)', 'Good Morning'), " +
                "('BL2', 4, 'Word', 'Good Evening', 'bl2_konbanwa.mp3', 'こんばんは', 'Konbanwa (kohn-bahn-wah)', 'Good Evening'), " +
                "('BL2', 5, 'Word', 'Good Night', 'bl2_oyasuminasai.mp3', 'おやすみなさい', 'Oyasuminasai (oh-yah-soo-mee-nah-sai)', 'Good Night'), " +
                "('BL2', 6, 'Word', 'Thank you', 'bl2_arigatou.mp3', 'ありがとう', 'Arigatou (ah-ree-gah-toh)', 'Thank You');");

// Lesson 3 Content
        db.execSQL("INSERT INTO lesson_content (lesson_id, content_order, content_type, content_data, path, word, pronunciation, translation) VALUES " +
                "('BL3', 1, 'Text', 'Learn numbers 1-10 in Japanese.', NULL, NULL, NULL, NULL), " +
                "('BL3', 2, 'Word', 'One', 'bl3_ichi.mp3', 'いち', 'Ichi (ee-chee)', 'One'), " +
                "('BL3', 3, 'Word', 'Two', 'bl3_ni.mp3', 'に', 'Ni (nee)', 'Two'), " +
                "('BL3', 4, 'Word', 'Three', 'bl3_san.mp3', 'さん', 'San (sahn)', 'Three'), " +
                "('BL3', 5, 'Word', 'Four', 'bl3_yon.mp3', 'よん', 'Yon (yohn)', 'Four'), " +
                "('BL3', 6, 'Word', 'Five', 'bl3_go.mp3', 'ご', 'Go (goh)', 'Five'), " +
                "('BL3', 7, 'Word', 'Six', 'bl3_roku.mp3', 'ろく', 'Roku (roh-koo)', 'Six'), " +
                "('BL3', 8, 'Word', 'Seven', 'bl3_nana.mp3', 'なな', 'Nana (nah-nah)', 'Seven'), " +
                "('BL3', 9, 'Word', 'Eight', 'bl3_hachi.mp3', 'はち', 'Hachi (hah-chee)', 'Eight'), " +
                "('BL3', 10, 'Word', 'Nine', 'bl3_kyuu.mp3', 'きゅう', 'Kyuu (kyoo)', 'Nine'), " +
                "('BL3', 11, 'Word', 'Ten', 'bl3_juu.mp3', 'じゅう', 'Juu (joo)', 'Ten'), " +
                "('BL3', 12, 'Word', 'Asking Time', 'bl3_asking_time.mp3', 'いま なんじ ですか？', 'Ima nanji desu ka? (What time is it?)', 'What time is it?');");

        // Lesson 4 Content
        db.execSQL("INSERT INTO lesson_content (lesson_id, content_order, content_type, content_data, path, word, pronunciation, translation) VALUES " +
                "('BL4', 1, 'Text', 'In this lesson, you will learn how to introduce yourself in Japanese.', NULL, NULL, NULL, NULL), " +
                "('BL4', 2, 'Word', 'My name is ___', 'bl4_watashi_wa.mp3', 'わたしは ___ です', 'Watashi wa ___ desu (wah-tah-shee wah ___ dess)', 'My name is ___'), " +
                "('BL4', 4, 'Word', 'I am a student', 'bl4_gakusei.mp3', 'わたしは がくせい です', 'Watashi wa gakusei desu (wah-tah-shee wah gah-koo-seh dess)', 'I am a student'), " +
                "('BL4', 6, 'Word', 'Nice to meet you', 'bl4_hajimemashite.mp3', 'はじめまして', 'Hajimemashite (hah-jee-meh-mash-teh)', 'Nice to meet you');");

        android.util.Log.d("DatabaseHelper", "Lesson 4 content for self-introduction inserted.");

        // Log the completion of data insertion
        android.util.Log.d("DatabaseHelper", "Default lesson content with updated words and pronunciation inserted.");
    }

    private void insertDefaultQuiz(SQLiteDatabase db) {
        // Insert default quizzes for Japanese (language_id = 1)
        db.execSQL("INSERT INTO quiz (quiz_id, language_id, level, quiz_title) VALUES " +
                "('BQ1', 1, 'B', 'Japanese Beginner Quiz 1'), " +
                "('BQ2', 1, 'B', 'Japanese Beginner Quiz 2'), " +
                "('BQ3', 1, 'B', 'Japanese Beginner Quiz 3'), " +
                "('BQ4', 1, 'B', 'Japanese Beginner Quiz 4');");
    }


    private void insertDefaultQuizContent(SQLiteDatabase db) {
        // Insert default quiz content for Japanese Beginner Quiz 1 (quiz_id = 'BQ1')
        db.execSQL("INSERT INTO quiz_content (content_id, quiz_id, sequence, content_type, title) VALUES " +
                "(1, 'BQ1', 1, 'question', 'What does “こんにちは” mean?'), " +
                "(2, 'BQ1', 2, 'question', 'What is the correct pronunciation of “ありがとう”?'), " +
                "(3, 'BQ1', 3, 'question', 'What is the translation of “さようなら”?'), " +
                "(4, 'BQ1', 4, 'question', 'Which word means “Yes” in Japanese?'), " +
                "(5, 'BQ1', 5, 'question', 'What is the meaning of “いいえ”?'), " +
                "(6, 'BQ1', 6, 'question', 'What does “おはよう” mean?');");

        db.execSQL("INSERT INTO quiz_content (content_id, quiz_id, sequence, content_type, title) VALUES " +
                "(7, 'BQ2', 1, 'question', 'What is the Japanese word for “Thank you”?'), " +
                "(8, 'BQ2', 2, 'question', 'How do you say “Good Evening” in Japanese?'), " +
                "(9, 'BQ2', 3, 'question', 'What does “おはようございます” mean?');");

        db.execSQL("INSERT INTO quiz_content (content_id, quiz_id, sequence, content_type, title) VALUES " +
                "(10, 'BQ3', 1, 'question', 'What is the meaning of “さようなら”?'), " +
                "(11, 'BQ3', 2, 'question', 'How do you pronounce “こんにちは”?'), " +
                "(12, 'BQ3', 3, 'question', 'What is the meaning of “はい”?');");

        db.execSQL("INSERT INTO quiz_content (content_id, quiz_id, sequence, content_type, title) VALUES " +
                "(13, 'BQ4', 1, 'question', 'What is the Japanese word for “No”?'), " +
                "(14, 'BQ4', 2, 'question', 'Which of these means “See you later”?'), " +
                "(15, 'BQ4', 3, 'question', 'What does “いいえ” mean?');");
    }


    private void insertDefaultQuizOption(SQLiteDatabase db) {
        // Insert options for content_id 1 (Question: What does “こんにちは” mean?)
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(1, 'Good Morning', 0), " +
                "(1, 'Good Afternoon', 1), " +
                "(1, 'Good Evening', 0), " +
                "(1, 'Hello', 0);");

        // Insert options for content_id 2 (Question: What is the correct pronunciation of “ありがとう”?)
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(2, 'Arigatou', 1), " +
                "(2, 'Aregato', 0), " +
                "(2, 'Arugato', 0), " +
                "(2, 'Arikato', 0);");

        // Insert options for content_id 3 (Question: What is the translation of “さようなら”?)
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(3, 'Goodbye', 1), " +
                "(3, 'See you', 0), " +
                "(3, 'Hello', 0), " +
                "(3, 'Thank you', 0);");

        // Insert options for content_id 4 (Question: Which word means “Yes” in Japanese?)
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(4, 'いいえ', 0), " +
                "(4, 'はい', 1), " +
                "(4, 'ありがとう', 0), " +
                "(4, 'こんにちは', 0);");

        // Insert options for content_id 5 (Question: What is the meaning of “いいえ”?)
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(5, 'Yes', 0), " +
                "(5, 'No', 1), " +
                "(5, 'Thank you', 0), " +
                "(5, 'Sorry', 0);");

        // Insert options for content_id 6 (Question: What does “おはよう” mean?)
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(6, 'Good Night', 0), " +
                "(6, 'Good Morning', 1), " +
                "(6, 'Hello', 0), " +
                "(6, 'Goodbye', 0);");

        // BQ2
        // Question 1
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(7, 'ありがとう', 1), " +
                "(7, 'おはよう', 0), " +
                "(7, 'さようなら', 0), " +
                "(7, 'こんばんは', 0);");

        // Question 2
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(8, 'こんばんは', 1), " +
                "(8, 'おやすみなさい', 0), " +
                "(8, 'こんにちは', 0), " +
                "(8, 'ありがとう', 0);");

        // Question 3
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(9, 'Good Morning', 1), " +
                "(9, 'Good Night', 0), " +
                "(9, 'Good Afternoon', 0), " +
                "(9, 'Goodbye', 0);");

        //BQ3
        // Question 1
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(10, 'Goodbye', 1), " +
                "(10, 'Hello', 0), " +
                "(10, 'Thank you', 0), " +
                "(10, 'See you', 0);");

        // Question 2
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(11, 'Konnichiwa (koh-nee-chee-wah)', 1), " +
                "(11, 'Arigatou (ah-ree-gah-toh)', 0), " +
                "(11, 'Ohayou (oh-hah-yoh)', 0), " +
                "(11, 'Konbanwa (kohn-bahn-wah)', 0);");

        // Question 3
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(12, 'Yes', 1), " +
                "(12, 'No', 0), " +
                "(12, 'Good Morning', 0), " +
                "(12, 'Thank you', 0);");

        // BQ4
        // Question 1
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(13, 'いいえ', 1), " +
                "(13, 'はい', 0), " +
                "(13, 'ありがとう', 0), " +
                "(13, 'こんにちは', 0);");

        // Question 2
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(14, 'じゃあね', 1), " +
                "(14, 'さようなら', 0), " +
                "(14, 'おはようございます', 0), " +
                "(14, 'ありがとう', 0);");

        // Question 3
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(15, 'No', 1), " +
                "(15, 'Yes', 0), " +
                "(15, 'Hello', 0), " +
                "(15, 'Goodbye', 0);");
    }

    private void insertDefaultLessonsKorean(SQLiteDatabase db) {
        // Insert Korean lessons
        db.execSQL("INSERT INTO lesson (lesson_id, level, lesson_number, language_id, lesson_title) VALUES " +
                "('BL5', 'B', 1, 2, 'Korean Alphabet (Hangul)'), " +
                "('BL6', 'B', 2, 2, 'Basic Korean Greetings');");
    }

    private void insertLessonContentBL5(SQLiteDatabase db) {
        db.execSQL("INSERT INTO lesson_content (lesson_id, content_order, content_type, content_data, path, word, pronunciation, translation) VALUES " +
                "('BL5', 1, 'Text', 'The Korean alphabet is called Hangul. It consists of consonants and vowels.', NULL, NULL, NULL, NULL), " +
                "('BL5', 2, 'Text', 'Here are some basic characters to learn.', NULL, NULL, NULL, NULL), " +
                "('BL5', 3, 'Word', NULL, 'bl5_giyeok.mp3', 'ㄱ', 'Giyeok (g/k)', 'G/K'), " +
                "('BL5', 4, 'Word', NULL, 'bl5_nieun.mp3', 'ㄴ', 'Nieun (n)', 'N'), " +
                "('BL5', 5, 'Word', NULL, 'bl5_digeut.mp3', 'ㄷ', 'Digeut (d/t)', 'D/T'), " +
                "('BL5', 6, 'Word', NULL, 'bl5_rieul.mp3', 'ㄹ', 'Rieul (r/l)', 'R/L'), " +
                "('BL5', 7, 'Word', NULL, 'bl5_mieum.mp3', 'ㅁ', 'Mieum (m)', 'M');");
    }

    private void insertLessonContentBL6(SQLiteDatabase db) {
        db.execSQL("INSERT INTO lesson_content (lesson_id, content_order, content_type, content_data, path, word, pronunciation, translation) VALUES " +
                "('BL6', 1, 'Text', 'Learn basic greetings in Korean.', NULL, NULL, NULL, NULL), " +
                "('BL6', 2, 'Word', 'Hello', 'bl6_annyeong.mp3', '안녕하세요', 'Annyeonghaseyo (an-nyung-ha-seh-yo)', 'Hello'), " +
                "('BL6', 3, 'Word', 'Thank you', 'bl6_kamsahamnida.mp3', '감사합니다', 'Kamsahamnida (kam-sa-ham-ni-da)', 'Thank you'), " +
                "('BL6', 4, 'Word', 'Goodbye (formal)', 'bl6_anyeonghi_kaseyo.mp3', '안녕히 가세요', 'Annyeonghi kaseyo (an-nyung-hee kah-seh-yo)', 'Goodbye'), " +
                "('BL6', 5, 'Word', 'Goodbye (informal)', 'bl6_annyeong_informal.mp3', '안녕', 'Annyeong (an-nyung)', 'Bye'), " +
                "('BL6', 6, 'Word', 'Yes', 'bl6_ne.mp3', '네', 'Ne (neh)', 'Yes'), " +
                "('BL6', 7, 'Word', 'No', 'bl6_aniyo.mp3', '아니요', 'Aniyo (ah-nee-yo)', 'No');");
    }

    private void insertDefaultQuizzesKorean(SQLiteDatabase db) {
        // Insert quizzes for Korean
        db.execSQL("INSERT INTO quiz (quiz_id, language_id, level, quiz_title) VALUES " +
                "('BQ5', 2, 'B', 'Korean Beginner Quiz 1'), " +
                "('BQ6', 2, 'B', 'Korean Beginner Quiz 2');");
    }

    private void insertQuizContentBLQ5(SQLiteDatabase db) {
        db.execSQL("INSERT INTO quiz_content (content_id, quiz_id, sequence, content_type, title) VALUES " +
                "(16, 'BQ5', 1, 'question', 'What is the Korean word for “Hello”?'), " +
                "(17, 'BQ5', 2, 'question', 'What does “감사합니다” mean?'), " +
                "(18, 'BQ5', 3, 'question', 'Which character represents the sound “M”?');");
    }

    private void insertQuizContentBLQ6(SQLiteDatabase db) {
        db.execSQL("INSERT INTO quiz_content (content_id, quiz_id, sequence, content_type, title) VALUES " +
                "(19, 'BQ6', 1, 'question', 'What is the pronunciation of “네”?'), " +
                "(20, 'BQ6', 2, 'question', 'How do you say “No” in Korean?'), " +
                "(21, 'BQ6', 3, 'question', 'What does “안녕히 가세요” mean?');");
    }

    private void insertQuizOptionsBLQ5(SQLiteDatabase db) {
        // Question 1
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(16, 'Kamsahamnida', 0), " +
                "(16, 'Annyeonghaseyo', 1), " +
                "(16, 'Aniyo', 0), " +
                "(16, 'Ne', 0);");

        // Question 2
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(17, 'Goodbye', 0), " +
                "(17, 'Thank you', 1), " +
                "(17, 'Hello', 0), " +
                "(17, 'No', 0);");

        // Question 3
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(18, 'ㄱ', 0), " +
                "(18, 'ㅁ', 1), " +
                "(18, 'ㄷ', 0), " +
                "(18, 'ㄹ', 0);");
    }

    private void insertQuizOptionsBLQ6(SQLiteDatabase db) {
        // Question 1
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(19, 'Aniyo (ah-nee-yo)', 0), " +
                "(19, 'Ne (neh)', 1), " +
                "(19, 'Annyeonghaseyo (an-nyung-ha-seh-yo)', 0), " +
                "(19, 'Kamsahamnida (kam-sa-ham-ni-da)', 0);");

        // Question 2
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(20, 'Ne (neh)', 0), " +
                "(20, 'Aniyo (ah-nee-yo)', 1), " +
                "(20, 'Annyeong (an-nyung)', 0), " +
                "(20, 'Kamsahamnida (kam-sa-ham-ni-da)', 0);");

        // Question 3
        db.execSQL("INSERT INTO option (content_id, option_text, is_correct) VALUES " +
                "(21, 'Good morning', 0), " +
                "(21, 'Goodbye', 1), " +
                "(21, 'No', 0), " +
                "(21, 'Yes', 0);");
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

    private void insertDefaultWordBankKorean(SQLiteDatabase db) {
        // Korean beginner
        db.execSQL("INSERT INTO word_bank (word, pronunciation, translation, proficiency_level, audio_path, language_id) VALUES " +
                "('사과', 'sagwa', 'Apple', 'B', 'kr_apple.mp3', 2), " +
                "('물', 'mul', 'Water', 'B', 'kr_water.mp3', 2), " +
                "('학교', 'hakgyo', 'School', 'B', 'kr_school.mp3', 2), " +
                "('친구', 'chingu', 'Friend', 'B', 'kr_friend.mp3', 2), " +
                "('책', 'chaek', 'Book', 'B', 'kr_book.mp3', 2);");

        android.util.Log.d("DatabaseHelper", "Default Korean words inserted.");
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
