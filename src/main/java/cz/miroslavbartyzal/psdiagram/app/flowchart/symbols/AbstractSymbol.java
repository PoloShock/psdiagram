/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.flowchart.symbols;

import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import cz.miroslavbartyzal.psdiagram.app.global.xmlAdapters.LinkedHashMapAdapter;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Tato abstraktní třída zajišťuje základní implementaci rozhraní Symbol.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractSymbol implements Symbol
{

    @XmlElement(name = "borderColor")
    private Color borderColor = Color.BLACK;
    @XmlElement(name = "shapeUpColor")
    private Color shapeUpColor = Color.WHITE;
    //private Color shapeUpColor = null; // = transparentní pozadí
    @XmlElement(name = "shapeDownColor")
    private Color shapeDownColor = new Color(230, 230, 230);
    //private Color shapeDownColor = Color.WHITE;
    @XmlElement(name = "hasShadow")
    private boolean hasShadow = true;
    private final Color errorBorderColor = new Color(137, 20, 20);
    private final Color errorShapeUpColor = Color.WHITE;
    private final Color errorShapeDownColor = new Color(205, 143, 144);
    private double minWidth;
    private double minHeight;
    private Shape shape;
    private String value = null;
    @XmlElement(name = "customValue")
    private String customValue = null;
    @XmlElement(name = "defaultValue")
    private String defaultValue = null;
    private String[] defaultSegmentDescriptions = new String[0];
    private boolean hasElseSegment = false;
    private int innerOutsCount = 0;
    private boolean overHang = false; // znaci, zda symbol obsahuje vybocujici sipku (cyklus for), ktera se napojuje na nasledujici symbol z boku (TBLR layout)
    private boolean hasPairSymbol = false; // znaci, ze symbol ma dalsi parovy symbol, v poradi za timto symbolem (cyklus loop)
    private boolean padded = false; // znaci, ze symbol se musi vyskytovat "v prostrednim ze trech bodu, z nichz vsechny tri davaji dohromady usecku"
    private final int textPadding = 6;
    private final int textLeading = 4;
    private ArrayList<TextLayout> textLayoutLines = new ArrayList<>();
    private ArrayList<Point2D> textLayoutOrigins = new ArrayList<>();
    @XmlElement(name = "commands")
    @XmlJavaTypeAdapter(LinkedHashMapAdapter.class)
    private LinkedHashMap<String, String> commands = null;
    @XmlElement(name = "commandsValid", defaultValue = "true")
    private boolean commandsValid = true;

    AbstractSymbol()
    {
    }

    AbstractSymbol(double minWidth, double minHeight)
    {//, EnumSymbol enumSymbolForFunctionJPanel) {
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        //this.enumSymbolForFunctionJPanel = enumSymbolForFunctionJPanel;
    }

    /**
     * Metoda jen pro unmarshalling
     */
    private String getValueAndSize()
    {
        return value;
    }

    // TODO: tohle resit asi s dodanim primo objektu Font.. takze zrusit tu private pretizenost a tak..
    @Override
    public void resetFontStyle(int style)
    {
        if (textLayoutLines != null && textLayoutLines.size() > 0) {
            setValueAndSize(value, style);
        }
    }

    private void setValueAndSize(String value, int fontStyle)
    {
        textLayoutLines.clear();
        textLayoutOrigins.clear();
        if (value == null) {
            value = "";
        }
        this.value = value;

        if (value.equals("") || shape.getBounds2D().getWidth() < minWidth) {  // 2. cast podminky: symbol jeste nebyl inicializovan
            reposResizeSymbol(shape.getBounds2D().getCenterX() - minWidth / 2,
                    shape.getBounds2D().getCenterY() - minHeight / 2, minWidth, minHeight); // na minimalni hodnoty
            if (value.equals("")) {
                return;
            }
        }

        String[] lines = value.split("\\n");
        double textsHeigt = textPadding;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].equals("")) {
                lines[i] = " ";
            }
            TextLayout textLayout = new TextLayout(lines[i], SettingsHolder.CODEFONT.deriveFont(
                    fontStyle), SettingsHolder.FONTRENDERCONTEXT);
            textsHeigt += textLayout.getBounds().getHeight() + textLeading;
            textLayoutLines.add(textLayout);
        }
        textsHeigt += textPadding - textLeading; // naprava - posledni textlayout nemel obsahovat textLeading...

        if (textsHeigt != shape.getBounds2D().getHeight()) {
            if (textsHeigt > minHeight) {
                double width = getWidthByHeightRatio(textsHeigt);
                reposResizeSymbol(shape.getBounds2D().getX(), shape.getBounds2D().getY(), width,
                        textsHeigt); // upravim vysku aby odpovidala vysce textů - dulezite pro nasledujici vypocet zvetseni sirky
                textsHeigt = textPadding;
            } else {
                reposResizeSymbol(shape.getBounds2D().getX(), shape.getBounds2D().getY(), minWidth,
                        minHeight); // minimalni hodnoty
                textsHeigt = (shape.getBounds2D().getHeight() - textsHeigt + 2 * textPadding) / 2;
            }
        } else {
            textsHeigt = textPadding;
        }

        double maxWith = 0;
        for (int i = 0; i < lines.length; i++) { // nemuzu toto provest v prvnim cyklu, protoze je nejdriv treba upravit vysku symbolu, aby odpovidala
            TextLayout textLayout = textLayoutLines.get(i);
            double boundsWidth = textLayout.getBounds().getWidth() + 2 * textPadding;
            double boundsHeight = textLayout.getBounds().getHeight();
            double boundsX = getMyCenterX() - boundsWidth / 2;
            double boundsMaxX = getMyCenterX() + boundsWidth / 2;
            double boundsY = shape.getBounds2D().getY() + textsHeigt;
            double boundsMaxY = boundsY + boundsHeight;

            try {
                double possibleWidth = 0;
                double overLapRatio;
                Point2D p1 = getShapeIntersectionPoint(boundsX, boundsY, getMyCenterX(), boundsY);
                Point2D p2 = getShapeIntersectionPoint(boundsMaxX, boundsY, getMyCenterX(), boundsY);
                Point2D p3 = getShapeIntersectionPoint(boundsX, boundsMaxY, getMyCenterX(),
                        boundsMaxY);
                Point2D p4 = getShapeIntersectionPoint(boundsMaxX, boundsMaxY, getMyCenterX(),
                        boundsMaxY);

                double leftWidth1 = getMyCenterX() - p1.getX();
                double rightWidth1 = p2.getX() - getMyCenterX();
                double leftWidth2 = getMyCenterX() - p3.getX();
                double rightWidth2 = p4.getX() - getMyCenterX();

                if (leftWidth1 > rightWidth1 + 1 || leftWidth1 < rightWidth1 - 1 || leftWidth2 > rightWidth2 + 1 || leftWidth2 < rightWidth2 - 1) { // nesymetricky symbol - odlisny pristup
                    overLapRatio = (boundsWidth + p2.getX() - p4.getX()) / (p2.getX() - p1.getX());
                    double overLapRatio2 = (boundsWidth + p1.getX() - p3.getX()) / (p4.getX() - p3.getX());
                    if (overLapRatio2 > overLapRatio) {
                        overLapRatio = overLapRatio2;
                    }
                    if (overLapRatio > 1) {
                        possibleWidth = shape.getBounds2D().getWidth() * overLapRatio;
                    }

                    double additionalMove = p4.getX() - boundsMaxX;
                    additionalMove += p1.getX() - boundsX;
                    additionalMove /= 2;
                    textLayoutOrigins.add(new Point2D.Double(
                            boundsX + additionalMove + textPadding - textLayout.getBounds().getX(),
                            boundsY - textLayout.getBounds().getY()));
                } else {
                    textLayoutOrigins.add(new Point2D.Double(
                            boundsX + textPadding - textLayout.getBounds().getX(),
                            boundsY - textLayout.getBounds().getY()));
                    possibleWidth = shape.getBounds2D().getWidth() + boundsWidth - (p2.getX() - p1.getX());
                    double possibleWidth2 = shape.getBounds2D().getWidth() + boundsWidth - (p4.getX() - p3.getX());
                    if (possibleWidth2 > possibleWidth) {
                        possibleWidth = possibleWidth2;
                    }
                }

                if (possibleWidth > maxWith) {
                    maxWith = possibleWidth;
                }
            } catch (NullPointerException e) { // v pripade prazdneho stringu
                textLayoutOrigins.add(new Point2D.Double(getMyCenterX(),
                        shape.getBounds2D().getCenterY()));
            }
            textsHeigt += boundsHeight + textLeading;
        }
        maxWith = getSymbolSpecificWidthEdit(maxWith);
        if (maxWith > shape.getBounds2D().getWidth()) { // nyni uz jedine zvetsovani - zmensit jiz nemuzu, protoze height je nastavena optimalne..
            double height = getHeightByWidthRatio(maxWith);
            reposResizeSymbol(shape.getBounds2D().getX(), shape.getBounds2D().getY(), maxWith,
                    height);
        }
    }

    /**
     * Voláním této metody se provede nastavení textu uvnitř symbolu na
     * požadovanou hodnotu. Zároveň je přepočítána i nezbytná velikost symbolu a
     * to tak, že je zachován jeho poměr stran.<br />
     * Umístění textu symbolu je prováděno pro každý řádek zvlášť, je tak
     * docíleno co nejpřesnější velikosti symbolu na základě jeho vnitřního
     * textu.
     *
     * @param value požadovaný text uvnitř symbolu
     */
    @XmlElement(name = "value")
    @Override
    public void setValueAndSize(String value)
    {
        setValueAndSize(value, Font.PLAIN);
    }

    /**
     * Využívá symbol Subroutine.
     * Slouží k přepsání
     *
     * @param width zamýšlená šířka symbolu
     * @return nepovinně upravená šířka
     */
    double getSymbolSpecificWidthEdit(double width)
    {
        return width;
    }

    void reposResizeSymbol(double x, double y, double width, double height)
    {
        for (Point2D p : textLayoutOrigins) {
            p.setLocation(
                    p.getX() - shape.getBounds2D().getX() + x + (width - shape.getBounds2D().getWidth()) / 2,
                    p.getY() - shape.getBounds2D().getY() + y + (height - shape.getBounds2D().getHeight()) / 2);
        }
        setRect(x, y, width, height);
    }

    // musi se menit i poloha řádků textu
    abstract void setRect(double x, double y, double width, double height);

    double getWidthByHeightRatio(double height)
    {
        return height / minHeight * minWidth;
    }

    double getHeightByWidthRatio(double width)
    {
        return width / minWidth * minHeight;
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
        reposResizeSymbol(x - shape.getBounds2D().getWidth() / 2, shape.getBounds2D().getY(),
                shape.getBounds2D().getWidth(), shape.getBounds2D().getHeight());
    }

    /**
     * Metoda pro nastavení pozice symbolu na požadovanou Yovou souřadnici
     * středu symbolu.
     *
     * @param y požadovaná Yová souřadnice středu symbolu
     */
    @Override
    public void setCenterY(double y)
    {
        reposResizeSymbol(shape.getBounds2D().getX(), y - shape.getBounds2D().getHeight() / 2,
                shape.getBounds2D().getWidth(), shape.getBounds2D().getHeight());
    }

    /**
     * Metoda pro získání textu uvnitř symbolu po řádcích, v podobě instancí
     * TextLayout.
     *
     * @return text uvnitř symbolu po řádcích, v podobě instancí TextLayout
     */
    @Override
    public ArrayList<TextLayout> getTextLayoutLines()
    {
        return textLayoutLines;
    }

    /**
     * Metoda pro získání bodů určujících umístění řádků textu symbolu.
     *
     * @return kolekce bodů určujících umístění řádků textu
     */
    @Override
    public ArrayList<Point2D> getTextLayoutOrigins()
    {
        return textLayoutOrigins;
    }

    void setTextLayoutLines(ArrayList<TextLayout> textLayoutLines)
    {
        this.textLayoutLines = textLayoutLines;
    }

    void setTextLayoutOrigins(ArrayList<Point2D> textLayoutOrigins)
    {
        this.textLayoutOrigins = textLayoutOrigins;
    }

    /**
     * Metoda pro získání Xové souřadnice středu symbolu.
     *
     * @return Xová souřadnice středu symbolu
     */
    @Override
    public double getCenterX()
    {
        return shape.getBounds2D().getCenterX();
    }

    /**
     * Metoda pro získání Yové souřadnice středu symbolu.
     *
     * @return Yová souřadnice středu symbolu
     */
    @Override
    public double getCenterY()
    {
        return shape.getBounds2D().getCenterY();
    }

    /**
     * Metoda pro získání Xové souřadnice symbolu.
     *
     * @return Xová souřadnice symbolu
     */
    @Override
    public double getX()
    {
        return shape.getBounds2D().getX();
    }

    /**
     * Metoda pro získání Yové souřadnice symbolu.
     *
     * @return Yová souřadnice symbolu
     */
    @Override
    public double getY()
    {
        return shape.getBounds2D().getY();
    }

    /**
     * Metoda pro získání bodu střetu okraje symbolu s polopřímkou určenou
     * vstupními souřadnicemi a středem symbolu.
     *
     * @param sourcePointX Xová souřadnice vstupního bodu polopřímky
     * @param sourcePointY Yová souřadnice vstupního bodu polopřímky
     * @return bod střetu okraje symbolu s polopřímkou určenou vstupními
     * souřadnicemi a středem symbolu
     */
    @Override
    public Point2D getIntersectionPoint(double sourcePointX, double sourcePointY)
    {
        if (sourcePointX == shape.getBounds2D().getCenterX()) {
            if (sourcePointY > shape.getBounds2D().getCenterY()) {
                return new Point2D.Double(shape.getBounds2D().getCenterX(),
                        shape.getBounds2D().getMaxY());
            } else if (sourcePointY < shape.getBounds2D().getCenterY()) {
                return new Point2D.Double(shape.getBounds2D().getCenterX(),
                        shape.getBounds2D().getY());
            } else {
                return new Point2D.Double(shape.getBounds2D().getCenterX(),
                        shape.getBounds2D().getCenterY());
            }
        } else if (sourcePointY == shape.getBounds2D().getCenterY()) {
            if (sourcePointX > shape.getBounds2D().getCenterX()) {
                return new Point2D.Double(shape.getBounds2D().getMaxX(),
                        shape.getBounds2D().getCenterY());
            } else if (sourcePointX < shape.getBounds2D().getCenterX()) {
                return new Point2D.Double(shape.getBounds2D().getX(),
                        shape.getBounds2D().getCenterY());
            } else {
                return new Point2D.Double(shape.getBounds2D().getCenterX(),
                        shape.getBounds2D().getCenterY());
            }
        } else {
            return getShapeIntersectionPoint(sourcePointX, sourcePointY);
        }
    }

    Point2D getShapeIntersectionPoint(double sourcePointX, double sourcePointY)
    {
        return getShapeIntersectionPoint(sourcePointX, sourcePointY,
                shape.getBounds2D().getCenterX(), shape.getBounds2D().getCenterY());
    }

    Point2D getShapeIntersectionPoint(double sourcePointX, double sourcePointY, double symbolPointX,
            double symbolPointY)
    {
        PathIterator pathIterator = shape.getPathIterator(null, 1);
        double[] firstCoordinates = new double[2];
        if (!pathIterator.isDone()) {
            if (pathIterator.currentSegment(firstCoordinates) != PathIterator.SEG_MOVETO) {
                throw new Error("Unexpected currentSegment!");
            }
        }
        pathIterator.next();
        int quadrant = getPointQuadrant(sourcePointX, sourcePointY);
        while (!pathIterator.isDone()) {
            double[] secondCoordinates = new double[6];
            int type = pathIterator.currentSegment(secondCoordinates);
            if (type == PathIterator.SEG_MOVETO || type == PathIterator.SEG_LINETO) {
                if (type == PathIterator.SEG_LINETO) {
                    if (isInSameQuadrant(quadrant, firstCoordinates[0], firstCoordinates[1]) || isInSameQuadrant(
                            quadrant, secondCoordinates[0], secondCoordinates[1])) {
                        double[] vEdge = new double[]{secondCoordinates[0] - firstCoordinates[0],
                            secondCoordinates[1] - firstCoordinates[1]};
                        double[] nVSegment = new double[]{-(symbolPointY - sourcePointY),
                            symbolPointX - sourcePointX};
                        double h = ((sourcePointX - firstCoordinates[0]) * nVSegment[0] + (sourcePointY - firstCoordinates[1]) * nVSegment[1]) / (vEdge[0] * nVSegment[0] + vEdge[1] * nVSegment[1]);
                        if (h >= 0 && h <= 1) {
                            Point2D p = new Point2D.Double(firstCoordinates[0] + vEdge[0] * h,
                                    firstCoordinates[1] + vEdge[1] * h);
                            if (isInSameQuadrant(quadrant, p.getX(), p.getY())) {
                                return p;
                            }
                        }
                    }
                }
                firstCoordinates = secondCoordinates;
            } else if (type != PathIterator.SEG_CLOSE) {
                throw new Error("Unexpected currentSegment! (Curves are not supported)");
            }
            pathIterator.next();
        }
        return null;
    }

    private int getPointQuadrant(double x, double y)
    {
        // zde je double na obtiz (kvuli nepresnostem frakci), zaokrouhluji tedy na 4 des. mista
        double myCenterX = Math.rint(getMyCenterX() * 10000.0d) / 10000.0d;
        double myCenterY = Math.rint(shape.getBounds2D().getCenterY() * 10000.0d) / 10000.0d;
        x = Math.rint(x * 10000.0d) / 10000.0d;
        y = Math.rint(y * 10000.0d) / 10000.0d;

        if (x < myCenterX) {
            if (y < myCenterY) {
                return 2;
            } else if (y > myCenterY) {
                return 3;
            } else {
                return 23;
            }
        } else if (x > myCenterX) {
            if (y < myCenterY) {
                return 1;
            } else if (y > myCenterY) {
                return 4;
            } else {
                return 14;
            }
        } else {
            if (y < myCenterY) {
                return 12;
            } else if (y > myCenterY) {
                return 34;
            } else {
                return 1234;
            }
        }
    }

    private boolean isInSameQuadrant(int quadrant, double x, double y)
    {
        int result = getPointQuadrant(x, y);
        if (quadrant == result || result == 1234 || quadrant == result % 10 || quadrant % 10 == result % 10 || quadrant % 10 == result) {
            return true;
        } else if (result > 10) {
            while (result % 10 != 0) {
                result--;
            }
            while (quadrant > 10 && quadrant % 10 != 0) {
                quadrant--;
            }
            if (quadrant == result || quadrant == result / 10) {
                return true;
            }
        }
        return false;
    }
    // nektere symboly totiz mohou mit s contains specialni zachazeni -> slouzi k pripadnemu prepsani

    /**
     * Metoda vrací true, když bod na vstupu je obsažen uvnitř tvaru symbolu.
     *
     * @param p bod určený k estimaci
     * @return true, když vstupní bod je obsažen uvnitř tvaru symbolu
     */
    @Override
    public boolean contains(Point2D p)
    {
        return getShape().contains(p);
    }

    /**
     * Metoda pro nastavení barvy okraje symbolu.
     *
     * @param borderColor požadovaná barva okraje symbolu
     */
    @Override
    public void setBorderColor(Color borderColor)
    {
        this.borderColor = borderColor;
    }

    /**
     * Metoda pro nastavení horní barvy gradientu výplně symbolu.<br />
     * Je-li nastavena hodnota null, symbol nebude vyplněn žádnou barvou.<br />
     * Není-li nastavena hodnota dolní barvy výplně, symbol nebude vyplněn
     * gradientem.
     *
     * @param shapeUpColor požadovaná horní barva výplně symbolu
     */
    @Override
    public void setShapeUpColor(Color shapeUpColor)
    {
        this.shapeUpColor = shapeUpColor;
    }

    /**
     * Metoda pro nastavení dolní barvy gradientu výplně symbolu. Je-li zadána
     * hodnota null, symbol nebude vyplňen gradientem a bude použita barva z
     * horní barvy symbolu.
     *
     * @param shapeDownColor požadovaná dolní barva výplně symbolu
     */
    @Override
    public void setShapeDownColor(Color shapeDownColor)
    {
        this.shapeDownColor = shapeDownColor;
    }

    void setShape(Shape shape)
    {
        this.shape = shape;
    }

    /*
     * void setValue(String value) {
     * this.value = value;
     * }
     */
    void setInnerOutsCount(int innerOutsCount)
    {
        this.innerOutsCount = innerOutsCount;
    }

    void setHasElseSegment(boolean hasElseSegment)
    {
        if (overHang) {
            throw new Error("Unsoperted combination!");
        }
        this.hasElseSegment = hasElseSegment;
    }

    /**
     * Metoda pro nastavení informace, zda symbol má být vykreslen se svým
     * stínem.
     *
     * @param hasShadow přítomnost stínu symbolu
     */
    @Override
    public void setHasShadow(boolean hasShadow)
    {
        this.hasShadow = hasShadow;
    }

    /**
     * Metoda pro nastavení hodnoty párového symbolu pro tento symbol.
     *
     * @param hasPairSymbol přítomnost párového symbolu tohoto symbolu
     */
    @Override
    public void setHasPairSymbol(boolean hasPairSymbol)
    {
        this.hasPairSymbol = hasPairSymbol;
    }

    void setOverHang(boolean overHang)
    {
        if (hasElseSegment) {
            throw new Error("Unsoperted combination!");
        }
        this.overHang = overHang;
        setPadded(overHang); // overhang symbolu je treba nativne priradit i padded
    }

    void setPadded(boolean padded)
    {
        this.padded = padded;
    }

    /**
     * Metoda pro získání aktuální hodnoty výšky symbolu.
     *
     * @return aktuální hodnota výšky symbolu
     */
    @Override
    public double getHeight()
    {
        return shape.getBounds().getHeight();
    }

    /**
     * Metoda pro získání aktuální hodnoty šířky symbolu.
     *
     * @return aktuální hodnota šířky symbolu
     */
    @Override
    public double getWidth()
    {
        return shape.getBounds().getWidth();
    }

    /**
     * Některé symboly (GotoLabel) mají odlišné veřejné a interní rozměry.
     * Například pro výpočet umístění textu vně GotoLabel se musí dít za
     * interní hodnoty centerX, zatímco navenek se počítá s jinou hodnotou.
     *
     * Slouží k případnému přepsání...
     *
     * @return interní x-ová souřadnice středu symbolu
     */
    double getMyCenterX()
    {
        return getCenterX();
    }

    void setValue(String value)
    {
        this.value = value;
    }

    int getTextLeading()
    {
        return textLeading;
    }

    int getTextPadding()
    {
        return textPadding;
    }

    /**
     * Metoda pro získání uživatelské textové hodnoty uvnitř symbolu.
     *
     * @return uživatelská textová hodnota uvnitř symbolu
     */
    @Override
    public String getCustomValue()
    {
        return customValue;
    }

    /**
     * Metoda pro nastavení uživatelské textové hdnoty uvnitř symbolu. Tento
     * text je zpravidla vytvořen uživatelem, narozdíl od výchozího.
     *
     * @param customValue požadovaná uživatelská textová hodnota uvnitř symbolu
     */
    @Override
    public void setCustomValue(String customValue)
    {
        this.customValue = customValue;
    }

    /**
     * Metoda pro získání výchozí textové hodnoty uvnitř symbolu.
     *
     * @return výchozí textová hodnota uvnitř symbolu
     */
    @Override
    public String getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * Metoda pro nastavení výchozí textové hodnoty uvnitř symbolu. Tento text
     * je zpravidla generován automaticky, narozdíl od uživatelského.
     *
     * @param defaultValue požadovaná výchozí textová hodnota uvnitř symbolu
     */
    @Override
    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    /**
     * Metoda pro získání aktuální textové hodnoty uvnitř symbolu.
     *
     * @return aktuální textová hodnota uvnitř symbolu
     */
    @Override
    public String getValue()
    {
        return value;
    }

    /**
     * Metoda pro získání možného počtu vnitřních segmentů symbolu.<br />
     * 0 - symbol nemůže mít žádné vnitřní segmenty
     * -1 - symbol může mít libovolný počet vnitřních segmentů
     * x - symbol má právě x vnitřních segmentů
     *
     * @return počet možných vnitřních segmentů symbolu
     */
    @Override
    public int getInnerOutsCount()
    {
        return innerOutsCount;
    }

    /**
     * Metoda pro získání minimální výšky symbolu.
     *
     * @return minimální výška symbolu
     */
    @Override
    public double getMinHeight()
    {
        return minHeight;
    }

    /**
     * Metoda pro získání minimální šířky symbolu.
     *
     * @return minimální šířka symbolu
     */
    @Override
    public double getMinWidth()
    {
        return minWidth;
    }

    /**
     * Metoda pro získání definice tvaru symbolu pomocí rozhraní Shape.
     *
     * @return tvar symbolu
     */
    @Override
    public Shape getShape()
    {
        return shape;
    }

    /**
     * Metoda pro získání barvy okraje symbolu
     *
     * @return barva okraje symbolu
     */
    @Override
    public Color getBorderColor()
    {
        return borderColor;
    }

    /**
     * Metoda pro získání horní barvy gradientu výplně symbolu.
     *
     * @return horní barva výplně symbolu
     */
    @Override
    public Color getShapeUpColor()
    {
        return shapeUpColor;
    }

    /**
     * Metoda pro získání dolní barvy gradientu výplně symbolu.
     *
     * @return dolní barva výplně symbolu
     */
    @Override
    public Color getShapeDownColor()
    {
        return shapeDownColor;
    }

    /**
     * Metoda pro získání informace, zda symbol má být vykreslen se svým stínem.
     *
     * @return true když symbol má být vykreslen s vlastním stínem
     */
    @Override
    public boolean hasShadow()
    {
        return hasShadow;
    }

    /**
     * Metoda pro získání informace, zda smybol disponuje else větví.
     *
     * @return true když symbol disponuje else větví
     */
    @Override
    public boolean hasElseSegment()
    {
        return hasElseSegment;
    }

    /**
     * Metoda pro získání iformace, zda symbol má svůj párový symbol.
     *
     * @return true když symbol má svůj párový symbol
     */
    @Override
    public boolean hasPairSymbol()
    {
        return hasPairSymbol;
    }

    /**
     * Metoda pro získání informace, zda symbol má byt vykreslen postranní
     * šipkou.
     *
     * @return true když symbol má být vykreslen s postranní šipkou
     */
    @Override
    public boolean isOverHang()
    {
        return overHang;
    }

    /**
     * Metoda pro získání informace, zda má být symbol odsazen.
     *
     * @return true když má být symbol odsazen
     */
    @Override
    public boolean isPadded()
    {
        return padded;
    }

    /**
     * Metoda pro získání výchozích textů deskripce vnitřních segmentů
     * symbolu (například symbol podmínky se svými "Ano", "Ne").<br />
     * Tyto výchozí deskripce se týkají jen větví, kterých se netýká žádná
     * výchozí hodnota na základě funkce symbolu.
     *
     * @return výchozí texty deskripce vnitřních segmentů symbolu
     */
    @Override
    public String[] getDefaultSegmentDescriptions()
    {
        return defaultSegmentDescriptions;
    }

    void setDefaultSegmentDescriptions(String[] defaultSegmentDescriptions)
    {
        this.defaultSegmentDescriptions = defaultSegmentDescriptions;
    }

    /**
     * Metoda pro získání funkce symbolu. Funkce symbolu jsou vráceny v podobě
     * mapy, kde klíč náleží identifikaci příkazu a hodnota hodnotě příkazu.
     *
     * @return funkce symbolu
     */
    @Override
    public LinkedHashMap<String, String> getCommands()
    {
        return commands;
    }

    /**
     * Metoda pro nastavení funkce symbolu.
     *
     * @param commands mapa určující funkce symbolu
     */
    @Override
    public void setCommands(LinkedHashMap<String, String> commands)
    {
        this.commands = commands;
    }

    @Override
    public boolean areCommandsValid()
    {
        return commandsValid;
    }

    @Override
    public void setCommandsValid(boolean commandsValid)
    {
        this.commandsValid = commandsValid;
    }

    @Override
    public Color getErrorBorderColor()
    {
        return errorBorderColor;
    }

    @Override
    public Color getErrorShapeUpColor()
    {
        return errorShapeUpColor;
    }

    @Override
    public Color getErrorShapeDownColor()
    {
        return errorShapeDownColor;
    }

}
