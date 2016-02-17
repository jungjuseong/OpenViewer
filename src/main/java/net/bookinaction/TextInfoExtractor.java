/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.bookinaction;

import net.bookinaction.listener.ImageLocationListener;
import net.bookinaction.listener.TextLocationListener;

import net.bookinaction.model.StripLine;
import net.bookinaction.model.StripString;
import net.bookinaction.model.StripperParam;
import net.bookinaction.model.Token;
import net.bookinaction.utils.*;
import net.bookinaction.analyzer.PatternAnalyzer;
import net.bookinaction.analyzer.SimpleTokenizer;

import javafx.scene.control.ProgressIndicator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This is an example on how to extract text from a specific area on the PDF document.
 */
public final class TextInfoExtractor {

	private String textCoordFile;
	//private static StripperParam  stripperParam;
	
    /**
     * This will print the documents text in a certain area.
     *
     * @param args The command line arguments.
     * @throws IOException If there is an error parsing the document.
     */
    
    static String[] circles_patterns = {
    		"([㉠㉡㉢㉣㉤]{1})",
    		"([①②③④⑤]{1})",
    		"([➊-➓]{1})", // "([⓫-⓴]{1})",
    		"([Ⓐ-Ⓩ]{1})",
    		"([ⓐ-ⓩ]{1})",
    		"([⑴-⒇]{1})",
    		"([⒈-⒛]{1})",
    		"([⒜-⒵]{1})",
    		"([0-9]+)"   		
    };
	
    public TextInfoExtractor() {
    }
    
    public TextInfoExtractor(String textCoordFile) {
    	this.textCoordFile = textCoordFile;
    	//this.stripperParam = stripperParam;
    }
 
    public void doTextPosition(String source, StripperParam sParam ) throws IOException {
    	
    	if (this.textCoordFile != null)
    		doTextPosition(source, this.textCoordFile, sParam);
    	
    }

    public static void getTextPositionFromPage(PDDocument document, StripperParam stripperParam, int pageNum, PrintWriter writer, boolean testMode) throws IOException {
        //System.out.println(String.format("getPage: %d", pageNum));
        
        PDPage page = document.getPage(pageNum-1); // pdfbox uses the 0-base index
    	PDRectangle cropBox = page.getCropBox();
        
        // extract image locations
        ImageLocationListener imageLocationsListener = new ImageLocationListener();

        List<Rectangle2D> imageRects = new ArrayList<Rectangle2D>();
        imageLocationsListener.setImageRects(imageRects);
        imageLocationsListener.processPage(page);

        // extract Text locations
        StripString stripString = new StripString();

        TextLocationListener stripper = new TextLocationListener(stripperParam, stripString);
        stripper.setSortByPosition(true);

        List<StripLine> stripLines = new ArrayList<StripLine>();

        stripper.setStartPage(pageNum);
        stripper.setEndPage(pageNum);

        try {
        	stripper.writeText(document, new OutputStreamWriter(new ByteArrayOutputStream()));
        }
        catch(IOException e) {
        	return;
        }   
        
        if (page.getContents() != null)
            stripper.processPage(page);

        // declare canvas and keep this position
        PDPageContentStream canvas = new PDPageContentStream(document, page, true, true, true);

        Stamper s = new Stamper(); // utility class

        if (testMode) {
	        // draw the bounding box of each character
	        for (int i = 0; i < stripString.size(); i++) {
	            // original Rectangle
	            s.showBox(canvas, stripString.boundingRect(i), cropBox, Color.GRAY80);
	        }
        }
        
        s.recordPageSize(writer, pageNum, cropBox);

        // splits into lines
        int lineNum = 1;
        int lineStart = 0, lineEnd = 0;
        String[] splits = stripString.toString().split("\r");

        SimpleTokenizer simpleTokenizer = new SimpleTokenizer();

        for (String lineText : splits) {

            if (lineText.length() < 1)
                continue;

            lineEnd = lineStart + lineText.length();

            Rectangle2D mergedRect = stripString.boundingRect(lineStart, lineEnd - 1);
            String sub = stripString.substring(lineStart, lineEnd);

            stripLines.add(new StripLine(pageNum, lineNum, lineStart, lineEnd, mergedRect));

            //System.out.println(String.format("%d-%d: %s - [%.0f %.0f %.0f %.0f]", pageNum, lineNum, sub,
            //        mergedRect.getX(), mergedRect.getY(), mergedRect.getWidth(), mergedRect.getHeight()));
            if (testMode) {
            	s.showBox(canvas, mergedRect, cropBox, Color.GREEN);
            }
            
            s.recordTextPosition(writer, sub, pageNum, mergedRect, "LINE");

            /******* get words in the line *********/
            List<Token> tokens = simpleTokenizer.getTokens(sub);

            for (String pattern : circles_patterns) {
            	List <Token> symbolTokens = PatternAnalyzer.getTokensByPattern(sub,pattern);
            	tokens.addAll(symbolTokens);
            }                
            
            for (Token t : tokens) {
                mergedRect = stripString.boundingRect(lineStart + t.getStart(), lineStart + t.getEnd() - 1);
                //System.out.println(String.format("%d-%d: %s - [%.0f %.0f %.0f %.0f]", pageNum, lineNum, t.getStem(), mergedRect.getX(), mergedRect.getY(), mergedRect.getWidth(), mergedRect.getHeight()));

                s.recordTextPosition(writer, t.getStem(), pageNum, mergedRect, "TEXT");
                
                if (testMode) {
                	s.showBox(canvas, mergedRect, cropBox, Color.RED);
                }

            }

            lineStart += lineText.length() + 1;
            lineNum++;
        }

        // -------------------

        // markup textMark annotation to the image
        int imageNum = 1;
        for (Rectangle2D imRect : imageRects) {
            //page.getAnnotations().add(annotationMaker.textMarkupAnnotation(Color.YELLOW, (Rectangle2D.Float) imRect, "image"+imageNum));

        	if (testMode) {
        		s.showBox(canvas, imRect, cropBox, Color.YELLOW);
        	}
            s.recordTextPosition(writer, "[image" + imageNum + "]", pageNum, imRect, "IMAGE");

            imageNum++;
        }

        canvas.close();
    }
    
    public void doTextPosition(String source, String coord_text, StripperParam stripperParam ) throws IOException {

        String source_pdf = source;         
        String new_file = source.split("\\.")[0] + "-new.pdf";

        PDDocument document = PDDocument.load(new File(source_pdf));

        PrintWriter writer = new PrintWriter(new File(coord_text));

        //s.recordHeader(writer, source_pdf, document.getNumberOfPages(), sParam);
        
        for (int i = 0; i < document.getNumberOfPages(); i++) {        	
        	getTextPositionFromPage(document,stripperParam, i+1, writer, true);            

        }

        if (document != null) {
            document.save(new_file);
            document.close();
        }

        if (writer != null)
            writer.close();

    }


}
