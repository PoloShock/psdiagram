/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.global.xmlAdapters;

import java.awt.geom.Point2D;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Tato třída představuje adaptér JAXB pro třídu Point2D.Double.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class Point2DDoubleAdapter extends XmlAdapter<Point2DDoubleAdapter.Point2DDoubleType, Point2D>
{

    /**
     * Marshalovací metoda JAXB.
     *
     * @param point bod, která má být marshalován
     * @return XML reprezentace bodu na vstupu
     */
    @Override
    public Point2DDoubleType marshal(Point2D point)
    {
        if (point == null) {
            return null;
        }
        return new Point2DDoubleType(point);
    }

    /**
     * Unmarshalovací metoda JAXB.
     *
     * @param type instance třídy Point2DDoubleType, ze které se má objekt
     * unmarshalovat
     * @return instance třídy Point2D
     */
    @Override
    public Point2D unmarshal(Point2DDoubleType type) throws Exception
    {
        return new Point2D.Double(type.x, type.y);
    }

    /**
     * Třída, kterou je JAXB schopen zpracovat.
     */
    public static class Point2DDoubleType
    {

        @XmlAttribute(name = "x")
        private double x;
        @XmlAttribute(name = "y")
        private double y;

        private Point2DDoubleType()
        {
        }

        public Point2DDoubleType(Point2D point)
        {
            x = point.getX();
            y = point.getY();
        }

    }

}
