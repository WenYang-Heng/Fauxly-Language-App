package com.example.fauxly.ui.fragment;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.fauxly.R;
import com.example.fauxly.database.DatabaseRepository;
import com.example.fauxly.model.DailyWord;
import com.example.fauxly.model.User;
import com.example.fauxly.model.UserLanguage;
import com.example.fauxly.utils.FragmentUtils;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;

public class CoursePageFragment extends Fragment {

    private String userId;
    private User user;
    private UserLanguage userLanguage;
    private ImageButton backButton, lessonBtn, flashCardBtn;
    private Button changeLanguageButton;
    private MaterialButton quizBtn;
    private TextView TVUsername, TVLanguage, TVLevel;
    private TextView wordTextView, pronunctionTextView, translatedWordTextView;
    private ImageButton audioButton;
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

        FragmentUtils.setFragmentContainerMarginBottom(requireActivity(), true);
        // Initialize UI elements
        backButton = view.findViewById(R.id.backButton);
        changeLanguageButton = view.findViewById(R.id.changeLanguageButton);
        TVUsername = view.findViewById(R.id.TVUsername);
        TVLanguage = view.findViewById(R.id.TVCourseTitle);
        TVLevel = view.findViewById(R.id.TVLevel);
        lessonBtn = view.findViewById(R.id.lessonBtn);
        flashCardBtn = view.findViewById(R.id.flashCardBtn);
        quizBtn = view.findViewById(R.id.quizBtn);

        // Initialize daily word
        wordTextView = view.findViewById(R.id.word);
        pronunctionTextView = view.findViewById(R.id.pronunciation);
        translatedWordTextView = view.findViewById(R.id.translatedWord);
        audioButton = view.findViewById(R.id.audioButton);

        // Set up back button click listener
        backButton.setOnClickListener(v -> navigateBackToHome());

        // Set up lesson button click listener
        lessonBtn.setOnClickListener(v -> navigateToLessonList());

        flashCardBtn.setOnClickListener(v -> navigateToFlashCardList());

        changeLanguageButton.setOnClickListener(v -> navigateToLanguageSelection());
        quizBtn.setOnClickListener(v -> navigateToQuizList());

        // Load user data
        loadUserData();

        // load daily word
        loadDailyWord();

        return view;
    }

    private void loadDailyWord() {
        if (userId == null || userLanguage == null) {
            wordTextView.setText("Please select a language");
            translatedWordTextView.setText("");
            pronunctionTextView.setText("");
            audioButton.setVisibility(View.GONE);
            return;
        }

        int languageId = userLanguage.getLanguageId();
        String proficiencyLevel = userLanguage.getProficiencyLevel();

        DailyWord todaysWord = repository.getTodaysWord(Integer.parseInt(userId), languageId, proficiencyLevel);

        if (todaysWord != null) {
            wordTextView.setText(todaysWord.getWord());
            pronunctionTextView.setText(todaysWord.getPronunciation());
            translatedWordTextView.setText(todaysWord.getTranslation());

            audioButton.setVisibility(View.VISIBLE);
            audioButton.setOnClickListener(v -> {
                playAudio(todaysWord.getAudioPath());

                if (userId != null && todaysWord != null) {
                    markWordAsLearned(Integer.parseInt(userId), todaysWord.getWordId());
                } else {
                    Log.e("CoursePageFragment", "User ID or Word is null. Cannot mark as learned.");
                }
            });
        } else {
            wordTextView.setText("No new word today.");
            translatedWordTextView.setText("");
            audioButton.setVisibility(View.GONE);
        }
    }


    private void playAudio(String audioPath) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor afd = requireContext().getAssets().openFd("audio/japanese/beginner/" + audioPath);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void markWordAsLearned(int userId, int wordId) {
        try {
            repository.insertUserWord(userId, wordId);
            Log.d("CoursePageFragment", "Word marked as learned: " + wordId);
        } catch (Exception e) {
            Log.e("CoursePageFragment", "Error marking word as learned: " + e.getMessage());
        }
    }

    private void navigateBackToHome() {
        // Navigate back to the HomeFragment
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(); // Navigates to the previous fragment in the stack
    }

    private void navigateToLessonList() {
        Fragment lessonListFragment = LessonListFragment.newInstance(
                userId,
                userLanguage != null ? userLanguage.getLanguageId() : -1,
                userLanguage != null ? userLanguage.getProficiencyLevel() : null
        );

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, lessonListFragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToFlashCardList() {
        Fragment flashCardFragment = FlashCardFragment.newInstance(userId);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, flashCardFragment)
                .addToBackStack(null)
                .commit();
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
        Fragment quizListFragment = QuizListFragment.newInstance(
                userId,
                userLanguage != null ? userLanguage.getLanguageId() : -1,
                userLanguage != null ? userLanguage.getProficiencyLevel() : null
        );

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, quizListFragment)
                .addToBackStack(null)
                .commit();
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
                        proficiencyLabel = "";
                        break;
                }

                TVLevel.setText(proficiencyLabel);
            } else {
                TVLanguage.setText("Not Assigned");
                TVLevel.setText("");
            }
        }
    }
}
