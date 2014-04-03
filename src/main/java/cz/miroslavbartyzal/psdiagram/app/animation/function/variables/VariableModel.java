/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.animation.function.variables;

import cz.miroslavbartyzal.psdiagram.app.gui.treeTable.AbstractTreeTableModel;
import cz.miroslavbartyzal.psdiagram.app.gui.treeTable.TreeTableModel;
import java.util.HashMap;

/**
 * Model pro instanci třídy TreeTable, reprezentující aktuální stav všech
 * proměnných. Použita je stromová struktura.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class VariableModel extends AbstractTreeTableModel
{

    private final String[] CNAMES = {"Název", "Hodnota"};
    private VariableNode myRoot;
    private HashMap<String, String> lastUpdateVars = new HashMap<>();

    /**
     * Konstruktor bez parametrů, zajišťující základní inicializaci tohoto
     * modelu.
     */
    public VariableModel()
    {
        super(null);
        myRoot = new VariableNode(new VariableNode[0], "root", "");
        super.root = myRoot;
    }

    /**
     * Metoda zajistí aktualizaci proměnných, podle zadaného parametru.
     *
     * @param variablesToUpdate proměnné s jejich hodnotami, které mají sloužit
     * pro aktualizaci těch stávajících
     * @return proměnné s jejich bývalými hodnotami, které byli v důsledku
     * aktualizace změněny/smazány
     */
    public HashMap<String, String> updateVariables(HashMap<String, String> variablesToUpdate)
    {
        lastUpdateVars = new HashMap<>(variablesToUpdate);

        myRoot.setHighlightFalse(this);
        HashMap<String, String> previousValues = myRoot.updateVariables(lastUpdateVars, this);
        //fireTreeStructureChanged(this, new Object[]{myRoot}, null, null);
        return previousValues;
    }

    /**
     * Metoda smaže všechny zobrazované proměnné.
     */
    public void clearVariables()
    {
        myRoot.clearChildren();
        fireTreeStructureChanged(this, new Object[]{myRoot}, null, null);

        /*
         * HashMap<String, String> u = new HashMap<>();
         * u.put("a", "100");
         * myRoot.updateVariables(u, this);
         * fireTreeNodesInserted(this, new Object[]{myRoot}, new int[]{0}, new
         * Object[]{myRoot.getChild(0)});
         */
    }

    /**
     * Vrací poslední proměnné s jejich hodnotami, které byli použity pro
     * aktualizaci tohto modelu.
     *
     * @return poslední proměnné s jejich hodnotami, které byli použity pro
     * aktualizaci tohto modelu
     */
    public HashMap<String, String> getLastUpdateVars()
    {
        return lastUpdateVars;
    }

    @Override
    public int getColumnCount()
    {
        return CNAMES.length;
    }

    @Override
    public String getColumnName(int column)
    {
        return CNAMES[column];
    }

    @Override
    public Object getValueAt(Object node, int column)
    {
        if (column == 0) {
            return ((VariableNode) node).getName();
        } else {
            return ((VariableNode) node).getValue();
        }
    }

    @Override
    public Object getChild(Object node, int i)
    {
        return ((VariableNode) node).getChild(i);
    }

    @Override
    public int getChildCount(Object node)
    {
        return ((VariableNode) node).getChildCount();
    }

    @Override
    public Class getColumnClass(int col)
    {
        return col == 0 ? TreeTableModel.class : Object.class;
    }

}
