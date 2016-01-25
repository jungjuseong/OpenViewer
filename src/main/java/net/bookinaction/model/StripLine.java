package net.bookinaction.model;

import java.awt.geom.Rectangle2D;

public class StripLine {

    public int pageNum;
    public int lineNum;

    public int startIndex;
    public int endIndex;
    public Rectangle2D boundingRect;

    public StripLine(int pageNum, int lineNum, int startIndex, int endIndex, Rectangle2D boundingRect) {
        this.pageNum = pageNum;
        this.lineNum = lineNum;
        this.boundingRect = boundingRect;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }
}
