package com.example.fauxly.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.fauxly.R;
import com.example.fauxly.database.DatabaseRepository;
import com.example.fauxly.model.User;
import com.example.fauxly.model.UserLanguage;
import com.example.fauxly.model.UserStats;

public class CoursePageFragment extends Fragment {

    private String userId;
    private User user;
    private UserLanguage userLanguage;
    private ImageButton backButton, lessonBtn;
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
        TVUsername = view.findViewById(R.id.TVUsername);
        TVLanguage = view.findViewById(R.id.TVCourseTitle);
        TVLevel = view.findViewById(R.id.TVLevel);
        lessonBtn = view.findViewById(R.id.lessonBtn);

        // Set up back button click listener
        backButton.setOnClickListener(v -> navigateBackToHome());

        // Set up lesson button click listener
        lessonBtn.setOnClickListener(v -> navigateToLessonList());

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
