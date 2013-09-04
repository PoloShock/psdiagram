/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.symbols;

import java.awt.Color;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * <p>
 * Toto rozhraní definuje symbol diagramu.<br />
 * Symbol je dále definován několika klíčovými atributy, jako je jeho tvar,
 * text, umístění textu, barva a několik logických parametrů důležitých pro jeho
 * správné vykreslení layoutem.
 * </p>
 * <p>
 * Výpis funkcí jednotlivých logických parametrů:<br />
 * - hasElseSegment: Určuje, zda symbol má disponovat else větví (jako např. if,
 * switch).<br />
 * - hasPairSymbol: Určuje, zda symbol vlastní párový element. Párovým elementem
 * se rozumí např. symbol konce cyklu. Atribut hasPairSymbol tedy definuje, že
 * layout po tomto symbolu očekává ještě jeho párovou část. Párová část již
 * tento atribut zapnutý nemá.<br />
 * - overHang: Určuje, že vykreslení cesty z tohoto symbolu k následujícímu má
 * být vedeno postranní špikou.<br />
 * - padded: Určuje, zda má být symbol odsazen. To znamená, že cesta (spojnice)
 * do/z symbolu může vstupovat/vystupovat jen shora/zdola.<br />
 * </p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public interface Symbol
{

    /**
     * Voláním této metody se nastaví text uvnitř symbolu na požadovanou
     * hodnotu. Zároveň je přepočítána i nezbytná velikost symbolu.
     *
     * @param value požadovaný text uvnitř symbolu
     */
    public void setValueAndSize(String value);

    /**
     * Metoda pro nastavení jiného stylu fontu.
     * Použij java.awt.Font.PLAIN, Font.BOLD...
     * <p/>
     * @param style požadovaný styl fontu
     */
    public void resetFontStyle(int style);

    /**
     * Metoda pro nastavení pozice symbolu na požadovanou Xovou souřadnici
     * středu symbolu.
     *
     * @param x požadovaná Xová souřadnice středu symbolu
     */
    public void setCenterX(double x);

    /**
     * Metoda pro nastavení pozice symbolu na požadovanou Yovou souřadnici
     * středu symbolu.
     *
     * @param y požadovaná Yová souřadnice středu symbolu
     */
    public void setCenterY(double y);

    /**
     * Metoda pro získání Xové souřadnice středu symbolu.
     *
     * @return Xová souřadnice středu symbolu
     */
    public double getCenterX();

    /**
     * Metoda pro získání Yové souřadnice středu symbolu.
     *
     * @return Yová souřadnice středu symbolu
     */
    public double getCenterY();

    /**
     * Metoda pro získání Xové souřadnice symbolu.
     *
     * @return Xová souřadnice symbolu
     */
    public double getX();

    /**
     * Metoda pro získání Yové souřadnice symbolu.
     *
     * @return Yová souřadnice symbolu
     */
    public double getY();

    //public void setShape(Shape shape);
    /**
     * Metoda pro získání aktuální hodnoty šířky symbolu.
     *
     * @return aktuální hodnota šířky symbolu
     */
    public double getWidth();

    /**
     * Metoda pro získání aktuální hodnoty výšky symbolu.
     *
     * @return aktuální hodnota výšky symbolu
     */
    public double getHeight();

    //public void setValue(String value);
    //public void setInnerOutsCount(int innerOutsCount);
    /**
     * Metoda pro nastavení barvy okraje symbolu.
     *
     * @param borderColor požadovaná barva okraje symbolu
     */
    public void setBorderColor(Color borderColor);

    /**
     * Metoda pro získání barvy okraje symbolu
     *
     * @return barva okraje symbolu
     */
    public Color getBorderColor();

    /**
     * Metoda pro nastavení horní barvy gradientu výplně symbolu.<br />
     * Je-li nastavena hodnota null, symbol nebude vyplněn žádnou barvou.<br />
     * Není-li nastavena hodnota dolní barvy výplně, symbol nebude vyplněn
     * gradientem.
     *
     * @param shapeUpColor požadovaná horní barva výplně symbolu
     */
    public void setShapeUpColor(Color shapeUpColor);

    /**
     * Metoda pro získání horní barvy gradientu výplně symbolu.
     *
     * @return horní barva výplně symbolu
     */
    public Color getShapeUpColor();

    /**
     * Metoda pro nastavení dolní barvy gradientu výplně symbolu. Je-li zadána
     * hodnota null, symbol nebude vyplňen gradientem a bude použita barva z
     * horní barvy symbolu.
     *
     * @param shapeDownColor požadovaná dolní barva výplně symbolu
     */
    public void setShapeDownColor(Color shapeDownColor);

    /**
     * Metoda pro získání dolní barvy gradientu výplně symbolu.
     *
     * @return dolní barva výplně symbolu
     */
    public Color getShapeDownColor();

    /**
     * Metoda pro získání aktuální textové hodnoty uvnitř symbolu.
     *
     * @return aktuální textová hodnota uvnitř symbolu
     */
    public String getValue();

    /**
     * Metoda pro získání uživatelské textové hodnoty uvnitř symbolu.
     *
     * @return uživatelská textová hodnota uvnitř symbolu
     */
    public String getCustomValue();

    /**
     * Metoda pro nastavení uživatelské textové hdnoty uvnitř symbolu. Tento
     * text je zpravidla vytvořen uživatelem, narozdíl od výchozího.
     *
     * @param customValue požadovaná uživatelská textová hodnota uvnitř symbolu
     */
    public void setCustomValue(String customValue);

    /**
     * Metoda pro získání výchozí textové hodnoty uvnitř symbolu.
     *
     * @return výchozí textová hodnota uvnitř symbolu
     */
    public String getDefaultValue();

    /**
     * Metoda pro nastavení výchozí textové hodnoty uvnitř symbolu. Tento text
     * je zpravidla generován automaticky, narozdíl od uživatelského.
     *
     * @param defaultValue požadovaná výchozí textová hodnota uvnitř symbolu
     */
    public void setDefaultValue(String defaultValue);

    /**
     * Metoda pro získání textu uvnitř symbolu po řádcích, v podobě instancí
     * TextLayout.
     *
     * @return text uvnitř symbolu po řádcích, v podobě instancí TextLayout
     */
    public ArrayList<TextLayout> getTextLayoutLines();

    /**
     * Metoda pro získání bodů určujících umístění řádků textu symbolu.
     *
     * @return kolekce bodů určujících umístění řádků textu
     */
    public ArrayList<Point2D> getTextLayoutOrigins();

    /**
     * Metoda pro získání možného počtu vnitřních segmentů symbolu.<br />
     * 0 - symbol nemůže mít žádné vnitřní segmenty
     * -1 - symbol může mít libovolný počet vnitřních segmentů
     * x - symbol má právě x vnitřních segmentů
     *
     * @return počet možných vnitřních segmentů symbolu
     */
    public int getInnerOutsCount();

    /**
     * Metoda pro získání minimální výšky symbolu.
     *
     * @return minimální výška symbolu
     */
    public double getMinHeight();

    /**
     * Metoda pro získání minimální šířky symbolu.
     *
     * @return minimální šířka symbolu
     */
    public double getMinWidth();

    /**
     * Metoda pro získání definice tvaru symbolu pomocí rozhraní Shape.
     *
     * @return tvar symbolu
     */
    public Shape getShape();

    //public void setHasElseSegment(boolean hasElseSegment);
    /**
     * Metoda pro získání informace, zda symbol má být vykreslen se svým stínem.
     *
     * @return true když symbol má být vykreslen s vlastním stínem
     */
    public boolean hasShadow();

    /**
     * Metoda pro nastavení informace, zda symbol má být vykreslen se svým
     * stínem.
     *
     * @param hasShadow přítomnost stínu symbolu
     */
    public void setHasShadow(boolean hasShadow);

    /**
     * Metoda pro získání informace, zda smybol disponuje else větví.
     *
     * @return true když symbol disponuje else větví
     */
    public boolean hasElseSegment();

    /**
     * Metoda pro nastavení hodnoty párového symbolu pro tento symbol.
     *
     * @param hasPairSymbol přítomnost párového symbolu tohoto symbolu
     */
    public void setHasPairSymbol(boolean hasPairSymbol);

    /**
     * Metoda pro získání iformace, zda symbol má svůj párový symbol.
     *
     * @return true když symbol má svůj párový symbol
     */
    public boolean hasPairSymbol();

    //public void setOverHang(boolean overHang);
    /**
     * Metoda pro získání informace, zda symbol má byt vykreslen postranní
     * šipkou.
     *
     * @return true když symbol má být vykreslen s postranní šipkou
     */
    public boolean isOverHang();

    //public void setPadded(boolean padded);
    /**
     * Metoda pro získání informace, zda má být symbol odsazen.
     *
     * @return true když má být symbol odsazen
     */
    public boolean isPadded();

    /**
     * Metoda pro získání bodu střetu okraje symbolu s polopřímkou určenou
     * vstupními souřadnicemi a středem symbolu.
     *
     * @param sourcePointX Xová souřadnice vstupního bodu polopřímky
     * @param sourcePointY Yová souřadnice vstupního bodu polopřímky
     * @return bod střetu okraje symbolu s polopřímkou určenou vstupními
     * souřadnicemi a středem symbolu
     */
    public Point2D getIntersectionPoint(double sourcePointX, double sourcePointY);

    /**
     * Metoda vrací true, když bod na vstupu je obsažen uvnitř tvaru symbolu.
     *
     * @param p bod určený k estimaci
     * @return true, když vstupní bod je obsažen uvnitř tvaru symbolu
     */
    public boolean contains(Point2D p);

    /**
     * Metoda pro získání výchozích textů deskripce vnitřních segmentů
     * symbolu (například symbol podmínky se svými "Ano", "Ne").<br />
     * Tyto výchozí deskripce se týkají jen větví, kterých se netýká žádná
     * výchozí hodnota na základě funkce symbolu.
     *
     * @return výchozí texty deskripce vnitřních segmentů symbolu
     */
    public String[] getDefaultSegmentDescriptions(); // tyto deskripce se tykaji jen vetvi, kterych se netyka zadna defaultni hodnota na zaklade funkce symbolu!

    /**
     * Metoda pro získání funkce symbolu. Funkce symbolu jsou vráceny v podobě
     * mapy, kde klíč náleží identifikaci příkazu a hodnota hodnotě příkazu.
     *
     * @return funkce symbolu
     */
    public LinkedHashMap<String, String> getCommands(); // LinkedHashMap je nutnost kvuli ukladani do xml - aby bylo vzdy stejne, musi byt prikazy fce vzdy stejne serazeny. Kdyby nebyli, nefungovalo by overeni totoznosti ulozeneho diagramu s aktualnim a nefungovalo by spravne undo/redo

    /**
     * Metoda pro nastavení funkce symbolu.
     *
     * @param commands mapa určující funkce symbolu
     */
    public void setCommands(LinkedHashMap<String, String> commands); // LinkedHashMap je nutnost kvuli ukladani do xml - aby bylo vzdy stejne, musi byt prikazy fce vzdy stejne serazeny. Kdyby nebyli, nefungovalo by overeni totoznosti ulozeneho diagramu s aktualnim a nefungovalo by spravne undo/redo

}
