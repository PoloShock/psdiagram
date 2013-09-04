/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.symbols;

import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Tato třída představuje symbol mezní značky.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlRootElement(name = "startEnd")
@XmlAccessorType(XmlAccessType.NONE)
public final class StartEnd extends AbstractSymbol
{

    private Path2D myShape = new Path2D.Double(Path2D.WIND_NON_ZERO);

    private StartEnd()
    {
        this("");
    }

    StartEnd(String value)
    {
        // pomer sirky:vysky - 3:1
        //super(60,20);
        super(75, 25);
        super.setShape(myShape);
        setValueAndSize(value);
    }

    @Override
    void setRect(double x, double y, double width, double height)
    {
        myShape.reset();
        Arc2D arc1 = new Arc2D.Double(x, y, height, height, 90, 180, Arc2D.OPEN);
        Arc2D arc2 = new Arc2D.Double(x + width - height, y, height, height, -90, 180, Arc2D.OPEN);
        myShape.moveTo(x + width - height / 2, y);
        myShape.lineTo(x + height / 2, y);
        PathIterator pathIterator = arc1.getPathIterator(null);//, 0.5);
        while (!pathIterator.isDone()) {
            double[] coordinates = new double[6];
            int type = pathIterator.currentSegment(coordinates);
            switch (type) {
                case PathIterator.SEG_MOVETO: {
                    //myShape.moveTo(coordinates[0], coordinates[1]);
                    break;
                }
                case PathIterator.SEG_LINETO: {
                    myShape.lineTo(coordinates[0], coordinates[1]);
                    break;
                }
                case PathIterator.SEG_CUBICTO: {
                    myShape.curveTo(coordinates[0], coordinates[1], coordinates[2], coordinates[3],
                            coordinates[4], coordinates[5]);
                    break;
                }
                default: {
                    throw new Error("Unexpected currentSegment!");
                }
            }
            pathIterator.next();
            if (pathIterator.isDone() && arc1 != null) {
                arc1 = null;
                pathIterator = arc2.getPathIterator(null);//, 0.5);
                myShape.lineTo(x + width - height / 2, y + height);
            }
        }
        myShape.closePath();
    }

    //@Override
    //Point2D getShapeIntersectionPoint(double sourcePointX, double sourcePointY) {/
        /*
     * Pro elipsu:
     *
     * double x = sourcePointX - myShape.getCenterX();
     * double y = sourcePointY - myShape.getCenterY();
     * double a = myShape.width/2;
     * double b = myShape.height/2;
     *
     * double h = (a * b) / Math.sqrt(Math.pow(a,2) * Math.pow(y,2) + Math.pow(b,2) * Math.pow(x,2));
     * return new Point2D.Double(myShape.getCenterX() + h * x, myShape.getCenterY() + h * y);
     */
    /*
     * Pro kruznici:
     *
     * double x = sourcePointX - myShape.getCenterX();
     * double y = sourcePointY - myShape.getCenterY();
     * double powerA = Math.pow(myShape.width/2,2);
     *
     * double h = powerA / Math.sqrt(powerA * Math.pow(y,2) + powerA * Math.pow(x,2));
     * return new Point2D.Double(myShape.getCenterX() + h * x, myShape.getCenterY() + h * y);
     */
    //}
}
