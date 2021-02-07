/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.global.xmlAdapters;

import java.awt.Color;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Tato třída představuje adaptér JAXB pro třídu Color.
 * <p/>
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class ColorAdapter extends XmlAdapter<String, Color>
{

    /**
     * Marshalovací metoda JAXB.
     * <p/>
     * @param c barva, která má být marshalována
     * @return XML reprezentace barvy na vstupu
     */
    @Override
    public String marshal(Color c)
    {
        if (c == null) {
            return null;
        }
        return c.getRed() + "," + c.getGreen() + "," + c.getBlue() + "," + c.getAlpha();
    }

    /**
     * Unmarshalovací metoda JAXB.
     * <p/>
     * @param s XML reprezentace třídy Color
     * @return instance třídy Color na základě vstupní XML hodnoty
     */
    @Override
    public Color unmarshal(String s)
    {
        String[] rgba = s.split(",");
        return new Color(Integer.parseInt(rgba[0]), Integer.parseInt(rgba[1]), Integer.parseInt(
                rgba[2]), Integer.parseInt(rgba[3]));
    }

}
