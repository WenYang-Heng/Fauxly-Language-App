package com.example.fauxly.model;

public class DailyWord {
    private int wordId;
    private String word;
    private String pronunciation;
    private String translation;
    private String audioPath;
    private int languageId;
    private String proficiencyLevel;

    public DailyWord(int wordId, String word, String pronunciation, String translation, String audioPath, int languageId, String proficiencyLevel) {
        this.wordId = wordId;
        this.word = word;
        this.pronunciation = pronunciation;
        this.translation = translation;
        this.audioPath = audioPath;
        this.languageId = languageId;
        this.proficiencyLevel = proficiencyLevel;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
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

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(String proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }
}
