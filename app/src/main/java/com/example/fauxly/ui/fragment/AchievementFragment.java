package com.example.fauxly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fauxly.R;
import com.example.fauxly.database.DatabaseRepository;
import com.example.fauxly.model.Achievement;
import com.example.fauxly.ui.adapter.AchievementAdapter;
import com.example.fauxly.utils.FragmentUtils;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AchievementFragment extends Fragment {

    private static final String ARG_USER_ID = "userId";
    private String userId;
    private RecyclerView achievementsRecyclerView;
    private AchievementAdapter achievementAdapter;
    private DatabaseRepository repository;
    private MaterialButton backButton;

    public AchievementFragment() {
        super(R.layout.fragment_achievement);
    }

    public static AchievementFragment newInstance(String userId) {
        AchievementFragment fragment = new AchievementFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achievement, container, false);

        FragmentUtils.setFragmentContainerMarginBottom(requireActivity(), true);
        achievementsRecyclerView = view.findViewById(R.id.achievementsLayout);
        backButton = view.findViewById(R.id.backButton);

        repository = new DatabaseRepository(requireContext());

        // Set up RecyclerView
        achievementsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<Achievement> achievements = repository.getAllAchievementsWithUserStatus(userId);
        achievementAdapter = new AchievementAdapter(achievements, this::onAchievementClicked);
        achievementsRecyclerView.setAdapter(achievementAdapter);

        // Back button logic
        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Add divider decoration
        DividerItemDecoration itemDecorator = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.achievement_divider));
        achievementsRecyclerView.addItemDecoration(itemDecorator);

        return view;
    }

    private void onAchievementClicked(Achievement achievement) {
        AchievementDetailFragment fragment = AchievementDetailFragment.newInstance(
                achievement.getTitle(), achievement.getDescription(), achievement.getDateAchieved());
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .addToBackStack(null)
                .commit();
    }
}
