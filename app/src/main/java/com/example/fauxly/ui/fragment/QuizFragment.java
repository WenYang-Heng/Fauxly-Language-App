package com.example.fauxly.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.fauxly.R;
import com.example.fauxly.database.DatabaseRepository;
import com.example.fauxly.model.Lesson;
import com.example.fauxly.model.LessonContent;
import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizFragment extends Fragment {

    private RadioGroup answersGroup;
    private Button confirmButton;
    private Button nextButton;
    private Button prevButton;
    private Button audioButton;
    private MaterialButton backButton;
    private TextView feedbackTextView;
    private TextView questionText;
    private TextView wordsTextView;
    private TextView pronunciationTextView;
    private TextView lessonTitleTV;

    private DatabaseRepository repository;

    private List<LessonContent> contents;
    private int currentIndex = 0; // Track current content index
    private String lessonId;
    private String userId;

    public QuizFragment() {
        super(R.layout.fragment_quiz);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_quiz, container, false);

        // Initialize views
        answersGroup = rootView.findViewById(R.id.answersGroup);
        confirmButton = rootView.findViewById(R.id.confirmButton);
        nextButton = rootView.findViewById(R.id.nextButton);
        prevButton = rootView.findViewById(R.id.prevButton);
        backButton = rootView.findViewById(R.id.backButton);
        audioButton = rootView.findViewById(R.id.audioButton);
        feedbackTextView = rootView.findViewById(R.id.feedbackTextView);
        questionText = rootView.findViewById(R.id.questionText);
        wordsTextView = rootView.findViewById(R.id.wordsTextView);
        pronunciationTextView = rootView.findViewById(R.id.pronunciationTextView);
        lessonTitleTV = rootView.findViewById(R.id.lessonTitle);

        confirmButton.setVisibility(View.GONE);

        repository = new DatabaseRepository(getContext());

        // Retrieve arguments
        Bundle args = getArguments();
        if (args != null) {
            lessonId = args.getString("lessonId");
            userId = args.getString("userId");
            String lessonTitle = args.getString("lessonTitle");

            // Set the lesson title
            if (lessonTitle != null) {
                lessonTitleTV.setText(lessonTitle);
            }
        }

        // Load lesson content
        if (lessonId != null && isLessonId(lessonId)) {
            loadLessonContent(lessonId);
        } else if (lessonId != null && isQuizId(lessonId)) {
            loadQuiz();
        } else {
            lessonTitleTV.setText("Invalid ID format");
        }

        // Back button functionality
        backButton.setOnClickListener(v -> navigateBackToLessonList());

        // Previous button click listener
        prevButton.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--; // Move to the previous content
                displayLessonContent(currentIndex);
            }
        });

        // Next button click listener
        nextButton.setOnClickListener(v -> {
            if (currentIndex < contents.size() - 1) {
                currentIndex++; // Move to the next content
                displayLessonContent(currentIndex);
            } else {
                navigateToQuizCompletedFragment(lessonTitleTV.getText().toString());
            }
        });

        return rootView;
    }

    private void loadLessonContent(String lessonId) {
        contents = repository.getLessonContents(lessonId);

        if (contents.isEmpty()) {
            lessonTitleTV.setText("No content found for this lesson");
            nextButton.setVisibility(View.GONE);
            prevButton.setVisibility(View.GONE);
            return;
        }

        // Display the first content
        displayLessonContent(currentIndex);

        nextButton.setOnClickListener(v -> {
            if (currentIndex < contents.size() - 1) {
                currentIndex++;
                displayLessonContent(currentIndex);
            } else {
                // Navigate to Quiz Completed Page on the last content
                navigateToQuizCompletedFragment(lessonTitleTV.getText().toString());
            }
        });

        // Previous button click listener
        prevButton.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                displayLessonContent(currentIndex);
            }
        });
    }

    private void displayLessonContent(int index) {
        LessonContent content = contents.get(index);

        // Reset visibility
        audioButton.setVisibility(View.GONE);
        wordsTextView.setVisibility(View.GONE);
        pronunciationTextView.setVisibility(View.GONE);
        questionText.setVisibility(View.GONE);

        switch (content.getContentType()) {
            case "Text":
                if (content.getContentData() != null) {
                    questionText.setVisibility(View.VISIBLE);
                    questionText.setText(content.getContentData());
                }
                break;

            case "Word":
                if (content.getContentData() != null) {
                    questionText.setVisibility(View.VISIBLE);
                    questionText.setText(content.getContentData());
                }
                wordsTextView.setVisibility(View.VISIBLE);
                wordsTextView.setText(content.getWord());
                pronunciationTextView.setVisibility(View.VISIBLE);
                pronunciationTextView.setText(content.getPronunciation());

                if (content.getPath() != null && !content.getPath().isEmpty()) {
                    audioButton.setVisibility(View.VISIBLE);
                    audioButton.setOnClickListener(v -> playAudio(content.getPath()));
                }
                break;

            case "Image":

                break;

            default:
                break;
        }

        if (index == contents.size() - 1) {
            nextButton.setText("Finish"); // Change text to "Finish" on the last content
        } else {
            nextButton.setText("Next"); // Reset text to "Next" for other contents
        }


        prevButton.setEnabled(index > 0); // Disable on the first content
        nextButton.setEnabled(index < contents.size()); // Enable for all valid contents
    }

    private void playAudio(String audioPath) {
        int resId = getResources().getIdentifier(audioPath, "raw", getContext().getPackageName());
        if (resId != 0) {
            MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), resId);
            if (mediaPlayer != null) {
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            }
        }
    }

    private void loadQuiz() {
        // Add quiz loading logic here
    }

    private boolean isLessonId(String id) {
        return id != null && id.matches("^(BL|IL)\\d+$");
    }

    private boolean isQuizId(String id) {
        return id != null && id.matches("^(BQZ|IQZ)\\d+$");
    }

    private void navigateBackToLessonList() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    private void navigateToQuizCompletedFragment(String lessonTitle) {
        Bundle bundle = new Bundle();
        bundle.putString("lessonTitle", lessonTitle);
        bundle.putString("lessonId", lessonId);
        bundle.putString("userId", userId);

        QuizCompletedFragment completedFragment = new QuizCompletedFragment();
        completedFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, completedFragment)
                .addToBackStack(null)
                .commit();
    }
}
