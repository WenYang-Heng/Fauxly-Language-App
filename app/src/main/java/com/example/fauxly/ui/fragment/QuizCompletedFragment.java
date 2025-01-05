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
import com.example.fauxly.database.DatabaseRepository;

public class QuizCompletedFragment extends Fragment {

    private static final String ARG_LESSON_TITLE = "lessonTitle";
    private static final String ARG_LESSON_ID = "lessonId";
    private static final String ARG_USER_ID = "userId";
    private String lessonTitle;
    private String userId;
    private String lessonId;
    private Button doneButton;
    private DatabaseRepository repository;

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
            lessonId = getArguments().getString(ARG_LESSON_ID);
            userId = getArguments().getString(ARG_USER_ID);
        }

        // Initialize repository
        repository = new DatabaseRepository(requireContext());
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

        doneButton = view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(v -> {
            updateLessonCompletion();
            navigateBackToLessonList();
        });

        return view;
    }

    private void updateLessonCompletion() {
        if (lessonId != null && userId != null) {
            repository.updateUserLessonCompletion(Integer.parseInt(userId), lessonId);
        }
    }

    private void navigateBackToLessonList() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Navigate back to LessonListFragment
        fragmentManager.popBackStack("LessonListFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
