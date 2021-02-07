/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.flowchart.symbols;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Tato třída představuje symbol vícecestného rozhodování.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlRootElement(name = "switch")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "switch")
public final class Switch extends Decision
{
    //private Path2D myShape = new Path2D.Double(Path2D.WIND_NON_ZERO, 4);

    private Switch()
    {
        this("");
    }

    Switch(String value)
    {
        super(value);
        super.setInnerOutsCount(-1);
        super.setDefaultSegmentDescriptions(new String[]{"Jinak"});

        /*
         * // pomer sirky:vysky - 3:2
         * super(60,40);
         * setValueAndSize(value);
         * super.setShape(myShape);
         * super.setInnerOutsCount(-1);
         * super.setHasElseSegment(true);
         */
    }

    /*
     * @Override
     * void setRect(double x, double y, double width, double height) {
     * myShape.reset();
     * myShape.moveTo(x, y + height/2);
     * myShape.lineTo(x + width/2, y);
     * myShape.lineTo(x + width, y + height/2);
     * myShape.lineTo(x + width/2, y + height);
     * myShape.lineTo(x, y + height/2);
     * myShape.closePath();
     * }
     */
}
