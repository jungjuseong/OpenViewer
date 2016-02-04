package net.bookinaction.model;

public class StripperParam {

    //private float averageCharTolerance;
    //private float dropThreshold;

    private float SPACE_MARGINAL_SCALE;
    private float SAME_LINE_THRESHOLD;

    public StripperParam(float SPACE_MARGINAL_SCALE, float SAME_LINE_THRESHOLD) {
        this.SPACE_MARGINAL_SCALE = SPACE_MARGINAL_SCALE;
        this.SAME_LINE_THRESHOLD = SAME_LINE_THRESHOLD;
    }

    public float getSpaceMarginalScale() { return SPACE_MARGINAL_SCALE; };
    public float getSameLineThreshold() { return SAME_LINE_THRESHOLD; };
}
