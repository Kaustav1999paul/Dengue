package com.example.dengue;

public class ModelLanguage {
    String languageCode, languageTitle;

    public ModelLanguage() {
    }

    public ModelLanguage(String languageCode, String languageTitle) {
        this.languageCode = languageCode;
        this.languageTitle = languageTitle;
    }


    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageTitle() {
        return languageTitle;
    }

    public void setLanguageTitle(String languageTitle) {
        this.languageTitle = languageTitle;
    }
}
