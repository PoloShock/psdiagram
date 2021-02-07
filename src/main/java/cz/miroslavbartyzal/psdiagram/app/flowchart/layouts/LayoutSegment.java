/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.flowchart.layouts;

import cz.miroslavbartyzal.psdiagram.app.flowchart.FlowchartSegment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Comment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Tato třída rozšiřuje třídu FlowchartSegment a reprezentuje tak kromě
 * logického segmentu diagramu i údaje, potřebné pro jakýkoliv layout. Jedná
 * se obecně o informace, které mohou potenciálně být nějakým způsobem
 * rozdílné pro jednotlivé layouty. Zárověň jsou však obecné, vykreslí je
 * Abstract Layout.<br />
 * Údaje, které tato třída obsahuje jsou například údaje o spojnici vedoucí k
 * tomuto segmentu a ven z tohoto segmentu. Je-li segment prázdný, nebo obsahuje
 * pouze symboly komentářů, měl by obsahovat pouze cestu od tohoto segmentu,
 * cesta vedoucí k tomuto segmentu by měla být prázdná.<br />
 * Tyto spojnice náleží právě segmentům, protože element ukládá jen spojnice
 * vedoucí k elementu dalšímu.</p>
 *
 * <p>Třída obsahuje metody pro práci s vnitřními "layoutovými" daty a metody
 * pro manipulaci s grafickou spojnicí, vedoucí k/z tohoto segmentu.<br />
 * Dále třída umožňuje uchovat libovolný počet číselných dat (tzv. extra-data).
 * Tyto údaje zpravidla layout používá k uložení poznámky o struktuře diagramu
 * po její analýze.<br />
 * Každý segment navíc může obsahovat textový údaj s jeho dekripcí. Je to
 * například údaj Ano/Ne u podmínkových větví a třída LayoutSegment ukládá
 * informaci o jeho umístění, jejž layout vypočítal.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "layoutSegment")
public final class LayoutSegment extends FlowchartSegment<LayoutSegment, LayoutElement>
{

    private ArrayList<Integer> extraLayoutData = new ArrayList<>(2);
    private Path2D pathToThisSegment = new Path2D.Double(Path2D.WIND_NON_ZERO, 2);
    private Path2D pathFromThisSegment = new Path2D.Double(Path2D.WIND_NON_ZERO, 5);
    private Point2D descriptionLocation = null;

    private LayoutSegment()
    {
    }

    /**
     * Kontruktor s parametrem pro rodičovský element segmentu. Má-li být
     * tento segment kořenový, použijeme null.
     *
     * @param parentElement rodičovský element segmentu
     */
    public LayoutSegment(LayoutElement parentElement)
    {
        super(parentElement);
    }

    @Override
    protected LayoutSegment getThis()
    {
        return this;
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
     * Metoda pro přidání symbolu do tohoto segmentu, v pořadí za daný element.
     * Symbol je automaticky zapouzdřen třídou LayoutElement.
     *
     * @param parentElement element, za který se má symbol zařadit
     * @param symbol symbol, který se má do tohoto segmentu vložit
     * @return element, reprezentující právě vložený symbol
     */
    public LayoutElement addSymbol(LayoutElement parentElement, Symbol symbol)
    {
        return addSymbol(parentElement, symbol, 0);
    }

    /**
     * Metoda pro přidání symbolu s daným počtem vnitřních segmentů do tohoto
     * segmentu, v pořadí za daný element. Symbol je automaticky zapouzdřen
     * třídou LayoutElement.
     *
     * @param parentElement element, za který se má symbol zařadit
     * @param symbol symbol, který se má do tohoto segmentu vložit
     * @param innerOutCount počet vnitřních segmentů vkládaného symbolu
     * @return element, reprezentující právě vložený symbol
     */
    public LayoutElement addSymbol(LayoutElement parentElement, Symbol symbol, int innerOutCount)
    {
        return super.addElement(parentElement, new LayoutElement(this, symbol, innerOutCount));
    }

    /**
     * Nastaví spojnici (cestu) vedoucí k tomuto segmentu.
     *
     * @param path spojnice (cesta) veducí k tomuto segmentu
     */
    public void setPathToThisSegment(Path2D path)
    {
        pathToThisSegment = path;
    }

    /**
     * Nastaví spojnici (cestu) vedoucí z tohoto segmentu.
     *
     * @param path spojnice (cesta) veducí z tohoto segmentu
     */
    public void setPathFromThisSegment(Path2D path)
    {
        pathFromThisSegment = path;
    }

    /**
     * Vrátí spojnici vedoucí k tomuto segmentu.
     *
     * @return spojnice vedoucí k tomuto segmentu
     */
    public Path2D getPathToThisSegment()
    {
        return pathToThisSegment;
    }

    /**
     * Vrátí spojnici vedoucí z tohoto segmentu.
     *
     * @return spojnice vedoucí z tohoto segmentu
     */
    public Path2D getPathFromThisSegment()
    {
        return pathFromThisSegment;
    }

    /**
     * Metoda pro smazání elementu. Je-li segment po smazání elementu prázdný,
     * nebo obsahuje jen komentáře, je automaticky smazána cesta (spojnice) k
     * tomuto segmentu.
     *
     * @param element element, který má být smazán
     */
    @Override
    public void removeElement(LayoutElement element)
    {
        super.removeElement(element);
        for (LayoutElement elmnt : this) {
            if (!(elmnt.getSymbol() instanceof Comment)) {
                return;
            }
        }
        pathToThisSegment = new Path2D.Double(Path2D.WIND_NON_ZERO, 2); // kdyz je segment prazdny nebo obsahuje jen komentare, je treba mu smazat pathToThisSegment
    }

    /**
     * Metoda pro nastavení bodu umístění deskripce. Toto nastavení
     * je specifické pro layout.
     *
     * @param descriptionLocation bod, který udává, na kterém místě bude
     * deskripce segmentu zobrazena.
     */
    public void setDescriptionLocation(Point2D descriptionLocation)
    {
        this.descriptionLocation = descriptionLocation;
    }

    public Point2D getDescriptionLocation()
    {
        return descriptionLocation;
    }

    /**
     * Metoda vrátí cestu od hlavního kořenového segmentu k tomuto segmentu.<br
     * />
     * Cesta má podobu číselného pole, v němž každé číslo reprezentuje index
     * elementu/vnitřního semgnetu. Cesta je řazena od kořenového segmentu
     * směrem k tomuto segmentu, kořenový segment je však vynechán, protože jeho
     * index je vždy 0 a je vždy v diagramu přítomen. Je-li tento segment
     * kořenový, metoda vrací pole o rozměru 0.
     *
     * @return indexová cesta od hlavního kořenového segmentu k tomuto segmentu
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
        if (super.getParentElement() == null) {
            return new int[path.length - 1]; // korenovy segment je automatika
        } else {
            int[] retPath = super.getParentElement().getPathFromMainSegment(new int[path.length + 1],
                    depth + 1);
            retPath[retPath.length - depth] = super.getParentElement().indexOfInnerSegment(this);
            return retPath;
        }
    }

}
