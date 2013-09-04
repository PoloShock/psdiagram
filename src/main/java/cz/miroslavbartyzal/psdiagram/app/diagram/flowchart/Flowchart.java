/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.diagram.flowchart;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Tato třída zapouzdřuje samotný diagram, včetně jeho logické
 * struktury (segmenty s elementy).<br />
 * Obsahuje některé základní operace nad diagramem, jako iteraci jednotlivými
 * segmenty diagramu a získání kořenového segmentu.</p>
 *
 * <p>Vývojový diagram je logicky uspořádán do segmentů. Segmenty pak obsahují
 * pouze libovolný počet elementů. Elementy, obsahující symboly diagramu, pak
 * mohou dále obsahovat segmenty. Diagram je tedy ve finále tvořen různě
 * vnořenými segmenty, které pak obsahují samotné elementy (symboly)
 * diagramu.</p>
 *
 * <p>Celá tato struktura pak připomíná strom, tedy se svými specifickými
 * pravidly.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlRootElement(name = "flowchart")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "flowchart")
public final class Flowchart<S extends FlowchartSegment<S, E>, E extends FlowchartElement<S, E>>
        implements Iterable<S>
{

    @XmlElement(name = "mainSegment", required = true)
    private S mainSegment;

    private Flowchart()
    {
    }

    /**
     * Konstruktor s povinným parametrem pro kořenový (první) segment diagramu.
     *
     * @param mainSegment kořenový (první) segment diagramu
     */
    public Flowchart(S mainSegment)
    {
        this.mainSegment = mainSegment;
    }

    /**
     * Vrací kořenový segment diagramu.
     *
     * @return kořenový segment diagramu
     */
    public S getMainSegment()
    {
        return mainSegment;
    }

    //Resim v samotnem segmentu...
    /*
     * public E findParentElement(S segment) {
     * if (segment.equals(mainSegment)) {
     * return null;
     * }
     * for (S sgmnt: this) {
     * for (E element: sgmnt) {
     * if (element.containsInnerSegment(segment)) {
     * return element;
     * }
     * }
     * }
     * return null;
     * }
     */
    //?
    /*
     * public E getIncompleteElement() {
     * for (S segment: this) {
     * if (segment.isEmpty()) {
     * E element = segment.getParentElement();
     * if (!(element.getSymbol() instanceof Decision)) {
     * return element;
     * } else if (element.indexOfInnerSegment(segment) == 0) {
     * return element;
     * }
     * }
     * }
     * return null;
     * }
     */
    /**
     * Vrací interátor nad segmenty, které tento diagram obsahuje.
     *
     * @return interátor nad segmenty, které tento diagram obsahuje
     */
    @Override
    public Iterator<S> iterator()
    {
        return new myIterator();
    }

    private final class myIterator implements Iterator<S>
    {

        private ArrayList<S> lSubFlowchartSegments = new ArrayList<>(20);
        private S actualSegment;

        public myIterator()
        {
            lSubFlowchartSegments.add(mainSegment);
        }

        @Override
        public boolean hasNext()
        {
            if (lSubFlowchartSegments.size() > 0) {
                return true;
            }
            return false;
        }

        @Override
        public S next()
        {
            actualSegment = lSubFlowchartSegments.get(0);
            lSubFlowchartSegments.remove(0);
            if (actualSegment != null) {
                for (E element : actualSegment) {
                    if (element.getInnerSegmentsCount() > 0) {
                        lSubFlowchartSegments.addAll(element.getInnerSegments());
                    }
                }
            }
            return actualSegment;
        }

        @Override
        public void remove()
        {
            actualSegment.getParentElement().removeInnerSegment(actualSegment);
            actualSegment = null;
        }

    }

}
