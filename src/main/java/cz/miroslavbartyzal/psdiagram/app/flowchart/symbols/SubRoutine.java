/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.flowchart.symbols;

import java.awt.geom.Path2D;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Tato třída představuje symbol předdefinovaného zpracování.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlRootElement(name = "subRoutine")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "subRoutine")
public final class SubRoutine extends AbstractSymbol
{
    //TODO umoznit prime vlozeni javascriptu(nebo i jinych?) v metode s navratovym typem - vystup ulozit do promenne

    private final Path2D myShape = new Path2D.Double(Path2D.WIND_NON_ZERO, 11);

    private SubRoutine()
    {
        this("");
    }

    SubRoutine(String value)
    {
        // pomer sirky:vysky - 3:2
        //super(60,40);
        super(75, 50);
        super.setShape(myShape);
        setValueAndSize(value);
    }

    @Override
    void setRect(double x, double y, double width, double height)
    {
        myShape.reset();
        myShape.moveTo(x, y);
        myShape.lineTo(x, y + height);
        myShape.lineTo(x + width, y + height);
        myShape.lineTo(x + width, y);
        myShape.lineTo(x, y);
        myShape.moveTo(x + width / 12, y);
        myShape.lineTo(x + width / 12, y + height);
        myShape.moveTo(x + width - width / 12, y);
        myShape.lineTo(x + width - width / 12, y + height);

        /*
         * myShape.moveTo(x + width/12, y);
         * myShape.lineTo(x + width/12, y + height);
         * myShape.lineTo(x + width - width/12, y + height);
         * myShape.lineTo(x + width - width/12, y);
         * myShape.lineTo(x, y);
         * myShape.lineTo(x, y + height);
         * myShape.lineTo(x + width/12, y + height);
         * myShape.moveTo(x + width - width/12, y + height);
         * myShape.lineTo(x + width, y + height);
         * myShape.lineTo(x + width, y);
         * myShape.lineTo(x + width - width/12, y);
         */

        /*
         * myShape.moveTo(x + width/12, y);
         * myShape.lineTo(x, y);
         * myShape.lineTo(x, y + height);
         * myShape.lineTo(x + width/12, y + height);
         * myShape.lineTo(x + width/12, y);
         * myShape.lineTo(x + width, y);
         * myShape.lineTo(x + width, y + height);
         * myShape.lineTo(x + width/12, y + height);
         * myShape.moveTo(x + width - width/12, y);
         * myShape.lineTo(x + width - width/12, y + height);
         */
    }

    @Override
    double getSymbolSpecificWidthEdit(double width)
    {
        return width + width / 6;
    }

}
