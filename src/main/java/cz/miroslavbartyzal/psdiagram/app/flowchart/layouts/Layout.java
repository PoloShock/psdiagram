/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.flowchart.layouts;

import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Comment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Joint;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import javax.swing.JMenuItem;

/**
 * <p>
 * Toto rozhraní definuje layout.<br />
 * Layout se stará o veškeré grafické vyobrazení vývojového diagramu. Tedy
 * například o správné vykreslení pozice symbolů, jejich textů, o vykreslení
 * cest mezi symboly a v neposlední řadě o rozmístění popisků.
 * </p>
 * <p>
 * Layout dále zprostředkovává operace nad samotným vývojovým diagramem, jako je
 * přidání symbolu, přesunutí symbolu, smazání symbolu.
 * </p>
 * <p>
 * Obecně layout může pracovat ve dvou rozdílných režimech. Je to režim editační
 * a režim needitační-náhledový.<br />
 * Při editačním režimu je vývojový diagram zobrazen včetně tzv. "jointů" (viz.
 * níže) a vykresluje aktuálně označený pár symbol-joint.<br />
 * Needitační-náhledový režim vývojový diagram zobrazuje v základním tvaru, bez
 * jointů a bez označeného páru. V tomto případě se neočekávají žádné úpravy
 * diagramu.
 * </p>
 * <p>
 * Jointem se rozumí místo potencionálního nového symbolu. Vkládá-li uživatel
 * symbol, vloží se vždy na místo označeného jointu, nejčastěji za označený
 * symbol. Označena je vždy dvojice joint-symbol. Vždy musí existovat právě
 * jedna taková označená dvojice.
 * </p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public interface Layout
{

    public static final Color FOCUSED_UP_COLOR = new Color(184, 209, 255);
    public static final Color FOCUSED_DOWN_COLOR = FOCUSED_UP_COLOR.darker();

//     static final int defaultSymbolPadding = 14;
//     public static final int defaultFlowchartPadding = 15;
//     public static final int defaultArrowLength = 12;
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
    public void paintFlowchart(Graphics2D g2d, boolean clipShadow);

    /**
     * Metoda, která prověří, zda vstupní cesta má obsahovat šipku v ústí.<br />
     * Výpočet probíhá na základě orientace cesty. Je-li její konec vzhledem k
     * počátku vlevo nebo nahoře, cesta by měla podle české normy být opatřena
     * šipkou.<br />
     * Metoda vrací cestu původní(která je zkrácená o případnou délku
     * šipky) a cestu šipky samotné.
     *
     * @param path cesta, která má být prověřena
     * @return pole s dvěma prvky. První je původní cesta(zkrácená o případnou
     * délku šipky), druhý prvek je samotná šipka; null když šipka není potřeba.
     */
    public Path2D[] shouldBeArrow(Path2D path);

    /**
     * Metoda přidá daný symbol na místo označeného jointu.
     *
     * @param symbol symbol, který má být přidán
     * @return element, reprezentující přidaný symbol
     */
    public LayoutElement addNewSymbol(Symbol symbol);

    /**
     * Metoda přidá daný symbol na místo označeného jointu.
     *
     * @param symbol symbol, který má být přidán
     * @param innerOutCount požadovaný počet vnitřních segmentů přidávaného
     * symbolu
     * @return element, reprezentující přidaný symbol
     */
    public LayoutElement addNewSymbol(Symbol symbol, int innerOutCount);

    /**
     * Metoda pro přesunutí daného elementu v pořadí za daný element.
     *
     * @param elementToMove element, který má být přesunut
     * @param destinationBeforeElement element, za který má být přesouvaný
     * element umístěn
     */
    public void moveElement(LayoutElement elementToMove, LayoutElement destinationBeforeElement);

    /**
     * Metoda pro vymazání daného elementu. S elementem se smažou i případné
     * další elementy na něj závislé, jako například párové komentáře, párové
     * elementy.
     *
     * @param element element, který má být vymazán
     */
    public void removeElement(LayoutElement element);

    /**
     * Metoda pro vymazání daného elementu. S elementem se smažou i případné
     * další elementy na něj závislé, jako například párové komentáře, párové
     * elementy.
     * Označený joint bude ten, pro který by přidávaný symbol zastoupil
     * symbol právě smazaný.
     *
     * @param element element, který má být vymazán
     */
    public void removeElementFocusItsJoint(LayoutElement element);

    /**
     * Focus označení korespondujícího symbolu nebude vykresleno. Voláním této metody se ruší
     * noFocusPaint.
     * Tento stav je zrusen pridanim noveho symbolu do diagramu.
     */
    public void setFocusJointOnly();

    /**
     * Layout vizuálně přestane vykreslovat jakýkoliv focus.
     * Tento stav je zrusen pridanim noveho symbolu do diagramu.
     */
    public void setNoFocusPaint();

    public void setFocusPaintToDefault();

    /**
     * Metoda pro získání vývojového diagramu v podobě instance Flowchart.
     *
     * @return instance Flowchart, reprezentující vývojový diagram
     */
    public Flowchart<LayoutSegment, LayoutElement> getFlowchart();

    /**
     * Metoda pro nastavení vývojového diagramu pro vyobrazení.
     * <p/>
     * @param flowchart vývojového diagramu pro vyobrazení
     */
    public void setFlowchart(Flowchart<LayoutSegment, LayoutElement> flowchart);

    /**
     * Vrací stav editačního módu.
     *
     * @return stav editačního módu
     */
    public boolean getEditMode();

    /**
     * Metoda pro nastavení editačního módu.
     *
     * @param editMode požadovaný stav editačního módu
     */
    public void setEditMode(boolean editMode);

    /**
     * Metoda pro získání nastavené délky šipky cesty.
     *
     * @return nastavená délka šipky cesty
     */
    public int getArrowLength();

    /**
     * Metoda pro nastavení délky šipky.
     *
     * @param arrowLength požadovaná délka šipky
     */
    public void setArrowLength(int arrowLength);

    /**
     * Metoda pro získání nastavené hodnoty odsazení diagramu.<br />
     * Odsazení diagramu je odsazení o nastavený počet pixelů na každém kraji
     * diagramu.
     *
     * @return nastavená velikost odsazení diagramu
     */
    public int getFlowchartPadding();

    /**
     * Metoda pro nastavení hodnoty odsazení diagramu.<br />
     * Odsazení diagramu je odsazení o nastavený počet pixelů na každém kraji
     * diagramu.
     *
     * @param flowchartPadding požadovaná hodnota odsazení diagramu
     */
    public void setFlowchartPadding(int flowchartPadding);

    /**
     * Metoda vrací nastavenou hodnotu "mezi-symbolového" odsazení.<br />
     * Mezi-symbolové odsazení reprezentuje hodnotu minimálního odsazení mezi
     * symboly.
     *
     * @return nastavená hodnota minimálního mezi-symbolového odsazení
     */
    public int getSymbolPadding();

    /**
     * Metoda pro nastavení hodnoty "mezi-symbolového" odsazení..<br />
     * Mezi-symbolové odsazení reprezentuje hodnotu minimálního odsazení mezi
     * symboly.
     *
     * @param symbolPadding požadovaná hodnota minimálního mezi-symbolového
     * odsazení
     */
    public void setSymbolPadding(int symbolPadding);

    /**
     * Metoda nastaví vstupní element jako označený.<br />
     * Zároveň nastaví jako označený i korespondující joint.
     *
     * @param focusedElement element, který má být nastaven jako označený
     */
    public void setFocusedElement(LayoutElement focusedElement);

    /**
     * Metoda pro získání aktuálně označeného symbolu.<br />
     * Je-li označen joint, vrací null. Pro získání označeného elementu který
     * nemá focus, použijte metodu getFocusedJoint.
     *
     * @return aktuálně označený symbol
     */
    public LayoutElement getFocusedElement();

    /**
     * Metoda nastaví vstupní joint jako označený.<br />
     * Zároveň nastaví jako označený i korespondující element.
     *
     * @param focusedJoint joint, který má být nastaven jako označený
     */
    public void setFocusedJoint(Joint focusedJoint);

    /**
     * Metoda pro získání kolekce všech jointů diagramu.
     *
     * @return kolekce všech jointů diagramu
     */
    public ArrayList<Joint> getlJoints();

    /**
     * Metoda, která vytvoří a vrátí cestu komentářového symbolu.<br />
     * (cesta komentářového symbolu je určena relativními souřadnicemi vzhledem
     * k jejímu počátku)
     *
     * @param commentSymbol kometářový symbol, jehož cesta má být vrácena
     * @param fromSymbol je-li komentář párový, symbol, k němuž se komentář
     * vztahuje; je-li komentář nepárový, uvádíme hodnotu null
     * @return cesta komentářového symbolu
     */
    public Path2D getCommentPathFromRelative(Comment commentSymbol, Symbol fromSymbol);

    /**
     * Metoda pro vyhledání druhého z párových elementů.
     *
     * @param element první z párového elementu
     * @return druhý z párového elementu
     */
    public LayoutElement findMyPairedElement(LayoutElement element);

    /**
     * Metoda pro vyhledání druhého z párových symbolů.
     *
     * @param symbol první z párového symbolu
     * @return druhý z párového symbolu
     */
    public Symbol findMyPairedSymbol(Symbol symbol);

    /**
     * Metoda pro vyhledání korespondujícího elementu, který reprezentuje daný
     * symbol.
     *
     * @param symbol symbol, jehož element má být nalezen
     * @return element reprezentující daný symbol
     */
    public LayoutElement findMyElement(Symbol symbol);

    /**
     * Metoda pro získání všech komentářových symbolů diagramu.
     *
     * @return kolekce všech komentářových symbolů diagramu
     */
    public ArrayList<Comment> getlCommentSymbols();

    /**
     * Stěžejní metoda layoutu. Voláním této metody layout přepočítá veškeré
     * rozmístění symbolů, propojovacích cest a popisků.
     */
    public void prepareFlowchart();

    /**
     * Metoda pro nastavení komentářového symbolu, jehož cesta má být
     * zvýrazněna.
     *
     * @param boldPathComment komentářový symbol, jehož cesta má být zvýrazněna
     */
    public void setBoldPathComment(Comment boldPathComment);

    /**
     * Metoda pro získání komentářového symbolu, který má aktuálně zvýrazněnou
     * cestu pro její modelování.
     *
     * @return komentářový symbol, který má aktuálně zvýrazněnou cestu pro
     * modelování
     */
    public Comment getBoldPathComment();

    /**
     * Vrací aktuální výšku diagramu.
     *
     * @return aktuální výška diagramu
     */
    public double getHeight();

    /**
     * Vrací aktuální šířku diagramu.
     *
     * @return aktuální šířka diagramu
     */
    public double getWidth();

    /**
     * Metoda pro získání všech závislých elementů na elementu vstupním.<br />
     * Metoda má využití např. při mazání elementu, kdy je třeba element samazat
     * i se všemi jeho závislými elementy.
     *
     * @param element element, jehož závislé elementy je třeba dohledat
     * @return kolekce závislých elementů na elementu vstupním
     */
    public ArrayList<LayoutElement> getMeAndMyDependants(LayoutElement element);

    /**
     * Metoda pro vložení více elementů naráz. Elementy se vloží na místo
     * aktuálně označeného jointu.
     *
     * @param elements kolekce elementů, které mají být vloženy
     */
    public void addElements(ArrayList<LayoutElement> elements);

    /**
     * Vrací aktuálně označený joint.
     *
     * @return aktuálně označený joint
     */
    public Joint getFocusedJoint();

    /**
     * Metoda pro získání možného nastavení konkrétního layoutu.
     *
     * @return možné nastavení konkrétního layoutu
     */
    public ArrayList<JMenuItem> getSettings();

    /**
     * Metoda pro získání instance Stroke, používané k vykreslení cest za Goto
     * symbolem
     *
     * @return instance Stroke, používaná k vykreslení cest za Goto symbolem
     */
    public BasicStroke getGotoStroke();

    /**
     * Metoda pro získání instance Stroke, používané k vykreslení komentářových
     * cest
     *
     * @return instance Stroke, používaná k vykreslení komentářových cest
     */
    public BasicStroke getCommentStroke();

}
