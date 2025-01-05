package com.example.fauxly.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.fauxly.R;
import com.example.fauxly.database.DatabaseRepository;
import com.example.fauxly.model.User;
import com.example.fauxly.model.UserLanguage;
import com.google.android.material.button.MaterialButton;

public class CoursePageFragment extends Fragment {

    private String userId;
    private User user;
    private UserLanguage userLanguage;
    private ImageButton backButton, lessonBtn, flashCardBtn;
    private Button changeLanguageButton;
    private MaterialButton quizBtn;
    private TextView TVUsername, TVLanguage, TVLevel;
    private DatabaseRepository repository;

    public CoursePageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the user ID from the Bundle
        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        }

        repository = new DatabaseRepository(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_page, container, false);

        // Initialize UI elements
        backButton = view.findViewById(R.id.backButton);
        changeLanguageButton = view.findViewById(R.id.changeLanguageButton);
        TVUsername = view.findViewById(R.id.TVUsername);
        TVLanguage = view.findViewById(R.id.TVCourseTitle);
        TVLevel = view.findViewById(R.id.TVLevel);
        lessonBtn = view.findViewById(R.id.lessonBtn);
        flashCardBtn = view.findViewById(R.id.flashCardBtn);
        quizBtn = view.findViewById(R.id.quizBtn);

        // Set up back button click listener
        backButton.setOnClickListener(v -> navigateBackToHome());

        // Set up lesson button click listener
        lessonBtn.setOnClickListener(v -> navigateToLessonList());

        flashCardBtn.setOnClickListener(v -> navigateToFlashCardList());

        changeLanguageButton.setOnClickListener(v -> navigateToLanguageSelection());
        quizBtn.setOnClickListener(v -> navigateToQuizList());

        // Load user data
        loadUserData();

        return view;
    }

    private void navigateBackToHome() {
        // Navigate back to the HomeFragment
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(); // Navigates to the previous fragment in the stack
    }

    private void navigateToLessonList() {
        if (userId != null && userLanguage != null) {
            // Pass userId, languageId, and proficiencyLevel to LessonListFragment
            Fragment lessonListFragment = LessonListFragment.newInstance(
                    userId,
                    userLanguage.getLanguageId(),
                    userLanguage.getProficiencyLevel()
            );

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, lessonListFragment) // Replace with your container ID
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToFlashCardList() {
        if (userId != null && userLanguage != null) {
            // Pass userId and languageId to FlashCardFragment
            Fragment flashCardFragment = FlashCardFragment.newInstance(
                    userId
            );

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, flashCardFragment) // Replace with your container ID
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToLanguageSelection() {
        if (userId != null) {
            // Navigate to LanguageSelectionFragment
            Fragment languageSelectionFragment = LanguageSelectionFragment.newInstance(userId);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, languageSelectionFragment)
                    .addToBackStack("CoursePageFragment")
                    .commit();
        }
    }

    private void navigateToQuizList() {
        if (userId != null && userLanguage != null) {
            Fragment quizListFragment = QuizListFragment.newInstance(
                    userId,
                    userLanguage.getLanguageId(),
                    userLanguage.getProficiencyLevel()
            );

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, quizListFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void loadUserData() {
        if (userId != null) {
            // Fetch user and user language information
            user = repository.getUserById(Integer.parseInt(userId));
            userLanguage = repository.getUserLanguageByUserId(Integer.parseInt(userId));

            // Update UI with user details
            if (user != null) {
                TVUsername.setText(user.getName());
            }

            if (userLanguage != null) {
                TVLanguage.setText(userLanguage.getLanguageName());

                String proficiency = userLanguage.getProficiencyLevel();
                String proficiencyLabel;

                switch (proficiency) {
                    case "B":
                        proficiencyLabel = "Beginner";
                        break;
                    case "I":
                        proficiencyLabel = "Intermediate";
                        break;
                    case "A":
                        proficiencyLabel = "Advanced";
                        break;
                    default:
                        proficiencyLabel = "Unknown";
                        break;
                }

                TVLevel.setText(proficiencyLabel);
            } else {
                TVLanguage.setText("Not Assigned");
                TVLevel.setText("N/A");
            }
        }
    }
}
