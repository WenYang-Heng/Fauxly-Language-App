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

import com.example.fauxly.R;

public class QuizCompletedFragment extends Fragment {

    private static final String ARG_LESSON_TITLE = "lessonTitle";
    private String lessonTitle;
    private Button doneBtn;

    public QuizCompletedFragment() {
        // Required empty public constructor
        super(R.layout.fragment_quiz_completed);
    }

    public static QuizCompletedFragment newInstance(String lessonTitle) {
        QuizCompletedFragment fragment = new QuizCompletedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LESSON_TITLE, lessonTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lessonTitle = getArguments().getString(ARG_LESSON_TITLE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_completed, container, false);

        // Set lesson title
        TextView lessonTitleTextView = view.findViewById(R.id.completedText);
        if (lessonTitle != null) {
            lessonTitleTextView.setText("Completed " + lessonTitle);
        }

        Button doneButton = view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(v -> navigateBackToLessonList());

        return view;
    }

    private void navigateBackToLessonList() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Navigate back to LessonListFragment
        fragmentManager.popBackStack("LessonListFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
