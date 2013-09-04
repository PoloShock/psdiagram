/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.diagram.animation.function;

import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.FlowchartSegment;
import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.symbols.Symbol;
import java.awt.font.TextLayout;
import java.awt.geom.Path2D;
import java.util.HashMap;

/**
 * <p>Tato třída zapouzdřuje veškeré údaje, potřebné pro úspěšné provedení
 * jednoho kroku zpět při průchodu vývojovým diagramem.<br />
 * Toho je docíleno navrácením předchozích stavů proměnných, neboli dosazením
 * uložených stavů, které obsahuje právě tato zapouzdřující třída.</p>
 *
 * <p>Aby bylo možno uložit co nejvíce takovýchto kroků zpět, musí být jeden
 * krok z paměťového hlediska co nejmenší. Toho je docíleno tak, že nejsou
 * uloženy všechny proměnné, ale jen ty, které v následujícím kroku byli
 * změněny.</p>
 *
 * <p>Krok zpět tedy obsahuje tyto informace:<br />
 * - element, jehož funkce smybolu byla provedena (údaj slouží pro zvýraznění
 * směrem zpět)<br />
 * - spojnice, které mají být zvýrazněny směrem zpět<br />
 * - předešlý text symbolu s informací o jeho provedené funkci<br />
 * - proměnné, které se po provedení funkce symbolu změnily<br />
 * - proměnné, které byly zvýrazněny před tím, než byla provedena funkce tohoto
 * symbolu</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class StepBack
{

    public LayoutElement prevElement;
    public Path2D[] paths; // jake cesty povedou od tohoto elementu
    public String prevProgressDesc; // predchozi popisek progresu symbolu
    public TextLayout segmentDesc;
    public HashMap<FlowchartSegment, HashMap<String, String>> prevVariables = new HashMap<>();
    public HashMap<String, String> prevPrevDisplayVariables = new HashMap<>();
    public HashMap<String, String> prevDisplayVariables = new HashMap<>();
    public HashMap<Symbol, String> prevForValues = new HashMap<>();

}
