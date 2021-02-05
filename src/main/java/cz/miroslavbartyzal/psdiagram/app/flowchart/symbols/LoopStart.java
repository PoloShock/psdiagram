/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.flowchart.symbols;

import java.awt.geom.Path2D;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Tato třída představuje symbol začátku cyklu. Tento symbol je vždy spárován se
 * symbolem konce cyklu (LoopEnd).
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlRootElement(name = "loopStart")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "loopStart")
public final class LoopStart extends AbstractSymbol
{

    private final Path2D myShape = new Path2D.Double(Path2D.WIND_NON_ZERO, 6);

    private LoopStart()
    {
        this("", false);
    }

    private boolean getOverHanged()
    {
        return super.isOverHang();
    }

    @XmlElement(name = "overHanged")
    private void setOverHanged(boolean overHanged)
    {
        super.setOverHang(overHanged);
    }

    LoopStart(boolean overHang)
    {
        this(null, overHang);
    }

    LoopStart(String value, boolean overHang)
    {
        // pomer sirky:vysky - 3:2
        //super(60,40);
        super(75, 50);
        super.setShape(myShape);
        setValueAndSize(value);
        super.setHasPairSymbol(true);
        if (overHang) {
            super.setOverHang(overHang);
        }
        super.setInnerOutsCount(1);
    }

    @Override
    void setRect(double x, double y, double width, double height)
    {
        myShape.reset();
        myShape.moveTo(x, y + height);
        myShape.lineTo(x, y + height / 2);
        myShape.lineTo(x + width / 4, y);
        myShape.lineTo(x + width - width / 4, y);
        myShape.lineTo(x + width, y + height / 2);
        myShape.lineTo(x + width, y + height);
        myShape.lineTo(x, y + height);
        myShape.closePath();
    }

}
