package com.example.fauxly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.fauxly.R;
import com.example.fauxly.database.DatabaseRepository;
import com.example.fauxly.model.User;
import com.example.fauxly.model.UserStats;

public class ProfileFragment extends Fragment {

    private static final String ARG_USER_ID = "userId";

    private String userId;
    private DatabaseRepository repository;
    private User user;
    private UserStats userStats;

    private TextView userNameTextView, userLevelTextView, progressTextView, quizCompletedTextView, lessonCompletedTextView, wordsLearnedTextView, xpEarnedTextView;
    private ProgressBar levelProgressBar;

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    public static ProfileFragment newInstance(String userId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }

        repository = new DatabaseRepository(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        userNameTextView = view.findViewById(R.id.userName);
        userLevelTextView = view.findViewById(R.id.userLevel);
        progressTextView = view.findViewById(R.id.progressText);
        levelProgressBar = view.findViewById(R.id.levelProgress);
        quizCompletedTextView = view.findViewById(R.id.quizCompleted);
        lessonCompletedTextView = view.findViewById(R.id.lessonCompleted);
        wordsLearnedTextView = view.findViewById(R.id.wordsLearned);
        xpEarnedTextView = view.findViewById(R.id.xpEarned);

        // Fetch and display user info
        fetchAndDisplayUserInfo();

        return view;
    }

    private void fetchAndDisplayUserInfo() {
        if (userId != null) {
            user = repository.getUserById(Integer.parseInt(userId));
            userStats = repository.getUserStatsById(Integer.parseInt(userId));

            if (user != null) {
                userNameTextView.setText(user.getName());
            }

            if (userStats != null) {
                userLevelTextView.setText("Lvl. " + userStats.getCurrentLevel());
                progressTextView.setText(userStats.getCurrentXp() + "/" + userStats.getLevelUpXp());
                levelProgressBar.setMax(userStats.getLevelUpXp());
                levelProgressBar.setProgress(userStats.getCurrentXp());

                // Placeholder data for statistics
                quizCompletedTextView.setText("15");
                lessonCompletedTextView.setText("10");
                wordsLearnedTextView.setText(String.valueOf(userStats.getWordsLearned()));
                xpEarnedTextView.setText(String.valueOf(userStats.getTotalXp()));
            }
        }
    }
}
