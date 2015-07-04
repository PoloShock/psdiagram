/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.flowchart.symbols;

import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.gui.managers.FlowchartEditManager;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.AbstractSymbolFunctionForm;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.BooleanValueFilter;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.ConstantFilter;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.NoArrayVariableFilter;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.NumericValueFilter;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.ValueFilter;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.VariableFilter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tato třída výčtového typu reprezentuje všechny symboly, které je možné
 * uživatelsky přidat do vývojového diagramu.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public enum EnumSymbol
{

    PROCESS
            {
                @Override
                public Symbol getInstance(String value)
                {
                    return new Process(value);
                }

                @Override
                public String getToolTipText()
                {
                    return "Zpracování";
                }

                @Override
                public AbstractSymbolFunctionForm getFunctionFormInstance(LayoutElement element,
                        FlowchartEditManager flowchartEditManager)
                {
                    return new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Process(
                            element, flowchartEditManager);
                }

                @Override
                public Class<? extends Symbol> getSymbolClass()
                {
                    return Process.class;
                }

                @Override
                public boolean areCommandsValid(Symbol symbol)
                {
                    if (!(symbol instanceof Process)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of Process symbol.");
                    }

                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands != null && !commands.isEmpty()) {
                        for (Map.Entry<String, String> entrySet : commands.entrySet()) {
                            String key = entrySet.getKey();
                            String value = AbstractSymbolFunctionForm.convertFromJSToPSDCommands(
                                    entrySet.getValue());
                            if (value.isEmpty()) {
                                continue;
                            }
                            switch (key) {
                                case "var":
                                    if (!VariableFilter.isValid(value)) {
                                        return false;
                                    }
                                    break;
                                case "value":
                                    if (!ValueFilter.isValid(value)) {
                                        return false;
                                    }
                                    break;
                                default:
                                    throw new IllegalArgumentException(
                                            "Unknown symbol command key '" + key + "'.");
                            }
                        }
                    }
                    return true;
                }

                @Override
                public boolean areAllCommandsPresent(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof Process)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of Process symbol.");
                    }
                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands == null || commands.isEmpty()) {
                        return false;
                    }

                    return commands.get("var") != null && !commands.get("var").matches("\\s*")
                    && commands.get("value") != null && !commands.get("value").matches("\\s*");
                }
            },
    IO
            {
                @Override
                public Symbol getInstance(String value)
                {
                    return new IO(value);
                }

                @Override
                public String getToolTipText()
                {
                    return "Vstup/Výstup";
                }

                @Override
                public AbstractSymbolFunctionForm getFunctionFormInstance(LayoutElement element,
                        FlowchartEditManager flowchartEditManager)
                {
                    return new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.IO(element,
                            flowchartEditManager);
                }

                @Override
                public Class<? extends Symbol> getSymbolClass()
                {
                    return IO.class;
                }

                @Override
                public boolean areCommandsValid(Symbol symbol)
                {
                    if (!(symbol instanceof IO)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of IO symbol.");
                    }

                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands != null && !commands.isEmpty()) {
                        for (Map.Entry<String, String> entrySet : commands.entrySet()) {
                            String key = entrySet.getKey();
                            String value = AbstractSymbolFunctionForm.convertFromJSToPSDCommands(
                                    entrySet.getValue());
                            if (value.isEmpty()) {
                                continue;
                            }
                            switch (key) {
                                case "var":
                                    if (!VariableFilter.isValid(value)) {
                                        return false;
                                    }
                                    break;
                                case "value":
                                    if (!ValueFilter.isValid(value)) {
                                        return false;
                                    }
                                    break;
                                default:
                                    throw new IllegalArgumentException(
                                            "Unknown symbol command key '" + key + "'.");
                            }
                        }
                    }
                    return true;
                }

                @Override
                public boolean areAllCommandsPresent(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof IO)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of IO symbol.");
                    }
                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands == null || commands.isEmpty()) {
                        return false;
                    }

                    return commands.get("var") != null && !commands.get("var").matches("\\s*")
                    || commands.get("value") != null && !commands.get("value").matches("\\s*");
                }
            },
    DECISION
            {
                @Override
                public Symbol getInstance(String value)
                {
                    return new Decision(value);
                }

                @Override
                public String getToolTipText()
                {
                    return "Rozhodování - podmínka";
                }

                @Override
                public AbstractSymbolFunctionForm getFunctionFormInstance(LayoutElement element,
                        FlowchartEditManager flowchartEditManager)
                {
                    return new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Decision(
                            element, flowchartEditManager);
                }

                @Override
                public Class<? extends Symbol> getSymbolClass()
                {
                    return Decision.class;
                }

                @Override
                public boolean areCommandsValid(Symbol symbol)
                {
                    if (!(symbol instanceof Decision)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of Decision symbol.");
                    }

                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands != null && !commands.isEmpty()) {
                        for (Map.Entry<String, String> entrySet : commands.entrySet()) {
                            String key = entrySet.getKey();
                            String value = AbstractSymbolFunctionForm.convertFromJSToPSDCommands(
                                    entrySet.getValue());
                            if (value.isEmpty()) {
                                continue;
                            }
                            switch (key) {
                                case "condition":
                                    if (!BooleanValueFilter.isValid(value)) {
                                        return false;
                                    }
                                    break;
                                default:
                                    throw new IllegalArgumentException(
                                            "Unknown symbol command key '" + key + "'.");
                            }
                        }
                    }
                    return true;
                }

                @Override
                public boolean areAllCommandsPresent(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof Decision)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of Decision symbol.");
                    }
                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands == null || commands.isEmpty()) {
                        return false;
                    }

                    return commands.get("condition") != null && !commands.get("condition").matches(
                            "\\s*");
                }
            },
    SWITCH
            {
                @Override
                public Symbol getInstance(String value)
                {
                    return new Switch(value);
                }

                @Override
                public String getToolTipText()
                {
                    return "Rozhodování - vícenásobné(switch)";
                }

                @Override
                public AbstractSymbolFunctionForm getFunctionFormInstance(LayoutElement element,
                        FlowchartEditManager flowchartEditManager)
                {
                    return new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Switch(
                            element, flowchartEditManager);
                }

                @Override
                public Class<? extends Symbol> getSymbolClass()
                {
                    return Switch.class;
                }

                @Override
                public boolean areCommandsValid(Symbol symbol)
                {
                    if (!(symbol instanceof Switch)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of Switch symbol.");
                    }

                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands != null && !commands.isEmpty()) {
                        for (Map.Entry<String, String> entrySet : commands.entrySet()) {
                            String key = entrySet.getKey();
                            String value = AbstractSymbolFunctionForm.convertFromJSToPSDCommands(
                                    entrySet.getValue());
                            if (value.isEmpty()) {
                                continue;
                            }
                            switch (key) {
                                case "conditionVar":
                                    if (!VariableFilter.isValid(value)) {
                                        return false;
                                    }
                                    break;
                                default:
                                    if (!ConstantFilter.isValid(value)) {
                                        return false;
                                    }
                                    break;
                            }
                        }
                    }
                    return true;
                }

                @Override
                public boolean areAllCommandsPresent(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof Switch)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of Switch symbol.");
                    }
                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands == null || commands.isEmpty()) {
                        return false;
                    }

                    if (commands.size() - 1 == element.getInnerSegmentsCount() - 1
                    && commands.get("conditionVar") != null && !commands.get("conditionVar").matches(
                            "\\s*")) {
                        for (int i = 1; i < element.getInnerSegmentsCount(); i++) {
                            if (commands.get("" + i) == null || commands.get("" + i).matches("\\s*")) {
                                return false;
                            }
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            },
    FOR
            {
                @Override
                public Symbol getInstance(String value)
                {
                    return new For(value);
                }

                @Override
                public String getToolTipText()
                {
                    return "Příprava - cyklus s pevným počtem opakování";
                }

                @Override
                public AbstractSymbolFunctionForm getFunctionFormInstance(LayoutElement element,
                        FlowchartEditManager flowchartEditManager)
                {
                    return new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.For(element,
                            flowchartEditManager);
                }

                @Override
                public Class<? extends Symbol> getSymbolClass()
                {
                    return For.class;
                }

                @Override
                public boolean areCommandsValid(Symbol symbol)
                {
                    if (!(symbol instanceof For)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of For symbol.");
                    }

                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands != null && !commands.isEmpty()) {
                        for (Map.Entry<String, String> entrySet : commands.entrySet()) {
                            String key = entrySet.getKey();
                            String value = AbstractSymbolFunctionForm.convertFromJSToPSDCommands(
                                    entrySet.getValue());
                            if (value.isEmpty()) {
                                continue;
                            }
                            switch (key) {
                                case "var":
                                    if (!NoArrayVariableFilter.isValid(value)) {
                                        return false;
                                    }
                                    break;
                                case "from":
                                    if (!NumericValueFilter.isValid(value)) {
                                        return false;
                                    }
                                    break;
                                case "to":
                                    if (!NumericValueFilter.isValid(value)) {
                                        return false;
                                    }
                                    break;
                                case "inc":
                                    if (!NumericValueFilter.isValid(value)) {
                                        return false;
                                    }
                                    break;
                                case "array":
                                    if (!VariableFilter.isValid(value)) {
                                        return false;
                                    }
                                    break;
                                default:
                                    throw new IllegalArgumentException(
                                            "Unknown symbol command key '" + key + "'.");
                            }
                        }
                    }
                    return true;
                }

                @Override
                public boolean areAllCommandsPresent(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof For)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of For symbol.");
                    }
                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands == null || commands.isEmpty()) {
                        return false;
                    }

                    return commands.get("var") != null && !commands.get("var").matches("\\s*")
                    && ((commands.get("from") != null && !commands.get("from").matches("\\s*")
                    && commands.get("to") != null && !commands.get("to").matches("\\s*")
                    && commands.get("inc") != null && !commands.get("inc").matches("\\s*"))
                    || (commands.get("array") != null && !commands.get("array").matches("\\s*")));
                }
            },
    LOOPCONDITIONUP
            {
                @Override
                public Symbol getInstance(String value)
                {
                    return new LoopStart(value, true);
                }

                @Override
                public String getToolTipText()
                {
                    return "Cyklus s podmínkou na začátku";
                }

                @Override
                public AbstractSymbolFunctionForm getFunctionFormInstance(LayoutElement element,
                        FlowchartEditManager flowchartEditManager)
                {
                    if (element.getSymbol().isOverHang()) {
                        return new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.LoopStart(
                                element, flowchartEditManager);
                    } else {
                        return null; // pripadne jen popis
                    }
                }

                @Override
                public Class<? extends Symbol> getSymbolClass()
                {
                    return LoopStart.class;
                }

                @Override
                public boolean areCommandsValid(Symbol symbol)
                {
                    if (!(symbol instanceof LoopStart)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of LoopStart symbol.");
                    }

                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands != null && !commands.isEmpty()) {
                        for (Map.Entry<String, String> entrySet : commands.entrySet()) {
                            String key = entrySet.getKey();
                            String value = AbstractSymbolFunctionForm.convertFromJSToPSDCommands(
                                    entrySet.getValue());
                            if (value.isEmpty()) {
                                continue;
                            }
                            switch (key) {
                                case "condition":
                                    if (!BooleanValueFilter.isValid(value)) {
                                        return false;
                                    }
                                    break;
                                default:
                                    throw new IllegalArgumentException(
                                            "Unknown symbol command key '" + key + "'.");
                            }
                        }
                    }
                    return true;
                }

                @Override
                public boolean areAllCommandsPresent(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof LoopStart)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of LoopStart symbol.");
                    }
                    if (!element.getSymbol().isOverHang()) {
                        return true; // symbol ma podminku dole -> zde neni co kontrolovat
                    }
                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands == null || commands.isEmpty()) {
                        return false;
                    }

                    return commands.get("condition") != null && !commands.get("condition").matches(
                            "\\s*");
                }
            },
    LOOPCONDITIONDOWN
            {
                @Override
                public Symbol getInstance(String value)
                {
                    return new LoopStart(value, false);
                }

                @Override
                public String getToolTipText()
                {
                    return "Cyklus s podmínkou na konci";
                }

                @Override
                public AbstractSymbolFunctionForm getFunctionFormInstance(LayoutElement element,
                        FlowchartEditManager flowchartEditManager)
                {
                    for (int i = element.getParentSegment().indexOfElement(element) - 1; i >= 0; i--) {
                        LayoutElement pairElement = element.getParentSegment().getElement(i);
                        if (pairElement.getSymbol() instanceof LoopStart) {
                            if (!pairElement.getSymbol().isOverHang()) {
                                return new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.LoopEnd(
                                        element,
                                        flowchartEditManager);
                            } else {
                                return null; // pripadne jen popis
                            }
                        }
                    }
                    return null;
                }

                @Override
                public Class<? extends Symbol> getSymbolClass()
                {
                    return LoopEnd.class; // zde pozor
                }

                @Override
                public boolean areCommandsValid(Symbol symbol)
                {
                    if (!(symbol instanceof LoopEnd)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of LoopEnd symbol.");
                    }

                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands != null && !commands.isEmpty()) {
                        for (Map.Entry<String, String> entrySet : commands.entrySet()) {
                            String key = entrySet.getKey();
                            String value = AbstractSymbolFunctionForm.convertFromJSToPSDCommands(
                                    entrySet.getValue());
                            if (value.isEmpty()) {
                                continue;
                            }
                            switch (key) {
                                case "condition":
                                    if (!BooleanValueFilter.isValid(value)) {
                                        return false;
                                    }
                                    break;
                                default:
                                    throw new IllegalArgumentException(
                                            "Unknown symbol command key '" + key + "'.");
                            }
                        }
                    }
                    return true;
                }

                @Override
                public boolean areAllCommandsPresent(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof LoopEnd)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of LoopEnd symbol.");
                    }
                    for (int i = element.getParentSegment().indexOfElement(element) - 1; i >= 0; i--) {
                        LayoutElement pairElement = element.getParentSegment().getElement(i);
                        if (pairElement.getSymbol() instanceof LoopStart) {
                            if (pairElement.getSymbol().isOverHang()) {
                                return true;  // symbol ma podminku nahore -> zde neni co kontrolovat
                            }
                        }
                    }
                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands == null || commands.isEmpty()) {
                        return false;
                    }

                    return commands.get("condition") != null && !commands.get("condition").matches(
                            "\\s*");
                }
            },
    COMMENT
            {
                @Override
                public Symbol getInstance(String value)
                {
                    return new Comment(value, false); // je treba navic rozhodnout, jedna-li se o parovy komentar, ci nikoliv. Defaultni je neparovy.
                }

                @Override
                public String getToolTipText()
                {
                    return "Anotace - komentář";
                }

                @Override
                public AbstractSymbolFunctionForm getFunctionFormInstance(LayoutElement element,
                        FlowchartEditManager flowchartEditManager)
                {
                    return null;
                }

                @Override
                public Class<? extends Symbol> getSymbolClass()
                {
                    return Comment.class;
                }

                @Override
                public boolean areCommandsValid(Symbol symbol)
                {
                    if (!(symbol instanceof Comment)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of Comment symbol.");
                    }

                    return true;
                }

                @Override
                public boolean areAllCommandsPresent(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof Comment)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of Comment symbol.");
                    }

                    return true;
                }
            },
    SUBROUTINE
            {
                @Override
                public Symbol getInstance(String value)
                {
                    return new SubRoutine(value);
                }

                @Override
                public String getToolTipText()
                {
                    return "Předdefinované zpracování";
                }

                @Override
                public AbstractSymbolFunctionForm getFunctionFormInstance(LayoutElement element,
                        FlowchartEditManager flowchartEditManager)
                {
                    return null;
                }

                @Override
                public Class<? extends Symbol> getSymbolClass()
                {
                    return SubRoutine.class;
                }

                @Override
                public boolean areCommandsValid(Symbol symbol)
                {
                    if (!(symbol instanceof SubRoutine)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of SubRoutine symbol.");
                    }

                    return true;
                }

                @Override
                public boolean areAllCommandsPresent(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof SubRoutine)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of SubRoutine symbol.");
                    }

                    return true;
                }
            },
    GOTO
            {
                @Override
                public Symbol getInstance(String value)
                {
                    return new Goto(value);
                }

                @Override
                public String getToolTipText()
                {
                    return "Spojka - goto, break, continue";
                }

                @Override
                public AbstractSymbolFunctionForm getFunctionFormInstance(LayoutElement element,
                        FlowchartEditManager flowchartEditManager)
                {
                    return new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Goto(
                            element, flowchartEditManager);
                }

                @Override
                public Class<? extends Symbol> getSymbolClass()
                {
                    return Goto.class;
                }

                @Override
                public boolean areCommandsValid(Symbol symbol)
                {
                    if (!(symbol instanceof Goto)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of Goto symbol.");
                    }

                    return true;
                }

                @Override
                public boolean areAllCommandsPresent(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof Goto)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of Goto symbol.");
                    }

                    return true;
                }
            },
    GOTOLABEL
            {
                @Override
                public Symbol getInstance(String value)
                {
                    return new GotoLabel(value);
                }

                @Override
                public String getToolTipText()
                {
                    return "Spojka - návěští";
                }

                @Override
                public AbstractSymbolFunctionForm getFunctionFormInstance(LayoutElement element,
                        FlowchartEditManager flowchartEditManager)
                {
                    return new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.GotoLabel(
                            element, flowchartEditManager);
                }

                @Override
                public Class<? extends Symbol> getSymbolClass()
                {
                    return GotoLabel.class;
                }

                @Override
                public boolean areCommandsValid(Symbol symbol)
                {
                    if (!(symbol instanceof GotoLabel)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of GotoLabel symbol.");
                    }

                    return true;
                }

                @Override
                public boolean areAllCommandsPresent(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof GotoLabel)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of GotoLabel symbol.");
                    }

                    return true;
                }
            },
    STARTEND
            {
                @Override
                public Symbol getInstance(String value)
                {
                    return new StartEnd(value);
                }

                @Override
                public String getToolTipText()
                {
                    return "Mezní značka - start/konec programu";
                }

                @Override
                public AbstractSymbolFunctionForm getFunctionFormInstance(LayoutElement element,
                        FlowchartEditManager flowchartEditManager)
                {
                    return null;
                }

                @Override
                public Class<? extends Symbol> getSymbolClass()
                {
                    return StartEnd.class;
                }

                @Override
                public boolean areCommandsValid(Symbol symbol)
                {
                    if (!(symbol instanceof StartEnd)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of StartEnd symbol.");
                    }

                    return true;
                }

                @Override
                public boolean areAllCommandsPresent(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof StartEnd)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of StartEnd symbol.");
                    }

                    return true;
                }
            },
    ELLIPSIS
            {
                @Override
                public Symbol getInstance(String value)
                {
                    return new Ellipsis(value);
                }

                @Override
                public String getToolTipText()
                {
                    return "Výpustka";
                }

                @Override
                public AbstractSymbolFunctionForm getFunctionFormInstance(LayoutElement element,
                        FlowchartEditManager flowchartEditManager)
                {
                    return null;
                }

                @Override
                public Class<? extends Symbol> getSymbolClass()
                {
                    return Ellipsis.class;
                }

                @Override
                public boolean areCommandsValid(Symbol symbol)
                {
                    if (!(symbol instanceof Ellipsis)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of Ellipsis symbol.");
                    }

                    return true;
                }

                @Override
                public boolean areAllCommandsPresent(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof Ellipsis)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of Ellipsis symbol.");
                    }

                    return true;
                }
            };

    /**
     * Vrací instanci konkrétního symbolu.
     *
     * @param value vnitřní textová hodnota symbolu, kterou má nová instance
     * nést
     * @return instance konkrétního symbolu
     */
    public abstract Symbol getInstance(String value);

    /**
     * Vrací textovou hodnotu pro tooltip-text konkrétního symbolu.
     *
     * @return textovou hodnotu pro tooltip-text konkrétního symbolu
     */
    public abstract String getToolTipText();

    /**
     * Vrací instanci třídy Class konkrétního symbolu.
     *
     * @return instanci třídy Class konkrétního symbolu
     */
    public abstract Class<? extends Symbol> getSymbolClass();

    /**
     * Metoda, která vrátí grafické rozhraní nastavení funkce symbolu.
     *
     * @param element element, jemuž grafické rozhraní má náležet
     * @param flowchartEditManager instance EditManageru, která má s grafickým
     * rozhraním funkcí interaktovat
     * @return grafické rozhraní nastavení funkce symbolu
     */
    public abstract AbstractSymbolFunctionForm getFunctionFormInstance(LayoutElement element,
            FlowchartEditManager flowchartEditManager);

    public abstract boolean areCommandsValid(Symbol symbol);

    public abstract boolean areAllCommandsPresent(LayoutElement element);

    /**
     * Vrací instanci této enumerační třídy, která reprezentuje symbol
     * specifikovaný jeho instancí třídy Class.
     *
     * @param symbolClass instance třídy Class, identifikující o který symbol se
     * má jednat
     * @return instanci této enumerační třídy, která reprezentuje specifikovaný
     * symbol
     */
    public static EnumSymbol getEnumSymbol(Class<? extends Symbol> symbolClass)
    {
        for (EnumSymbol enumSymbol : EnumSymbol.values()) {
            if (enumSymbol.getSymbolClass().equals(symbolClass)) {
                return enumSymbol;
            }
        }
        return null;
    }

    /*
     * private Symbol symbol;
     *
     * EnumSymbol(Symbol symbol) {
     * this.symbol = symbol;
     * }
     *
     * public Symbol getInstance(String value) {
     * return symbol;
     * }
     */
}
