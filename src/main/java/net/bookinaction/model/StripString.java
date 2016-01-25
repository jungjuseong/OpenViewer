package net.bookinaction.model;


import org.apache.pdfbox.pdmodel.font.PDFont;

import net.bookinaction.utils.Vector;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class StripString {

    private StringBuilder textBuilder;
    private List<Rectangle2D> boundingRects;
    private List<PDFont> fonts;
    private List<Float> fontSizes;
    private List<Vector> positions;

    public StripString() {
        textBuilder = new StringBuilder();
        boundingRects = new ArrayList<Rectangle2D>();
        fonts = new ArrayList<PDFont>();
        fontSizes = new ArrayList<Float>();
        positions = new ArrayList<Vector>();
    }

    public String toString() {
        return textBuilder.toString();
    }

    public String substring(int beginIndex, int endIndex) {
        return textBuilder.toString().substring(beginIndex, endIndex);
    }

    public int size() {
        return textBuilder.length();
    }

    public Rectangle2D boundingRect(int index) {
        return boundingRects.get(index);
    }

    public PDFont getFont(int index) {
        return fonts.get(index);
    }

    public float getFontSize(int index) {
        return fontSizes.get(index);
    }

    public Vector getPosition(int index) {
        return positions.get(index);
    }

    public Rectangle2D boundingRect(int start, int end) {

        if (start == end)
            return boundingRects.get(start);

        Rectangle2D resultRect = boundingRects.get(start);
        for (int i = start+1; i <= end; i++) {
            resultRect =  resultRect.createUnion(boundingRects.get(i));
        }

        return resultRect;
    }

    public void add(String token, Vector position, Rectangle2D boundingRect, PDFont font, float fontSize) {

        if (token.length() != 1) {
            System.out.printf("Note: {%s}  It's dialect character !\n", token);
        }

        textBuilder.append(token.charAt(0)); // add only one character
        boundingRects.add(boundingRect);
        fonts.add(font);
        fontSizes.add(fontSize);
        positions.add(position);
    }
}
