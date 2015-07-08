/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.flowchart.symbols;

import java.awt.geom.Path2D;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Tato třída představuje symbol For(each) cyklu.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlRootElement(name = "for")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "for")
public final class For extends AbstractSymbol
{

    private final Path2D myShape = new Path2D.Double(Path2D.WIND_NON_ZERO, 6);

    private For()
    {
        this("");
    }

    For(String value)
    {
        // pomer sirky:vysky - 3:2
        //super(60,40);
        super(75, 50);
        super.setShape(myShape);
        setValueAndSize(value);
        super.setOverHang(true);
        super.setInnerOutsCount(1);
    }

    @Override
    void setRect(double x, double y, double width, double height)
    {
        myShape.reset();
        myShape.moveTo(x, y + height / 2);
        myShape.lineTo(x + width / 6, y);
        myShape.lineTo(x + width - width / 6, y);
        myShape.lineTo(x + width, y + height / 2);
        myShape.lineTo(x + width - width / 6, y + height);
        myShape.lineTo(x + width / 6, y + height);
        myShape.lineTo(x, y + height / 2);
        myShape.closePath();
    }

}
