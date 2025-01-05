package com.example.fauxly.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.example.fauxly.R;
import com.example.fauxly.database.DatabaseRepository;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProficiencyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProficiencyFragment extends Fragment {

    private static final String ARG_USER_ID = "userId";
    private static final String ARG_LANGUAGE_ID = "languageId";
    private DatabaseRepository repository;

    private String userId;
    private int languageId;
    private String selectedProficiency = null;

    public ProficiencyFragment() {
        // Required empty public constructor
        super(R.layout.fragment_proficiency);
    }

    // TODO: Rename and change types and number of parameters
    public static ProficiencyFragment newInstance(String userId, int languageId) {
        ProficiencyFragment fragment = new ProficiencyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        args.putInt(ARG_LANGUAGE_ID, languageId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
            languageId = getArguments().getInt(ARG_LANGUAGE_ID);
        }
        repository = new DatabaseRepository(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proficiency, container, false);

        RadioGroup proficiencyGroup = view.findViewById(R.id.proficiencyGroup);
        Button continueButton = view.findViewById(R.id.buttonContinue);
        View backButton = view.findViewById(R.id.backButton);

        // Enable the Continue button only when a proficiency level is selected
        proficiencyGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.beginner) {
                selectedProficiency = "B"; // Beginner
            } else if (checkedId == R.id.intermediate) {
                selectedProficiency = "I"; // Intermediate
            } else if (checkedId == R.id.advanced) {
                selectedProficiency = "A"; // Advanced
            }
            continueButton.setEnabled(selectedProficiency != null);
        });


        // Handle Continue button click
        continueButton.setOnClickListener(v -> {
            if (userId != null && selectedProficiency != null) {
                // Insert user language proficiency into the database
                repository.insertUserLanguage(Integer.parseInt(userId), languageId, selectedProficiency);
                navigateToCoursePage();
            }
        });

        // Handle Back button click
        backButton.setOnClickListener(v -> navigateBack());

        return view;
    }

    private void navigateToCoursePage() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Navigate back to CoursePageFragment
        fragmentManager.popBackStack("CoursePageFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void navigateBack() {
        // Navigate back to the LanguageSelectionFragment
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}