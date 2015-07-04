/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.flowchart.symbols;

import java.awt.geom.Path2D;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Tato třída představuje symbol konce cyklu. Tento symbol je vždy spárován se
 * symbolem začátku cyklu (LoopStart).
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlRootElement(name = "loopEnd")
@XmlAccessorType(XmlAccessType.NONE)
public final class LoopEnd extends AbstractSymbol
{

    private Path2D myShape = new Path2D.Double(Path2D.WIND_NON_ZERO, 6);

    /**
     * Základní kontruktor této třídy. Vytvoří instanci konce cyklu s prázdným
     * vnitřním textem.
     */
    public LoopEnd()
    {
        this("");
    }

    /**
     * Konstruktor, určující vnitřní text symbolu.
     * <p/>
     * @param value vnitřní text symbolu
     */
    public LoopEnd(String value)
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
        myShape.lineTo(x + width, y);
        myShape.lineTo(x + width, y + height / 2);
        myShape.lineTo(x + width - width / 4, y + height);
        myShape.lineTo(x + width / 4, y + height);
        myShape.lineTo(x, y + height / 2);
        myShape.lineTo(x, y);
        myShape.closePath();
    }

}
