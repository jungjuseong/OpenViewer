package net.bookinaction;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import net.bookinaction.utils.DotPattern;
import net.bookinaction.utils.SimpleRenderer;

// PDF의 글자 색을 바꾸고 닷패턴 이미지로 만든다.
public class PageToDotPattern {

	public PageToDotPattern() throws IOException {
		super();
	}

	static Logger log = Logger.getLogger(PageToDotPattern.class.getName());

	static final float SCALE = 6f;
	
	public static void renderDocument(String job_file, String render_pdf) throws IOException {
		
		PDDocument document = null;

		try {
			document = PDDocument.load(new File(job_file));
			
			for (int page = 0; page < document.getNumberOfPages(); page++) {               
                
				log.info(String.format("%s - page %d", job_file, page + 1));					
				renderPage(document, page);				
			}
			document.save(render_pdf);

			log.info(String.format("%s Done", job_file));

		} 
		finally {
			document.close();
		}
	}
	
	public static void renderPage(PDDocument document, int page) throws IOException  {		
		
		PDPage pdPage = document.getPage(page);		
        PDRectangle cropBox = pdPage.getCropBox();
		PDFRenderer pdfRenderer = new SimpleRenderer(document);
			
		BufferedImage renderImage = null;
		try {
			renderImage = pdfRenderer.renderImageWithDPI(page, 72.0f * SCALE);
			Graphics2D g2d = renderImage.createGraphics();
			g2d.scale(SCALE, SCALE);
			g2d.dispose();
			
			PDImageXObject pdImage = LosslessFactory.createFromImage(document, renderImage); 
			PDPageContentStream contentStream = new PDPageContentStream(document, pdPage, true, true);
		
			contentStream.drawImage(pdImage, cropBox.getLowerLeftX(), cropBox.getLowerLeftY(), pdImage.getWidth()/SCALE, pdImage.getHeight()/SCALE);
			contentStream.close();
		}
		catch (IOException e) {
			//
		}
		
	}
		
	// use itext library
	public static void addPatternImage(String doc_file, String output_file, String paperSize) throws IOException { 		

		PdfStamper stamper = null;
		PdfReader reader = null;

		try {	
			reader = new PdfReader(doc_file);
			stamper = new PdfStamper(reader, new FileOutputStream(output_file));
	
			for (int page = 1; page <= reader.getNumberOfPages(); page++) {
				
				log.info(DotPattern.getPath(paperSize, page));
				Image coordImage = Image.getInstance(DotPattern.getPath(paperSize, page));

				PdfContentByte canvas = stamper.getOverContent(page);
				com.itextpdf.text.Rectangle cropRect = reader.getCropBox(page);
				if (cropRect == null) 
					cropRect = reader.getPageSize(page);

				addDotPatternToPage(cropRect, canvas, coordImage);
			}
			
			stamper.close();
			
			log.info(String.format("Done"));
		}
		catch (DocumentException e) {
			;
		}
		finally {
			reader.close();
		}	
	}
	
	final static float DOT_PATTERN_SCALE = 6f;
	
	private static void addDotPatternToPage(com.itextpdf.text.Rectangle cropRect, PdfContentByte canvas, Image coordImage) 
			throws IOException, DocumentException { 
								
		Rectangle cropBox = new Rectangle(cropRect);
		
		canvas.saveState();
		
		if (coordImage.isMaskCandidate())
			coordImage.makeMask();

		coordImage.scalePercent(DOT_PATTERN_SCALE, DOT_PATTERN_SCALE);

		float yOffset = coordImage.getScaledHeight() - (float) cropBox.getHeight();
		
		coordImage.setAbsolutePosition((float) cropBox.getX(), (float) (cropBox.getY() - yOffset));
		canvas.addImage(coordImage);
		
		canvas.restoreState();		
	}
}