/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.flowchart.symbols;

import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.gui.balloonToolTip.MaxBalloonSizeCallback;
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
                    return "Zpracování - přiřazení hodnoty do proměnné";
                }

                @Override
                public AbstractSymbolFunctionForm getFunctionFormInstance(LayoutElement element,
                        FlowchartEditManager flowchartEditManager,
                        MaxBalloonSizeCallback maxBalloonSizeCallback)
                {
                    return new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Process(
                            element, flowchartEditManager, maxBalloonSizeCallback);
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
                            String value = entrySet.getValue();
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

                @Override
                public void regenerateSymbolValues(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof Process)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of Process symbol.");
                    }
                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands == null || commands.isEmpty() || !commands.containsKey("var") || !commands.containsKey(
                            "value")) {
                        return;
                    }
                    String var = commands.get("var");
                    String value = commands.get("value");
                    cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Process.generateValues(
                            symbol, var, value);
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
                        FlowchartEditManager flowchartEditManager,
                        MaxBalloonSizeCallback maxBalloonSizeCallback)
                {
                    return new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.IO(element,
                            flowchartEditManager, maxBalloonSizeCallback);
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
                            String value = entrySet.getValue();
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

                @Override
                public void regenerateSymbolValues(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof IO)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of IO symbol.");
                    }
                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands == null || commands.isEmpty() || !commands.containsKey("var") && !commands.containsKey(
                            "value")) {
                        return;
                    }
                    if (commands.containsKey("var")) {
                        String var = commands.get("var");
                        cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.IO.generateIValues(
                                symbol, var);
                    } else {
                        String value = commands.get("value");
                        cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.IO.generateOValues(
                                symbol, value);
                    }
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
                        FlowchartEditManager flowchartEditManager,
                        MaxBalloonSizeCallback maxBalloonSizeCallback)
                {
                    return new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Decision(
                            element, flowchartEditManager, maxBalloonSizeCallback);
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
                            String value = entrySet.getValue();
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

                @Override
                public void regenerateSymbolValues(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof Decision)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of Decision symbol.");
                    }
                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands == null || commands.isEmpty() || !commands.containsKey("condition")) {
                        return;
                    }
                    String condition = commands.get("condition");
                    cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Decision.generateValues(
                            symbol, condition);
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
                        FlowchartEditManager flowchartEditManager,
                        MaxBalloonSizeCallback maxBalloonSizeCallback)
                {
                    return new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Switch(
                            element, flowchartEditManager, maxBalloonSizeCallback);
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
                            String value = entrySet.getValue();
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

                @Override
                public void regenerateSymbolValues(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof Switch)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of Switch symbol.");
                    }
                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands == null || commands.isEmpty() || !commands.containsKey(
                            "conditionVar")) {
                        return;
                    }
                    String[] segmentConstants = new String[element.getInnerSegmentsCount() - 1];
                    for (int i = 1; i < element.getInnerSegmentsCount(); i++) {
                        if (!commands.containsKey("" + i)) {
                            return;
                        } else {
                            segmentConstants[i - 1] = commands.get("" + i);
                        }
                    }
                    String conditionVar = commands.get("conditionVar");
                    cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Switch.generateValues(
                            element, conditionVar, segmentConstants);
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
                        FlowchartEditManager flowchartEditManager,
                        MaxBalloonSizeCallback maxBalloonSizeCallback)
                {
                    return new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.For(element,
                            flowchartEditManager, maxBalloonSizeCallback);
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
                            String value = entrySet.getValue();
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

                @Override
                public void regenerateSymbolValues(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof For)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of For symbol.");
                    }
                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands == null || commands.isEmpty() || !commands.containsKey("var") || !commands.containsKey(
                            "array") && (!commands.containsKey("from") || !commands.containsKey("to") || !commands.containsKey(
                            "inc"))) {
                        return;
                    }
                    if (commands.containsKey("array")) {
                        String var = commands.get("var");
                        String array = commands.get("array");
                        cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.For.generateForeachValues(
                                symbol, var, array);
                    } else {
                        String var = commands.get("var");
                        String from = commands.get("from");
                        String to = commands.get("to");
                        String inc = commands.get("conditionVar");
                        cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.For.generateForValues(
                                symbol, var, from, to, inc);
                    }
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
                        FlowchartEditManager flowchartEditManager,
                        MaxBalloonSizeCallback maxBalloonSizeCallback)
                {
                    if (element.getSymbol().isOverHang()) {
                        return new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.LoopStart(
                                element, flowchartEditManager, maxBalloonSizeCallback);
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
                            String value = entrySet.getValue();
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

                @Override
                public void regenerateSymbolValues(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof LoopStart)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of LoopStart symbol.");
                    }
                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands == null || commands.isEmpty() || !commands.containsKey("condition")) {
                        return;
                    }
                    String condition = commands.get("condition");
                    cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.LoopStart.generateValues(
                            symbol, condition);
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
                        FlowchartEditManager flowchartEditManager,
                        MaxBalloonSizeCallback maxBalloonSizeCallback)
                {
                    for (int i = element.getParentSegment().indexOfElement(element) - 1; i >= 0; i--) {
                        LayoutElement pairElement = element.getParentSegment().getElement(i);
                        if (pairElement.getSymbol() instanceof LoopStart) {
                            if (!pairElement.getSymbol().isOverHang()) {
                                return new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.LoopEnd(
                                        element, flowchartEditManager, maxBalloonSizeCallback);
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
                            String value = entrySet.getValue();
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

                @Override
                public void regenerateSymbolValues(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof LoopEnd)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of LoopEnd symbol.");
                    }
                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands == null || commands.isEmpty() || !commands.containsKey("condition")) {
                        return;
                    }
                    String condition = commands.get("condition");
                    cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.LoopEnd.generateValues(
                            symbol, condition);
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
                        FlowchartEditManager flowchartEditManager,
                        MaxBalloonSizeCallback maxBalloonSizeCallback)
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

                @Override
                public void regenerateSymbolValues(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof Comment)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of Comment symbol.");
                    }
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
                        FlowchartEditManager flowchartEditManager,
                        MaxBalloonSizeCallback maxBalloonSizeCallback)
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

                @Override
                public void regenerateSymbolValues(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof SubRoutine)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of SubRoutine symbol.");
                    }
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
                        FlowchartEditManager flowchartEditManager,
                        MaxBalloonSizeCallback maxBalloonSizeCallback)
                {
                    return new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Goto(
                            element, flowchartEditManager, maxBalloonSizeCallback);
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

                @Override
                public void regenerateSymbolValues(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof Goto)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of Goto symbol.");
                    }
                    LinkedHashMap<String, String> commands = symbol.getCommands();
                    if (commands == null || commands.isEmpty() || !commands.containsKey("mode")) {
                        return;
                    }
                    switch (commands.get("mode")) {
                        case "break":
                            cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Goto.generateBreakValues(
                                    symbol);
                            break;
                        case "continue":
                            cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Goto.generateContinueValues(
                                    symbol);
                            break;
                        case "goto":
                            cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Goto.generateGotoValues(
                                    symbol);
                            break;
                    }
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
                        FlowchartEditManager flowchartEditManager,
                        MaxBalloonSizeCallback maxBalloonSizeCallback)
                {
                    return new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.GotoLabel(
                            element, flowchartEditManager, maxBalloonSizeCallback);
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

                @Override
                public void regenerateSymbolValues(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof GotoLabel)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of GotoLabel symbol.");
                    }
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
                        FlowchartEditManager flowchartEditManager,
                        MaxBalloonSizeCallback maxBalloonSizeCallback)
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

                @Override
                public void regenerateSymbolValues(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof StartEnd)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of StartEnd symbol.");
                    }
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
                    return "Výpustka - doslova pár teček";
                }

                @Override
                public AbstractSymbolFunctionForm getFunctionFormInstance(LayoutElement element,
                        FlowchartEditManager flowchartEditManager,
                        MaxBalloonSizeCallback maxBalloonSizeCallback)
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

                @Override
                public void regenerateSymbolValues(LayoutElement element)
                {
                    Symbol symbol = element.getSymbol();
                    if (!(symbol instanceof Ellipsis)) {
                        throw new IllegalArgumentException(
                                "The symbol passed to this method should be instance of Ellipsis symbol.");
                    }
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
     * @param maxBalloonSizeCallback
     * @return grafické rozhraní nastavení funkce symbolu
     */
    public abstract AbstractSymbolFunctionForm getFunctionFormInstance(LayoutElement element,
            FlowchartEditManager flowchartEditManager,
            MaxBalloonSizeCallback maxBalloonSizeCallback);

    public abstract boolean areCommandsValid(Symbol symbol);

    public abstract boolean areAllCommandsPresent(LayoutElement element);

    public abstract void regenerateSymbolValues(LayoutElement element);

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
