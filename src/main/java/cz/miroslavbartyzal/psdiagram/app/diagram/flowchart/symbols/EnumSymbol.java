/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.symbols;

import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.diagram.gui.managers.FlowchartEditManager;
import cz.miroslavbartyzal.psdiagram.app.diagram.gui.symbolFunctionForms.AbstractSymbolFunctionForm;

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
            return new cz.miroslavbartyzal.psdiagram.app.diagram.gui.symbolFunctionForms.Process(
                    element, flowchartEditManager);
        }

        @Override
        public Class<? extends Symbol> getSymbolClass()
        {
            return Process.class;
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
            return new cz.miroslavbartyzal.psdiagram.app.diagram.gui.symbolFunctionForms.IO(element,
                    flowchartEditManager);
        }

        @Override
        public Class<? extends Symbol> getSymbolClass()
        {
            return IO.class;
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
            return new cz.miroslavbartyzal.psdiagram.app.diagram.gui.symbolFunctionForms.Decision(
                    element, flowchartEditManager);
        }

        @Override
        public Class<? extends Symbol> getSymbolClass()
        {
            return Decision.class;
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
            return new cz.miroslavbartyzal.psdiagram.app.diagram.gui.symbolFunctionForms.Switch(
                    element, flowchartEditManager);
        }

        @Override
        public Class<? extends Symbol> getSymbolClass()
        {
            return Switch.class;
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
            return new cz.miroslavbartyzal.psdiagram.app.diagram.gui.symbolFunctionForms.For(element,
                    flowchartEditManager);
        }

        @Override
        public Class<? extends Symbol> getSymbolClass()
        {
            return For.class;
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
                return new cz.miroslavbartyzal.psdiagram.app.diagram.gui.symbolFunctionForms.LoopStart(
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
                        return new cz.miroslavbartyzal.psdiagram.app.diagram.gui.symbolFunctionForms.LoopEnd(
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
            return new cz.miroslavbartyzal.psdiagram.app.diagram.gui.symbolFunctionForms.Goto(
                    element, flowchartEditManager);
        }

        @Override
        public Class<? extends Symbol> getSymbolClass()
        {
            return Goto.class;
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
            return new cz.miroslavbartyzal.psdiagram.app.diagram.gui.symbolFunctionForms.GotoLabel(
                    element, flowchartEditManager);
        }

        @Override
        public Class<? extends Symbol> getSymbolClass()
        {
            return GotoLabel.class;
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
