/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.flowchart.symbols;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Tato třída představuje symbol vstupu/výstupu.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlRootElement(name = "io")
@XmlAccessorType(XmlAccessType.NONE)
public final class IO extends AbstractSymbol
{

    private Path2D myShape = new Path2D.Double(Path2D.WIND_NON_ZERO, 4);

    private IO()
    {
        this("");
    }

    IO(String value)
    {
        // pomer sirky:vysky - 2:1
        //super(75,37.5);
        super(90, 45);
        super.setShape(myShape);
        setValueAndSize(value);
    }

    @Override
    void setRect(double x, double y, double width, double height)
    {
        myShape.reset();
        myShape.moveTo(x, y + height);
        myShape.lineTo(x + width / 5, y);
        myShape.lineTo(x + width, y);
        myShape.lineTo(x + width - width / 5, y + height);
        myShape.lineTo(x, y + height);
        myShape.closePath();
    }

    /**
     * Metoda pro získání bodu střetu okraje symbolu s polopřímkou určenou
     * vstupními souřadnicemi a středem symbolu.
     *
     * @param sourcePointX Xová souřadnice vstupního bodu polopřímky
     * @param sourcePointY Yová souřadnice vstupního bodu polopřímky
     * @return bod střetu okraje symbolu s polopřímkou určenou vstupními
     * souřadnicemi a středem symbolu
     */
    @Override
    public Point2D getIntersectionPoint(double sourcePointX, double sourcePointY)
    {
        if (sourcePointX == myShape.getBounds2D().getCenterX()) {
            if (sourcePointY > myShape.getBounds2D().getCenterY()) {
                return new Point2D.Double(myShape.getBounds2D().getCenterX(),
                        myShape.getBounds2D().getMaxY());
            } else if (sourcePointY < myShape.getBounds2D().getCenterY()) {
                return new Point2D.Double(myShape.getBounds2D().getCenterX(),
                        myShape.getBounds2D().getY());
            } else {
                return new Point2D.Double(myShape.getBounds2D().getCenterX(),
                        myShape.getBounds2D().getCenterY());
            }
        } else if (sourcePointY == myShape.getBounds2D().getCenterY()) {
            if (sourcePointX > myShape.getBounds2D().getCenterX()) {
                return new Point2D.Double(
                        myShape.getBounds2D().getMaxX() - myShape.getBounds2D().getWidth() / 10,
                        myShape.getBounds2D().getCenterY());
            } else if (sourcePointX < myShape.getBounds2D().getCenterX()) {
                return new Point2D.Double(
                        myShape.getBounds2D().getX() + myShape.getBounds2D().getWidth() / 10,
                        myShape.getBounds2D().getCenterY());
            } else {
                return new Point2D.Double(myShape.getBounds2D().getCenterX(),
                        myShape.getBounds2D().getCenterY());
            }
        } else {
            return super.getShapeIntersectionPoint(sourcePointX, sourcePointY);
        }
    }

}
