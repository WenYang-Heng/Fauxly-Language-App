package com.example.fauxly.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fauxly.MainActivity;
import com.example.fauxly.R;
import com.example.fauxly.database.DatabaseRepository;
import com.example.fauxly.model.User;
import com.example.fauxly.model.UserStats;
import com.example.fauxly.utils.AchievementTracker;
import com.example.fauxly.utils.FragmentUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private static final String ARG_USER_ID = "userId";
    private LinearLayout day1, day2, day3, day4, day5;
    private DatabaseRepository repository;
    private String userId;
    private UserStats stats;
    private User user;
    private TextView TVUsername;
    private TextView dailyStreak;
    private ImageButton IBCourse;

    private OnUserIdChangeListener userIdChangeListener;

    // Interface for communication
    public interface OnUserIdChangeListener {
        void onUserIdChanged(String newUserId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserIdChangeListener) {
            userIdChangeListener = (OnUserIdChangeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnUserIdChangeListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        } else {
            userId = ((MainActivity) requireActivity()).getUserId();
        }
        repository = new DatabaseRepository(requireContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentUtils.setFragmentContainerMarginBottom(requireActivity(), false);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the LinearLayouts
        day1 = view.findViewById(R.id.day1);
        day2 = view.findViewById(R.id.day2);
        day3 = view.findViewById(R.id.day3);
        day4 = view.findViewById(R.id.day4);
        day5 = view.findViewById(R.id.day5);

        repository = new DatabaseRepository(getContext());
        TVUsername = view.findViewById(R.id.TVUsername);
        dailyStreak = view.findViewById(R.id.dailyStreak);
        IBCourse = view.findViewById(R.id.IBCourse);

        IBCourse.setOnClickListener(v -> {
            navigateToCourseFragment();
        });

        fetchUser();

        // Fetch user streak and set up the UI
        fetchUserStats();

        if(user != null) {
            TVUsername.setText(user.getName());
        }

        return view;
    }

    private void navigateToCourseFragment() {
        Fragment courseFragment = new CoursePageFragment();

        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        courseFragment.setArguments(bundle);

        // Replace the current fragment with the new one
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, courseFragment) // Replace with your FrameLayout ID
                .addToBackStack(null) // Add to back stack for back navigation
                .commit();
    }

    private void fetchUser() {
        user = repository.getUserById(Integer.parseInt(userId));
        if (user != null && userIdChangeListener != null) {
            userIdChangeListener.onUserIdChanged(String.valueOf(user.getUserId()));
        }
    }

    private void fetchUserStats() {
        stats = repository.getUserStatsById(Integer.parseInt(userId)); // Replace with actual user ID logic

        if (stats != null) {
            String lastClaimDate = stats.getLastClaimDate();
            int fiveDayLoginStreak = stats.getFiveDayLoginStreak();

            // Check if it's a new day
            boolean isNewDay = shouldResetStreak(lastClaimDate);

            // If it's a new day and the streak is completed (5 days), reset
            if (fiveDayLoginStreak == 5 && isNewDay) {
                repository.resetUserStreak(Integer.parseInt(userId)); // Reset streak in database
                fiveDayLoginStreak = 0; // Reset local streak
                Toast.makeText(getContext(), "Streak reset! Start again.", Toast.LENGTH_SHORT).show();
            } else if (isNewDay) {
                // Reset streak for missed claim
                repository.resetUserStreak(Integer.parseInt(userId));
                fiveDayLoginStreak = 0; // Reset local streak
            }

            // Update the UI based on the streak
            updateStreakUI(fiveDayLoginStreak);
        } else {
            Toast.makeText(getContext(), "User stats not found!", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean shouldResetStreak(String lastClaimDate) {
        if (lastClaimDate == null || lastClaimDate.isEmpty()) return true;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date lastDate = dateFormat.parse(lastClaimDate);
            Date currentDate = new Date();

            // Check if the current date is more than 1 day after the last claim date
            return !dateFormat.format(currentDate).equals(dateFormat.format(lastDate));
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Reset streak on parsing errors
        }
    }

    private void updateStreakUI(int fiveDayLoginStreak) {
        // Reset all days to default disabled state
        resetDayUI(day1);
        resetDayUI(day2);
        resetDayUI(day3);
        resetDayUI(day4);
        resetDayUI(day5);

        // Update daily streak value
        if (stats != null) {
            dailyStreak.setText(String.valueOf(stats.getTotalLoginStreak())); // Convert to String
        }

        // Update claimed days
        if (fiveDayLoginStreak >= 1) setDayClaimed(day1);
        if (fiveDayLoginStreak >= 2) setDayClaimed(day2);
        if (fiveDayLoginStreak >= 3) setDayClaimed(day3);
        if (fiveDayLoginStreak >= 4) setDayClaimed(day4);
        if (fiveDayLoginStreak >= 5) setDayClaimed(day5);

        // Enable the current claimable day only if allowed to claim
        if (canClaimForToday()) {
            if (fiveDayLoginStreak == 0) enableDayForClaim(day1, 1);
            else if (fiveDayLoginStreak == 1) enableDayForClaim(day2, 2);
            else if (fiveDayLoginStreak == 2) enableDayForClaim(day3, 3);
            else if (fiveDayLoginStreak == 3) enableDayForClaim(day4, 4);
            else if (fiveDayLoginStreak == 4) enableDayForClaim(day5, 5);
        }
    }


    private void resetDayUI(LinearLayout day) {
        day.setBackgroundResource(R.drawable.xp_claim_rounded_corners); // Default background
        day.setClickable(false); // Disable clicking
    }

    private void setDayClaimed(LinearLayout day) {
        day.setBackgroundResource(R.drawable.xp_claimed); // Change to claimed background
        day.setClickable(false); // Ensure claimed days are not clickable
    }

    private void enableDayForClaim(LinearLayout day, int dayNumber) {
        day.setClickable(true);
        day.setOnClickListener(v -> claimXP(day, dayNumber));
        day.setBackgroundResource(R.drawable.xp_ready_to_claim); // Highlight as claimable
    }

    private boolean canClaimForToday() {
        if (stats.getLastClaimDate() == null) return true;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date lastClaimDate = dateFormat.parse(stats.getLastClaimDate());
            Date currentDate = new Date();

            // Return true if last claim date is not the same as today's date
            return !dateFormat.format(lastClaimDate).equals(dateFormat.format(currentDate));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void claimXP(LinearLayout day, int dayNumber) {
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        int xpGained = 200;
        stats.setCurrentXp(stats.getCurrentXp() + xpGained);
        stats.setTotalXp(stats.getTotalXp() + xpGained); // Update total XP

        // Check if user needs to level up
        if (stats.getCurrentXp() >= stats.getLevelUpXp()) {
            stats.setCurrentXp(stats.getCurrentXp() - stats.getLevelUpXp()); // Carry over remaining XP
            stats.setCurrentLevel(stats.getCurrentLevel() + 1); // Increment the level

            // Notify the user
            Toast.makeText(getContext(), "Congratulations! You've leveled up to Level " + stats.getCurrentLevel(), Toast.LENGTH_SHORT).show();
        }

        // Update the database with new XP and level
        repository.updateUserStatsXpAndLevel(
                Integer.parseInt(userId),
                stats.getCurrentXp(),
                stats.getCurrentLevel(),
                stats.getTotalXp()
        );

        // Update the database to mark the day as claimed
        repository.updateUserStreakAndDate(Integer.parseInt(userId), dayNumber, currentDate);

        AchievementTracker tracker = new AchievementTracker(requireContext());
        tracker.evaluateAchievements(Integer.parseInt(userId));

        // Fetch updated user stats to refresh the UI
        fetchUserStats();

        // Notify the user
        Toast.makeText(getContext(), "Day " + dayNumber + " claimed! You earned 200 XP!", Toast.LENGTH_SHORT).show();
    }


}
