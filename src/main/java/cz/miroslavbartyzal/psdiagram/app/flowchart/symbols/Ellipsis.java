/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.flowchart.symbols;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Tato třída představuje symbol výpustky.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlRootElement(name = "ellipsis")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "ellipsis")
public final class Ellipsis extends AbstractSymbol
{

    private Path2D myShape = new Path2D.Double(Path2D.WIND_NON_ZERO, 7);

    private Ellipsis()
    {
        this("");
    }

    Ellipsis(String value)
    {
        super(1, 19);
        super.setShape(myShape);
        setValueAndSize(value);
        super.setPadded(true);
        setRect(0, 0, 0, 0);
    }

    /**
     * Metoda s prázdným tělem - výpustka nemůže obsahovat text.
     */
    @Override
    public void setValueAndSize(String value)
    {
    }

    /**
     * Metoda s prázdným tělem - výpustka nemůže obsahovat text.
     * <p>
     * @param defaultValue
     */
    @Override
    public void setDefaultValue(String defaultValue)
    {
    }

    /**
     * Metoda s prázdným tělem - výpustka nemůže obsahovat text.
     * <p>
     * @param customValue
     */
    @Override
    public void setCustomValue(String customValue)
    {
    }

    /**
     * Metoda pro nastavení pozice symbolu na požadovanou Xovou souřadnici
     * středu symbolu.
     *
     * @param x požadovaná Xová souřadnice středu symbolu
     */
    @Override
    public void setCenterX(double x)
    {
        setRect(x, myShape.getBounds2D().getY(), 0, 0);
    }

    @Override
    void setRect(double x, double y, double width, double height)
    {
        double actY = y;
        myShape.reset();
        myShape.moveTo(x, y);
        myShape.lineTo(x, y + 1);
        actY += (super.getMinHeight() / 2) / 2;
        myShape.moveTo(x, actY);

        double line = ((super.getMinHeight() / 2) / 4) * 0.6;
        double gap = ((super.getMinHeight() / 2) / 2) - line * 1.5;

        actY += line;
        myShape.lineTo(x, actY);
        actY += gap;
        myShape.moveTo(x, actY);
        actY += line;
        myShape.lineTo(x, actY);
        actY += gap;
        myShape.moveTo(x, actY);
        actY += line;
        myShape.lineTo(x, actY);

        myShape.moveTo(x, y + super.getMinHeight() - 1);
        myShape.lineTo(x, y + super.getMinHeight());
    }

    /**
     * Metoda vrací na základě vstupní souřadnice bod ústí do výpustky shora,
     * nebo zdola.
     *
     * @param sourcePointX Xová souřadnice vstupního bodu polopřímky
     * @param sourcePointY Yová souřadnice vstupního bodu polopřímky
     * @return bod ústí do výpustky shora, nebo zdola
     */
    @Override
    public Point2D getIntersectionPoint(double sourcePointX, double sourcePointY)
    {
        if (sourcePointY <= myShape.getBounds2D().getY()) {
            return new Point2D.Double(myShape.getBounds2D().getX(), myShape.getBounds2D().getY());
        } else if (sourcePointY >= myShape.getBounds2D().getMaxY()) {
            return new Point2D.Double(myShape.getBounds2D().getX(), myShape.getBounds2D().getMaxY());
        } else {
            return null;
        }
    }

    /**
     * Metoda vrací true, když bod na vstupu je obsažen uvnitř tvaru symbolu.
     * Za vnitřek symbolu se u výpustky považuje i okolí středu Xové souřadníce
     * +- 5ti pixelů.
     *
     * @param p bod určený k estimaci
     * @return true, když vstupní bod je obsažen uvnitř tvaru symbolu
     */
    @Override
    public boolean contains(Point2D p)
    {
        Rectangle2D rec = myShape.getBounds2D();
        rec.setFrame(rec.getX() - 5, rec.getY(), rec.getWidth() + 10, rec.getHeight());
        return rec.contains(p);
    }

}
