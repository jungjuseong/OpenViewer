package net.bookinaction.model;


public class Token {
    private String stem;
    private int start;
    private int end;

    public Token(String stem, int start, int end) {

        this.stem = stem;
        this.start = start;
        this.end = end;
    }

    public String getStem() {
        return stem;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
