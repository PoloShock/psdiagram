/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.symbols;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>Tato třída představuje symbol výstupní Spojky (návěští).</p>
 *
 * <p>Tento symbol je svým způsobem unikátní, obsahuje totiž svou vlastní
 * spojnici (proměnná myHair), která se napojuje na hlavní tok segmentu
 * layoutu. Tento symbol je tedy v segmentu umístěn vždy na jeho levé straně,
 * nikoliv ve svém středu.<br />
 * I proto je třeba s tímto symbolem nákládat obezřetně a brát tuto skutečnost v
 * potaz.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlRootElement(name = "gotoLabel")
@XmlAccessorType(XmlAccessType.NONE)
public final class GotoLabel extends AbstractSymbol
{

    private Path2D myShape = new Path2D.Double(Path2D.WIND_NON_ZERO);
    private Ellipse2D circle;
    private int myHair = 14;

    private GotoLabel()
    {
        this("");
    }

    GotoLabel(String value)
    {
        //super((20 + 14)*2,20);
        super((25 + 14) * 2, 25);
        this.myHair = 14;
        /*
         * if (value == null || value.equals("")) {
         * throw new Error("You have to define value in GotoLabel!");
         * }
         */
        super.setShape(myShape);
        setValueAndSize(value);
        super.setPadded(true);
    }

    /*
     * public void setMyHair(int myHair) {
     * this.myHair = myHair;
     * setRect(myShape.getBounds2D().getX(), myShape.getBounds2D().getY(),
     * myShape.getBounds2D().getHeight());
     * }
     */
    /**
     * Metoda pro získání bodu střetu okraje symbolu s polopřímkou určenou
     * vstupními souřadnicemi a středem symbolu.<br />
     * Je třeba brát zřetel na to, že bod se hledá výhradně v oválné části
     * symbolu, nikoliv již v jeho spojnici.
     *
     * @param sourcePointX Xová souřadnice vstupního bodu polopřímky
     * @param sourcePointY Yová souřadnice vstupního bodu polopřímky
     * @return bod střetu okraje symbolu s polopřímkou určenou vstupními
     * souřadnicemi a středem symbolu
     */
    @Override
    public Point2D getIntersectionPoint(double sourcePointX, double sourcePointY)
    {
        if (sourcePointX == circle.getBounds2D().getCenterX()) {
            if (sourcePointY > circle.getBounds2D().getCenterY()) {
                return new Point2D.Double(circle.getBounds2D().getCenterX(),
                        circle.getBounds2D().getMaxY());
            } else if (sourcePointY < circle.getBounds2D().getCenterY()) {
                return new Point2D.Double(circle.getBounds2D().getCenterX(),
                        circle.getBounds2D().getY());
            } else {
                return new Point2D.Double(circle.getBounds2D().getCenterX(),
                        circle.getBounds2D().getCenterY());
            }
        } else if (sourcePointY == circle.getBounds2D().getCenterY()) {
            if (sourcePointX > circle.getBounds2D().getCenterX()) {
                return new Point2D.Double(circle.getBounds2D().getMaxX(),
                        circle.getBounds2D().getCenterY());
            } else if (sourcePointX < circle.getBounds2D().getCenterX()) {
                return new Point2D.Double(circle.getBounds2D().getX(),
                        circle.getBounds2D().getCenterY());
            } else {
                return new Point2D.Double(circle.getBounds2D().getCenterX(),
                        circle.getBounds2D().getCenterY());
            }
        } else {
            return getShapeIntersectionPoint(sourcePointX, sourcePointY);
        }
    }

    @Override
    Point2D getShapeIntersectionPoint(double sourcePointX, double sourcePointY)
    {
        double x = sourcePointX - circle.getCenterX();
        double y = sourcePointY - circle.getCenterY();
        double powerA = Math.pow(circle.getWidth() / 2, 2);

        double h = powerA / Math.sqrt(powerA * Math.pow(y, 2) + powerA * Math.pow(x, 2));
        return new Point2D.Double(circle.getCenterX() + h * x, circle.getCenterY() + h * y); // pro komentarovou sipku
    }

    /**
     * Vrátí délku spojnice této spojky.
     * <p/>
     * @return délku spojnice této spojky
     */
    public int getMyHair()
    {
        return myHair;
    }

    /**
     * Vrátí oválnou část této spojky.
     * <p/>
     * @return oválnou část této spojky
     */
    public Ellipse2D getMyCircle()
    {
        return circle;
    }

    @Override
    void setRect(double x, double y, double width, double height)
    {
        myShape.reset();
        circle = new Ellipse2D.Double(x, y, height, height);
        PathIterator pathIterator = circle.getPathIterator(null);//, 0.5);
        while (!pathIterator.isDone()) {
            double[] coordinates = new double[6];
            int type = pathIterator.currentSegment(coordinates);
            switch (type) {
                case PathIterator.SEG_MOVETO: {
                    myShape.moveTo(coordinates[0], coordinates[1]);
                    break;
                }
                case PathIterator.SEG_LINETO: {
                    myShape.lineTo(coordinates[0], coordinates[1]);
                    break;
                }
                case PathIterator.SEG_CUBICTO: {
                    myShape.curveTo(coordinates[0], coordinates[1], coordinates[2], coordinates[3],
                            coordinates[4], coordinates[5]);
                    break;
                }
                case PathIterator.SEG_CLOSE: {
                    myShape.closePath();
                    break;
                }
                default: {
                    throw new Error("Unexpected currentSegment!");
                }
            }
            pathIterator.next();
        }
        myShape.moveTo(myShape.getBounds2D().getMaxX(), myShape.getBounds2D().getCenterY());
        myShape.lineTo(myShape.getBounds2D().getMaxX() + myHair, myShape.getBounds2D().getCenterY());
        myShape.moveTo(myShape.getBounds2D().getMaxX() + myShape.getBounds2D().getWidth(),
                myShape.getBounds2D().getCenterY());
    }

    /*
     * @Override
     * public double getCenterX() {
     *
     * }
     *
     * @Override
     * public double getCenterY() {
     *
     * }
     */
    @Override
    double getMyCenterX()
    {
        return circle.getCenterX();
    }

    @Override
    double getHeightByWidthRatio(double width)
    {
        return width + myShape.getBounds2D().getHeight() - myShape.getBounds2D().getWidth(); // numuz odecist vlas, je aktualne zmutovany!
    }

}
