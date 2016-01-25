package net.bookinaction.utils;


import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;

public class Color {

    public static final PDColor RED = new PDColor(new float[]{1, 0, 0}, PDDeviceRGB.INSTANCE);
    public static final PDColor BLUE = new PDColor(new float[]{0, 0, 1}, PDDeviceRGB.INSTANCE);
    public static final PDColor GREEN = new PDColor(new float[]{0, 1, 0}, PDDeviceRGB.INSTANCE);
    public static final PDColor YELLOW = new PDColor(new float[]{1, 1, 0}, PDDeviceRGB.INSTANCE);

    public static final PDColor BLACK = new PDColor(new float[]{0, 0, 0}, PDDeviceRGB.INSTANCE);
    public static final PDColor GRAY10 = new PDColor(new float[]{.1f, .1f, .1f}, PDDeviceRGB.INSTANCE);
    public static final PDColor GRAY20 = new PDColor(new float[]{.2f, .2f, .2f}, PDDeviceRGB.INSTANCE);

    public static final PDColor GRAY30 = new PDColor(new float[]{.3f, .3f, .3f}, PDDeviceRGB.INSTANCE);
    public static final PDColor GRAY40 = new PDColor(new float[]{.4f, .4f, .4f}, PDDeviceRGB.INSTANCE);
    public static final PDColor GRAY50 = new PDColor(new float[]{.5f, .5f, .5f}, PDDeviceRGB.INSTANCE);
    public static final PDColor GRAY70 = new PDColor(new float[]{.7f, .7f, .7f}, PDDeviceRGB.INSTANCE);
    public static final PDColor GRAY80 = new PDColor(new float[]{.8f, .8f, .8f}, PDDeviceRGB.INSTANCE);
    public static final PDColor GRAY90 = new PDColor(new float[]{.9f, .9f, .9f}, PDDeviceRGB.INSTANCE);
}
