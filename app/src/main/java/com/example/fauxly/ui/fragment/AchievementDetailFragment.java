package com.example.fauxly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fauxly.R;
import com.google.android.material.imageview.ShapeableImageView;

public class AchievementDetailFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_DATE = "date";

    private String title;
    private String description;
    private String date;

    public AchievementDetailFragment() {
        // Required empty public constructor
    }

    public static AchievementDetailFragment newInstance(String title, String description, String date) {
        AchievementDetailFragment fragment = new AchievementDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESCRIPTION, description);
        args.putString(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            description = getArguments().getString(ARG_DESCRIPTION);
            date = getArguments().getString(ARG_DATE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achievement_detail, container, false);

        // Initialize UI elements
        ShapeableImageView profilePicture = view.findViewById(R.id.profilePicture);
        TextView titleTextView = view.findViewById(R.id.achievement_detail_title);
        TextView descriptionTextView = view.findViewById(R.id.achievement_detail_description);
        TextView dateTextView = view.findViewById(R.id.achievement_detail_date);
        Button backButton = view.findViewById(R.id.backButton);

        titleTextView.setText(title);
        descriptionTextView.setText(description);
        dateTextView.setText(String.format("Achieved on: %s", date));

        // Back button click listener to navigate back to the previous fragment
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }
}
