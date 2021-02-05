/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.flowchart.symbols;

import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import java.awt.Font;
import java.awt.font.TextLayout;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Tato třída představuje symbol komentáře.<br />
 * Komentář může být párový, čili vázaný na nějaký jiný nekomentářový symbol,
 * nebo nepárový, stojící sám na vlastním řádku.</p>
 *
 * <p>
 * Komentář dále obsahuje relativní souřadnice spojnice, která vede od
 * počátku, k tomuto symbolu. Tato spojnice může být lomená libovolným počtem
 * bodů.<br />
 * Podle posledního relativního bodu spojnice je komentář také automaticky
 * orientován doprava, či doleva tak, aby spojnice nezasahovala do prostoru
 * symbolu samotného.</p>
 *
 * <p>
 * Je-li komentář zapouzdřen jako Element, cesta k dalšímu elementu obsahuje
 * právě komentářovou spojnici k samotnému komentářovému symbolu. Komentářový
 * element tak stojí se svou spojnicí sám, a je na layoutu, aby spojnici vedoucí
 * přes tento komentář od předchozího nekomentářového elementu k
 * následujícímu nekomentářovému elementu vypočítal a uložil správně.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlRootElement(name = "comment")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "comment")
public final class Comment extends AbstractSymbol
{

    private final Path2D myShape = new Path2D.Double(Path2D.WIND_NON_ZERO, 3);
    @XmlElement(name = "relativePointToSymbol")
    private ArrayList<Point2D> lRelativeMiddlePointsToSymbol = new ArrayList<>();
    private boolean toRightSite = true;
    @XmlElement(name = "alwaysLeftAlign")
    private boolean alwaysLeftAlign = false;
    @XmlElement(name = "relativeX")
    private double relativeX = 0; // relativni pozice komentare vuci pocatku, navadi ke stredove X souradnici, ne ke skutecne X
    @XmlElement(name = "relativeY")
    private double relativeY = 0; // navadi ke stredove Y souradnici, ne ke skutecne
    private double maxWidthText = 0;

    private Comment()
    {
        this("", false);
    }

    private boolean getPaired()
    {
        return super.hasPairSymbol();
    }

    @XmlElement(name = "paired")
    private void setPaired(boolean paired)
    {
        super.setHasPairSymbol(paired);
    }

    Comment(boolean paired)
    {
        this(null, paired);
    }

    Comment(String value, boolean paired)
    {
        // pomer sirky:vysky - 1.5:10
        super(7.5, 50);
        super.setShape(myShape);
        setValueAndSize(value);
        super.setShapeUpColor(null);
        if (paired) {
            super.setHasPairSymbol(paired);
        }
        relativeX = 50;
        relativeY = -10;
    }

    @Override
    public void resetFontStyle(int style)
    {
        if (super.getValue() != null && !super.getValue().equals("")) {
            setValueAndSize(super.getValue(), style);
        }
    }

    private void setValueAndSize(String value, int fontStyle)
    {
        maxWidthText = 0;
        ArrayList<TextLayout> textLayoutLines = super.getTextLayoutLines();
        ArrayList<Point2D> textLayoutOrigins = super.getTextLayoutOrigins();
        textLayoutLines.clear();
        textLayoutOrigins.clear();
        if (value == null) {
            value = "";
        }
        super.setValue(value);

        if (value.equals("")) {
            reposResizeSymbol(getCenterX(), myShape.getBounds2D().getY(), super.getMinWidth(),
                    super.getMinHeight()); // na minimalni hodnoty
            return;
        }
        int textPadding = super.getTextPadding();
        int textLeading = super.getTextLeading();
        String[] lines = value.split("\\n");
        double textsHeigt = textPadding;
        for (int i = 0; i < lines.length; i++) { // mereni vysky textů a nejsirsiho textu
            if (lines[i].equals("")) {
                lines[i] = " ";
            }
            TextLayout textLayout = new TextLayout(lines[i], SettingsHolder.CODEFONT.deriveFont(
                    fontStyle), SettingsHolder.FONTRENDERCONTEXT);
            if (textLayout.getBounds().getWidth() > maxWidthText) {
                maxWidthText = textLayout.getBounds().getWidth();
            }
            textsHeigt += textLayout.getBounds().getHeight() + textLeading;
            textLayoutLines.add(textLayout);
        }
        textsHeigt += textPadding - textLeading; // naprava - posledni textlayout nemel obsahovat textLeading...

        if (textsHeigt != myShape.getBounds2D().getHeight()) {
            if (textsHeigt > super.getMinHeight()) {
                reposResizeSymbol(getCenterX(), myShape.getBounds2D().getY(), super.getMinWidth(),
                        textsHeigt); // upravim vysku aby odpovidala vysce textů - dulezite pro nasledujici vypocet rozmisteni textů
                textsHeigt = textPadding;
            } else {
                reposResizeSymbol(getCenterX(), myShape.getBounds2D().getY(), super.getMinWidth(),
                        super.getMinHeight()); // minimalni hodnoty
                textsHeigt = (myShape.getBounds2D().getHeight() - textsHeigt + 2 * textPadding) / 2;
            }
        } else {
            textsHeigt = textPadding;
        }

        for (int i = 0; i < lines.length; i++) { // nemuzu toto provest v prvnim cyklu, protoze je nejdriv treba upravit vysku symbolu, aby odpovidala
            TextLayout textLayout = textLayoutLines.get(i);
            double boundsX = getCenterX(); // usti sipky
            if (toRightSite) {
                boundsX += super.getMinWidth();
            } else if (!alwaysLeftAlign) {
                boundsX -= super.getMinWidth() + textLayout.getBounds().getWidth();
            } else {
                boundsX -= super.getMinWidth() + maxWidthText;
            }

            textLayoutOrigins.add(new Point2D.Double(boundsX - textLayout.getBounds().getX(),
                    myShape.getBounds2D().getY() + textsHeigt - textLayout.getBounds().getY()));
            textsHeigt += textLayout.getBounds().getHeight() + textLeading;
        }
    }

    /**
     * Metoda je přepsána z třídy AbstractSymbol za tím účelem, aby symbol byl
     * zvětšován/zmenšován jen při jeho Yové souřadnici - tedy jen jeho výška.
     *
     * @param value požadovaný text uvnitř symbolu
     */
    @Override
    public void setValueAndSize(String value)
    { // zajima me jen vyska, proto přepisuji z abstraktní třídy
        setValueAndSize(value, Font.PLAIN);
    }

    /**
     * Metoda pro nastavení pozice symbolu na požadovanou Xovou souřadnici
     * středu symbolu. Navíc jsou automaticky přepočítány relativní souřadnice
     * komentářové spojnice.
     *
     * @param x požadovaná Xová souřadnice středu symbolu
     */
    @Override
    public void setCenterX(double x)
    {
        // centrerX = X usti
        reposResizeSymbol(x, myShape.getBounds2D().getY(), super.getMinWidth(),
                myShape.getBounds2D().getHeight());
    }

    /**
     * Metoda pro nastavení pozice symbolu na požadovanou Yovou souřadnici
     * středu symbolu. Navíc jsou automaticky přepočítány relativní souřadnice
     * komentářové spojnice.
     *
     * @param y požadovaná Yová souřadnice středu symbolu
     */
    @Override
    public void setCenterY(double y)
    {
        // centrerY = Y usti
        reposResizeSymbol(getCenterX(), y - myShape.getBounds2D().getHeight() / 2,
                super.getMinWidth(), myShape.getBounds2D().getHeight());
    }

    @Override
    void setRect(double x, double y, double width, double height)
    {
        /*
         * myShape.reset();
         * if (toRightSite) {
         * myShape.moveTo(x + super.getMinWidth(), y);
         * myShape.lineTo(x, y);
         * myShape.lineTo(x, y + height);
         * myShape.lineTo(x + super.getMinWidth(), y + height);
         * } else {
         * myShape.moveTo(x, y);
         * myShape.lineTo(x + super.getMinWidth(), y);
         * myShape.lineTo(x + super.getMinWidth(), y + height);
         * myShape.lineTo(x, y + height);
         * }
         */

        myShape.reset();
        double myWidth;
        if (toRightSite) {
            myWidth = super.getMinWidth();
        } else {
            myWidth = -super.getMinWidth();
        }
        myShape.moveTo(x + myWidth, y);
        myShape.lineTo(x, y);
        myShape.lineTo(x, y + height);
        myShape.lineTo(x + myWidth, y + height);
    }

    /**
     * Metoda vrací bod ústí do komentáře.<br />
     * Je předpokládáno, že se na tuto skutečnost ptá layout, za účelem umístění
     * tohoto komentáře. Proto na základě výsledku je v případě potřeby komentář
     * přeorientován na levou/pravou stranu.
     *
     * @param sourcePointX Xová souřadnice vstupního bodu polopřímky
     * @param sourcePointY Yová souřadnice vstupního bodu polopřímky
     * @return bod ústí do komentáře
     */
    @Override
    public Point2D getIntersectionPoint(double sourcePointX, double sourcePointY)
    {
        if (!toRightSite && sourcePointX < getCenterX()) {
            toRightSite = true;
            //relativeX += super.getMinWidth();
            reposResizeSymbol(myShape.getBounds2D().getMaxX(), myShape.getBounds2D().getY(),
                    super.getMinWidth(), myShape.getBounds2D().getHeight());
            setValueAndSize(super.getValue());
        } else if (toRightSite && sourcePointX > getCenterX()) {
            toRightSite = false;
            //relativeX -= super.getMinWidth();
            reposResizeSymbol(myShape.getBounds2D().getX(), myShape.getBounds2D().getY(),
                    super.getMinWidth(), myShape.getBounds2D().getHeight());
            setValueAndSize(super.getValue());
        }
        return new Point2D.Double(getCenterX(), super.getCenterY());

        /*
         * double x = getX();
         * if (sourcePointX < x && !toRightSite) {
         * toRightSite = true;
         * relativeX -= getWidth();
         * reposResizeSymbol(x, myShape.getBounds2D().getY(),
         * super.getMinWidth(), myShape.getBounds2D().getHeight());
         * } else if (sourcePointX > x + getWidth() && toRightSite) {
         * toRightSite = false;
         * relativeX += getWidth();
         * reposResizeSymbol(x + getWidth(), myShape.getBounds2D().getY(),
         * super.getMinWidth(), myShape.getBounds2D().getHeight());
         * }
         * return new Point2D.Double(getCenterX(), super.getCenterY());
         */
    }

    @Override
    void reposResizeSymbol(double x, double y, double width, double height)
    {
        if (toRightSite) {
            for (Point2D p : super.getTextLayoutOrigins()) {
                p.setLocation(p.getX() - getCenterX() + x,
                        p.getY() - myShape.getBounds2D().getY() + y + (height - myShape.getBounds2D().getHeight()) / 2);
            }
        } else {
            for (Point2D p : super.getTextLayoutOrigins()) {
                p.setLocation(p.getX() - getCenterX() + x,
                        p.getY() - myShape.getBounds2D().getY() + y + (height - myShape.getBounds2D().getHeight()) / 2);
            }
        }
        setRect(x, y, width, height);
    }

    /**
     * Metoda pro získání Xové souřadnice vstupu do komentáře. Nikoliv jeho
     * středu!
     *
     * @return Xová souřadnice vstupu do komentáře
     */
    @Override
    public double getCenterX()
    {
        // centrerX = X usti
        if (toRightSite) {
            return myShape.getBounds2D().getX();
        }
        return myShape.getBounds2D().getMaxX();
    }

    /**
     * Metoda pro získání |Yové souřadnice vstupu do komentáře. Nikoliv jeho
     * středu!
     *
     * @return Xová souřadnice vstupu do komentáře
     */
    @Override
    public double getX()
    {
        if (toRightSite) {
            return myShape.getBounds2D().getX();
        } else {
            return myShape.getBounds2D().getX() - maxWidthText;
        }
    }

    /**
     * Metoda pro získání aktuální orientace komentáře.
     *
     * @return true, když je komentář orientován doparava
     */
    public boolean istoRightSite()
    {
        return toRightSite;
    }

    /**
     * Metoda pro získání aktuální hodnoty šířky komentáře, včetně jeho textu.
     *
     * @return aktuální hodnota šířky symbolu
     */
    @Override
    public double getWidth()
    {
        return super.getMinWidth() + maxWidthText;
    }

    /**
     * Metoda vrací true, když bod na vstupu je obsažen uvnitř symbolu
     * komentáře, včetně textu.
     *
     * @param p bod určený k estimaci
     * @return true, když vstupní bod obsažen uvnitř symbolu komentáře, včetně
     * textu
     */
    @Override
    public boolean contains(Point2D p)
    {
        Rectangle2D frame = new Rectangle2D.Double(getX(), myShape.getBounds2D().getY(), getWidth(),
                myShape.getBounds2D().getHeight());
        return frame.contains(p);
    }

    /**
     * Nastaví relativní Xovou souřadnici symbolu komentáře od jeho ústí.
     *
     * @param relativeX relativní Xová souřadnici symbolu komentáře od jeho ústí
     */
    public void setRelativeX(double relativeX)
    {
        this.relativeX = relativeX;
    }

    /**
     * Nastaví relativní Yovou souřadnici symbolu komentáře od jeho ústí.
     *
     * @param relativeY relativní Yová souřadnici symbolu komentáře od jeho ústí
     */
    public void setRelativeY(double relativeY)
    {
        this.relativeY = relativeY;
    }

    /**
     * Nastaví relativní souřadnice lomené čáry spojnice vedoucí k tomuto
     * komentáři.
     *
     * @param lRelativeMiddlePointsToSymbol relativní souřadnice lomené čáry
     * spojnice vedoucí k tomuto komentáři
     */
    public void setRelativeMiddlePointsToSymbol(ArrayList<Point2D> lRelativeMiddlePointsToSymbol)
    {
        this.lRelativeMiddlePointsToSymbol = lRelativeMiddlePointsToSymbol;
    }

    /**
     * Vrací relativní Xovou souřadnici symbolu komentáře vůči jeho ústí.
     * <p/>
     * @return relativní Xová souřadnice symbolu komentáře vůči jeho ústí
     */
    public double getRelativeX()
    {
        return relativeX;
    }

    /**
     * Vrací relativní Yovou souřadnici symbolu komentáře vůči jeho ústí.
     * <p/>
     * @return relativní Yová souřadnice symbolu komentáře vůči jeho ústí
     */
    public double getRelativeY()
    {
        return relativeY;
    }

    /**
     * Vrací relativní souřadnice lomené čáry spojnice vedoucí k tomuto
     * komentáři.
     * <p/>
     * @return relativní souřadnice lomené čáry spojnice vedoucí k tomuto
     * komentáři
     */
    public ArrayList<Point2D> getRelativeMiddlePointsToSymbol()
    {
        return lRelativeMiddlePointsToSymbol;
    }

}
