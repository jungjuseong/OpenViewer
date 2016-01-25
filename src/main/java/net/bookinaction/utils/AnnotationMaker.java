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
package net.bookinaction.utils;


import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationSquareCircle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

import java.awt.geom.Rectangle2D;

/**
 * Add annotations to pages of a PDF document.
 */
public final class AnnotationMaker
{
    static final float INCH = 72;

    static PDBorderStyleDictionary borderThick = new PDBorderStyleDictionary();
    static PDBorderStyleDictionary borderThin = new PDBorderStyleDictionary();
    static PDBorderStyleDictionary borderULine = new PDBorderStyleDictionary();

    public AnnotationMaker()
    {
        borderThick.setWidth(INCH / 12);  // 12th inch
        borderThin.setWidth(INCH / 72); // 1 point
        borderULine.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
        borderULine.setWidth(INCH / 72); // 1 point
    }

    public PDAnnotationTextMarkup textMarkupAnnotation(PDColor color, Rectangle2D.Float position, String comment) {
        // Now add the markup annotation, a highlight to PDFBox text
        PDAnnotationTextMarkup txtMark = new PDAnnotationTextMarkup(PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT);
        txtMark.setColor(color);
        txtMark.setConstantOpacity((float)0.2); // 20% transparent

        // Set the rectangle containing the markup
        txtMark.setRectangle(new PDRectangle(position.x, position.y, position.width, position.height));
        // work out the points forming the four corners of the annotations
        // set out in anti clockwise form (Completely wraps the text)
        // OK, the below doesn't match that description.
        // It's what acrobat 7 does and displays properly!
        float[] quads = new float[8];
        quads[0] = position.x;  // x1
        quads[1] = position.y + position.height; // y1
        quads[2] = position.x + position.width; // x2
        quads[3] = quads[1]; // y2
        quads[4] = quads[0];  // x3
        quads[5] = position.y; // y3
        quads[6] = quads[2]; // x4
        quads[7] = quads[5]; // y5

        txtMark.setQuadPoints(quads);
        txtMark.setContents(comment);

        return txtMark;
    }
    public PDAnnotationLink linkAnnotation(PDColor color, Rectangle2D.Float position, String url) {
        // Now add the link annotation, so the clickme works
        PDAnnotationLink txtLink = new PDAnnotationLink();
        txtLink.setBorderStyle(borderULine);

        // Set the rectangle containing the link
        txtLink.setRectangle(new PDRectangle(position.x, position.y, position.width, position.height));

        // add an action
        PDActionURI action = new PDActionURI();
        action.setURI(url);
        txtLink.setAction(action);

        return txtLink;
    }

    public PDAnnotationSquareCircle squareAnnotation(PDColor color, Rectangle2D.Float position, String message) {

        PDAnnotationSquareCircle aSquare = new PDAnnotationSquareCircle(PDAnnotationSquareCircle.SUB_TYPE_SQUARE);
        aSquare.setContents(message);
        aSquare.setColor(color);
        aSquare.setBorderStyle(borderThin);

        aSquare.setRectangle(new PDRectangle(position.x, position.y, position.width, position.height));

        return aSquare;
    }

    public PDAnnotationSquareCircle circleAnnotation(PDColor fillColor, PDColor lineColor, Rectangle2D.Float position, String message) {
        // Now draw a few more annotations
        PDAnnotationSquareCircle aCircle = new PDAnnotationSquareCircle(PDAnnotationSquareCircle.SUB_TYPE_CIRCLE);
        aCircle.setContents(message);
        aCircle.setInteriorColor(fillColor);  // Fill in circle in red
        aCircle.setColor(lineColor); // The border itself will be blue
        aCircle.setBorderStyle(borderThin);

        aCircle.setRectangle(new PDRectangle(position.x, position.y, position.width, position.height));

        return aCircle;
    }


}
