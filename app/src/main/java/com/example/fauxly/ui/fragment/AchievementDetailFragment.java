package com.example.fauxly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fauxly.R;
import com.google.android.material.button.MaterialButton;

public class AchievementDetailFragment extends Fragment {

    private static final String ARG_ACHIEVEMENT_TITLE = "title";
    private static final String ARG_ACHIEVEMENT_DESCRIPTION = "description";
    private static final String ARG_DATE_ACHIEVED = "dateAchieved";

    private String title;
    private String description;
    private String dateAchieved;

    public static AchievementDetailFragment newInstance(String title, String description, String dateAchieved) {
        AchievementDetailFragment fragment = new AchievementDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACHIEVEMENT_TITLE, title);
        args.putString(ARG_ACHIEVEMENT_DESCRIPTION, description);
        args.putString(ARG_DATE_ACHIEVED, dateAchieved);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_ACHIEVEMENT_TITLE);
            description = getArguments().getString(ARG_ACHIEVEMENT_DESCRIPTION);
            dateAchieved = getArguments().getString(ARG_DATE_ACHIEVED);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achievement_detail, container, false);

        TextView titleTextView = view.findViewById(R.id.achievement_detail_title);
        TextView descriptionTextView = view.findViewById(R.id.achievement_detail_description);
        TextView dateAchievedTextView = view.findViewById(R.id.achievement_detail_date);
        MaterialButton backButton = view.findViewById(R.id.backButton);

        titleTextView.setText(title);
        descriptionTextView.setText(description);
        // Handle null or empty dateAchieved
        if (dateAchieved == null || dateAchieved.isEmpty()) {
            dateAchievedTextView.setText("Not achieved yet");
        } else {
            dateAchievedTextView.setText("Achieved on: " + dateAchieved);
        }

        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }
}
