/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.flowchart.symbols;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Tato třída představuje symbol vstupní Spojky (goto, break, continue).
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlRootElement(name = "goto")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "goto")
public final class Goto extends AbstractSymbol
{

    private final Ellipse2D.Double myShape = new Ellipse2D.Double();

    private Goto()
    {
        this("");
    }

    Goto(String value)
    {
        //super(20,20);
        super(25, 25);
        if (value == null || value.equals("")) {
            value = "__\nBR\n__";
            super.setDefaultValue(value);
            LinkedHashMap<String, String> commands = new LinkedHashMap<>();
            commands.put("mode", "break");
            super.setCommands(commands);
        }
        super.setShape(myShape);
        setValueAndSize(value);
        super.setPadded(true);
    }

    @Override
    void setRect(double x, double y, double width, double height)
    {
        myShape.setFrame(x, y, width, height);
    }

    /*
     * @Override
     * public Point2D getIntersectionPoint(double sourcePointX, double sourcePointY) {
     * if (sourcePointX == myShape.getCenterX()) {
     * if (sourcePointY > myShape.getCenterY()) {
     * return new Point2D.Double(myShape.getCenterX(), myShape.getMaxY());
     * } else if (sourcePointY < myShape.getCenterY()) {
     * return new Point2D.Double(myShape.getCenterX(), myShape.getY());
     * } else {
     * return null;
     * }
     * } else if (sourcePointY == myShape.getCenterY()) {
     * if (sourcePointX > myShape.getCenterX()) {
     * return new Point2D.Double(myShape.getMaxX(), myShape.getCenterY());
     * } else if (sourcePointX < myShape.getCenterX()) {
     * return new Point2D.Double(myShape.getX(), myShape.getCenterY());
     * } else {
     * return null;
     * }
     * } else {
     * return getShapeIntersectionPoint(sourcePointX, sourcePointY);
     * }
     * }
     */
    @Override
    Point2D getShapeIntersectionPoint(double sourcePointX, double sourcePointY)
    {
        double x = sourcePointX - myShape.getCenterX();
        double y = sourcePointY - myShape.getCenterY();
        double powerA = Math.pow(myShape.width / 2, 2);

        double h = powerA / Math.sqrt(powerA * Math.pow(y, 2) + powerA * Math.pow(x, 2));
        return new Point2D.Double(myShape.getCenterX() + h * x, myShape.getCenterY() + h * y);
    }

}
