package com.example.fauxly.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fauxly.R;
import com.example.fauxly.database.DatabaseRepository;
import com.example.fauxly.model.Quiz;
import com.example.fauxly.ui.adapter.QuizAdapter;
import com.example.fauxly.utils.NavigationUtil;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class QuizListFragment extends Fragment {

    private static final String ARG_USER_ID = "userId";
    private static final String ARG_LANGUAGE_ID = "languageId";
    private static final String ARG_PROFICIENCY_LEVEL = "proficiencyLevel";

    private String userId;
    private int languageId;
    private String proficiencyLevel;

    private RecyclerView quizRecyclerView;
    private QuizAdapter quizAdapter;
    private DatabaseRepository repository;
    private MaterialButton backButton;

    public static QuizListFragment newInstance(String userId, int languageId, String proficiencyLevel) {
        QuizListFragment fragment = new QuizListFragment();
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
        View view = inflater.inflate(R.layout.fragment_quiz_list, container, false);

        // Initialize RecyclerView
        quizRecyclerView = view.findViewById(R.id.quizRecyclerView);
        quizRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Add divider decoration
        DividerItemDecoration itemDecorator = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.achievement_divider));
        quizRecyclerView.addItemDecoration(itemDecorator);

        backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Navigate back
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Load quizzes and populate RecyclerView
        loadQuizzes();

        return view;
    }

    private void loadQuizzes() {
        // Fetch quizzes with completion status
        List<Quiz> quizzes = repository.getQuizzesWithCompletionStatus(
                Integer.parseInt(userId), proficiencyLevel, languageId
        );

        if (quizzes != null && !quizzes.isEmpty()) {
            quizAdapter = new QuizAdapter(quizzes, quiz -> navigateToQuizFragment(quiz));
            quizRecyclerView.setAdapter(quizAdapter);
        } else {
            System.out.println("No quizzes found.");
        }
    }


    private void navigateToQuizFragment(Quiz quiz) {
        Log.d("QuizListFragment", "Navigating to QuizFragment with ID: " + quiz.getQuizId());
        NavigationUtil.navigateToQuizFragment(
                requireActivity().getSupportFragmentManager(),
                repository,
                userId,
                quiz.getQuizId(),
                quiz.getTitle(),
                false // Indicates it's a quiz
        );
    }


}
