package net.bookinaction.utils;

import java.awt.Color;
import java.awt.Paint;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

public class MyPDFRenderer extends PDFRenderer {

	static ColorSpace ICC_COLORSPACE;

	public MyPDFRenderer(PDDocument document) {
		super(document);

		ICC_Profile iccProfile;
		try {
			iccProfile = ICC_Profile.getInstance("e:/pdf_jobs/Adobe ICC Profiles (end-user)/CMYK/UncoatedFOGRA29.icc");
			ICC_COLORSPACE = new ICC_ColorSpace(iccProfile);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException {
		return new MyPageDrawer(parameters);
	}

	private float[] toFloat(int c, int m, int y, int k) {
		
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
			
			String colorspace = color.getColorSpace().getName();
			PDGraphicsState gs = getGraphicsState();
			
			//if (gs.getNonStrokingColor() == color) {

				//System.out.println(String.format("NonStrokingColor: 0x%x", color.toRGB()));

				if (color.toRGB() < 0x003f3f3f) { // black
					float[] new_cmyk = toFloat(0x4d, 0x64, 0x07, 0x0); 
					return new Color(ICC_COLORSPACE, new_cmyk, 1f);
				}
			//}
			
			return super.getPaint(color);
		}

		protected Paint getPaint2(PDColor color) throws IOException {

			String colorspace = color.getColorSpace().getName();
			PDGraphicsState gs = getGraphicsState();
			// gs.setNonStrokingColor(color);

			if (gs.getNonStrokingColor() == color && "DeviceCMYK".equals(colorspace)) {
				float[] cmyk = color.getComponents();

				if (cmyk[3] > 0.5) {
					System.out.println(
							String.format("DeviceCMYK: [%.1f %.1f %.1f %.1f] !", cmyk[0], cmyk[1], cmyk[2], cmyk[3]));

					float[] new_cmyk = { 0.95f, 0f, .0f, 0f }; // replace
																// color
					Color color2 = new Color(ICC_COLORSPACE, new_cmyk, 1f);

					// if (color2 != null)
					// return color2;
				}
			}

			if ("DeviceRGB".equals(colorspace)) {
				float[] rgb = color.getComponents();

				if (rgb[0] == 0.0 && rgb[1] == 0.0 && rgb[2] == 0.0) {
					System.out.println(String.format("DeviceRGB: [%.1f %.1f %.1f] !", rgb[0], rgb[1], rgb[2]));

					float[] new_cmyk = { 0.0f, 0.9f, .0f, 0f }; // replace
																// color
					Color color2 = new Color(ICC_COLORSPACE, new_cmyk, 1f);

					// if (color2 != null)
					// return color2;
				}
			}

			if (gs.getNonStrokingColor() == color) {

				System.out.println(String.format("NonStrokingColor: 0x%x", color.toRGB()));

				if (color.toRGB() < 0x003f3f3f) { // black
					float[] new_cmyk = { 0.95f, 0.05f, .9f, 0f }; // replace
																	// color
					Color color2 = new Color(ICC_COLORSPACE, new_cmyk, 1f);

					return color2;
				}
			}

			// if ("DeviceGray".equals(colorspace) ||
			// "Separation".equals(colorspace))
			// return super.getPaint(color);

			return super.getPaint(color);
		}

		/**
		 * Glyph bounding boxes.
		 */
		@Override
		protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode, Vector displacement)
				throws IOException {

			// draw glyph
			super.showGlyph(textRenderingMatrix, font, code, unicode, displacement);

		}
	}
}