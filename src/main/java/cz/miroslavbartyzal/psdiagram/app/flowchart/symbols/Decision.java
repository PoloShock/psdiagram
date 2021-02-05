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
 * Tato třída představuje symbol rozhodování.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlRootElement(name = "decision")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "decision")
public class Decision extends AbstractSymbol
{

    private final Path2D myShape = new Path2D.Double(Path2D.WIND_NON_ZERO, 4);

    private Decision()
    {
        this("");
    }

    Decision(String value)
    {
        // pomer sirky:vysky - 3:2
        //super(60,40);
        super(75, 50);
        super.setShape(myShape);
        setValueAndSize(value);
        super.setInnerOutsCount(2);
        super.setHasElseSegment(true);
        super.setDefaultSegmentDescriptions(new String[]{"Ne", "Ano"});
    }

    @Override
    void setRect(double x, double y, double width, double height)
    {
        myShape.reset();
        myShape.moveTo(x, y + height / 2);
        myShape.lineTo(x + width / 2, y);
        myShape.lineTo(x + width, y + height / 2);
        myShape.lineTo(x + width / 2, y + height);
        myShape.lineTo(x, y + height / 2);
        myShape.closePath();
    }

}
