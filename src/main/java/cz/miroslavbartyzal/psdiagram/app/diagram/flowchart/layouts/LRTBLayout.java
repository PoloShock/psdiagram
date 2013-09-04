/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.layouts;

import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.symbols.Symbol;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import javax.swing.JComponent;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class LRTBLayout extends TBLRLayout
{

    /**
     * Základní konstruktor s parametrem, určující plátno, na které má být
     * vývojový diagram vykreslován.<br />
     * Vývojový diagram je vytvořen automaticky, obsahuje pouze počáteční a
     * koncový symbol.
     *
     * @param canvas plátno, na které má být vývojový diagram vykreslován
     */
    public LRTBLayout(JComponent canvas)
    {
        super(canvas);
    }

    /**
     * Konstruktor s parametry určující plátno, na které má být
     * vývojový diagram, určený druhým parametrem, vykreslován.
     *
     * @param canvas plátno, na které má být vývojový diagram vykreslován
     * @param flowchart vývojový diagram k vykreslení<br />
     * Je-li zadán null, vývojový diagram je vytvořen automaticky a obsahuje
     * pouze počáteční a koncový symbol.
     */
    public LRTBLayout(JComponent canvas, Flowchart<LayoutSegment, LayoutElement> flowchart)
    {
        super(canvas, flowchart);
    }

    /**
     * Metoda, která pomocí instance Graphics2D zajistí vykreslení vývojového
     * diagramu.
     *
     * @param g2d instance Graphics2D, pomocí které bude vývojový diagram
     * vykreslen
     * @param clipShadow určuje, jakým způsobem se mají vykreslit stíny
     * symbolů.<br />
     * True - stíny budou vykresleny obrysem symbolu - výpočetně náročnější,
     * používá se při exportu do PDF<br />
     * False - stíny budou vykresleny bez ořezu plnou velikostí symbolu. Symbol
     * bude vykreslen následně, čímž překryje požadovanou část stínu a vznikne
     * totožný efekt jako při metodě ořezávání.
     */
    @Override
    public void paintFlowchart(Graphics2D g2d, boolean clipShadow)
    {
        AffineTransform af = g2d.getTransform();
        g2d.rotate(Math.toRadians(90));
        // MRIZKA
        if (super.getEditMode()) {
            g2d.setColor(new Color(245, 245, 245));
            for (Line2D line : super.lGrid) {
                g2d.draw(line);
            }
        }

        super.paintFlowchart(g2d, clipShadow);
        g2d.setTransform(af);
    }

    @Override
    void drawSymbol(Graphics2D g2d, Symbol symbol, LayoutElement element, boolean clipShadow)
    {
        AffineTransform af = g2d.getTransform();
        g2d.rotate(Math.toRadians(-90));
        super.drawSymbol(g2d, symbol, element, clipShadow);
        g2d.setTransform(af);
    }

}
