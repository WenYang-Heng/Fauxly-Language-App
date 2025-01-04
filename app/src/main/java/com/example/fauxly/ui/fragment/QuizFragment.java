package com.example.fauxly.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
    private Button audioButton;
    private TextView feedbackTextView;
    private TextView questionText;
    private TextView wordsTextView;
    private TextView pronunciationTextView;

    public QuizFragment() {
        // Required empty public constructor
        super(R.layout.fragment_quiz);
    }
    private String correctColor = "#AFDC9C";
    private String incorrectColor = "#FF9393";
    private String correctTextColor = "#2B7A0A";
    private String incorrectTextColor = "#BA2828";
    private DatabaseRepository repository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_quiz, container, false);

        answersGroup = rootView.findViewById(R.id.answersGroup);
        confirmButton = rootView.findViewById(R.id.confirmButton);
        nextButton = rootView.findViewById(R.id.nextButton);
        audioButton = rootView.findViewById(R.id.audioButton);
        feedbackTextView = rootView.findViewById(R.id.feedbackTextView);
        questionText = rootView.findViewById(R.id.questionText);
        wordsTextView = rootView.findViewById(R.id.wordsTextView);
        pronunciationTextView = rootView.findViewById(R.id.pronunciationTextView);
        TextView lessonTitle = rootView.findViewById(R.id.lessonTitle);
        Button audioButton = rootView.findViewById(R.id.audioButton);

        confirmButton.setVisibility(View.GONE);

        repository = new DatabaseRepository(getContext());

        String lessonId = getArguments() != null ? getArguments().getString("lessonId") : null;

        // Check ID format to determine whether to load a lesson or quiz
        if (isLessonId(lessonId)) {
            loadLessonContent(lessonId, questionText, lessonTitle, nextButton, audioButton);
        } else if (isQuizId(lessonId)) {
            loadQuiz();
        } else {
            lessonTitle.setText("Invalid ID format");
        }

        confirmButton.setOnClickListener(v -> {
            int selectedId = answersGroup.getCheckedRadioButtonId();
            checkAnswer(selectedId);
        });

        return rootView;
    }

    private void loadLessonContent(String lessonId, TextView questionText, TextView lessonTitle,
                            Button nextButton, Button audioButton) {
        List<LessonContent> contents = repository.getLessonContents(lessonId);

        if (contents.isEmpty()) {
            lessonTitle.setText("No content found for this lesson");
            nextButton.setVisibility(View.GONE);
            return;
        }

        // Track current content index
        final int[] currentIndex = {0};

        // Load the first content item
        displayLessonContent(contents, currentIndex[0], questionText, lessonTitle, audioButton);

        // Next button click listener
        nextButton.setOnClickListener(v -> {
            currentIndex[0]++;
            if (currentIndex[0] < contents.size()) {
                displayLessonContent(contents, currentIndex[0], questionText, lessonTitle, audioButton);
            } else {
                navigateToQuizCompletedFragment(lessonTitle.getText().toString());
            }
        });
    }

    private void displayLessonContent(List<LessonContent> contents, int index, TextView questionText,
                                      TextView lessonTitle, Button audioButton) {
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
                // Handle image content (if needed)
                break;

            default:
                break;
        }
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

    }


    private void checkAnswer(int selectedId) {
        String correctAnswer = "Answer C"; // need to get correct answer from database
        RadioButton selectedRadioButton = answersGroup.findViewById(selectedId);
        if (selectedRadioButton != null) {
            if (selectedRadioButton.getText().equals(correctAnswer)) {
                feedbackTextView.setText("You got it right!");
                updateFeedbackBackground(correctColor, correctTextColor);
            } else {
                feedbackTextView.setText("That is incorrect! Correct Answer: " + correctAnswer);
                updateFeedbackBackground(incorrectColor, incorrectTextColor);
            }
        }
        feedbackTextView.setVisibility(View.VISIBLE);
    }

    private void updateFeedbackBackground(String backgroundColor, String textColor){
        GradientDrawable background = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.rounded_corners);
        background.setColor(Color.parseColor(backgroundColor));
        feedbackTextView.setTextColor(Color.parseColor(textColor));
        feedbackTextView.setBackground(background);
    }

    private boolean isLessonId(String id) {
        return id.matches("^(BL|IL)\\d+$"); // Matches BL1, BL2, ..., IL1, IL2, etc.
    }

    private boolean isQuizId(String id) {
        return id.matches("^(BQZ|IQZ)\\d+$"); // Matches BQZ1, BQZ2, ..., IQZ1, IQZ2, etc.
    }

    private void navigateToQuizCompletedFragment(String lessonTitle) {
        Bundle bundle = new Bundle();
        bundle.putString("lessonTitle", lessonTitle); // Pass the lesson title

        QuizCompletedFragment completedFragment = new QuizCompletedFragment();
        completedFragment.setArguments(bundle);

        // Navigate to QuizCompletedFragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, completedFragment) // Replace with your container ID
                .addToBackStack(null)
                .commit();
    }

}