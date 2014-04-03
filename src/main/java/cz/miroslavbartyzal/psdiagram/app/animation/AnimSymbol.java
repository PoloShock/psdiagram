/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.animation;

import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * <p>Tato třída pouze zapouzdřuje informace potřebné k vykreslení symbolu,
 * který se nachází v animačním módu.</p>
 *
 * <p>Informace obsahují tyto položky:<br />
 * - symbol samotný<br />
 * - gradient jeho výplně, případně barvu výplně<br />
 * - gradient záře kuličky<br />
 * - relativní pozici případného stínu symbolu<br />
 * - barvu stínu symbolu<br />
 * - text s informací o zpracované funkci symbolu<br />
 * - umístění textu</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class AnimSymbol
{

    private Symbol symbol;
    private GradientPaint symbolGradient;
    private Color symbolColor;
    private RadialGradientPaint ballShineGradient;
    private double shadeTransX;
    private double shadeTransY;
    private Color shadeColor;
    private TextLayout progressDesc;
    private String progressString; // jen pro archivni ucel
    private Point2D progressDescPoint;

    /**
     * Základní konstruktor této třídy. Vytvoří instanci zapouzdřující pouze
     * symbol samotný.
     *
     * @param symbol symbol, který má tato třída reprezentovat
     */
    public AnimSymbol(Symbol symbol)
    {
        this(symbol, null, null, 0, 0, null);
    }

    /**
     * Vytvoří instanci obsahující informace o symbolu, jeho gradientu výplně a
     * gradientu záře kuličky.
     *
     * @param symbol symbol, který má tato třída reprezentovat
     * @param symbolGradient gradient výplně AnimSymbolu
     * @param ballShineGradient gradient záře kuličky
     */
    public AnimSymbol(Symbol symbol, GradientPaint symbolGradient, GradientPaint ballShineGradient)
    {
        this(symbol, symbolGradient, ballShineGradient, 0, 0, null);
    }

    /**
     * Kontruktor, inicializující všechny základní parametry této třídy.
     *
     * @param symbol symbol, který má tato třída reprezentovat
     * @param symbolGradient gradient výplně AnimSymbolu
     * @param ballShineGradient gradient záře kuličky
     * @param shadeTransX posun Xové souřadníce stínu vůči symbolu
     * @param shadeTransY posun Yové souřadníce stínu vůči symbolu
     * @param shadeColor barva stínu
     */
    public AnimSymbol(Symbol symbol, GradientPaint symbolGradient, GradientPaint ballShineGradient,
            double shadeTransX, double shadeTransY, Color shadeColor)
    {
        this.symbol = symbol;
        this.symbolGradient = symbolGradient;
        this.shadeTransX = shadeTransX;
        this.shadeTransY = shadeTransY;
        this.shadeColor = shadeColor;
    }

    /**
     * Vrací barvu stínu tohoto AnimSymbolu.
     *
     * @return barva stínu tohoto AnimSymbolu
     */
    public Color getShadeColor()
    {
        return shadeColor;
    }

    /**
     * Vrací gradient tohoto AnimSymbolu.
     *
     * @return gradient tohoto AnimSymbolu
     */
    public GradientPaint getSymbolGradient()
    {
        return symbolGradient;
    }

    /**
     * Vrací gradient záře kuličky, zasahující tento symbol.
     *
     * @return gradient záře kuličky
     */
    public RadialGradientPaint getBallShineGradient()
    {
        return ballShineGradient;
    }

    /**
     * Vrací údaj o posunutí Xové souřadníce stínu vůči symbolu
     *
     * @return posun Xové souřadníce stínu vůči symbolu
     */
    public double getShadeTransX()
    {
        return shadeTransX;
    }

    /**
     * Vrací údaj o posunutí Yové souřadníce stínu vůči symbolu
     *
     * @return posun Yové souřadníce stínu vůči symbolu
     */
    public double getShadeTransY()
    {
        return shadeTransY;
    }

    /**
     * Vrací instanci Symbol, reprezentující tento AnimSymbol
     *
     * @return instance Symbol, reprezentující tento AnimSymbol
     */
    public Symbol getSymbol()
    {
        return symbol;
    }

    /**
     * Vrací barvu výplně tohoto AnimSymbolu
     *
     * @return barva výplně tohoto AnimSymbolu
     */
    public Color getSymbolColor()
    {
        return symbolColor;
    }

    /**
     * Vrací instanci TextLayout, reprezentující text s informací o zpracované
     * funkci symbolu.
     *
     * @return instanci TextLayout, reprezentující text s informací o
     * zpracované funkci symbolu
     */
    public TextLayout getProgressDesc()
    {
        return progressDesc;
    }

    /**
     * Vrací umístění textu s informací o zpracované funkci symbolu.
     *
     * @return umístění textu s informací o zpracované funkci symbolu
     */
    public Point2D getProgressDescPoint()
    {
        return progressDescPoint;
    }

    /**
     * Vrací text s informací o zpracované funkci symbolu.
     *
     * @return text s informací o zpracované funkci symbolu
     */
    public String getProgressString()
    {
        return progressString;
    }

    /**
     * Nastaví text s informací o zpracované funkci symbolu. Tento text slouží
     * pro archivační účel - není kontrolováno, zda koresponduje s textem
     * instance TextLayout.
     *
     * @param progressString text s informací o zpracované funkci symbolu
     */
    public void setProgressString(String progressString)
    {
        this.progressString = progressString;
    }

    /**
     * Nastaví barvu stínu tohoto AnimSymbolu.
     *
     * @param shadeColor barva stínu tohoto AnimSymbolu
     */
    public void setShadeColor(Color shadeColor)
    {
        this.shadeColor = shadeColor;
    }

    /**
     * Nastaví gradient výplně tohoto AnimSymbolu.
     *
     * @param symbolGradient gradient výplně
     */
    public void setSymbolGradient(GradientPaint symbolGradient)
    {
        this.symbolGradient = symbolGradient;
    }

    /**
     * Nastaví gradient záře kuličky, ovlivňující výplň tohoto symbolu.
     *
     * @param ballShineGradient gradient záře kuličky
     */
    public void setBallShineGradient(RadialGradientPaint ballShineGradient)
    {
        this.ballShineGradient = ballShineGradient;
    }

    /**
     * Nastaví údaj o posunutí Xové souřadníce stínu vůči symbolu
     *
     * @param shadeTransX posunutí Xové souřadníce stínu vůči symbolu
     */
    public void setShadeTransX(double shadeTransX)
    {
        this.shadeTransX = shadeTransX;
    }

    /**
     * Nastaví údaj o posunutí Yové souřadníce stínu vůči symbolu
     *
     * @param shadeTransY posunutí Yové souřadníce stínu vůči symbolu
     */
    public void setShadeTransY(double shadeTransY)
    {
        this.shadeTransY = shadeTransY;
    }

    /**
     * Nastaví barvu výplně symbolu.
     *
     * @param symbolColor barva výplně symbolu
     */
    public void setSymbolColor(Color symbolColor)
    {
        this.symbolColor = symbolColor;
    }

    /**
     * Nastaví TextLayout s informací o zpracované funkci symbolu. Zároveň je
     * automaticky vygenerován údaj o jeho umístění.
     *
     * @param progressDesc TextLayout s informací o zpracované funkci symbolu
     */
    public void setProgressDesc(TextLayout progressDesc)
    {
        this.progressDesc = progressDesc;
        if (progressDesc != null) {
            Rectangle2D bounds = progressDesc.getBounds();
            progressDescPoint = new Point2D.Double(symbol.getCenterX() - bounds.getCenterX(),
                    symbol.getY() - bounds.getMaxY() - 1); // -1 pixely aby nesplyval text s okrajem symbolu
        } else {
            progressDescPoint = null;
        }
    }

}
