/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms;

import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.global.RegexFunctions;
import cz.miroslavbartyzal.psdiagram.app.gui.managers.FlowchartEditManager;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.AbstractFilter;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

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
    private final LayoutElement element;
    private boolean hasCommandsToSet = true; // indikuje, zda tento formular disponuje textovymi poli pro ovlivneni funkce symbolu - pouziva se pri dispatchingu textu symbolu pri zapnute zalozce funkce

    AbstractSymbolFunctionForm(LayoutElement element, FlowchartEditManager flowchartEditManager)
    {
        this.element = element;
        this.flowchartEditManager = flowchartEditManager;
    }

    abstract void addDocumentListeners();

    abstract void generateValues();

    public abstract JTextField getJTextFieldToDispatchKeyEventsAt();

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

    public static void enhanceWithUndoRedoCapability(final JTextComponent textcomp)
    {
        final UndoManager undo = new UndoManager();
        undo.setLimit(1000);
        // Listen for undo and redo events
        textcomp.getDocument().addUndoableEditListener(new UndoableEditListener()
        {
            @Override
            public void undoableEditHappened(UndoableEditEvent evt)
            {
                undo.addEdit(evt.getEdit());
            }
        });
        // Create an undo action and add it to the text component
        textcomp.getActionMap().put("Undo",
                new AbstractAction("Undo")
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        try {
                            if (undo.canUndo()) {
                                undo.undo();
                                DocumentFilter documentFilter = ((AbstractDocument) textcomp.getDocument()).getDocumentFilter();
                                if (documentFilter != null && documentFilter instanceof AbstractFilter) {
                                    ((AbstractFilter) documentFilter).parseInputAndUpdateGUI();
                                }
                            }
                        } catch (CannotUndoException e) {
                        }
                    }
                });
        // Bind the undo action to ctl-Z
        textcomp.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        // Create a redo action and add it to the text component
        textcomp.getActionMap().put("Redo",
                new AbstractAction("Redo")
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        try {
                            if (undo.canRedo()) {
                                undo.redo();
                                DocumentFilter documentFilter = ((AbstractDocument) textcomp.getDocument()).getDocumentFilter();
                                if (documentFilter != null && documentFilter instanceof AbstractFilter) {
                                    ((AbstractFilter) documentFilter).parseInputAndUpdateGUI();
                                }
                            }
                        } catch (CannotRedoException e) {
                        }
                    }
                });
        // Bind the redo action to ctl-Y
        textcomp.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
    }

    /**
     *
     * @param command a | b
     * @return a || b
     */
    public static String convertFromPSDToJSCommands(String command)
    {
        if (command == null) {
            return command;
        }

        // v uvozovkach si nesmim vsimat niceho
        String[] commandWithoutQ = RegexFunctions.splitString(command, "\"([^\"\\\\]|\\\\.)*\"?");
        for (int i = 0; i < commandWithoutQ.length; i += 2) {
            // convert doublechar operators to their unicode equivalent and back so the chars that were part of the doublechars are not misclasified
            commandWithoutQ[i] = commandWithoutQ[i].replace("!=", "≠").replace(">=", "≥").replace(
                    "<=", "≤").replace("=", "==").replace("≠", "!=").replace("≥", ">=").replace("≤",
                            "<=");
        }

        String cmnd = "";
        for (String commandPart : commandWithoutQ) {
            cmnd += commandPart;
        }

        return cmnd;
    }

    /**
     *
     * @param command a || b
     * @return a | b
     */
    public static String convertFromJSToPSDCommands(String command)
    {
        if (command == null) {
            return command;
        }

        // v uvozovkach si nesmim vsimat niceho
        String[] commandWithoutQ = RegexFunctions.splitString(command, "\"([^\"\\\\]|\\\\.)*\"?");
        for (int i = 0; i < commandWithoutQ.length; i += 2) {
            commandWithoutQ[i] = commandWithoutQ[i].replace("==", "=");
        }

        String cmnd = "";
        for (String commandPart : commandWithoutQ) {
            cmnd += commandPart;
        }

        return cmnd;
    }

    /**
     *
     * @param command a || b
     * @return a | b
     */
    public static String convertToPSDDisplayCommands(String command)
    {
        if (command == null) {
            return command;
        }

        // v uvozovkach si nesmim vsimat niceho
        String[] commandWithoutQ = RegexFunctions.splitString(command, "\"([^\"\\\\]|\\\\.)*\"?");
        for (int i = 0; i < commandWithoutQ.length; i += 2) {
            commandWithoutQ[i] = commandWithoutQ[i].replace("!=", "≠").replace("!", "¬").replace(
                    ">=", "≥").replace("<=", "≤");
        }

        String cmnd = "";
        for (String commandPart : commandWithoutQ) {
            cmnd += commandPart;
        }

        return cmnd;
    }

}
