package com.example.fauxly.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fauxly.R;
import com.example.fauxly.database.DatabaseRepository;
import com.example.fauxly.model.UserStats;
import com.example.fauxly.utils.AchievementTracker;

public class QuizCompletedFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_ID = "id";
    private static final String ARG_USER_ID = "userId";
    private static final String ARG_IS_LESSON = "isLesson";

    private String title;
    private String id;
    private String userId;
    private boolean isLesson;
    private Button doneButton;
    private DatabaseRepository repository;

    public QuizCompletedFragment() {
        super(R.layout.fragment_quiz_completed);
    }

    public static QuizCompletedFragment newInstance(String title, String id, String userId, boolean isLesson) {
        QuizCompletedFragment fragment = new QuizCompletedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_ID, id);
        args.putString(ARG_USER_ID, userId);
        args.putBoolean(ARG_IS_LESSON, isLesson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            id = getArguments().getString(ARG_ID);
            userId = getArguments().getString(ARG_USER_ID);
            isLesson = getArguments().getBoolean(ARG_IS_LESSON);
        }

        repository = new DatabaseRepository(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_completed, container, false);

        // Set the title
        TextView titleTextView = view.findViewById(R.id.completedText);
        if (title != null) {
            titleTextView.setText("Completed " + title);
        }

        // Set up Done button
        doneButton = view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(v -> {
            updateCompletionStatus();
            trackAchievements();
            navigateBackToCorrectList();
        });

        return view;
    }

    private void updateCompletionStatus() {
        if (id != null && userId != null) {
            int userIdInt = Integer.parseInt(userId);

            boolean isCompleted = isLesson
                    ? repository.isLessonCompleted(userIdInt, id) // Check if lesson is completed
                    : repository.isQuizCompleted(userIdInt, id); // Check if quiz is completed

            if (!isCompleted) {
                if (isLesson) {
                    // Mark lesson as complete
                    repository.updateUserLessonCompletion(userIdInt, id);
                } else {
                    // Mark quiz as complete
                    repository.updateUserQuizCompletion(userIdInt, id);
                }

                // Fetch current stats
                UserStats stats = repository.getUserStatsById(userIdInt);
                if (stats != null) {
                    // Grant 500 XP
                    stats.setCurrentXp(stats.getCurrentXp() + 500);
                    stats.setTotalXp(stats.getTotalXp() + 500);

                    Toast.makeText(requireContext(), "You earned " + 500 + " XP!", Toast.LENGTH_SHORT).show();

                    // Check for level-up
                    while (stats.getCurrentXp() >= stats.getLevelUpXp()) {
                        stats.setCurrentXp(stats.getCurrentXp() - stats.getLevelUpXp()); // Carry over remaining XP
                        stats.setCurrentLevel(stats.getCurrentLevel() + 1); // Increment the level

                        Toast.makeText(requireContext(), "Congratulations! You've leveled up to Level " + stats.getCurrentLevel() + "!", Toast.LENGTH_SHORT).show();
                    }

                    // Update the stats in the database
                    repository.updateUserStatsXpAndLevel(
                            userIdInt,
                            stats.getCurrentXp(),
                            stats.getCurrentLevel(),
                            stats.getTotalXp()
                    );
                }
            }
        }
    }


    private void trackAchievements() {
        AchievementTracker tracker = new AchievementTracker(requireContext());
        int userIdInt = Integer.parseInt(userId);
        tracker.evaluateAchievements(userIdInt);
    }

    private void navigateBackToCorrectList() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Navigate back to the correct list fragment
        if (isLesson) {
            fragmentManager.popBackStack("LessonListFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            fragmentManager.popBackStack("QuizListFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
