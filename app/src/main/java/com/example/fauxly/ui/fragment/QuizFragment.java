package com.example.fauxly.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizFragment extends Fragment {

    private RadioGroup answersGroup;
    private Button confirmButton;
    private TextView feedbackTextView;

    public QuizFragment() {
        // Required empty public constructor
        super(R.layout.fragment_quiz);
    }
    private String correctColor = "#AFDC9C";
    private String incorrectColor = "#FF9393";
    private String correctTextColor = "#2B7A0A";
    private String incorrectTextColor = "#BA2828";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_quiz, container, false);

        answersGroup = rootView.findViewById(R.id.answersGroup);
        confirmButton = rootView.findViewById(R.id.confirmButton);
        feedbackTextView = rootView.findViewById(R.id.feedbackTextView);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = answersGroup.getCheckedRadioButtonId();
                checkAnswer(selectedId);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
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
}