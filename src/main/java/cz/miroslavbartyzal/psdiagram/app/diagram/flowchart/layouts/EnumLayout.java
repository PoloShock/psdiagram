/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.layouts;

import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.Flowchart;
import javax.swing.JComponent;

/**
 * Tato třída výčtového typu určuje dostupné layouty vývojového diagramu.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public enum EnumLayout
{

    TBLRLayout
    {
        @Override
        public String getName()
        {
            return "Normový Layout";
        }

        @Override
        public Layout getInstance(JComponent canvas,
                Flowchart<LayoutSegment, LayoutElement> flowchart)
        {
            return new TBLRLayout(canvas, flowchart);
        }
//    },
//    LRTBLayout {
//        @Override
//        public String getName() {
//            return "LRTB Layout";
//        }
//
//        @Override
//        public Layout getInstance(JComponent canvas, Flowchart<LayoutSegment, LayoutElement> flowchart) {
//            return new LRTBLayout(canvas, flowchart);
//        }
    };

    /**
     * Vrací uživatelsky čitelný název konkrétního layoutu.
     * <p/>
     * @return uživatelsky čitelný název konkrétního layoutu
     */
    public abstract String getName();

    /**
     * Metoda pro získání instance konkrétního layoutu.
     * <p/>
     * @param canvas plátno, na které má být vývojový diagram vykreslován
     * @param flowchart vývojový diagram k vykreslení
     * @return instace konkrétního layoutu
     */
    public abstract Layout getInstance(JComponent canvas,
            Flowchart<LayoutSegment, LayoutElement> flowchart);

}
