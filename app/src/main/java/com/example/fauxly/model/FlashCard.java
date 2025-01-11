package com.example.fauxly.model;

public class FlashCard {
    private int flashcardId;
    private String name;
    private int itemCount;

    public FlashCard(int flashcardId, String name, int itemCount) {
        this.flashcardId = flashcardId;
        this.name = name;
        this.itemCount = itemCount;
    }

    public int getFlashcardId() {
        return flashcardId;
    }

    public String getName() {
        return name;
    }

    public int getItemCount() {
        return itemCount;
    }
}
