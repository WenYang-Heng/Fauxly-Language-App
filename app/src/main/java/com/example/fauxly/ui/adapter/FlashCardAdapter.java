package com.example.fauxly.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fauxly.R;
import com.example.fauxly.database.DatabaseRepository;
import com.example.fauxly.model.FlashCard;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class FlashCardAdapter extends RecyclerView.Adapter<FlashCardAdapter.FlashCardViewHolder> {

    private final List<FlashCard> flashCardList;
    private final OnFlashCardClickListener clickListener;
    private final DatabaseRepository repository;

    public interface OnFlashCardClickListener {
        void onFlashCardClick(int flashcardId, String flashcardName);
    }

    public FlashCardAdapter(List<FlashCard> flashCardList, OnFlashCardClickListener clickListener, DatabaseRepository repository) {
        this.flashCardList = flashCardList;
        this.clickListener = clickListener;
        this.repository = repository;
    }

    @NonNull
    @Override
    public FlashCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flashcard_item, parent, false);
        return new FlashCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashCardViewHolder holder, int position) {
        FlashCard flashCard = flashCardList.get(position);
        holder.titleTextView.setText(flashCard.getName());
        holder.itemCountTextView.setText(flashCard.getItemCount() + " card");

        // Handle flashcard click
        holder.itemView.setOnClickListener(v -> clickListener.onFlashCardClick(flashCard.getFlashcardId(), flashCard.getName()));

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            // Remove from database
            repository.deleteFlashCardById(flashCard.getFlashcardId());

            // Remove from list and notify adapter
            flashCardList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, flashCardList.size());
        });
    }

    @Override
    public int getItemCount() {
        return flashCardList.size();
    }

    static class FlashCardViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, itemCountTextView;
        MaterialButton deleteButton;

        public FlashCardViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.flashCardTitle);
            itemCountTextView = itemView.findViewById(R.id.itemCount);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
