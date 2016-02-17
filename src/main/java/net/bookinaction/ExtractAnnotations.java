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
import net.bookinaction.utils.AnnotationMaker;
import net.bookinaction.utils.Color;
import net.bookinaction.utils.Stamper;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.*;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * This is an example of how to access a URL in a PDF document.
 *
 * @author Ben Litchfield
 */
public final class ExtractAnnotations {
    /**
     * Constructor.
     */
    private ExtractAnnotations() {
        //utility class
    }

    /**
     * This will create a hello world PDF document.
     * <br />
     * see usage() for commandline
     *
     * @param args Command line arguments.
     * @throws IOException If there is an error extracting the URLs.
     */

    static final String[] jobs = {
            "e:/pdf_jobs/stn/텍스트인식 및 해설강의 설정_PEET 모의고사 샘플 - annotation",
            "e:/pdf_jobs/stn/2016_T.G.E.F로즈리파이널봉투모의고사_문제_1회내지_1쇄 - annotation",
            "e:/pdf_jobs/stn/영단기-토익 실전모의고사 1000제 Ver.2-ink",
            "e:/pdf_jobs/stn/공단기-공무원_필수암기노트_한국사 - ink",
            "e:/pdf_jobs/stn/공단기-봉투모의고사_1회-ink",
            "e:/pdf_jobs/changbee/contents-solution/컨텐츠솔루션 국3-1 샘플-본문 - ink"
    };

    final static Float[][] params = {
            {0f, 0f, 1f, 1f, 0f, 0f, 1f, 1f},
            {0f, 0f, 0.8f, 0.7f, 5f, 3f, 0.86f, 0.5f},
            {0f, 0f, 0.8f, 0.7f, 5f, 3f, 0.86f, 0.5f},
            {0f, 0f, 0.8f, 0.7f, 5f, 3f, 0.86f, 0.5f},
            {0f, 0f, 0.8f, 0.7f, 5f, 3f, 0.86f, 0.5f},
            {0f, 0f, 0.8f, 0.7f, 5f, 3f, 0.86f, 0.5f},
            {0f, 0f, 0.8f, 0.7f, 5f, 3f, 0.86f, 0.5f},
    };

    public static void main(String[] args) throws IOException {

        ExtractAnnotations ex = new ExtractAnnotations();

        int jobNum = 0;
        for (String job : jobs) {
            ex.doJob(job, params[jobNum++]);
            System.out.println(job + " Done!");
        }
    }

    public void doJob(String job, Float[] pA) throws IOException {

        PDDocument document = null;

        Stamper s = new Stamper(); // utility class

        final String job_file = job + ".pdf";
        final String dic_file = job + "-dict.txt";
        final String new_job = job + "-new.pdf";

        PrintWriter writer = new PrintWriter(dic_file);

        ImageLocationListener imageLocationsListener = new ImageLocationListener();
        AnnotationMaker annotMaker = new AnnotationMaker();

        try {
            document = PDDocument.load(new File(job_file));

            int pageNum = 0;
            for (PDPage page : document.getPages()) {
                pageNum++;

                PDRectangle cropBox = page.getCropBox();

                List<PDAnnotation> annotations = page.getAnnotations();

                // extract image locations
                List<Rectangle2D> imageRects = new ArrayList<Rectangle2D>();
                imageLocationsListener.setImageRects(imageRects);
                imageLocationsListener.processPage(page);

                int im = 0;
                for (Rectangle2D pdImageRect : imageRects) {
                    s.recordImage(writer, pageNum, "[im" + im + "]", (Rectangle2D.Float) pdImageRect);
                    annotations.add(annotMaker.squareAnnotation(Color.YELLOW, (Rectangle2D.Float) pdImageRect, "[im" + im + "]"));
                    im++;
                }

                PDFTextStripperByArea stripper = new PDFTextStripperByArea();

                int j = 0;
                List<PDAnnotation> viableAnnots = new ArrayList();

                for (PDAnnotation annot : annotations) {
                    if (annot instanceof PDAnnotationTextMarkup || annot instanceof PDAnnotationLink) {

                        stripper.addRegion(Integer.toString(j++), s.getAwtRect(s.adjustedRect(annot.getRectangle(), pA[0], pA[1], pA[2], pA[3]), cropBox));
                        viableAnnots.add(annot);

                    } else if (annot instanceof PDAnnotationPopup || annot instanceof PDAnnotationText) {
                        viableAnnots.add(annot);

                    }
                }

                stripper.extractRegions(page);

                List<PDRectangle> rects = new ArrayList<PDRectangle>();

                List<String> comments = new ArrayList<String>();
                List<String> highlightTexts = new ArrayList<String>();

                j = 0;
                for (PDAnnotation viableAnnot : viableAnnots) {

                    if (viableAnnot instanceof PDAnnotationTextMarkup) {
                        String highlightText = stripper.getTextForRegion(Integer.toString(j++));
                        String withoutCR = highlightText.replace((char) 0x0A, '^');

                        String comment = viableAnnot.getContents();

                        String colorString = String.format("%06x", viableAnnot.getColor().toRGB());

                        PDRectangle aRect = s.adjustedRect(viableAnnot.getRectangle(), pA[4], pA[5], pA[6], pA[7]);
                        rects.add(aRect);
                        comments.add(comment);
                        highlightTexts.add(highlightText);

                        s.recordTextMarkup(writer, pageNum, comment, withoutCR, aRect, colorString);

                    } else if (viableAnnot instanceof PDAnnotationText) {
                        String comment = viableAnnot.getContents();
                        String colorString = String.format("%06x", viableAnnot.getColor().toRGB());

                        for (Rectangle2D pdImageRect : imageRects) {
                            if(pdImageRect.contains(viableAnnot.getRectangle().getLowerLeftX(), viableAnnot.getRectangle().getLowerLeftY())) {
                                s.recordTextMarkup(writer, pageNum, comment, "", (Rectangle2D.Float) pdImageRect, colorString);
                                annotations.add(annotMaker.squareAnnotation(Color.GREEN, (Rectangle2D.Float) pdImageRect, comment));
                            };
                        }
                    }
                }
                PDPageContentStream canvas = new PDPageContentStream(document, page, true, true,true);


                int i = 0;
                for (PDRectangle pdRect : rects) {
                    String comment = comments.get(i);
                    String highlightText = highlightTexts.get(i);
                    //annotations.add(linkAnnotation(pdRect, comment, highlightText));
                    //annotations.add(annotationSquareCircle(pdRect, BLUE));
                    s.showBox(canvas,
                            new Rectangle2D.Float(pdRect.getLowerLeftX(), pdRect.getUpperRightY(),pdRect.getWidth(), pdRect.getHeight()), cropBox, Color.BLUE);

                    i++;
                }
                canvas.close();
            }
            writer.close();
            document.save(new_job);

        } finally {
            if (document != null) {
                document.close();
            }

        }


    }

}
