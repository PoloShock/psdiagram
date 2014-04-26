/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.debug.function.variables;

import cz.miroslavbartyzal.psdiagram.app.gui.treeTable.AbstractTreeTableModel;
import cz.miroslavbartyzal.psdiagram.app.global.RegexFunctions;
import java.util.*;

/**
 * Tato třída představuje jednu proměnnou modelu VariableModel.<br />
 * Proměnné jsou uskupeny ve stromové struktuře a proto proměnná může být buď
 * jednoduchá, tzn. spojení název-hodnota, představující list stromu, nebo se
 * může jednat o pole. V takovém případě se jedná o větev a tato proměnná
 * vlastní ještě své potomky stejné třídy v podobě indexů tohoto pole (které
 * mohou být opět poly atd.).
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class VariableNode implements Comparable<VariableNode>
{

    private static final String highlightStart = "<html><span style=\"font-weight:bold;\">";
    private static final String highlightEnd = "</span></html>";
    private String name;
    private String value;
    private VariableNode[] path;
    private TreeSet<VariableNode> children = new TreeSet<>();
    private boolean highlighted = true;

    protected VariableNode(VariableNode[] path, String name, String value)
    {
        this.path = path;
        this.name = name;
        this.value = value;
        if (path.length == 0) {
            highlighted = false; // korenovy node
        }

        setArrayChildren(null);
    }

    private void setArrayChildren(AbstractTreeTableModel ttm)
    {
        String[] childrenValues = RegexFunctions.getBracketElements(value);
        VariableNode[] pathForChild = getPathForChild();
        TreeMap<Integer, VariableNode> removedNodes = new TreeMap<>();

        // odstraneni prebytecnych potomku
        int i = 0;
        for (Iterator<VariableNode> it = children.iterator(); it.hasNext(); i++) {
            VariableNode child = it.next();
            if (Integer.valueOf(child.getName().substring(1, child.getName().length() - 1)) > childrenValues.length - 1) {
                removedNodes.put(i, child);
                it.remove();
            }
        }
        if (ttm != null && !removedNodes.isEmpty()) {
            ttm.fireTreeNodesRemoved(this, pathForChild, convertIntegers(removedNodes.keySet()),
                    removedNodes.values().toArray(new VariableNode[0]));
        }

        ArrayList<VariableNode> insertedNodes = new ArrayList<>();
        setvalues:
        for (i = 0; i < childrenValues.length; i++) {
            for (VariableNode child : children) {
                if (child.getName().equals("[" + i + "]")) {
                    if (!child.getValue().equals(childrenValues[i])) {
                        child.setValue(childrenValues[i], ttm);
                    }
                    continue setvalues;
                }
            }
            VariableNode vn = new VariableNode(pathForChild, "[" + i + "]", childrenValues[i]);
            children.add(vn);
            insertedNodes.add(vn);
        }
        if (ttm != null && !insertedNodes.isEmpty()) {
            int[] insertedChildrenIndexes = new int[insertedNodes.size()];
            int lastIndex = 0;
            i = 0;
            for (Iterator<VariableNode> it = children.iterator(); it.hasNext(); i++) {
                if (insertedNodes.contains(it.next())) {
                    insertedChildrenIndexes[lastIndex] = i;
                    lastIndex++;
                }
            }
            ttm.fireTreeNodesInserted(this, pathForChild, insertedChildrenIndexes,
                    insertedNodes.toArray(new VariableNode[0]));
        }
    }

    private VariableNode[] getPathForChild()
    {
        VariableNode[] pathForChild = new VariableNode[path.length + 1];
        System.arraycopy(path, 0, pathForChild, 0, path.length);
        pathForChild[path.length] = this;
        return pathForChild;
    }

    /*
     * private void setHighlighted(boolean highlighted) {
     * this.highlighted = highlighted;
     * if (highlighted) { // zvyrazneni musi byt i vsichni predci
     * if (parent != null && !parent.isHighlighted()) { // zabraneni zbytecne
     * rekurze navic pri prvnim vytvareni struktury
     * parent.setHighlighted(highlighted);
     * }
     * } else { // odebrani zvyrazneni i vsem potomkum
     * for (VariableNode child: children) {
     * if (child.isHighlighted()) {
     * child.setHighlighted(highlighted);
     * }
     * }
     * }
     * }
     */
    private String setValue(String value, AbstractTreeTableModel ttm)
    {
        String prevValue = this.value;
        this.value = value;
        highlighted = true;
        ttm.fireTreeNodesChanged(this, path, new int[]{path[path.length - 1].getChildIndex(this)},
                new VariableNode[]{this});
        setArrayChildren(ttm);
        return prevValue;
    }

    private boolean isHighlighted()
    {
        return highlighted;
    }

    private int getChildIndex(VariableNode child)
    {
        int i = 0;
        for (Iterator<VariableNode> it = children.iterator(); it.hasNext(); i++) {
            if (it.next().equals(child)) {
                return i;
            }
        }
        return -1;
    }

    private static int[] convertIntegers(Collection<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++) {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }

    protected HashMap<String, String> updateVariables(HashMap<String, String> variablesToUpdate,
            AbstractTreeTableModel ttm)
    {
        HashMap<String, String> previousValues = new HashMap<>();

        TreeMap<Integer, VariableNode> nodesToRemove = new TreeMap<>();
        ArrayList<VariableNode> nodesToInsert = new ArrayList<>();

        VariableNode[] pathForChild = getPathForChild();
        updateVars:
        for (String varName : variablesToUpdate.keySet()) {
            int i = 0;
            for (Iterator<VariableNode> it = children.iterator(); it.hasNext(); i++) {
                VariableNode child = it.next();
                if (child.getName().equals(varName)) {
                    if (variablesToUpdate.get(varName).equals("")) { // smazani promenne
                        previousValues.put(varName, child.getValue());
                        nodesToRemove.put(i, child);
                    } else {
                        previousValues.put(varName, child.setValue(variablesToUpdate.get(varName),
                                ttm));
                    }
                    continue updateVars;
                }
            }
            // nenasli jsme odpovidajici existujici promennou
            if (!variablesToUpdate.get(varName).equals("")) {
                previousValues.put(varName, "");
                nodesToInsert.add(new VariableNode(pathForChild, varName, variablesToUpdate.get(
                        varName)));
            }
        }

        if (!nodesToRemove.isEmpty()) {
            for (VariableNode vn : nodesToRemove.values()) {
                children.remove(vn);
            }
            ttm.fireTreeNodesRemoved(this, pathForChild, convertIntegers(nodesToRemove.keySet()),
                    nodesToRemove.values().toArray(new VariableNode[0]));
        }
        if (!nodesToInsert.isEmpty()) {
            children.addAll(nodesToInsert);
            int[] insertedChildrenIndexes = new int[nodesToInsert.size()];
            int lastIndex = 0;
            int i = 0;
            for (Iterator<VariableNode> it = children.iterator(); it.hasNext(); i++) {
                if (nodesToInsert.contains(it.next())) {
                    insertedChildrenIndexes[lastIndex] = i;
                    lastIndex++;
                }
            }
            ttm.fireTreeNodesInserted(this, pathForChild, insertedChildrenIndexes,
                    nodesToInsert.toArray(new VariableNode[0]));
        }

        return previousValues;
    }

    protected void setHighlightFalse(AbstractTreeTableModel ttm)
    {
        if (highlighted) {
            highlighted = false;
            ttm.fireTreeNodesChanged(this, path,
                    new int[]{path[path.length - 1].getChildIndex(this)}, new VariableNode[]{this});
        }
        for (VariableNode child : children) {
            if (child.isHighlighted()) {
                child.setHighlightFalse(ttm);
            }
        }
    }

    protected void clearChildren()
    {
        children.clear();
    }

    /**
     * Metoda vrací potomka na určeném indexu. Potomkem se rozumí záznam
     * konkrétní proměnné.
     *
     * @param index index proměnné - potomka, kterého si přejeme získat
     * @return potomek na určeném indexu
     */
    public VariableNode getChild(int index)
    {
        Iterator<VariableNode> it = children.iterator();
        int i = 0;
        while (i < index) {
            it.next();
            i++;
        }
        return it.next();
    }

    /**
     * Vrací celkový počet potomků.
     *
     * @return celkový počet potomků
     */
    public int getChildCount()
    {
        return children.size();
    }

    /**
     * Vrací název proměnné.
     *
     * @return název proměnné
     */
    public String getName()
    {
        if (highlighted) {
            return highlightStart + name + highlightEnd;
        } else {
            return name;
        }
    }

    /**
     * Vrací hodnotu proměnné.
     *
     * @return hodnota proměnné
     */
    public String getValue()
    {
        if (highlighted) {
            return highlightStart + value + highlightEnd;
        } else {
            return value;
        }
    }

    @Override
    public String toString()
    {
        return getName();
    }

    @Override
    public int compareTo(VariableNode t)
    {
        return name.compareTo(t.name);
    }

}
