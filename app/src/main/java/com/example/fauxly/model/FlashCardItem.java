package com.example.fauxly.model;

public class FlashCardItem {
    private int cardId;
    private String word;
    private String pronunciation;
    private String translation;
    private String path;
    private int flashcardId;

    public FlashCardItem(int cardId, String word, String pronunciation, String translation, String path, int flashcardId) {
        this.cardId = cardId;
        this.word = word;
        this.pronunciation = pronunciation;
        this.translation = translation;
        this.path = path;
        this.flashcardId = flashcardId;
    }

    public int getCardId() {
        return cardId;
    }

    public String getWord() {
        return word;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public String getTranslation() {
        return translation;
    }

    public String getPath() {
        return path;
    }

    public int getFlashcardId() {
        return flashcardId;
    }
}
