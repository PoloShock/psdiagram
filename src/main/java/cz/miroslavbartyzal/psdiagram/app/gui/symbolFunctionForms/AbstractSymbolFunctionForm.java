/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms;

import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.gui.managers.FlowchartEditManager;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.event.DocumentListener;

/**
 * Tato abstraktní třída představuje obecný formulář pro editaci funkce
 * symbolu.<br />
 * Zajišťuje některé jeho základní, obecné metody, platné i pro všechny
 * navazujcící formuláře.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public abstract class AbstractSymbolFunctionForm extends javax.swing.JPanel implements
        DocumentListener
{

    private FlowchartEditManager flowchartEditManager;
    private LayoutElement element;
    private boolean hasCommandsToSet = true; // indikuje, zda tento formular disponuje textovymi poli pro ovlivneni funkce symbolu - pouziva se pri dispatchingu textu symbolu pri zapnute zalozce funkce

    AbstractSymbolFunctionForm(LayoutElement element, FlowchartEditManager flowchartEditManager)
    {
        this.element = element;
        this.flowchartEditManager = flowchartEditManager;
    }

    abstract void addDocumentListeners();

    abstract void generateValues();

    /*
     * FlowchartEditManager getFlowchartEditManager() {
     * return flowchartEditManager;
     * }
     */
    LayoutElement getElement()
    {
        return element;
    }

    void fireChangeEventToEditManager()
    {
        flowchartEditManager.actionPerformed(new ActionEvent(this, this.hashCode(),
                "edit/defaultsChanged"));
    }

    void trimSize()
    {
        this.setPreferredSize(this.getLayout().minimumLayoutSize(this));
        this.repaint();
    }

    /**
     * Než navratí hodnotu preferované velikosti formuláře z rodičovské třídy,
     * upraví jeho velikost tak, aby byla opravdu optimální (byl zde problém s
     * měnícím se počtem řádků textu radiobuttonů).
     *
     * @return preferovanou velikost formuláře
     */
    @Override
    public Dimension getPreferredSize()
    {
        trimSize(); // pri zviceradkoveni radiobuttonu u foru se rozmer neaktualizoval, timto se to da vyresit..
        return super.getPreferredSize();
    }

    FlowchartEditManager getFlowchartEditManager()
    {
        return flowchartEditManager;
    }

    void setFlowchartEditManager(FlowchartEditManager flowchartEditManager)
    {
        this.flowchartEditManager = flowchartEditManager;
    }

    /**
     * Vratí true, když formulář má textová pole s nastaveními funkce symbolu.
     * Tato funkce je zpravidla využívána pro případnou editaci textu symbolu
     * přímo z označeného plátna vývojového diagramu.
     *
     * @return true, když formulář má textová pole s nastaveními funkce symbolu
     */
    public boolean hasCommandsToSet()
    {
        return hasCommandsToSet;
    }

    void setHasCommandsToSet(boolean hasCommandsToSet)
    {
        this.hasCommandsToSet = hasCommandsToSet;
    }

}
