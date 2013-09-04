/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.symbols;

import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.layouts.LayoutSegment;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * <p>Tato třída představuje Joint, který nelze uživatelsky vložit.<br />
 * Joint představuje a označuje takové místo ve vývojovém diagramu, kam uživatel
 * může vložit některý z ostatních symbolů, které má k dispozici.</p>
 *
 * <p>Joint musí vždy náležet rodičovskému elementu a rodičovskému segmentu. Tím
 * je Joint zároveň možné jedinečně rozpoznat.</p>
 * <p/>
 * <p>Barva Jointu je určena barvou svého rodičovského elementu.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class Joint extends AbstractSymbol
{

    private Ellipse2D.Double myShape;
    private LayoutElement parentElement;
    private LayoutSegment parentSegment; // pritomen pro rozliseni vetve např u podminky (parentelement bude pritomen dvakrat totozny) a take pro urychleni pripadneho vyhledavani elementu v diagramu

    /**
     * Knstruktor, zajišťující inicializaci Jointu.
     * <p/>
     * @param parentElement rodičovský element
     * @param parentSegment rodičovský segment
     */
    public Joint(LayoutElement parentElement, LayoutSegment parentSegment)
    {
        super(8, 8);
        this.parentElement = parentElement;
        this.parentSegment = parentSegment;
        myShape = new Ellipse2D.Double(0, 0, super.getMinWidth(), super.getMinHeight());
        super.setShape(myShape);
        // nastavim barvu jako ma rodicovsky element
        super.setBorderColor(parentElement.getSymbol().getBorderColor());
        if (parentElement.getSymbol().getShapeUpColor() != null) {
            super.setShapeUpColor(parentElement.getSymbol().getShapeUpColor());
            super.setShapeDownColor(parentElement.getSymbol().getShapeDownColor());
        } else {
            super.setShapeUpColor(Color.WHITE);
            super.setShapeDownColor(new Color(230, 230, 230));
        }
        super.setHasShadow(false);
    }

    /**
     * Vrací rodičovský element tohoto Jointu.
     * <p/>
     * @return rodičovský element tohoto Jointu
     */
    public LayoutElement getParentElement()
    {
        return parentElement;
    }

    /**
     * Vrací rodičovský segment tohoto Jointu.
     * <p/>
     * @return rodičovský segment tohoto Jointu
     */
    public LayoutSegment getParentSegment()
    {
        return parentSegment;
    }

    /**
     * Metoda s prázdným tělem - Joint nemůže obsahovat text.
     */
    @Override
    public void setValueAndSize(String value)
    {
    }

    /**
     * Metoda vrací vždy souřadnice středu symbolu.
     */
    @Override
    public Point2D getIntersectionPoint(double sourcePointX, double sourcePointY)
    {
        return new Point2D.Double(myShape.getCenterX(), myShape.getCenterY());
    }

    @Override
    void setRect(double x, double y, double width, double height)
    {
        myShape.setFrame(x, y, width, height);
    }

}
