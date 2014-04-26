/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.debug.function.variables.variableScopes;

import cz.miroslavbartyzal.psdiagram.app.flowchart.FlowchartSegment;
import java.util.HashMap;

/**
 * <p>Tato třída představuje blokový přístup k proměnným a zároveň jejich
 * úložiště.</p>
 *
 * <p>Blokový přístup k proměnným se vyznačuje tím, že proměnné jsou platné jen
 * po takovou dobu, co je tok programu přítomen v segmentu (nebo jeho
 * podsegmentech) kde tyto proměnné vznikly. V momentě, kdy se tok průchodu
 * vývojovým diagramem ocitne mimo, v rodičovském segmentu, tyto proměnné
 * zanikají.<br />
 * Tento přístup k proměnným je znám například z programovacího jazyka
 * Java...</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class BlockScopeVariables implements VariablesScope
{

    private HashMap<FlowchartSegment, HashMap<String, String>> segmentVars = new HashMap<>();
    private FlowchartSegment actualSegment;

    /**
     * Nastaví aktuální segment, kterého se budou týkat příští nastavené
     * proměnné.
     *
     * @param segment segment, se kterým mají být příští proměnné svázány
     * @return proměnné a jejich hodnoty, které byly následkem změny segmentu
     * smazány
     */
    @Override
    public HashMap<FlowchartSegment, HashMap<String, String>> setActualSegment(
            FlowchartSegment segment)
    {
        if (actualSegment != null && actualSegment.equals(segment)) {
            return new HashMap<>();
        }
        actualSegment = segment;
        HashMap<FlowchartSegment, HashMap<String, String>> sVars = new HashMap<>();

        sVars.put(segment, getSegmentVariables(segment));
        while (segment.getParentElement() != null) {
            segment = segment.getParentElement().getParentSegment();
            sVars.put(segment, getSegmentVariables(segment));
        }

        HashMap<FlowchartSegment, HashMap<String, String>> erasedVars = new HashMap<>();
        for (FlowchartSegment sgmnt : segmentVars.keySet()) {
            if (!sVars.containsKey(sgmnt)) {
                erasedVars.put(sgmnt, getSegmentVariables(sgmnt));
            }
        }

        segmentVars = sVars;
        return erasedVars;
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
     * Vrátí kopii všech proměnných s jejich hodnotami, které jsou aktuálně
     * deklarovány, nezávisle na segmentech, se kterými jsou svázány.
     *
     * @return kopie všech proměnných s jejich hodnotami, které jsou aktuálně
     * deklarovány
     */
    @Override
    public HashMap<String, String> getAllVariables()
    {
        HashMap<String, String> variables = new HashMap<>();
        for (HashMap<String, String> hashMap : segmentVars.values()) {
            variables.putAll(hashMap);
        }
        return variables;
    }

    /**
     * Metoda aktualizuje všechny proměnné hodnotami z proměnných na vstupu.
     *
     * @param updatedVariables proměnné a jejich hodnoty, které mají být
     * nastaveny
     * @return předchozí hodnoty proměnných a jejich svázaný segment, které byli
     * aktualizací ztraceny
     */
    @Override
    public HashMap<FlowchartSegment, HashMap<String, String>> updateVariables(
            HashMap<String, String> updatedVariables)
    {
        HashMap<FlowchartSegment, HashMap<String, String>> prevVars = new HashMap<>();

        HashMap<String, String> updatedVars = new HashMap<>(updatedVariables);
        for (FlowchartSegment segment : segmentVars.keySet()) {
            for (String var : updatedVars.keySet()) {
                if (segmentVars.get(segment).containsKey(var)) {
                    if (prevVars.get(segment) == null) {
                        prevVars.put(segment, new HashMap<String, String>());
                    }
                    prevVars.get(segment).put(var, segmentVars.get(segment).get(var));

                    if (updatedVars.get(var).equals("")) {
                        // prikaz pro vymazani promenne - pouziva se jen pri kroku zpet
                        segmentVars.get(segment).remove(var);
                    } else {
                        segmentVars.get(segment).put(var, updatedVars.remove(var));
                    }
                }
            }
        }

        if (!updatedVars.isEmpty()) { // byly vytvoreny nove promenne
            if (prevVars.get(actualSegment) == null) {
                prevVars.put(actualSegment, new HashMap<String, String>());
            }
            for (String var : updatedVars.keySet()) {
                if (!updatedVars.get(var).equals("")) {
                    prevVars.get(actualSegment).put(var, "");
                    segmentVars.get(actualSegment).put(var, updatedVars.get(var));
                }
            }
        }

        return prevVars;
    }

    /*
     * @Override
     * public void putVariable(String variable, String value) {
     * for (FlowchartSegment segment : segmentVars.keySet()) {
     * for (String var: segmentVars.get(segment).keySet()) {
     * if (var.equals(variable)) {
     * segmentVars.get(segment).put(variable, value);
     * return;
     * }
     * }
     * }
     * segmentVars.get(actualSegment).put(variable, value);
     * }
     */
    /**
     * Vrátí všechny proměnné svázané s daným segmentem.
     *
     * @param segment segment, jehož proměnné chceme získat
     * @return proměnné svázané s aktuálně nastaveným segmentem
     */
    @Override
    public HashMap<String, String> getSegmentVariables(FlowchartSegment segment)
    {
        HashMap<String, String> variables = segmentVars.get(segment);
        if (variables != null) {
            return variables;
        } else {
            return new HashMap<>();
        }
    }

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
    @Override
    public void hackupdateVariables(
            HashMap<FlowchartSegment, HashMap<String, String>> hackUpdateVariables)
    {
        for (FlowchartSegment segment : hackUpdateVariables.keySet()) {
            for (String var : hackUpdateVariables.get(segment).keySet()) {
                if (segmentVars.get(segment) != null) {
                    if (hackUpdateVariables.get(segment).get(var).equals("")) {
                        // prikaz pro vymazani promenne - pouziva se jen pri kroku zpet
                        segmentVars.get(segment).remove(var);
                    } else {
                        segmentVars.get(segment).put(var, hackUpdateVariables.get(segment).get(var));
                    }
                }
            }
        }
    }

}
