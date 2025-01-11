package com.example.fauxly.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fauxly.R;
import com.example.fauxly.model.Achievement;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    private final List<Achievement> achievements;
    private final OnAchievementClickListener listener;

    public AchievementAdapter(List<Achievement> achievements, OnAchievementClickListener listener) {
        this.achievements = achievements;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.achievement_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Achievement achievement = achievements.get(position);

        holder.titleTextView.setText(achievement.getTitle());
        holder.descriptionTextView.setText(achievement.getDescription());

        // Grey out the FlexboxLayout if not achieved
        if (achievement.isAchieved()) {
            holder.flexboxLayout.setAlpha(1.0f); // Full color
        } else {
            holder.flexboxLayout.setAlpha(0.5f); // Greyed out
        }

        holder.itemView.setOnClickListener(v -> listener.onAchievementClicked(achievement));
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final FlexboxLayout flexboxLayout;
        private final TextView titleTextView;
        private final TextView descriptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flexboxLayout = itemView.findViewById(R.id.achievementItem);
            titleTextView = itemView.findViewById(R.id.achievement_title);
            descriptionTextView = itemView.findViewById(R.id.achievement_description);
        }
    }

    public interface OnAchievementClickListener {
        void onAchievementClicked(Achievement achievement);
    }
}
