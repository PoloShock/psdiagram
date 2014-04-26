/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.debug.function.variables.variableScopes;

import cz.miroslavbartyzal.psdiagram.app.flowchart.FlowchartSegment;
import java.util.HashMap;

/**
 * <p>Tato třída představuje globální přístup k proměnným a zároveň jejich
 * úložiště.</p>
 *
 * <p>Globální přístup k proměnným se vyznačuje tím, že proměnné jsou po
 * vlastním vytvoření platné již po celou dobu spuštění průchodu programu,
 * nezávisle na segmentu, ve kterém byli vytvořeny.<br />
 * Tento přístup k proměnným je znám například z programovacího jazyka
 * Pascal...</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class GlobalScopeVariables implements VariablesScope
{

    private HashMap<String, String> variables = new HashMap<>();
    private FlowchartSegment actualSegment;

    /**
     * Vrátí kopii všech proměnných s jejich hodnotami, které jsou aktuálně
     * deklarovány.
     *
     * @return kopie všech proměnných s jejich hodnotami, které jsou aktuálně
     * deklarovány
     */
    @Override
    public HashMap<String, String> getAllVariables()
    {
        return new HashMap<>(variables);
    }

    /**
     * Metoda aktualizuje všechny proměnné hodnotami z proměnných na vstupu.
     *
     * @param updatedVariables proměnné a jejich hodnoty, které mají být
     * nastaveny
     * @return Předchozí hodnoty proměnných, které byli aktualizací ztraceny.
     * Segment nehraje roli.
     */
    @Override
    public HashMap<FlowchartSegment, HashMap<String, String>> updateVariables(
            HashMap<String, String> updatedVariables)
    {
        HashMap<FlowchartSegment, HashMap<String, String>> prevVars = new HashMap<>();
        prevVars.put(actualSegment, new HashMap<String, String>());

        for (String var : updatedVariables.keySet()) {
            if (variables.get(var) == null) {
                prevVars.get(actualSegment).put(var, "");
            } else {
                prevVars.get(actualSegment).put(var, variables.get(var));
            }

            if (updatedVariables.get(var).equals("")) {
                // prikaz pro vymazani promenne - pouziva se jen pri kroku zpet
                variables.remove(var);
            } else {
                variables.put(var, updatedVariables.get(var));
            }
        }
        return prevVars;
    }

    /*
     * @Override
     * public void putVariable(String variable, String value) {
     * variables.put(variable, value);
     * }
     */
    /**
     * Nastaví aktuální segment, kterého se mají týkat příští nastavené
     * proměnné. V tomto přístupu se jedná o bezpředmětnou položku, pouze
     * informačního charakteru.
     *
     * @param segment segment, se kterým mají být příští proměnné svázány
     * @return vždy prázdná mapa
     */
    @Override
    public HashMap<FlowchartSegment, HashMap<String, String>> setActualSegment(
            FlowchartSegment segment)
    {
        actualSegment = segment;
        return new HashMap<>();
    }

    /**
     * Vrátí aktuálně nastavený segment.
     *
     * @return aktuálně nastavený segment
     */
    @Override
    public FlowchartSegment getActualSegment()
    {
        return actualSegment;

    }

    /**
     * Tato metoda volá jen metodu getAllVariables(), je tedy totožná.
     *
     * @param segment může být null
     * @return kopie všech proměnných s jejich hodnotami, které jsou aktuálně
     * deklarovány
     */
    @Override
    public HashMap<String, String> getSegmentVariables(FlowchartSegment segment)
    {
        return getAllVariables();
    }

    /**
     * Tato metoda je v principu stejná jako metoda updateVariables, s tím
     * rozdílem, že je umožněno specifikovat i segment, se kterým mají
     * jednotlivé proměnné k aktualizaci být svázány. V této třídě se segmenty k
     * proměnným nevážou, proto na vstupu uvedené segmenty nehrají žádnou
     * roli.<br />
     * Pro smazání proměnné se uvádí její název a hodnota jako přázdný řetězec.
     *
     * @param hackUpdateVariables proměnné s jejich hodnotami, které mají být
     * nastaveny
     */
    @Override
    public void hackupdateVariables(
            HashMap<FlowchartSegment, HashMap<String, String>> hackUpdateVariables)
    {
        for (HashMap<String, String> vars : hackUpdateVariables.values()) {
            for (String var : vars.keySet()) {
                if (vars.get(var).equals("")) {
                    // prikaz pro vymazani promenne - pouziva se jen pri kroku zpet
                    variables.remove(var);
                } else {
                    variables.put(var, vars.get(var));
                }
            }
        }
    }

}
