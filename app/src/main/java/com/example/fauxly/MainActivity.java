package com.example.fauxly;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.fauxly.database.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Delete the entire database
//        boolean isDeleted = deleteDatabase("MAD2007.db");
//
//        if (isDeleted) {
//            Log.d("DatabaseTest", "Database deleted successfully.");
//        } else {
//            Log.d("DatabaseTest", "Failed to delete the database or it does not exist.");
//        }

//         Reinitialize the database
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table';", null);

        if (cursor.moveToFirst()) {
            do {
                Log.d("DatabaseTest", "Table Name: " + cursor.getString(0));
            } while (cursor.moveToNext());
        } else {
            Log.d("DatabaseTest", "No tables found.");
        }

        cursor.close();

    }
}

