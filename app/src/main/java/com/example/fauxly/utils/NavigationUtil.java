package com.example.fauxly.utils;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.fauxly.R;
import com.example.fauxly.database.DatabaseRepository;
import com.example.fauxly.ui.fragment.QuizFragment;

public class NavigationUtil {
    public static void navigateToQuizFragment(
            FragmentManager fragmentManager,
            DatabaseRepository repository,
            String userId,
            String id, // Quiz ID or Lesson ID
            String title,
            boolean isLesson
    ) {
        if (isLesson) {
            // Handle lesson-specific logic
            if (!repository.isUserLessonExists(Integer.parseInt(userId), id)) {
                repository.insertUserLesson(Integer.parseInt(userId), id, 0);
            }
        } else {
            // Handle quiz-specific logic
            if (!repository.isUserQuizExists(Integer.parseInt(userId), id)) {
                repository.insertUserQuiz(Integer.parseInt(userId), id, 0);
            }
        }

        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("title", title);
        bundle.putString("userId", userId);
        bundle.putBoolean("isLesson", isLesson);

        QuizFragment quizFragment = new QuizFragment();
        quizFragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, quizFragment)
                .addToBackStack(isLesson ? "LessonListFragment" : "QuizListFragment")
                .commit();
    }

}
