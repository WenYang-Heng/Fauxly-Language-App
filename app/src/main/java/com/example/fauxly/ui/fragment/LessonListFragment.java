package com.example.fauxly.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fauxly.R;
import com.example.fauxly.database.DatabaseRepository;
import com.example.fauxly.model.Lesson;
import com.example.fauxly.ui.adapter.LessonAdapter;
import com.example.fauxly.utils.NavigationUtil;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class LessonListFragment extends Fragment {

    private static final String ARG_USER_ID = "userId";
    private static final String ARG_LANGUAGE_ID = "languageId";
    private static final String ARG_PROFICIENCY_LEVEL = "proficiencyLevel";

    private String userId;
    private int languageId;
    private String proficiencyLevel;

    private RecyclerView lessonRecyclerView;
    private LessonAdapter lessonAdapter;
    private DatabaseRepository repository;
    private MaterialButton backButton;
    private TextView errorTextView;

    public static LessonListFragment newInstance(String userId, int languageId, String proficiencyLevel) {
        LessonListFragment fragment = new LessonListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        args.putInt(ARG_LANGUAGE_ID, languageId);
        args.putString(ARG_PROFICIENCY_LEVEL, proficiencyLevel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
            languageId = getArguments().getInt(ARG_LANGUAGE_ID);
            proficiencyLevel = getArguments().getString(ARG_PROFICIENCY_LEVEL);
        }

        repository = new DatabaseRepository(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson_list, container, false);

        // Initialize RecyclerView
        lessonRecyclerView = view.findViewById(R.id.lessonRecyclerView);
        lessonRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        errorTextView = view.findViewById(R.id.errorTextView);

        // Add divider decoration
        DividerItemDecoration itemDecorator = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.achievement_divider));
        lessonRecyclerView.addItemDecoration(itemDecorator);

        backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Navigate back
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        if (languageId == -1) {
            displayError("Please select a language to view lessons.");
        } else {
            errorTextView.setVisibility(View.GONE);
            loadLessons();
        }

        return view;
    }

    private void loadLessons() {
        // Fetch lessons with completion status
        List<Lesson> lessons = repository.getLessonsWithCompletionStatus(
                Integer.parseInt(userId), proficiencyLevel, languageId
        );

        if (lessons != null && !lessons.isEmpty()) {
            lessonAdapter = new LessonAdapter(lessons, lesson -> navigateToQuizFragment(lesson));
            lessonRecyclerView.setAdapter(lessonAdapter);
        } else {
            System.out.println("No lessons found.");
        }
    }

    private void navigateToQuizFragment(Lesson lesson) {
        NavigationUtil.navigateToQuizFragment(
                requireActivity().getSupportFragmentManager(),
                repository,
                userId,
                lesson.getLessonId(),
                lesson.getLessonTitle(),
                true // Indicates it's a lesson
        );
    }

    private void displayError(String message) {
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(message);
        lessonRecyclerView.setVisibility(View.GONE); // Hide RecyclerView
    }
}
