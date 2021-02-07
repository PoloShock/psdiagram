/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.flowchart;

import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import java.util.ArrayList;
import java.util.TreeSet;
import jakarta.xml.bind.annotation.*;

/**
 * <p>Tato abstraktní třída reprezentuje logický element diagramu - symbol
 * diagramu.<br />
 * Tento element tedy zapouzdřuje samotný symbol diagramu. Element dále může
 * vlastnit své vnitřní segmenty (viz. třída FlowchartSegment). Jedná se
 * například o podmínku (true a else větev), cyklus.<br />
 * Každý element má v sobě rovněž uložen rodičovský segment, neboť každý element
 * musí být součást některého segmentu.</p>
 *
 * <p>Třída obsahuje metody pro práci s vnitřními segmenty elementu a pro
 * nastavení rodičovského segmentu.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "flowchartElement")
public abstract class FlowchartElement<S extends FlowchartSegment<S, E>, E extends FlowchartElement<S, E>>
{

    @XmlAttribute(name = "xmlID", required = true)
    @XmlID
    private String xmlID;
    @XmlAttribute(name = "parentSegment", required = true)
    @XmlIDREF
    private S parentSegment;
    @XmlAnyElement(lax = true)
    private Symbol symbol;
    @XmlElement(name = "segment", nillable = true)
    private ArrayList<S> lInnerSegments; // nulty index znaci else vetev!

    protected FlowchartElement()
    {
    }

    /**
     * Konstruktor s parametry pro rodičovský segment, pro symbol diagramu,
     * který má tento element reprezentovat a pro počet vnitřních segmentů
     * symbolu. Má-li symbol pevný počet vnitřních segmentů, na parametru
     * "innerOutCount" nezáleží.
     *
     * @param parentSegment rodičovský segment tohoto elementu
     * @param symbol symbol, který má tento element reprezentovat
     * @param innerOutCount počet vnitřních segmentů symbolu; má-li symbol pevný
     * počet vnitřních segmentů, na tomto parametru nezáleží
     */
    protected FlowchartElement(S parentSegment, Symbol symbol, int innerOutCount)
    {
//        xmlID = parentSegment.getUniqueID(parentSegment.getBookedIDs(null));
        this.parentSegment = parentSegment;
        this.symbol = symbol;
        if (symbol.getInnerOutsCount() != 0) {
            if (symbol.getInnerOutsCount() != -1) {
                if (symbol.hasElseSegment()) {
                    lInnerSegments = new ArrayList<>(symbol.getInnerOutsCount());
                    for (int i = 0; i < symbol.getInnerOutsCount(); i++) {
                        lInnerSegments.add(null);
                    }
                } else {
                    lInnerSegments = new ArrayList<>(symbol.getInnerOutsCount() + 1);
                    for (int i = 0; i < symbol.getInnerOutsCount() + 1; i++) {
                        lInnerSegments.add(null);
                    }
                }
            } else if (symbol.hasElseSegment() && innerOutCount > 0) {
                lInnerSegments = new ArrayList<>(innerOutCount);
                for (int i = 0; i < innerOutCount; i++) {
                    lInnerSegments.add(null);
                }
            } else if (!symbol.hasElseSegment() && innerOutCount > 0) {
                lInnerSegments = new ArrayList<>(innerOutCount + 1);
                for (int i = 0; i < innerOutCount + 1; i++) {
                    lInnerSegments.add(null);
                }
            } else if (innerOutCount == 0) {
                //JOptionPane.showMessageDialog(null, "You must specify number of inner outs for this symbol!", "Error", JOptionPane.ERROR_MESSAGE);
                throw new Error("You must specify number of inner outs for this symbol!");
            } else {
                throw new Error("Specified number of inner outs is incorrect!");
            }
        }
    }

    /**
     * Metoda pro přidání vnitřního segmentu.
     *
     * @param segment segmnet, který má být přidán jako vnitřní
     */
    public void addInnerSegment(S segment)
    {
        if (symbol.getInnerOutsCount() != -1) {
            throw new Error("You can not add inner outs for this symbol!");
        }
        lInnerSegments.ensureCapacity(lInnerSegments.size() + 1);
        lInnerSegments.add(segment);
        segment.checkIDUniqueness();
    }

    /**
     * Metoda pro nastavení daného segmentu na specifický index vnitřních
     * segmentů.
     *
     * @param index index, označující místo, na které má být segment vložen
     * @param segment segment, který má být vložen jako vnitřní
     */
    public void setInnerSegment(int index, S segment)
    {
        if (index > lInnerSegments.size() - 1 || index < 0) {
            throw new Error("Specified index is out of innerSegments range!");
        }
        lInnerSegments.set(index, segment);
        segment.checkIDUniqueness();
    }

    /**
     * Metoda pro vymazání daného vnitřního segmentu tohoto elementu.
     *
     * @param segment segment, který má být smazán
     */
    public void removeInnerSegment(S segment)
    {
        if (symbol.getInnerOutsCount() != -1) {
            throw new Error("You can not remove inner outs for this symbol!");
        } else if ((!symbol.hasElseSegment() && getInnerSegmentsCount() < 2) || (symbol.hasElseSegment() && getInnerSegmentsCount() < 3)) {
            throw new Error("You can not remove any more inner outs for this symbol!");
        }
        lInnerSegments.remove(segment);
        lInnerSegments.trimToSize();
    }

    /**
     * Metoda pro získání vnitřního segmentu na daném indexu.
     *
     * @param index index určuje místo, ze kterého má být vnitřní segment vrácen
     * @return vnitřní segment pod daným indexem
     */
    public S getInnerSegment(int index)
    {
        return lInnerSegments.get(index);
    }

    /**
     * Metoda vrací kolekci všech vnitřních segmentů tohto elementu.
     *
     * @return kolekce všech vnitřních segmentů tohto elementu
     */
    public ArrayList<S> getInnerSegments()
    {
        return lInnerSegments;
    }

    /**
     * Metoda vrací index daného vnitřního segmentu.
     *
     * @param segment segment, jehož index chceme vypátrat
     * @return index daného segmentu
     */
    public int indexOfInnerSegment(S segment)
    {
        return lInnerSegments.indexOf(segment);
    }

    /**
     * Metoda vrací počet vnitřních segmentů tohto elementu.
     *
     * @return počet vnitřních segmentů tohto elementu
     */
    public int getInnerSegmentsCount()
    {
        if (lInnerSegments == null) {
            return 0;
        }
        return lInnerSegments.size();
    }

    /**
     * Metoda pro zjištění, zda daný segment je obsažen ve vnitřních segmentech
     * tohoto elementu.
     *
     * @param segment segment, jehož přítomnost chceme vypátrat
     * @return true nebo false, pokud je segment ve vnitřních segmentech tohoto
     * elementu obsažen
     */
    public boolean containsInnerSegment(S segment)
    {
        return lInnerSegments.contains(segment);
    }

    /**
     * Metoda pro nastavení rodičovského segmentu tohoto elementu.
     *
     * @param parentSegment segment, který má být nastaven jako rodičovský
     */
    void setParentSegment(S parentSegment)
    {
        this.parentSegment = parentSegment;
    }

    /**
     * Metoda pro získání symbolu, který tento element zapouzdřuje.
     *
     * @return symbol, který tento element zapouzdřuje
     */
    public Symbol getSymbol()
    {
        return symbol;
    }

    /**
     * Metoda pro získání rodičovského segmentu tohoto elementu.
     *
     * @return rodičovský segment tohoto elementu
     */
    public S getParentSegment()
    {
        return parentSegment;
    }

    /**
     * Metoda, která ověří jedinečnost identifikačního čísla tohoto elementu a
     * všech jeho vnitřních segment (a jejich elementů atd.) v rámci celého
     * vývojového diagramu.<br />
     * Identifikační číslo je potřeba při ukládání diagramu do XML.
     */
    public void checkIDUniqueness()
    {
        S mainSegment = parentSegment;
        while (mainSegment.getParentElement() != null) {
            mainSegment = mainSegment.getParentElement().getParentSegment();
        }
        TreeSet<Integer> bookedIDs = new TreeSet<>();
        parentSegment.getBookedIDs(this, mainSegment, bookedIDs);
        checkIDUniqueness(bookedIDs);
    }

    protected final void checkIDUniqueness(TreeSet<Integer> bookedIDs)
    {
        if (xmlID == null || bookedIDs.contains(Integer.valueOf(xmlID))) {
            // je treba ziskat unikatni ID
            xmlID = parentSegment.getUniqueID(bookedIDs);
        }
        bookedIDs.add(Integer.valueOf(xmlID));
        if (lInnerSegments != null) {
            for (S segment : lInnerSegments) {
                if (segment != null) {
                    segment.checkIDUniqueness(bookedIDs);
                }
            }
        }
    }

    protected String getXmlID()
    {
        return xmlID;
    }

}
