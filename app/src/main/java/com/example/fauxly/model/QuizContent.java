package com.example.fauxly.model;

public class QuizContent {
    private int contentId;
    private String quizId;
    private int sequence;
    private String contentType; // Example: "question", "image", "video"
    private String title;

    public QuizContent(int contentId, String quizId, int sequence, String contentType, String title) {
        this.contentId = contentId;
        this.quizId = quizId;
        this.sequence = sequence;
        this.contentType = contentType;
        this.title = title;
    }

    // Getters and Setters
    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
