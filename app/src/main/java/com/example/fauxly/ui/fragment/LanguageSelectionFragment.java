package com.example.fauxly.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.fauxly.R;
import com.google.android.material.button.MaterialButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LanguageSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LanguageSelectionFragment extends Fragment {

    private static final int JAPANESE_ID = 1;
    private static final int KOREAN_ID = 2;
    private static final String ARG_USER_ID = "userId";
    private String userId;
    private RadioGroup languageGroup;
    private Button continueButton;
    private MaterialButton backButton;
    private int selectedLanguageId = -1; // Default invalid language ID

    public LanguageSelectionFragment() {
        // Required empty public constructor
        super(R.layout.fragment_language_selection);
    }

    // TODO: Rename and change types and number of parameters
    public static LanguageSelectionFragment newInstance(String userId) {
        LanguageSelectionFragment fragment = new LanguageSelectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_language_selection, container, false);

        languageGroup = view.findViewById(R.id.languageGroup);
        backButton = view.findViewById(R.id.backButton);
        continueButton = view.findViewById(R.id.buttonContinue);

        // Disable the button initially
        continueButton.setEnabled(false);

        backButton.setOnClickListener(v -> navigateBack());

        // Enable the button only when a language is selected
        languageGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.japanese) {
                selectedLanguageId = JAPANESE_ID;
            } else if (checkedId == R.id.korean) {
                selectedLanguageId = KOREAN_ID;
            }
            continueButton.setEnabled(selectedLanguageId != -1);
        });

        // Handle continue button click
        continueButton.setOnClickListener(v -> navigateToProficiencyFragment(view));

        return view;
    }

    private void navigateToProficiencyFragment(View view) {
        if (userId != null && selectedLanguageId != -1) {
            // Create a bundle with the arguments
            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);
            bundle.putInt("languageId", selectedLanguageId);

            // Create the ProficiencyFragment instance and set arguments
            ProficiencyFragment proficiencyFragment = new ProficiencyFragment();
            proficiencyFragment.setArguments(bundle);

            // Use FragmentManager to replace the current fragment
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container_view, proficiencyFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void navigateBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}