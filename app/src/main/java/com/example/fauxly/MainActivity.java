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
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.fauxly.database.DatabaseHelper;
import com.example.fauxly.ui.fragment.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnUserIdChangeListener {
    BottomNavigationView bottomNavigationView;
    private String userId = "100";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Delete the entire database
        boolean isDeleted = deleteDatabase("MAD2007.db");

        if (isDeleted) {
            Log.d("DatabaseTest", "Database deleted successfully.");
        } else {
            Log.d("DatabaseTest", "Failed to delete the database or it does not exist.");
        }

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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        // Retrieve the NavHostFragment and NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNavigationView, navController);

            // Dynamically handle navigation item selection
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.homeFragment) {
                    navController.navigate(R.id.homeFragment);
                    return true;

                } else if (itemId == R.id.profileFragment) {
                    // Dynamically pass userId to ProfileFragment
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", userId); // Pass updated userId dynamically
                    navController.navigate(R.id.profileFragment, bundle);
                    return true;

                } else if (itemId == R.id.quizFragment) {
                    navController.navigate(R.id.quizFragment);
                    return true;
                }

                return false;
            });
        } else {
            Log.e("MainActivity", "NavHostFragment is null. Navigation setup failed.");
        }

    }

    @Override
    public void onUserIdChanged(String newUserId) {
        this.userId = newUserId;
        Log.d("MainActivity", "User ID updated: " + newUserId);
    }
}

