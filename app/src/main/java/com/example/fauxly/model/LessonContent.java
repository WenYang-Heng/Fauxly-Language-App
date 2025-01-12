package com.example.fauxly.model;

public class LessonContent {
    private int contentId;       // Auto-incrementing ID for each content piece
    private String lessonId;     // References lesson_id in lesson table
    private int contentOrder;    // Order of the content in the lesson
    private String contentType;  // Type of content (e.g., Text, Image, Word, etc.)
    private String contentData;  // Textual data (e.g., description or details for the content)
    private String path;         // File path or URL for multimedia content
    private String word;         // Individual word (if applicable)
    private String pronunciation; // English pronunciation for the word
    private String translation;

    // Constructor
    public LessonContent(int contentId, String lessonId, int contentOrder, String contentType, String contentData, String path, String word, String pronunciation, String translation) {
        this.contentId = contentId;
        this.lessonId = lessonId;
        this.contentOrder = contentOrder;
        this.contentType = contentType;
        this.contentData = contentData;
        this.path = path;
        this.word = word;
        this.pronunciation = pronunciation;
        this.translation = translation;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    // Getters and Setters
    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public int getContentOrder() {
        return contentOrder;
    }

    public void setContentOrder(int contentOrder) {
        this.contentOrder = contentOrder;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentData() {
        return contentData;
    }

    public void setContentData(String contentData) {
        this.contentData = contentData;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    @Override
    public String toString() {
        return "LessonContent{" +
                "contentId=" + contentId +
                ", lessonId='" + lessonId + '\'' +
                ", contentOrder=" + contentOrder +
                ", contentType='" + contentType + '\'' +
                ", contentData='" + contentData + '\'' +
                ", path='" + path + '\'' +
                ", word='" + word + '\'' +
                ", pronunciation='" + pronunciation + '\'' +
                '}';
    }
}
