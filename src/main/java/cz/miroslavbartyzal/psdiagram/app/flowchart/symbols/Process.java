/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.flowchart.symbols;

import java.awt.geom.Rectangle2D;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Tato třída představuje symbol zpracování.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlRootElement(name = "process")
@XmlAccessorType(XmlAccessType.NONE)
public final class Process extends AbstractSymbol
{

    private Rectangle2D.Double myShape = new Rectangle2D.Double();

    private Process()
    {
        this("");
    }

    Process(String value)
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
        myShape.setRect(x, y, width, height);
    }

}
