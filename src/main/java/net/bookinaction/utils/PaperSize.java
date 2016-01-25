package net.bookinaction.utils;

public enum PaperSize {

	B5(6.9f, 9.8f),
	B4(9.8f, 13.9f),
	A4(8.3f, 11.7f),
	A3(11.7f, 16.5f);
	
	private final float width;
	private final float height; // unit
	
	static final float UNIT = 72.0f;
	
	PaperSize(float widthInInch, float heightInInch) {
		this.width = widthInInch * UNIT;
		this.height = heightInInch * UNIT;
	}

	public float getWidth() { return width; }
    public float getHeight() { return height; }
}
