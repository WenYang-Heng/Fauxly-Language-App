package com.example.fauxly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.fauxly.MainActivity;
import com.example.fauxly.R;
import com.example.fauxly.database.DatabaseRepository;
import com.example.fauxly.model.User;
import com.example.fauxly.model.UserStats;
import com.example.fauxly.utils.AssetUtils;
import com.example.fauxly.utils.FragmentUtils;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileFragment extends Fragment {

    private static final String ARG_USER_ID = "userId";

    private String userId;
    private DatabaseRepository repository;
    private User user;
    private UserStats userStats;

    private TextView userNameTextView,
            userLevelTextView,
            progressTextView,
            quizCompletedTextView,
            lessonCompletedTextView,
            wordsLearnedTextView,
            xpEarnedTextView;
    private Button achievementButton;
    private Button logoutButton;
    private ProgressBar levelProgressBar;
    private ShapeableImageView lvlBadge;

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

        FragmentUtils.setFragmentContainerMarginBottom(requireActivity(), false);
        // Initialize views
        userNameTextView = view.findViewById(R.id.userName);
        userLevelTextView = view.findViewById(R.id.userLevel);
        progressTextView = view.findViewById(R.id.progressText);
        levelProgressBar = view.findViewById(R.id.levelProgress);
        quizCompletedTextView = view.findViewById(R.id.quizCompleted);
        lessonCompletedTextView = view.findViewById(R.id.lessonCompleted);
        wordsLearnedTextView = view.findViewById(R.id.wordsLearned);
        xpEarnedTextView = view.findViewById(R.id.xpEarned);
        achievementButton = view.findViewById(R.id.achievementButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        lvlBadge = view.findViewById(R.id.lvlBadge);

        achievementButton.setOnClickListener(v -> navigateToAchievementFragment());
        logoutButton.setOnClickListener(v -> logout());

        // Fetch and display user info
        fetchAndDisplayUserInfo();

        return view;
    }

    private void logout() {
        // Navigate back to LoginFragment
        NavController navController = NavHostFragment.findNavController(this);

        // Reset userId in MainActivity
        ((MainActivity) requireActivity()).setUserId(null);

        navController.navigate(R.id.action_profileFragment_to_loginFragment);
    }

    private void navigateToAchievementFragment() {
        AchievementFragment achievementFragment = AchievementFragment.newInstance(userId);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, achievementFragment)
                .addToBackStack("ProfileFragment")
                .commit();
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

                // Fetch completed lessons count
                int completedLessons = repository.getCompletedLessonsCount(Integer.parseInt(userId));
                lessonCompletedTextView.setText(String.valueOf(completedLessons));

                // Fetch completed quizzes count
                int completedQuizzes = repository.getCompletedQuizzesCount(Integer.parseInt(userId));
                quizCompletedTextView.setText(String.valueOf(completedQuizzes));

                int learnedWordsCount = repository.getLearnedWordCount(Integer.parseInt(userId));
                wordsLearnedTextView.setText(String.valueOf(learnedWordsCount));
                xpEarnedTextView.setText(String.valueOf(userStats.getTotalXp()));
            }

            displayLevelBadge();
        }
    }

    private void displayLevelBadge() {
        if (userStats != null) {
            int currentLevel = userStats.getCurrentLevel();
            String badgeFileName = "lvl_badges/lvl_" + currentLevel + ".jpg";

            AssetUtils.loadImageFromAssets(requireContext(), badgeFileName, lvlBadge);
        }
    }


}
