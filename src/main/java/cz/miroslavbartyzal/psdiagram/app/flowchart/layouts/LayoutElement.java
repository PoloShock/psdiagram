/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.flowchart.layouts;

import cz.miroslavbartyzal.psdiagram.app.flowchart.FlowchartElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Tato třída rozšiřuje třídu FlowchartElement a reprezentuje tak kromě
 * logického elementu diagramu i údaje, potřebné pro jakýkoliv layout. Jedná
 * se obecně o informace, které mohou potencionálně být nějakým způsobem
 * rozdílné pro jednotlivé layouty. Zárověň jsou však obecné, vykreslí je
 * Abstract Layout.<br />
 * Údaje, které tato třída obsahuje se týkají jen konkrétního elementu, který
 * zapouzdřuje - tedy například údaje o spojnici vedoucí z tohoto elementu k
 * dalšímu a podobně. Vždy je uchovávána dvojice element-spojnice.</p>
 *
 * <p>Třída obsahuje metody pro práci s vnitřními "layoutovými" daty a metody
 * pro manipulaci s grafickou spojnicí, vedoucí od tohoto elementu k
 * dalšímu.<br />
 * Dále třída umožňuje uchovat libovolný počet číselných dat (tzv. extra-data).
 * Tyto údaje zpravidla layout používá k uložení poznámky o struktuře diagramu
 * po její analýze.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlRootElement(name = "layoutElement")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "layoutElement")
public final class LayoutElement extends FlowchartElement<LayoutSegment, LayoutElement>
{

    private ArrayList<Integer> extraLayoutData = new ArrayList<>(1);
    private Path2D pathToNextSymbol = new Path2D.Double(Path2D.WIND_NON_ZERO, 4);

    private LayoutElement()
    {
    }

    /**
     * Kontruktor, který zajistí vytvoření elementu zapouzdřujícího daný symbol.
     *
     * @param parentSegment požadovaný rodičovský segment tohoto elementu
     * @param symbol symbol, který má tento element reprezentovat
     */
    public LayoutElement(LayoutSegment parentSegment, Symbol symbol)
    {
        this(parentSegment, symbol, 0);
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
    public LayoutElement(LayoutSegment parentSegment, Symbol symbol, int innerOutCount)
    {
        super(parentSegment, symbol, innerOutCount);
        if (symbol.getInnerOutsCount() != 0) {
            if (symbol.getInnerOutsCount() != -1) {
                if (symbol.hasElseSegment()) {
                    for (int i = 0; i < symbol.getInnerOutsCount(); i++) {
                        super.setInnerSegment(i, new LayoutSegment(this));
                    }
                } else {
                    for (int i = 1; i < symbol.getInnerOutsCount() + 1; i++) {
                        super.setInnerSegment(i, new LayoutSegment(this));
                    }
                }
            } else if (symbol.hasElseSegment() && innerOutCount > 0) {
                for (int i = 0; i < innerOutCount; i++) {
                    super.setInnerSegment(i, new LayoutSegment(this));
                }
            } else if (!symbol.hasElseSegment() && innerOutCount > 0) {
                for (int i = 1; i < innerOutCount + 1; i++) {
                    super.setInnerSegment(i, new LayoutSegment(this));
                }
            } else if (innerOutCount == 0) {
                throw new Error("You must specify number of inner outs for this symbol!");
            } else {
                throw new Error("Specified number of inner outs is incorrect!");
            }

            // prirazeni deskripce segmentum
            for (int i = 0; i < symbol.getDefaultSegmentDescriptions().length; i++) {
                this.getInnerSegment(i).setDescription(symbol.getDefaultSegmentDescriptions()[i]);
            }
        }
    }

    /**
     * Vrátí číselný údaj extra-dat pod daným indexem.
     *
     * @param index index požadovaného číselného údaje
     * @return číselný údaj extra-dat pod daným indexem
     */
    public int getExtraLayoutData(int index)
    {
        return extraLayoutData.get(index);
    }

    /**
     * Nastaví pod daný index extra-dat, daný číselný údaj. Případné přeskočené
     * indexy v kolekci metoda automaticky vyplní nulou.
     *
     * @param index index, pod který se mají data uložit
     * @param data data, která mají být uložena
     */
    public void setExtraLayoutData(int index, int data)
    {
        while (index >= extraLayoutData.size()) {
            extraLayoutData.add(0);
        }
        extraLayoutData.set(index, data);
    }

    /**
     * Metoda vymaže všechny extra-data.
     */
    public void clearExtraData()
    {
        extraLayoutData.clear();
    }

    /**
     * Nastaví spojnici (cestu) k dalšímu symbolu (elementu), který logicky
     * následuje za tímto.
     *
     * @param path spojnice, která má reprezentovat cestu k následujícímu
     * symbolu.
     */
    public void setPathToNextSymbol(Path2D path)
    {
        pathToNextSymbol = path;
    }

    /**
     * Vrátí spojnici vedoucí k následujícímu symbolu.
     *
     * @return spojnice vedoucí k následujícímu symbolu
     */
    public Path2D getPathToNextSymbol()
    {
        return pathToNextSymbol;
    }

    /**
     * Metoda spočítá a vrátí počet úseček, které obsahuje spojnice k
     * následujícímu symbolu diagramu.
     *
     * @return počet úseček, které obsahuje spojnice k následujícímu symbolu
     * diagramu
     */
    public int SizeOfPathToNextSymbol()
    {
        PathIterator pathIterator = pathToNextSymbol.getPathIterator(null);
        int i;
        for (i = 0; !pathIterator.isDone(); i++) {
            pathIterator.next();
        }
        return i;
    }

    /**
     * Metoda vrátí cestu od hlavního kořenového segmentu k tomuto elementu.<br
     * />
     * Cesta má podobu číselného pole, v němž každé číslo reprezentuje index
     * elementu/vnitřního semgnetu. Cesta je řazena od kořenového segmentu
     * směrem k tomuto elementu, kořenový segment je však vynechán, protože jeho
     * index je vždy 0 a je vždy v diagramu přítomen.
     *
     * @return indexová cesta od hlavního kořenového segmentu k tomuto elementu
     */
    public int[] getPathFromMainSegment()
    {
        return getPathFromMainSegment(new int[1], 1);
    }

    protected int[] getPathFromMainSegment(int[] path, int depth)
    {
        if (depth == 0) {
            depth = 1; // zacinam vzdy od 0
        }
        int[] retPath = super.getParentSegment().getPathFromMainSegment(new int[path.length + 1],
                depth + 1);
        retPath[retPath.length - depth] = super.getParentSegment().indexOfElement(this);
        return retPath;
    }

}
