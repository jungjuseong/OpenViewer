package net.bookinaction.utils;

import java.awt.Color;
import java.awt.Paint;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

public class SimpleRenderer extends PDFRenderer {

	static ColorSpace ICC_COLORSPACE;

	public SimpleRenderer(PDDocument document) {
		super(document);

		try {
			InputStream adobeCCFile = getClass()
					.getResource("/net/bookinaction/utils/res/AdobeICCProfiles/CMYK/UncoatedFOGRA29.icc").openStream();
			ICC_Profile iccProfile = ICC_Profile.getInstance(adobeCCFile);

			ICC_COLORSPACE = new ICC_ColorSpace(iccProfile);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException {
		return new MyPageDrawer(parameters);
	}

	private float[] IntToFloat(int c, int m, int y, int k) {

		float[] cmyk = new float[4];

		cmyk[0] = (float) c / 255;
		cmyk[1] = (float) m / 255;
		cmyk[2] = (float) y / 255;
		cmyk[3] = (float) k / 255;

		return cmyk;
	}

	/**
	 * Example PageDrawer subclass with custom rendering.
	 */
	private class MyPageDrawer extends PageDrawer {
		MyPageDrawer(PageDrawerParameters parameters) throws IOException {
			super(parameters);
		}

		/**
		 * Color replacement.
		 */
		@Override
		protected Paint getPaint(PDColor color) throws IOException {

			if (color.toRGB() < 0x003f3f3f) { // black
				
				Integer pc[] = PantoneColor.get("440");
				
				return new Color(ICC_COLORSPACE, IntToFloat(pc[0], pc[1], pc[2], 0x0), 1f);			

			}
			
			return super.getPaint(color);
		}

	}
}