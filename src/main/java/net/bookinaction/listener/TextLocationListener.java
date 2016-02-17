package net.bookinaction.listener;

import net.bookinaction.model.StripString;
import net.bookinaction.model.StripperParam;
import net.bookinaction.utils.Vector;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextLocationListener extends PDFTextStripper {

	private StripperParam stripperParam;
	private StripString stripString;

	private List<Vector> startPoints;
	private List<Vector> endPoints;

	public TextLocationListener(StripperParam stripperParam, StripString stripString) throws IOException {
		super();

		this.stripperParam = stripperParam;
		this.stripString = stripString;

		startPoints = new ArrayList<Vector>();
		endPoints = new ArrayList<Vector>();
	}

	private float calculateDistance(Vector x0, Vector x1) {

		return x1.subtract(x0).length();
	}

	public Rectangle2D grow(final Rectangle2D.Float rect, float dw, float dh) {

		float x = rect.x - dw;
		float y = rect.y - dh;
		float width = rect.width + dw + dw;
		float height = rect.height + dh + dh;

		return new Rectangle2D.Float(x, y, width, height);
	}

	public Rectangle2D translate(Rectangle2D.Float rect, float mx, float my) {

		return new Rectangle2D.Float(rect.x + mx, rect.y + my, rect.width, rect.height);
	}


	private Rectangle2D getAdjustedRect(TextPosition textPos)  {

		PDFont font = textPos.getFont();
		BoundingBox bbox = null;
		try {
			bbox = font.getBoundingBox();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// advance width, bbox height (glyph space)
		float xadvance = 0f;
		try {
			xadvance = font.getWidth(textPos.getCharacterCodes()[0]);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Rectangle2D.Float rect = new Rectangle2D.Float(0, bbox.getLowerLeftY(), xadvance, bbox.getHeight());

		// glyph space -> user space
		// note: text.getTextMatrix() is not the Text Matrix, it's the Text Rendering Matrix
		AffineTransform affineTrans = textPos.getTextMatrix().createAffineTransform();
		
		if (font instanceof PDType3Font) {
			affineTrans.concatenate(font.getFontMatrix().createAffineTransform());
		} else {			
			affineTrans.scale(1 / 1000f, 1 / 1000f);
		}
		Shape s = affineTrans.createTransformedShape(rect);
		
		return s.getBounds2D();
	}

	/**
	 * Override the default functionality of PDFTextStripper.
	 */

	final static String SPACE = " ";
	final static String NEWLINE = "\r";

	@Override
	protected void writeString(String text, List<TextPosition> textPositions) {

		for (TextPosition textPos : textPositions) {

			Rectangle2D adjustedRect = getAdjustedRect(textPos);

			Vector startPoint = new Vector(textPos.getX(), textPos.getY(), 1f);
			Vector endPoint = new Vector(textPos.getX() + textPos.getWidth(), textPos.getY(), 1f);

			boolean firstRender = (0 == stripString.size());
			boolean hardReturn = false;

			int lastIndex = endPoints.size() - 1;

			if (!firstRender) {

				//Vector x1 = startPoints.get(lastIndex);
				Vector x2 = endPoints.get(lastIndex);

				float distance = calculateDistance(x2, startPoint);

				// we should probably base this on the current font metrics, but
				// 2.0 point seems to be sufficient for the time being
				if (distance > textPos.getWidth() * stripperParam.getSameLineThreshold())
					hardReturn = true;
			}

			if (hardReturn) {

				startPoints.add(startPoint);
				endPoints.add(endPoint);

				stripString.add(NEWLINE, startPoint, adjustedRect, textPos.getFont(), textPos.getFontSizeInPt());

			} else if (!firstRender) {
				// insert a space if the trailing character of the previous string
				// wasn't a space, and the leading character of the current string isn't a space
				String lastStripChar = stripString.substring(lastIndex, lastIndex + 1);

				if (lastStripChar.charAt(0) != ' ' && textPos.getUnicode().charAt(0) != ' ') {

					float spacing = endPoints.get(lastIndex).subtract(startPoint).length();
					float marginalWidth = (float) textPos.getWidth() / stripperParam.getSpaceMarginalScale();

					if (spacing > marginalWidth) {

						startPoints.add(startPoint);
						endPoints.add(endPoint);
						stripString.add(SPACE, startPoint, adjustedRect, textPos.getFont(), textPos.getFontSizeInPt());
					}
				}
			}

			startPoints.add(startPoint);
			endPoints.add(endPoint);
			stripString.add(textPos.getUnicode(), startPoint, adjustedRect, textPos.getFont(), textPos.getFontSizeInPt());
		}
	}
}
