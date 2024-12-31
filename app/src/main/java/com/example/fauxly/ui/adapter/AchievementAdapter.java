package com.example.fauxly.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fauxly.R;
import com.example.fauxly.model.Achievement;

import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Achievement achievement);
    }

    private final List<Achievement> achievementList;
    private final OnItemClickListener listener;

    public AchievementAdapter(List<Achievement> achievementList, OnItemClickListener listener) {
        this.achievementList = achievementList;
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
        Achievement achievement = achievementList.get(position);
        holder.title.setText(achievement.getTitle());
        holder.description.setText(achievement.getDescription());

        // Set the click listener for the item
        holder.itemView.setOnClickListener(v -> listener.onItemClick(achievement));
    }

    @Override
    public int getItemCount() {
        return achievementList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.achievement_title);
            description = itemView.findViewById(R.id.achievement_description);
        }
    }
}
