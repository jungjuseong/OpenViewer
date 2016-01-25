package net.bookinaction.utils;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;

import net.bookinaction.model.StripperParam;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.PrintWriter;

public class Stamper {

    public Rectangle2D.Float getAwtRect(PDRectangle pdRect, PDRectangle cropBox) {

        //need to reposition link rectangle to match text space
        //if( page.getRotation() == 0 )
        float x = pdRect.getLowerLeftX();
        float y = cropBox.getHeight() - pdRect.getUpperRightY() + pdRect.getLowerLeftY();

        return new Rectangle2D.Float(x, y, pdRect.getWidth(), pdRect.getHeight());
    }


    public void showText(PDPageContentStream canvas, String message, Rectangle2D rect, PDFont fontKor, PDFont fontEng, PDColor textColor) throws IOException {

        try {
            canvas.beginText();

            if (message.matches("[a-zA-Z0-9]+")) {
                canvas.setFont(fontEng, (float) rect.getHeight());
            }
            else if (message.matches("[\\p{IsHangul}a-zA-Z0-9\\s\\r]+")) {
                canvas.setFont(fontKor, (float) rect.getHeight());

            }
            canvas.setNonStrokingColor(textColor);
            canvas.newLineAtOffset((float) rect.getX(), (float) rect.getY());

            canvas.showText(message);
        }
        finally {
            canvas.endText();
        }

    }

    public PDRectangle adjustedRect(PDRectangle rect, float dx, float dy, float scalex, float scaley) {

        return new PDRectangle(rect.getLowerLeftX() + dx, rect.getLowerLeftY() + dy, rect.getWidth() * scalex, rect.getHeight() * scaley);
    }

    public void showBox(PDPageContentStream canvas, Rectangle2D rect, PDColor color) throws IOException {

        canvas.saveGraphicsState();

        canvas.setLineWidth(0.1f);
        canvas.setStrokingColor(color);

        canvas.addRect((float) rect.getX(), (float) rect.getY(), (float) rect.getWidth(), (float) rect.getHeight());
        canvas.stroke();

        canvas.saveGraphicsState();
    }


    public void recordTextMarkup(PrintWriter writer, int pageNum, String comment, String highlightText, Rectangle2D awtRect, String colorString) {

        writer.printf("%d,\"%s\"%s\t# [%.0f %.0f %.0f %.0f] - %s \n", pageNum, highlightText,
                (comment != null) ? "," + comment : "",
                awtRect.getX(), awtRect.getY(), awtRect.getWidth(), awtRect.getHeight(), colorString);
        writer.println();
    }

    public void recordTextMarkup(PrintWriter writer, int pageNum, String comment, String highlightText, PDRectangle pdRect, String colorString) {

        writer.printf("%d,\"%s\"%s\t# [%.0f %.0f %.0f %.0f] - %s \n", pageNum, highlightText,
                (comment != null) ? "," + comment : "",
                pdRect.getLowerLeftX(), pdRect.getLowerLeftY(), pdRect.getWidth(), pdRect.getHeight(), colorString);

        writer.println();
    }

    public void recordImage(PrintWriter writer, int pageNum, String comment, Rectangle2D awtRect) {

        writer.printf("%d,%s\t# ImageRect [%.0f %.0f %.0f %.0f]\n", pageNum, (comment != null) ? comment : "",
                awtRect.getX(), awtRect.getY(), awtRect.getWidth(), awtRect.getHeight());
        writer.println();
    }


    public void recordHeader(PrintWriter writer, String job, int pages, StripperParam sParam) {
        writer.println(String.format("# Document: %s", job));
        writer.println(String.format("# Page: %d", pages));


        writer.println(String.format("# SPACE_MARGINAL_SCALE: %.1f", sParam.getSpaceMarginalScale()));
        writer.println(String.format("# SAME_LINE_THRESHOLD: %.1f", sParam.getSameLineThreshold()));

    }

    public void recordPageSize(PrintWriter writer, int pageNum, PDRectangle cropBox) {
        float llx = cropBox.getLowerLeftX();
        float lly = cropBox.getLowerLeftY();

        float width = cropBox.getWidth();
        float height = cropBox.getHeight();

        writer.println(String.format("Page " + pageNum + ": [%.0f %.0f %.0f %.0f]\t# [%.1f %.1f %.1f %.1f] mm",
                llx, lly, llx + width, lly + height,
                25.4 * llx / 72f, 25.4 * lly / 72f, 25.4 * (llx + width) / 72f, 25.4 * (lly + height) / 72f));

    }

    public void recordTextPosition(PrintWriter writer, String token, int pageNum, Rectangle2D boundingRect, String attr) {

        float llx = (float) boundingRect.getX();
        float lly = (float) boundingRect.getY();
        float urx = llx + (float) boundingRect.getWidth();
        float ury = lly + (float) boundingRect.getHeight();

        writer.println(String.format("{\"%s\",\"%d\",\"%.0f %.0f %.0f %.0f\",\"%s\"}", token, pageNum, llx, lly, urx, ury, attr));
    }

}
