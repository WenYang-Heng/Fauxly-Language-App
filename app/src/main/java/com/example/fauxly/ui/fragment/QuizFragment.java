package com.example.fauxly.ui.fragment;

import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fauxly.R;
import com.example.fauxly.database.DatabaseRepository;
import com.example.fauxly.model.Lesson;
import com.example.fauxly.model.LessonContent;
import com.example.fauxly.model.Option;
import com.example.fauxly.model.QuizContent;
import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

public class QuizFragment extends Fragment {

    private RadioGroup answersGroup;
    private Button confirmButton;
    private Button nextQuizButton;
    private Button nextButton;
    private Button prevButton;
    private MaterialButton audioButton;
    private MaterialButton flashcardButton;
    private MaterialButton backButton;
    private TextView feedbackTextView;
    private TextView questionText;
    private TextView wordsTextView;
    private TextView pronunciationTextView;
    private TextView lessonTitleTV;
    private TextView titleTV;
    private ProgressBar contentProgress;
    private TextView progressText;

    private DatabaseRepository repository;

    private List<LessonContent> contents;
    private List<QuizContent> quizContents;
    private int currentIndex = 0; // Track current content index
    private String id;
    private String userId;

    private String correctColor = "#AFDC9C";
    private String incorrectColor = "#FF9393";
    private String correctTextColor = "#2B7A0A";
    private String incorrectTextColor = "#BA2828";

    public QuizFragment() {
        super(R.layout.fragment_quiz);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_quiz, container, false);

        // Initialize views
        answersGroup = rootView.findViewById(R.id.answersGroup);
        confirmButton = rootView.findViewById(R.id.confirmButton);
        nextQuizButton = rootView.findViewById(R.id.nextQuiz);
        nextButton = rootView.findViewById(R.id.nextButton);
        prevButton = rootView.findViewById(R.id.prevButton);
        backButton = rootView.findViewById(R.id.backButton);
        flashcardButton = rootView.findViewById(R.id.flashCardBtn);
        audioButton = rootView.findViewById(R.id.audioButton);
        feedbackTextView = rootView.findViewById(R.id.feedbackTextView);
        questionText = rootView.findViewById(R.id.questionText);
        wordsTextView = rootView.findViewById(R.id.wordsTextView);
        pronunciationTextView = rootView.findViewById(R.id.pronunciationTextView);
        lessonTitleTV = rootView.findViewById(R.id.lessonTitle);
        titleTV = rootView.findViewById(R.id.titleTV);
        contentProgress = rootView.findViewById(R.id.contentProgress);
        progressText = rootView.findViewById(R.id.progressText);

        confirmButton.setVisibility(View.GONE);

        repository = new DatabaseRepository(getContext());

        // Retrieve arguments
        Bundle args = getArguments();
        if (args != null) {
            id = args.getString("id");
            Log.d("QuizFragment", "Retrieved Lesson/Quiz ID: " + id);
            userId = args.getString("userId");
            String title = args.getString("title"); // Used for both lesson and quiz titles

            if (title != null) {
                lessonTitleTV.setText(title);
            }

            if (id != null && isLessonId(id)) {
                Log.d("QuizFragment", "Lesson/Quiz ID: " + id);
                titleTV.setText("Lesson");
                loadLessonContent(id);
                toggleButtonVisibilityForLesson(); // Toggle visibility for lessons
            } else if (id != null && isQuizId(id)) {
                Log.d("QuizFragment", "Lesson/Quiz ID: " + id);
                titleTV.setText("Quiz");
                loadQuizContent(id);
                toggleButtonVisibilityForQuiz(); // Toggle visibility for quizzes
            }
             else {
                lessonTitleTV.setText("Invalid ID format");
            }
        }

        nextQuizButton.setOnClickListener(v -> {
            if (currentIndex < quizContents.size() - 1) {
                currentIndex++;
                displayQuizContent(quizContents.get(currentIndex));

                // Reset button visibility
                confirmButton.setVisibility(View.VISIBLE);
                nextQuizButton.setVisibility(View.GONE);
            } else {
                // Navigate to Quiz Completed Fragment
                navigateToQuizCompletedFragment(lessonTitleTV.getText().toString());
            }
        });

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

        // Set progress bar max value
        contentProgress.setMax(contents.size());

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
        flashcardButton.setVisibility(View.GONE);

        // Update progress bar and text
        contentProgress.setProgress(index + 1); // Progress is 1-based
        progressText.setText((index + 1) + "/" + contents.size());

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
                if (content.getWord() != null) {
                    wordsTextView.setVisibility(View.VISIBLE);
                    wordsTextView.setText(content.getWord());
                }
                if (content.getPronunciation() != null) {
                    pronunciationTextView.setVisibility(View.VISIBLE);
                    pronunciationTextView.setText(content.getPronunciation());
                }
                if (content.getPath() != null && !content.getPath().isEmpty()) {
                    audioButton.setVisibility(View.VISIBLE);
                    audioButton.setOnClickListener(v -> playAudio(content.getPath()));
                }
                flashcardButton.setVisibility(View.VISIBLE);
                flashcardButton.setOnClickListener(v -> addToFlashcard(content));
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

    private void addToFlashcard(LessonContent content) {
        if (content.getWord() == null || content.getWord().isEmpty()) {
            Toast.makeText(requireContext(), "No word available to add.", Toast.LENGTH_SHORT).show();
            return;
        }

        String message = repository.addWordToFlashcard(
                Integer.parseInt(userId),
                content.getWord(),
                content.getPronunciation(),
                content.getTranslation(),
                content.getPath()
        );

        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void playAudio(String audioPath) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor afd = requireContext().getAssets().openFd("audio/japanese/beginner/" + audioPath);

            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            mediaPlayer.prepare();
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(mp -> {
                mediaPlayer.release();
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error playing audio", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadQuizContent(String quizId) {
        quizContents = repository.getQuizContents(quizId);

        if (quizContents.isEmpty()) {
            lessonTitleTV.setText("No content found for this quiz");
            nextButton.setVisibility(View.GONE);
            prevButton.setVisibility(View.GONE);
            confirmButton.setVisibility(View.GONE);
            return;
        }

        // Set progress bar max value
        contentProgress.setMax(contents.size());

        currentIndex = 0;
        displayQuizContent(quizContents.get(currentIndex));

        nextButton.setOnClickListener(v -> {
            if (currentIndex < quizContents.size() - 1) {
                currentIndex++;
                displayQuizContent(quizContents.get(currentIndex));
            } else {
                navigateToQuizCompletedFragment("Quiz Completed");
            }
        });

        prevButton.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                displayQuizContent(quizContents.get(currentIndex));
            }
        });
    }

    private void displayQuizContent(QuizContent content) {
        // Hide feedback text at the start of the question
        feedbackTextView.setVisibility(View.GONE);

        // Set the question text
        questionText.setText(content.getTitle());

        // Update progress bar and text
        contentProgress.setProgress(currentIndex + 1); // Progress is 1-based
        progressText.setText((currentIndex + 1) + "/" + quizContents.size());

        // Retrieve options for the current content
        List<Option> options = repository.getOptionsByContentId(content.getContentId());
        Log.d("QuizFragment", "Options retrieved: " + options);

        // Ensure the RadioGroup is visible and clear any previous options
        answersGroup.setVisibility(View.VISIBLE);
        answersGroup.removeAllViews(); // Clear existing RadioButtons

        // Dynamically add options to the RadioGroup
        for (Option option : options) {
            RadioButton radioButton = createRadioButton(option.getOptionText(), option.getOptionId());
            answersGroup.addView(radioButton);
        }

        // Reset button visibility
        confirmButton.setVisibility(View.VISIBLE);
        nextQuizButton.setVisibility(View.GONE);

        // Reset the checked state of the RadioGroup
        answersGroup.clearCheck();

        // Set up the confirm button
        confirmButton.setOnClickListener(v -> {
            int selectedId = answersGroup.getCheckedRadioButtonId();
            checkAnswer(selectedId, content.getContentId());
        });

        // Reset all RadioButtons to enabled state
        for (int i = 0; i < answersGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) answersGroup.getChildAt(i);
            radioButton.setEnabled(true);
            radioButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.radio_button)); // Reset background
        }

        wordsTextView.setVisibility(View.GONE);
        pronunciationTextView.setVisibility(View.GONE);
        feedbackTextView.setVisibility(View.GONE);
        feedbackTextView.setText("");
        feedbackTextView.setBackground(null);

    }

    private RadioButton createRadioButton(String text, int id) {
        RadioButton radioButton = new RadioButton(getContext());

        // Set the text
        radioButton.setText(text);

        // Set the ID
        radioButton.setId(id);

        // Set padding
        radioButton.setPadding(50, 50, 50, 50); // Left padding of 20dp, top, right, and bottom 0

        // Set text style and color
        radioButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.radio_button_quiz));
        radioButton.setTextSize(16);

        // Remove default button style
        radioButton.setButtonDrawable(null);

        // Set background
        radioButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.radio_button));

        // Set margin (requires LayoutParams)
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 30);
        radioButton.setLayoutParams(params);

        return radioButton;
    }

    private void checkAnswer(int selectedOptionId, int contentId) {
        // Fetch correct answer from the database
        boolean isCorrect = repository.isCorrectOption(selectedOptionId, contentId);

        // Get the selected RadioButton
        RadioButton selectedRadioButton = answersGroup.findViewById(selectedOptionId);

        if (selectedRadioButton != null) {
            // Display feedback
            feedbackTextView.setVisibility(View.VISIBLE);
            if (isCorrect) {
                feedbackTextView.setText("You got it right!");
                updateFeedbackBackground(correctColor, correctTextColor);
            } else {
                // Fetch the correct answer for display
                String correctAnswer = repository.getCorrectOptionText(contentId);
                feedbackTextView.setText("That is incorrect! Correct Answer: " + correctAnswer);
                updateFeedbackBackground(incorrectColor, incorrectTextColor);
            }

            // Disable the RadioGroup to prevent further selection
            for (int i = 0; i < answersGroup.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) answersGroup.getChildAt(i);
                radioButton.setEnabled(false);
            }

            // Hide confirm button and show nextQuiz button
            confirmButton.setVisibility(View.GONE);
            nextQuizButton.setVisibility(View.VISIBLE);
        }
    }

    private void toggleButtonVisibilityForLesson() {
        confirmButton.setVisibility(View.GONE);
        nextQuizButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.VISIBLE);
        prevButton.setVisibility(View.VISIBLE);
    }

    private void toggleButtonVisibilityForQuiz() {
        confirmButton.setVisibility(View.VISIBLE);
        nextQuizButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
        prevButton.setVisibility(View.GONE);
    }

    private void updateFeedbackBackground(String backgroundColor, String textColor){
        GradientDrawable background = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.rounded_corners);
        background.setColor(Color.parseColor(backgroundColor));
        feedbackTextView.setTextColor(Color.parseColor(textColor));
        feedbackTextView.setBackground(background);
    }
    private boolean isLessonId(String id) {
        return id != null && id.matches("^(BL|IL)\\d+$");
    }

    private boolean isQuizId(String id) {
        return id != null && id.matches("^(BQ|IQ)\\d+$");
    }

    private void navigateBackToLessonList() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    private void navigateToQuizCompletedFragment(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("id", id);
        bundle.putString("userId", userId);
        bundle.putBoolean("isLesson", isLessonId(id));

        QuizCompletedFragment completedFragment = new QuizCompletedFragment();
        completedFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, completedFragment)
                .addToBackStack(null)
                .commit();
    }

}
