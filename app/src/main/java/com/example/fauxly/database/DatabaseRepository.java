package com.example.fauxly.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fauxly.model.Lesson;
import com.example.fauxly.model.LessonContent;

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


}
