/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.flowchart;

import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import java.awt.font.TextLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import javax.xml.bind.annotation.*;

/**
 * <p>
 * Tato abstraktní třída reprezentuje logický segment diagramu - logickou
 * jednotku, která sama dále obsahuje elementy (samotné symboly diagramu).
 * Každý element musí být obsažen právě v takovém segmentu.</p>
 *
 * <p>
 * První segment je vždy kořenový a je uložen ve třídě Flowchart.
 * Segment pak obsahuje libovolný počet elementů - symbolů diagramu. Některé
 * elementy pak umožňují i vložení vlastního segmentu (například symbol
 * podmínky). Takový segment pak představuje samostatnou větev elementu,
 * kdy tento segment zároveň obsahuje i záznam o jeho rodičovském elementu -
 * elementu, který tento segment vlastní (řečený element podmínky).</p>
 *
 * <p>
 * Třída disponuje některými základními operacemi pro manipulaci s jejími
 * elementy, jako například přidat element, přesunout element, smazat element
 * atd., navíc obsahuje deskripci segmnetu (např. Ano/Ne/Jinak). Třída
 * zároveň implementuje rozhraní Iterable, jejími elementy tak lze standartně
 * iterovat.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "flowchartSegment")
public abstract class FlowchartSegment<S extends FlowchartSegment<S, E>, E extends FlowchartElement<S, E>>
        implements Iterable<E>
{

    @XmlAttribute(name = "xmlID", required = true)
    @XmlID
    private String xmlID;
    @XmlAttribute(name = "parentElement", required = true)
    @XmlIDREF
    private E parentElement;
    @XmlElement(name = "element")
    private ArrayList<E> lElements = new ArrayList<>(20); // kolekce pro uchovani elementu
    private TextLayout descriptionLayout = null;
    private String description = null;
    @XmlElement(name = "defaultDescription")
    private String defaultDescription = null;
    @XmlElement(name = "customDescription")
    private String customDescription = null;

    protected FlowchartSegment()
    {
    }

    /**
     * Kontruktor s parametrem pro rodičovský element segmentu. Má-li být
     * tento segment kořenový, použijeme null.
     *
     * @param parentElement rodičovský element segmentu
     */
    @SuppressWarnings("LeakingThisInConstructor")
    protected FlowchartSegment(E parentElement)
    {
        if (parentElement == null) {
            xmlID = "0";
        }
        this.parentElement = parentElement;
    }

    /**
     * Abstraktní metoda, která zaručí, že implementující třída vrátí instanci
     * sebe samé. Tato metoda je třeba ke správné funkci přidání elementu, kdy
     * každý element musí obsahovat odkaz na rodičovský segment.
     *
     * @return instance vlastního segmentu
     */
    protected abstract S getThis();

    /**
     * Metoda pro vložení elementu. Je nutné specifikovat parametr
     * "beforeElement",
     * podle kterého se identifikuje, kam se vkládaný element umístí. Parametr
     * "element" pak reprezentuje samotný element, který se vloží právě za
     * element "beforeElement".<br />
     * Metoda dále po vložení elementu invokuje jeho metodu checkIDUniqueness(),
     * pro zajištění jedinečnosti identifikačních čísel v rámci celého
     * vývojového diagramu.
     *
     * @param beforeElement element, pod který se vkládaný element má vložit
     * @param element element který se má vložit
     * @return vložený element
     */
    public E addElement(E beforeElement, E element)
    {
        if (beforeElement == null && lElements.isEmpty()) {
            lElements.add(element);
            element.setParentSegment(getThis());
            element.checkIDUniqueness();
            return lElements.get(0);
        }
        int destIndex = lElements.indexOf(beforeElement) + 1;
        lElements.add(destIndex, element);
        element.setParentSegment(getThis());
        element.checkIDUniqueness();
        return lElements.get(destIndex);
    }

    /*
     * Metoda pro vložení elementu. Je nutné specifikovat parametr "destIndex",
     * který určuje pozici, na kterou se má vkládaný element vložit a dále
     * parametr "element", který reprezentuje samotný vkládaný element.<br />
     * Metoda vrací vložený element.
     *
     * @param destIndex index, na který se má element vložit
     * @param element element který se má vložit
     * @return vložený element
     *
     * protected E addElement(int destIndex, E element) {
     * try {
     * lElements.add(destIndex, element);
     * return lElements.get(destIndex);
     * } catch (IndexOutOfBoundsException e) {
     * throw new Error("IndexOutOfBoundsException!");
     * }
     * }
     */
    /**
     * Metoda pro přesunutí daného elementu za element destinační. Daný element
     * k přesunutí musí být obsažen v tomto segmentu, destinační element
     * nikoliv.
     *
     * @param elementToMove element, který chceme přesunout
     * @param destinationBeforeElement element, za který se zařadí přesouvaný
     * element
     */
    public void moveElement(E elementToMove, E destinationBeforeElement)
    {
        lElements.remove(elementToMove);
        destinationBeforeElement.getParentSegment().addElement(destinationBeforeElement,
                elementToMove);
    }

    /**
     * Metoda pro smazání elementu.
     *
     * @param element element, který má být smazán
     */
    public void removeElement(E element)
    {
        lElements.remove(element);
    }

    /**
     * Metoda vrací požadovaný element na základě jeho indexu.
     *
     * @param index index požadovaného elementu
     * @return požadovaný element
     */
    public E getElement(int index)
    {
        return lElements.get(index);
    }

    /**
     * Metoda pro získání indexu daného elementu.
     *
     * @param element element, jehož index chceme získat
     * @return index daného elementu
     */
    public int indexOfElement(E element)
    {
        return lElements.indexOf(element);
    }

    /**
     * Metoda pro zjištění počtu elementů v segmentu.
     *
     * @return počet elementů v segmentu
     */
    public int size()
    {
        return lElements.size();
    }

    /**
     * Metoda pro zjištění, zda-li segment je prázdný, tedy bez elementů.
     *
     * @return true pokud je segment prázdný
     */
    public boolean isEmpty()
    {
        return lElements.isEmpty();
    }

    /**
     * Metoda pro zjištění, zda daný element je obsažen v tomto segmentu.
     *
     * @param element element, jehož přítomnost chceme vypátrat
     * @return true pokud je element v segmentu obsažen
     */
    public boolean containsElement(E element)
    {
        return lElements.contains(element);
    }

    /**
     * Metoda pro získání rodičovského elementu tohoto segmentu. Je-li segment
     * kořenový, vrací null.
     *
     * @return rodičovský element tohoto segmentu
     */
    public E getParentElement()
    {
        return parentElement;
    }

    /**
     * Metoda pro nastavení deskripce segmentu. Text deskripce se pak v
     * závislosti na použitém layoutu zobrazí v blízkosti segmentu.
     * Deskripce se používá například u symbolu podmíněného procesu, kdy pro
     * uživatele jasně definuje, která z jeho věteví se vykoná při nesplněné,
     * nebo naopak splněné podmínce.
     *
     * @param description text, který má být nastaven jako deskripce segmentu
     */
    @XmlElement(name = "description")
    public void setDescription(String description)
    {
        if (description == null || description.equals("")) {
            descriptionLayout = null;
            this.description = null;
        } else {
            TextLayout desc = new TextLayout(description, SettingsHolder.SMALL_CODEFONT,
                    SettingsHolder.FONTRENDERCONTEXT);
            this.descriptionLayout = desc;
            this.description = description;
        }
    }

    /**
     * Metoda pro získání deskripce segmentu ve formě objektu TextLayout. Tento
     * objekt se pak využívá například k změření optických okrajů textu a k jeho
     * vykreslení
     *
     * @return objekt TextLayout, obsahující text deskripce
     */
    public TextLayout getDescriptionLayout()
    {
        return descriptionLayout;
    }

    /**
     * Metoda pro získání textu deskripce segmentu.
     *
     * @return text deskripce segmentu
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Metoda pro získání uživatelského textu deskripce segmentu.
     *
     * @return uživatelský text deskripce segmentu
     */
    public String getCustomDescription()
    {
        return customDescription;
    }

    /**
     * Metoda pro získání výchozího textu deskripce segmentu.
     *
     * @return výchozí text deskripce segmentu
     */
    public String getDefaultDescription()
    {
        return defaultDescription;
    }

    /**
     * Metoda pro nastavení uživatelského textu segmentu. Tento text je
     * zpravidla vytvořen uživatelem, narozdíl od výchozího.
     *
     * @param customDescription text uživatelské deskripce segmentu
     */
    public void setCustomDescription(String customDescription)
    {
        this.customDescription = customDescription;
    }

    /**
     * Metoda pro nastavení výchozího textu segmentu. Tento text je zpravidla
     * vygenerován automaticky, narozdíl od uživateslkého.
     *
     * @param defaultDescripton výchozí text deskripce segmentu
     */
    public void setDefaultDescripton(String defaultDescripton)
    {
        this.defaultDescription = defaultDescripton;
    }

    /**
     * Vrací interátor nad elementy, které tento segment obsahuje.
     *
     * @return interátor nad elementy, které tento segment obsahuje
     */
    @Override
    public Iterator<E> iterator()
    {
        return lElements.iterator();
        /*
         * return new Iterator<E>()
         * {
         * private int actualIndex = -1;
         *
         * @Override
         * public boolean hasNext() {
         * if (lElements.size()-1 > actualIndex) {
         * return true;
         * }
         * return false;
         * }
         *
         * @Override
         * public E next() {
         * actualIndex++;
         * return lElements.get(actualIndex);
         * }
         *
         * @Override
         * public void remove() {
         * lElements.remove(actualIndex);
         * }
         * };
         */
    }

    /**
     * Metoda, která ověří jedinečnost identifikačního čísla tohoto segmentu a
     * všech jeho elementů (a jejich vnitřních segmentů atd.) v rámci celého
     * vývojového diagramu.<br />
     * Identifikační číslo je potřeba při ukládání diagramu do XML.
     */
    public void checkIDUniqueness()
    {
        S mainSegment = getThis();
        while (mainSegment.getParentElement() != null) {
            mainSegment = mainSegment.getParentElement().getParentSegment();
        }
        TreeSet<Integer> bookedIDs = new TreeSet<>();
        getBookedIDs(this, mainSegment, bookedIDs);
        checkIDUniqueness(bookedIDs);
    }

    protected final void checkIDUniqueness(TreeSet<Integer> bookedIDs)
    {
        if (xmlID == null || bookedIDs.contains(Integer.valueOf(xmlID))) {
            // je treba ziskat unikatni ID
            xmlID = getUniqueID(bookedIDs);
        }
        bookedIDs.add(Integer.valueOf(xmlID));
        for (E element : lElements) {
            element.checkIDUniqueness(bookedIDs);
        }
    }

    protected final void getBookedIDs(Object exception, S segment, TreeSet<Integer> bookedSetToSave)
    {
        if (segment.getXmlID() == null || (exception != null && segment.equals(exception))) {
            return;
        }
        bookedSetToSave.add(Integer.valueOf(segment.getXmlID()));
        for (E element : segment) {
            if (exception == null || !element.equals(exception)) {
                bookedSetToSave.add(Integer.valueOf(element.getXmlID()));
                if (element.getInnerSegments() != null) {
                    for (S innerSegment : element.getInnerSegments()) {
                        if (innerSegment != null) {
                            getBookedIDs(exception, innerSegment, bookedSetToSave);
                        }
                    }
                }
            }
        }
    }

    protected final String getUniqueID(TreeSet<Integer> bookedIDs)
    {
        for (int i = 0; i <= Integer.MAX_VALUE; i++) {
            if (!bookedIDs.contains(i)) {
                return String.valueOf(i);
            }
        }
        return null;
    }

    protected String getXmlID()
    {
        return xmlID;
    }

}
