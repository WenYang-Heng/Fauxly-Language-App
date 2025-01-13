package com.example.fauxly.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
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
import com.example.fauxly.utils.AssetUtils;
import com.example.fauxly.utils.FragmentUtils;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
    private ShapeableImageView lvl_badge;
    private TextView currentLevel;

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
        lvl_badge = view.findViewById(R.id.lvl_badge);
        currentLevel = view.findViewById(R.id.currentLevel);

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
        stats = repository.getUserStatsById(Integer.parseInt(userId));

        if (stats != null) {
            String lastClaimDate = stats.getLastClaimDate();
            int fiveDayLoginStreak = stats.getFiveDayLoginStreak();

            currentLevel.setText(String.valueOf(stats.getCurrentLevel()));

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

            displayLevelBadge();
        } else {
            Toast.makeText(getContext(), "User stats not found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayLevelBadge() {
        if (stats != null) {
            int currentLevel = stats.getCurrentLevel();
            String badgeFileName = "lvl_badges/lvl_" + currentLevel + ".jpg";

            AssetUtils.loadImageFromAssets(requireContext(), badgeFileName, lvl_badge);
        }
    }


    private boolean shouldResetStreak(String lastClaimDate) {
        lastClaimDate = "2025-01-10";
        if (lastClaimDate == null || lastClaimDate.isEmpty()) {
            Log.d("DailyClaim", "No last claim date found. Streak should reset.");
            return true; // No last claim, reset streak
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date lastDate = dateFormat.parse(lastClaimDate);
            Date currentDate = new Date();

            long differenceInMillis = currentDate.getTime() - lastDate.getTime();
            long daysDifference = differenceInMillis / (24 * 60 * 60 * 1000);

            Log.d("DailyClaim", "Last Claim Date: " + dateFormat.format(lastDate));
            Log.d("DailyClaim", "Current Date: " + dateFormat.format(currentDate));
            Log.d("DailyClaim", "Days Difference: " + daysDifference);

            boolean shouldReset = daysDifference > 1;
            Log.d("DailyClaim", "Should Reset Streak: " + shouldReset);

            return shouldReset;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DailyClaim", "Error parsing dates: " + e.getMessage());
            return true; // Default to reset on error
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
        String lastClaimDate = stats.getLastClaimDate();

        if (lastClaimDate == null || lastClaimDate.isEmpty()) {
            Log.d("DailyClaim", "No last claim date found. Claim is allowed.");
            return true; // No last claim, allow claiming
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getDefault()); // Explicitly set time zone

            Date lastDate = dateFormat.parse(lastClaimDate);
            Date currentDate = new Date();

            Log.d("DailyClaim", "System Time: " + currentDate.toString());
            Log.d("DailyClaim", "Last Claim Date: " + dateFormat.format(lastDate));
            Log.d("DailyClaim", "Formatted Current Date: " + dateFormat.format(currentDate));

            long differenceInMillis = currentDate.getTime() - lastDate.getTime();
            long daysDifference = differenceInMillis / (24 * 60 * 60 * 1000);

            Log.d("DailyClaim", "Days Difference: " + daysDifference);

            boolean canClaim = daysDifference > 0;
            Log.d("DailyClaim", "Can Claim Today: " + canClaim);

            return canClaim;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DailyClaim", "Error parsing dates: " + e.getMessage());
            return false;
        }
    }


    private void claimXP(LinearLayout day, int dayNumber) {
        Log.d("DailyClaim", "Attempting to claim XP for Day " + dayNumber);

        if (!canClaimForToday()) {
            Log.d("DailyClaim", "Claim denied. User already claimed today.");
            Toast.makeText(getContext(), "You can only claim once per day!", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        Log.d("DailyClaim", "Claiming XP. Current Date: " + currentDate);

        int xpGained = 200;
        stats.setCurrentXp(stats.getCurrentXp() + xpGained);
        stats.setTotalXp(stats.getTotalXp() + xpGained); // Update total XP

        Log.d("DailyClaim", "XP Gained: " + xpGained);
        Log.d("DailyClaim", "Current XP: " + stats.getCurrentXp());
        Log.d("DailyClaim", "Total XP: " + stats.getTotalXp());

        if (stats.getCurrentXp() >= stats.getLevelUpXp()) {
            while (stats.getCurrentXp() >= stats.getLevelUpXp()) {
                stats.setCurrentXp(stats.getCurrentXp() - stats.getLevelUpXp());
                stats.setCurrentLevel(stats.getCurrentLevel() + 1);

                Log.d("DailyClaim", "Leveled up! New Level: " + stats.getCurrentLevel());
            }
            Toast.makeText(getContext(), "Congratulations! You've leveled up to Level " + stats.getCurrentLevel() + "!", Toast.LENGTH_SHORT).show();
        }

        repository.updateUserStatsXpAndLevel(
                Integer.parseInt(userId),
                stats.getCurrentXp(),
                stats.getCurrentLevel(),
                stats.getTotalXp()
        );

        repository.updateUserStreakAndDate(Integer.parseInt(userId), dayNumber, currentDate);
        Log.d("DailyClaim", "Streak updated for Day " + dayNumber);

        AchievementTracker tracker = new AchievementTracker(requireContext());
        tracker.evaluateAchievements(Integer.parseInt(userId));
        Log.d("DailyClaim", "Achievements evaluated.");

        fetchUserStats();

        Toast.makeText(getContext(), "Day " + dayNumber + " claimed! You earned 200 XP!", Toast.LENGTH_SHORT).show();
    }



}
