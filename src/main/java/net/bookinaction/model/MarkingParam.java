package net.bookinaction.model;

import org.apache.pdfbox.pdmodel.graphics.color.PDColor;

public class MarkingParam {

    private String englishFontPath;
    private String koreanFontPath;
    private PDColor textColor;

    public MarkingParam(String koreanFontPath, String englishFontPath, PDColor textColor) {
        this.koreanFontPath = koreanFontPath;
        this.englishFontPath = englishFontPath;
        this.textColor = textColor;
    }
    public String getEnglishFontPath() { return englishFontPath; };
    public String getKoreanFontPath() { return koreanFontPath; };
    public PDColor getTextColor() { return textColor; };

}
