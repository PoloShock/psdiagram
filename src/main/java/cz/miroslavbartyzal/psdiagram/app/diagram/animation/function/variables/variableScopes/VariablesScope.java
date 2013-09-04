/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.diagram.animation.function.variables.variableScopes;

import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.FlowchartSegment;
import java.util.HashMap;

/**
 * Toto rozhraní určuje přístup k proměnným a zároveň i jejich úložiště.<br />
 * Pro konkrétní přístupy k proměnným prosím nahlédněte do některé z
 * implementujících tříd.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public interface VariablesScope
{

    /**
     * Nastaví aktuální segment, kterého se budou týkat příští nastavené
     * proměnné.
     *
     * @param segment segment, se kterým mají být příští proměnné svázány
     * @return proměnné a jejich hodnoty, které byly následkem změny segmentu
     * smazány
     */
    public HashMap<FlowchartSegment, HashMap<String, String>> setActualSegment(
            FlowchartSegment segment);

    /**
     * Vrátí aktuálně nastavený segment.
     *
     * @return aktuálně nastavený segment
     */
    public FlowchartSegment getActualSegment();

    /**
     * Vrátí kopii všech proměnných s jejich hodnotami, které jsou aktuálně
     * deklarovány.
     *
     * @return kopie všech proměnných s jejich hodnotami, které jsou aktuálně
     * deklarovány
     */
    public HashMap<String, String> getAllVariables();

    //public void putVariable(String variable, String value);
    /**
     * Vrátí všechny proměnné svázané s daným segmentem.
     *
     * @param segment segment, jehož proměnné chceme získat
     * @return proměnné svázané s aktuálně nastaveným segmentem
     */
    public HashMap<String, String> getSegmentVariables(FlowchartSegment segment);

    /**
     * Metoda aktualizuje všechny proměnné hodnotami z proměnných na vstupu.
     *
     * @param updatedVariables proměnné a jejich hodnoty, které mají být
     * nastaveny
     * @return předchozí hodnoty proměnných a jejich svázaný segment, které byli
     * aktualizací ztraceny
     */
    public HashMap<FlowchartSegment, HashMap<String, String>> updateVariables(
            HashMap<String, String> updatedVariables);

    /**
     * Tato metoda je v principu stejná jako metoda updateVariables, s tím
     * rozdílem, že je umožněno specifikovat i segment, se kterým mají
     * jednotlivé proměnné k aktualizaci být svázány.<br />
     * Tento přístup by neměl být používán, výjimkou je "krok zpět", kde je tato
     * bližší specifikace potřeba.<br />
     * Pro smazání proměnné se uvádí její název a hodnota jako přázdný řetězec.
     *
     * @param hackUpdateVariables proměnné s jejich hodnotami a segmentem se
     * kterými mají být svázány, které mají být nastaveny
     */
    public void hackupdateVariables(
            HashMap<FlowchartSegment, HashMap<String, String>> hackUpdateVariables);

}
