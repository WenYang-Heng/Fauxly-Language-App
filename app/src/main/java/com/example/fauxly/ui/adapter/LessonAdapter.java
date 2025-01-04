package com.example.fauxly.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fauxly.R;
import com.example.fauxly.model.Lesson;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private final List<Lesson> lessons;
    private final OnLessonClickListener listener;

    public LessonAdapter(List<Lesson> lessons, OnLessonClickListener listener) {
        this.lessons = lessons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_item, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.lessonTitle.setText(lesson.getLessonTitle());

        // Set click listener for each item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLessonClick(lesson.getLessonId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView lessonTitle;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            lessonTitle = itemView.findViewById(R.id.lessonTitle); // Match the ID in lesson_item.xml
        }
    }

    public interface OnLessonClickListener {
        void onLessonClick(String lessonId);
    }
}
