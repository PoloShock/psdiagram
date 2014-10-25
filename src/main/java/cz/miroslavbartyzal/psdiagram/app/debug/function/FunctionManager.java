/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.debug.function;

import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Comment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.StartEnd;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.For;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.GotoLabel;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Goto;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.debug.DebugAnimator;
import cz.miroslavbartyzal.psdiagram.app.debug.function.variables.variableScopes.BlockScopeVariables;
import cz.miroslavbartyzal.psdiagram.app.debug.function.variables.variableScopes.GlobalScopeVariables;
import cz.miroslavbartyzal.psdiagram.app.debug.function.variables.variableScopes.VariablesScope;
import cz.miroslavbartyzal.psdiagram.app.flowchart.FlowchartElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.FlowchartSegment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.Layout;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.gui.managers.FlowchartDebugManager;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingDeque;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

/**
 * <p>
 * Tato třída spravuje veškeré řízení průběhu diagramu na základě
 * zpracovaných funkcí symbolů.<br />
 * Je jakýmsi managerem, který řídí třídu Animator a poslouchá příkazy shora, z
 * třídy FlowchartAnimationManager. Pro zpracování samotné funkce symbolu je
 * určena třída ElementFunctionBed, jejíž metody jsou volány právě touto
 * třídou.</p>
 * <p>
 * Třída zároveň uchovává paměťový zásobník s funkcí "krok zpět" (viz. třída
 * StepBack). Velikost tohoto zásobníku je defaultně omezena na 1000 kroků.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class FunctionManager
{

    private final int STACKLIMIT = 1000;
    private Thread launchThread;
    private final FlowchartDebugManager flowchartDebugManager;
    private final JPanel jPanelDiagram;
    private final DebugAnimator animator;
    private final Layout layout;
    //private boolean blockScopeVariables;
    private VariablesScope variables;
    private LinkedBlockingDeque<StepBack> stepBacks = new LinkedBlockingDeque<>(STACKLIMIT);
    private final ArrayList<Symbol> breakpointSymbols = new ArrayList<>();
    private LayoutElement nextElement = null;
    private LayoutElement actualElement = null;
    private HashMap<Symbol, String> forVarValue = new HashMap<>();
    private boolean deleteForVar = false;

    /**
     * Kontruktor s parametry potřebnými k spravování řízení toku animace.
     *
     * @param layout instance třídy Layout, obsahující vývojový diagram k
     * procházení
     * @param jPanelDiagram JPanel, který má sloužit jako kreslící plátno
     * @param jSliderSpeed JSlider ovlivňující rychlost kuličky
     * @param flowchartDebugManager instance třídy
     * FlowchartAnimationManager, jež bude informována o průběhu toku procházení
     * diagramu
     */
    public FunctionManager(Layout layout, JPanel jPanelDiagram, JSlider jSliderSpeed,
            FlowchartDebugManager flowchartDebugManager)
    {
        this.jPanelDiagram = jPanelDiagram;
        this.layout = layout;
        this.animator = new DebugAnimator(layout, jPanelDiagram, jSliderSpeed, this);
        this.flowchartDebugManager = flowchartDebugManager;
    }

    /**
     * Metoda zinicializuje třídu Animator a tím spustí i grafický přechod do
     * animačního režimu aplikace.
     */
    public void entryAnimMode()
    {
        animator.init();
    }

    /**
     * Metoda resetuje celou animaci a nastaví komponentám původní hodnoty,
     * potřebné pro opuštění animačního režimu.
     */
    public void exitAnimMode()
    {
        stop();
        breakpointSymbols.clear();
        jPanelDiagram.setBackground(Color.WHITE);
    }

    /**
     * Metoda spustí animaci průchodu vývojovým diagramem.
     */
    public void play()
    {
        animator.play();
        if (!animator.isPlayBuffered()) {
            next();
        }
    }

    public void globalPause()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                flowchartDebugManager.actionPerformed(new ActionEvent(this,
                        this.hashCode(), "animation/pause"));
                jPanelDiagram.repaint();
            }
        });
    }

    /**
     * Metoda pozastaví probýhající animaci průchodu vývojovým diagramem.
     * Probíhá-li rychlé spuštění průchodu, pozastaví právě jej.
     *
     * @return Stav zásobníku s kroky zpět. True znamená, že zásobník má alespoň
     * jeden možný krok zpět, false naopak značí prázdný zásobník.
     */
    public boolean pause()
    {
        if (launchThread != null && launchThread.isAlive()) {
            launchThread.interrupt();
            try {
                launchThread.join();
            } catch (InterruptedException ex) {
            }
        } else if (animator.isPlaying()) {
            animator.pause();
        }
        return !stepBacks.isEmpty();
    }

    /**
     * Metoda spustí rychlý průchod diagramem. Pro tento účel je použito nové
     * vlákno, aby proces mohl být pozastaven a mohl být v průběhu i
     * vykreslován.
     */
    public void launch()
    {
        launchThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Runnable runnable = new Runnable()
                {
                    private boolean paused = false;

                    @Override
                    public void run()
                    {
                        if (!paused) { // don't allow further next()s if we interrupted ourselves
                            paused = next().debugShouldBeHalted;
                            if (paused) {
                                globalPause();
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                };

                do {
                    try {
                        SwingUtilities.invokeAndWait(runnable);
                    } catch (InterruptedException | InvocationTargetException ex) {
                        return;
                    }
                } while (nextElement != null && !breakpointSymbols.contains(nextElement.getSymbol()));
                if (nextElement != null && breakpointSymbols.contains(nextElement.getSymbol())) {
                    globalPause();
                }
            }
        });
        launchThread.start();
    }

    /**
     * Voláním této metody se provede krok zpět. Dříve zvýrazněná cesta a symbol
     * jsou uvedeny do původní podoby, stejně tak ostatní parametry průchodu
     * diagramem, jako text s informací o proběhlé funkci symbolu nad ním,
     * proměnné.
     *
     * @return Stav zásobníku s kroky zpět. True znamená, že zásobník má alespoň
     * jeden možný krok zpět, false naopak značí prázdný zásobník.
     */
    public boolean previous()
    {
        StepBack stepBack = stepBacks.pop();

        nextElement = actualElement;
        actualElement = stepBack.prevElement;

        variables.setActualSegment(nextElement.getParentSegment());

        variables.hackupdateVariables(stepBack.prevVariables);
        flowchartDebugManager.updateVariables(stepBack.prevDisplayVariables);
        flowchartDebugManager.updateVariables(stepBack.prevPrevDisplayVariables);

        for (Symbol forSymbol : stepBack.prevForValues.keySet()) {
            if (stepBack.prevForValues.get(forSymbol) == null) {
                forVarValue.remove(forSymbol);
            } else {
                /*
                 * if (!nextElement.getSymbol().equals(forSymbol) &&
                 * forVarValue.get(forSymbol) != null) {
                 * // je treba zpet dodat promennou for vetvi
                 * HashMap<String, String> forVar = new HashMap<>();
                 * forVar.put(forSymbol.getCommands().get("var"),
                 * forVarValue.get(forSymbol));
                 * variables.updateVariables(forVar);
                 * }
                 */
                forVarValue.put(forSymbol, stepBack.prevForValues.get(forSymbol));
            }
        }

        animator.doPath(nextElement.getSymbol(), stepBack.prevProgressDesc, stepBack.paths,
                stepBack.segmentDesc, false);
        if (!layout.getFlowchart().getMainSegment().getElement(0).equals(nextElement)) {
            animator.setActiveSymbol(nextElement.getSymbol());
        } else {
            animator.setActiveSymbol(null);
        }

        return !stepBacks.isEmpty();
    }

    /**
     * Voláním této metody se provede krok vpřed. Tím se provede funkce
     * aktuálního symbolu čekajícího na provedení, jeho zvýraznění a zvýrazní se
     * také spojnice vedoucí k symbolu následujícímu.
     *
     * @return NextOutput objekt se stavem zásobníku s kroky zpět. True znamená, že zásobník má
     * alespoň jeden možný krok zpět, false naopak značí prázdný zásobník. Dále obsahuje informaci
     * o tom, zda by se mělo automatické spouštění příkazu pozastavit (kvůli chybě nebo zrušenému
     * dialogu symbolu In/Out)
     */
    public NextOutput next()
    {
        StepBack stepBack = new StepBack();
        Path2D gotoLabelPath = null;

        stepBack.prevElement = actualElement;
        if (actualElement != null) {
            if (actualElement.getSymbol() instanceof Goto && nextElement.getSymbol() instanceof GotoLabel) { // jedeme z navesti, je treba pridat cestu od GotoLabel
                GotoLabel gotoLabel = (GotoLabel) nextElement.getSymbol();
                gotoLabelPath = new Path2D.Double();
                gotoLabelPath.moveTo(gotoLabel.getCenterX() - gotoLabel.getMyHair(),
                        gotoLabel.getCenterY());
                gotoLabelPath.lineTo(gotoLabel.getCenterX(), gotoLabel.getCenterY());
            }
            actualElement = nextElement;
        } else {
            // prvni krok
            initVariablesScope();
            actualElement = layout.getFlowchart().getMainSegment().getElement(0);
            if (actualElement.getSymbol() instanceof Comment) {
                actualElement = layout.getFlowchart().getMainSegment().getElement(1);
            }
            variables.setActualSegment(actualElement.getParentSegment());
        }

        HashMap<String, String> allVars = variables.getAllVariables();

        if (actualElement.getSymbol() instanceof For && actualElement.getSymbol().getCommands() != null) {
            if (!forVarValue.containsKey(actualElement.getSymbol())) { // prvni inicializace for symbolu
                if (allVars.containsKey(actualElement.getSymbol().getCommands().get("var"))) {
                    /*
                     * Prejmenuji promennou cyklu, aby se inicializovala (je to signal pro tridu ElementFunctionBed,
                     * ktery ocekava promennou zacinajici znakem "0") - ikdyz se neinicializuje (for-each kdyz je nulova
                     * delka pole), ve skutecnosti zustane.
                     */
                    allVars.put("0" + actualElement.getSymbol().getCommands().get("var"),
                            allVars.remove(actualElement.getSymbol().getCommands().get("var")));
                }

                stepBack.prevForValues.put(actualElement.getSymbol(), forVarValue.get(
                        actualElement.getSymbol()));
                forVarValue.put(actualElement.getSymbol(), "");
            } else {
                // jednorazove pridani jiz inicializovane For promenne
                allVars.put(actualElement.getSymbol().getCommands().get("var"), forVarValue.get(
                        actualElement.getSymbol()));
            }
        }

        FunctionResult functionResult = ElementFunctionBed.getResult(actualElement, allVars);

        nextElement = functionResult.nextElement;
        if (nextElement == null) { // konec procházení diagramu
            if (actualElement.getSymbol() instanceof StartEnd) { // jestli symbol neni StartEnd, doslo k chybe a symbol nevykreslim jako provedeny
                animator.doPath(actualElement.getSymbol(), functionResult.progressDesc, null, null,
                        true);
            }
            animator.setActiveSymbol(null);
            flowchartDebugManager.animationDone(!stepBacks.isEmpty());

            stepBacks.addFirst(stepBack);
            return new NextOutput(!stepBacks.isEmpty(), functionResult.haltDebug);
        }
        FlowchartSegment updateSegment = nextElement.getParentSegment();

        HashMap<String, String> updateDisplayVars = new HashMap<>();
        HashMap<FlowchartSegment, HashMap<String, String>> prevVariables = new HashMap<>();

        if (!functionResult.updatedVariables.isEmpty()) {
            updateDisplayVars.putAll(functionResult.updatedVariables);

            if (actualElement.getSymbol() instanceof For) {
                variables.setActualSegment(actualElement.getInnerSegment(1)); // for poskytuje svou promennou inner vetvi, ne vetvi ve ktere se nachazi
                if (!actualElement.equals(nextElement) && !actualElement.getInnerSegment(1).equals(
                        updateSegment)) {
                    // opoustim for, smazu jeho udrzovanou promennou
                    stepBack.prevForValues.put(actualElement.getSymbol(), forVarValue.get(
                            actualElement.getSymbol()));
                    forVarValue.remove(actualElement.getSymbol());
                    if (deleteForVar) {
                        updateDisplayVars.put(actualElement.getSymbol().getCommands().get("var"), ""); // smazani promenne cyklu z vizualniho prehledu po predchozim pozdrzeni smazani
                    }
                }
                deleteForVar = false;
            }
            prevVariables.putAll(variables.updateVariables(functionResult.updatedVariables));
        }

        // jestli jsem byl uvnitr for a ted budu vystupovat, musim uchovat promennou cyklu
        String forVar = null;
        String forValue = null;
        if (nextElement.getSymbol() instanceof For && forVarValue.containsKey(
                nextElement.getSymbol()) && !nextElement.getInnerSegment(1).equals(updateSegment)) {
            if (!isWithinMe(nextElement, variables.getActualSegment())) {
                // byl drive proveden goto skok (nebo break) ven z tohoto foru, musim smazat udaj o pruchodu
                stepBack.prevForValues.put(nextElement.getSymbol(), forVarValue.get(
                        nextElement.getSymbol()));
                forVarValue.remove(nextElement.getSymbol());
            } else {
                forVar = nextElement.getSymbol().getCommands().get("var");
                forValue = variables.getSegmentVariables(nextElement.getInnerSegment(1)).get(forVar); // prvne se pokusim ziskat promennou od samotne for vetve - kdyz byla promenna deklarovana forem
                if (forValue == null) { // promenna cyklu nemusela byt deklarovana samotnym cyklem
                    forValue = variables.getAllVariables().get(forVar);
                }
                if (!actualElement.equals(nextElement) || !forVarValue.get(nextElement.getSymbol()).equals(
                        "")) {
                    // pri prazdnem tele for je treba ve stepback ponechat puvodni inicializaci
                    stepBack.prevForValues.put(nextElement.getSymbol(), forVarValue.get(
                            nextElement.getSymbol()));
                }
                forVarValue.put(nextElement.getSymbol(), forValue);
            }
        }

        HashMap<FlowchartSegment, HashMap<String, String>> ErasedVars = variables.setActualSegment(
                updateSegment);
        prevVariables.putAll(ErasedVars);
        for (HashMap<String, String> vars : ErasedVars.values()) {
            for (String var : vars.keySet()) {
                updateDisplayVars.put(var, "");
            }
        }
        if (forVar != null && forValue != null) {
            if (updateDisplayVars.get(forVar) != null && updateDisplayVars.get(forVar).equals("")) {
                if (!actualElement.equals(nextElement)) {
                    deleteForVar = true;
                    updateDisplayVars.remove(forVar); // pozdrzeni promenne cyklu ve vizualnim prehledu promennych
                } else {
                    // promenna nebyla jeste ani zakreslena, protoze se jedna o cyklus s prazdnym telem
                    updateDisplayVars.put(forVar, forValue);
                }
            }
        }

        stepBack.prevPrevDisplayVariables = flowchartDebugManager.getLastUpdateVars();
        stepBack.prevDisplayVariables = flowchartDebugManager.updateVariables(updateDisplayVars);
        stepBack.prevVariables = prevVariables;

        Path2D[] paths = null;
        if (functionResult.paths != null) {
            if (gotoLabelPath == null) {
                paths = functionResult.paths;
            } else { // jedeme z navesti, je treba pridat cestu od GotoLabel
                paths = new Path2D[functionResult.paths.length + 1];
                paths[0] = gotoLabelPath;
                for (int i = 1; i < paths.length; i++) {
                    paths[i] = functionResult.paths[i - 1];
                }
            }
        }

        stepBack.paths = paths;
        stepBack.prevProgressDesc = animator.getSymbolProgressDesc(actualElement.getSymbol());
        stepBack.segmentDesc = functionResult.segmentDesc;

        if (animator.isPlaying()) {
            animator.animPath(actualElement.getSymbol(), functionResult.progressDesc, paths,
                    functionResult.segmentDesc);
        } else {
            animator.doPath(actualElement.getSymbol(), functionResult.progressDesc, paths,
                    functionResult.segmentDesc, true);
        }
        animator.setActiveSymbol(nextElement.getSymbol());

        if (stepBacks.size() == STACKLIMIT) { // maximum udrzovanych kroku je limitovano, aby jsme nezabrali moc pameti
            stepBacks.removeLast();
        }
        stepBacks.addFirst(stepBack);
        return new NextOutput(!stepBacks.isEmpty(), functionResult.haltDebug);
    }

    /**
     * Voláním této metody se resetuje animace průchodu vývojovým diagramem.
     * Všechny symboly a spojnice jsou navráceny do "neprošlého" stavu, jsou
     * vymazány proměnné a zásobník kroků zpět.
     */
    public void stop()
    {
        if (launchThread != null && launchThread.isAlive()) {
            launchThread.interrupt();
            try {
                launchThread.join();
            } catch (InterruptedException ex) {
            }

            SwingUtilities.invokeLater(new Runnable()
            { // musim kod spustit az po sleze - v tuto chvili s nejvetsi pravdepodobnosti jsou stale spusteny metody od launch
                @Override
                public void run()
                {
                    myStop();
                }
            });
        } else {
            myStop();
        }
    }

    private void myStop()
    {
        animator.stop();
        animator.setActiveSymbol(null);
        nextElement = null;
        actualElement = null;
        forVarValue = new HashMap<>();
        stepBacks = new LinkedBlockingDeque<>(STACKLIMIT);
    }

    /**
     * Nastaví breakpoint na daný symbol. Breakpoint způsobí, že kdykoliv je v
     * "rychlém průchodu diagramu" dosaženo tohoto symbolu, je průchod
     * pozastaven.
     *
     * @param symbol symbol, který má představovat breakpoint
     */
    public void toggleBreakpoint(Symbol symbol)
    {
        if (symbol != null) {
            if (breakpointSymbols.contains(symbol)) {
                breakpointSymbols.remove(symbol);
            } else {
                breakpointSymbols.add(symbol);
            }
            jPanelDiagram.repaint();
        }
    }

    /**
     * Metoda vrátí všechny aktuálně nastavené breakpointy.
     *
     * @return aktuálně nastavené brakpointy - symboly
     */
    public ArrayList<Symbol> getBreakpointSymbols()
    {
        return breakpointSymbols;
    }

    /**
     * Metoda vrací instanci třídy Animator, starající se o animaci průchodu
     * tohoto diagramu.
     *
     * @return instance třídy Animator, starající se o animaci průchodu
     * tohoto diagramu
     */
    public DebugAnimator getAnimator()
    {
        return animator;
    }

    // ***********************************************************
    // **********************PRIVATE METHODS**********************
    // ***********************************************************
    private boolean isWithinMe(FlowchartElement me, FlowchartSegment segment)
    {
        if (segment.getParentElement() != null) {
            if (segment.getParentElement().equals(me)) {
                return true;
            } else {
                return isWithinMe(me, segment.getParentElement().getParentSegment());
            }
        } else {
            return false;
        }
    }

    private void initVariablesScope()
    {
        //blockScopeVariables = Settings.isBlockScopeVariables();
        if (SettingsHolder.settings.isBlockScopeVariables()) {
            variables = new BlockScopeVariables();
        } else {
            variables = new GlobalScopeVariables();
        }
    }

    /*
     * private void countSymbol(Symbol symbol) {
     * if (symbolCounter.get(symbol) != null) {
     * symbolCounter.put(symbol, symbolCounter.get(symbol) + 1);
     * } else {
     * symbolCounter.put(symbol, 1);
     * }
     * }
     */
    public final class NextOutput
    {

        public boolean stepBacksNotEmpty;
        public boolean debugShouldBeHalted;

        public NextOutput(boolean stepBacksNotEmpty, boolean debugShouldBeHalted)
        {
            this.stepBacksNotEmpty = stepBacksNotEmpty;
            this.debugShouldBeHalted = debugShouldBeHalted;
        }

    }

}
