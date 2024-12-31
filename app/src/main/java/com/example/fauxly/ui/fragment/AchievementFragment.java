package com.example.fauxly.ui.fragment;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fauxly.R;
import com.example.fauxly.model.Achievement;
import com.example.fauxly.ui.adapter.AchievementAdapter;

import java.util.ArrayList;
import java.util.List;

public class AchievementFragment extends Fragment {

    public AchievementFragment() {
        // Required empty public constructor
        super(R.layout.fragment_achievement);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_achievement, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.achievementsLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get data from sqlite
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new Achievement("Achievement 1", "Description of achievement"));
        achievements.add(new Achievement("Achievement 2", "Description of achievement"));
        achievements.add(new Achievement("Achievement 3", "Description of achievement"));

        // Set up the adapter with a click listener
        AchievementAdapter adapter = new AchievementAdapter(achievements, achievement -> {
            // Pass title, description, and date to AchievementDetailFragment
            AchievementDetailFragment detailFragment = AchievementDetailFragment.newInstance(
                    achievement.getTitle(),
                    achievement.getDescription(),
                    "25 November 2024" // Replace with actual date from your data source
            );

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_view, detailFragment);
            transaction.addToBackStack(null); // Add to back stack for navigation
            transaction.commit();
        });


        recyclerView.setAdapter(adapter);

        // Add divider decoration
        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.achievement_divider));
        recyclerView.addItemDecoration(itemDecorator);

        return view;
    }
}
