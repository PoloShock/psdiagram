/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.debug.function;

import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import java.awt.font.TextLayout;
import java.awt.geom.Path2D;
import java.util.HashMap;

/**
 * Tato třída zapouzdřuje výsledek zpracování funkce symbolu.<br />
 * Obsahuje instanci třídy Element, která označuje příští symbol k zpracování,
 * dále spojnice, které k tomuto symbolu vedou, text s informací o zpracované
 * funkci právě provedeného symbolu, deskripci segmentu, která má být zvýrazněna
 * a proměnné, které byli provedeným symbolem přidány/změněny.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class FunctionResult
{

    public LayoutElement nextElement; // jaky bude pristi element
    public Path2D[] paths; // jaka cesta povede k pristimu elementu
    public String progressDesc; // popisek progresu aktualniho elementu
    public HashMap<String, String> updatedVariables = new HashMap<>();
    public boolean haltDebug = false;
    //public FlowchartSegment updateSegment;
    public TextLayout segmentDesc;
    //public String[] inputs;

}
