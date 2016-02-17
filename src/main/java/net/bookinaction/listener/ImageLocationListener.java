package net.bookinaction.listener;

import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.state.*;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;

/**
 * This is an example on how to get the x/y coordinates of image locations.
 * @author Ben Litchfield
 */
public class ImageLocationListener extends PDFStreamEngine
{
    /**
     * Default constructor.
     * @throws IOException If there is an error loading text stripper properties.
     */

    private List<Rectangle2D> imageRects;

    public ImageLocationListener() throws IOException
    {

        addOperator(new Concatenate());
        addOperator(new DrawObject());
        addOperator(new SetGraphicsStateParameters());
        addOperator(new Save());
        addOperator(new Restore());
        addOperator(new SetMatrix());
    }

    public void setImageRects(List<Rectangle2D> imageRects) {
        this.imageRects = imageRects;
    }

    /**
     * This is used to handle an operation.
     *
     * @param operator The operation to perform.
     * @param operands The list of arguments.
     *
     * @throws IOException If there is an error processing the operation.
     */
    @Override
    protected void processOperator( Operator operator, List<COSBase> operands) throws IOException
    {
        String operation = operator.getName();
        if( "Do".equals(operation) ) {
            COSName objectName = (COSName) operands.get(0);
            PDXObject xobject = getResources().getXObject( objectName );
            if( xobject instanceof PDImageXObject) {

                PDImageXObject image = (PDImageXObject) xobject;

                org.apache.pdfbox.util.Matrix ctmNew = getGraphicsState().getCurrentTransformationMatrix();
                float imageXScale = ctmNew.getScalingFactorX();
                float imageYScale = ctmNew.getScalingFactorY();

                //System.out.println(String.format("%s - [%.1f %.1f %.0f x %.0f]", objectName.getName(),
                //        ctmNew.getTranslateX(), ctmNew.getTranslateY(), imageXScale, imageYScale));

                imageRects.add(new Rectangle2D.Float(ctmNew.getTranslateX(), ctmNew.getTranslateY(), imageXScale, imageYScale));

            }
            else if(xobject instanceof PDFormXObject) {
                PDFormXObject form = (PDFormXObject)xobject;
                showForm(form);
            }
        }
        else {
            super.processOperator( operator, operands);
        }
    }

}