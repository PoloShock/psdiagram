/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.managers;

import cz.miroslavbartyzal.psdiagram.app.debug.DebugAnimator;
import cz.miroslavbartyzal.psdiagram.app.debug.function.FunctionManager;
import cz.miroslavbartyzal.psdiagram.app.debug.function.variables.VariableModel;
import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.Layout;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Comment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.gui.MainWindow;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.font.TextLayout;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.swing.*;

/**
 * Tato třída je vrchním správcem animačního režimu aplikace. Je zároveň
 * posluchačem událostí přicházejících z uživatelského rozhraní a obstarává tedy
 * předání příkazů dílčím třídám se specifickými účely.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class FlowchartDebugManager implements KeyListener, ActionListener,
        MouseMotionListener, MouseListener
{

    private MainWindow mainWindow;
    private JSlider jSliderSpeed;
    private JButton jButtonToolPlayPause;
    private JButton jButtonToolPrevious;
    private JButton jButtonToolNext;
    private JButton jButtonToolStop;
    private JButton jButtonLaunch;
    private JPopupMenu breakpointPopup;
    private Layout layout;
    private DebugAnimator animator;
    private VariableModel variableModel;
    private JPanel jPanelDiagram;
    private Symbol breakpointSymbol = null;
    private FunctionManager functionManager;
    private boolean easterEgg = false;
    private Point2D easterPoint;

    /**
     * Konstruktor, zajišťující základní spojení s klíčovými prvky uživatelského
     * rozhraní.
     *
     * @param mainWindow hlavní okno aplikace
     * @param variableModel vizuální správce proměnných
     * @param jPanelDiagram plátno aplikace
     * @param jSliderSpeed posuvník, jímž se ovládá rychlost animace průchodu
     * vývojovým diagramem
     * @param jButtonToolPlayPause tlačítko pro spuštěnní/pozastavení animace
     * @param jButtonToolPrevious tlačítko pro krok zpět
     * @param jButtonToolNext tlačítko pro krok vpřed
     * @param jButtonToolStop tlačítko pro zastavení, resetu animace
     * @param jButtonLaunch tlačítko pro rychlý průchod diagramem
     */
    public FlowchartDebugManager(MainWindow mainWindow, VariableModel variableModel,
            JPanel jPanelDiagram, JSlider jSliderSpeed, JButton jButtonToolPlayPause,
            JButton jButtonToolPrevious, JButton jButtonToolNext, JButton jButtonToolStop,
            JButton jButtonLaunch)
    {
        this.mainWindow = mainWindow;
        this.variableModel = variableModel;
        this.jPanelDiagram = jPanelDiagram;
        this.jSliderSpeed = jSliderSpeed;
        this.jButtonToolPlayPause = jButtonToolPlayPause;
        this.jButtonToolPrevious = jButtonToolPrevious;
        this.jButtonToolNext = jButtonToolNext;
        this.jButtonToolStop = jButtonToolStop;
        this.jButtonLaunch = jButtonLaunch;
        this.layout = mainWindow.getFlowchartLayout();
        this.functionManager = new FunctionManager(this.layout, jPanelDiagram, jSliderSpeed, this);
        this.animator = functionManager.getAnimator();

        breakpointPopup = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Vložit/Smazat breakpoint         Poklepání",
                new javax.swing.ImageIcon(getClass().getResource(
                "/img/menuitems/16-Breakpoint.png")));
        menuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                functionManager.toggleBreakpoint(breakpointSymbol);
                breakpointSymbol = null;
            }
        });
        breakpointPopup.add(menuItem);
    }

    // **********************ActionListener**********************
    /**
     * Metoda pro příjem událostí, týkající se animace vývojového diagramu.
     *
     * @param ae nová událost
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        String[] action = ae.getActionCommand().split("/");
        switch (action[0]) {
            case "mode": {
                if (action[1].equals("animationMode")) {
                    if (!mainWindow.getAnimationMode()) { // predstaveni zacina
                        for (LayoutSegment segment : layout.getFlowchart()) {
                            if (segment != null) {
                                for (LayoutElement element : segment) {
                                    if (element != null && element.getSymbol() != null) {
                                        element.getSymbol().resetFontStyle(Font.BOLD);
                                    }
                                }
                            }
                        }

                        variableModel.clearVariables();
                        functionManager.entryAnimMode();
                    } else {
                        for (LayoutSegment segment : layout.getFlowchart()) {
                            if (segment != null) {
                                for (LayoutElement element : segment) {
                                    if (element != null && element.getSymbol() != null) {
                                        element.getSymbol().resetFontStyle(Font.PLAIN);
                                    }
                                }
                            }
                        }

                        functionManager.exitAnimMode();
                        exitAnimMode();
                    }
                    mainWindow.setAnimationMode(!mainWindow.getAnimationMode());
                }
                break;
            }
            case "animation": {
                switch (action[1]) {
                    case "play": {
                        functionManager.play();
                        play();
                        break;
                    }
                    case "pause": {
                        pause();
                        jButtonToolPrevious.setEnabled(functionManager.pause());
                        break;
                    }
                    case "previous": {
                        jButtonToolPrevious.setEnabled(functionManager.previous());
                        setForwardEnabled(true);
                        break;
                    }
                    case "next": {
                        if (!jButtonToolPlayPause.getActionCommand().equals("animation/pause")) {
                            jButtonToolPrevious.setEnabled(functionManager.next());
                        } else {
                            functionManager.next(); // pri probihajici animaci kulicky nelze provadet krok zpet
                        }
                        break;
                    }
                    case "stop": {
                        functionManager.stop();
                        variableModel.clearVariables();
                        stop();
                        break;
                    }
                    case "launch": {
                        launch();
                        functionManager.launch();
                        break;
                    }
                }
                break;
            }
        }
    }

    // TODO umožnit "snapshot" aktuálně zobrazeného diagramu?
    /**
     * Metoda předá třídě Animator příkaz k vykreslení diagramu.
     *
     * @param g2d Instance Graphics2D, která se má použít k vykreslení diagramu
     */
    public void paintFlowchart(Graphics2D g2d)
    {
        animator.paintFlowchart(g2d);
    }

    /**
     * Tato metoda je volána, když průchod vývojového diagramu dosáhl konce.
     * Metoda nastaví tlačítka ovládání animace do správných stavů.
     *
     * @param prevButtonEnabled True když je možné použít funkci Krok zpět
     */
    public void animationDone(boolean prevButtonEnabled)
    {
        jSliderSpeed.setEnabled(true);
        setPlayPauseButton(true);
        setForwardEnabled(false);
        jButtonToolPrevious.setEnabled(prevButtonEnabled);
        jButtonToolStop.setEnabled(true);
    }

    /**
     * Metoda vrací poslední dávku proměnných, která byla použita pro
     * aktualizaci vizuální prezentace proměnných.
     *
     * @return poslední dávka proměnných, která byla použita pro aktualizaci
     * vizuální prezentace proměnných
     */
    public HashMap<String, String> getLastUpdateVars()
    {
        return variableModel.getLastUpdateVars();
    }

    /**
     * Metoda předá dávku aktualizace proměnných jejich vizuální prezentaci.
     *
     * @param updatedVariables proměnné, které se mají aktualizovat
     * @return předchozí stavy proměnných, které byli aktualizací změněny
     */
    public HashMap<String, String> updateVariables(HashMap<String, String> updatedVariables)
    {
        return variableModel.updateVariables(updatedVariables);
    }

    // **********************MouseListener**********************
    /**
     * Jedná-li se o poklepání a je-li zamířeno na některý symbol,
     * označí/odoznačí daný symbol jako brakpoint.
     *
     * @param me nová událost kliknutí
     */
    @Override
    public void mouseClicked(MouseEvent me)
    {
        if (me.getClickCount() % 2 == 0) {
            Symbol symbol = getNoCommentSymbolContaining(layout.getFlowchart(), getTransformedPoint(
                    me.getPoint(), new Point2D.Double()));
            if (symbol != null) {
                functionManager.toggleBreakpoint(symbol);
            }
        }
    }

    /**
     * Metoda s prázdným tělem.
     */
    @Override
    public void mouseEntered(MouseEvent me)
    {
    }

    /**
     * Metoda s prázdným tělem.
     */
    @Override
    public void mouseExited(MouseEvent me)
    {
    }

    /**
     * Je-li dle platformy čas na ukázání dialogu, ukáže dialog.<br />
     * Je-li pozastavena animace vývojového diagramu, a je-li kurzor nad
     * kuličkou značící průchod diagramem, aktivuje se easter-egg, umožňující
     * hýbání s řečenou kuličkou. :)
     *
     * @param me
     */
    @Override
    public void mousePressed(MouseEvent me)
    {
        maybeShowPopup(me);
        if (!easterEgg && !me.isControlDown()) {
            Point2D p = getTransformedPoint(me.getPoint(), new Point2D.Double());
            if (!animator.isPlaying() && animator.ballContains(p.getX(), p.getY())) {
                // easter-egg! :)
                easterEgg = true;
                easterPoint = animator.getBallCoordenates();
                jPanelDiagram.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                        new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),
                        "null"));
                MainWindow.setStatusText("Easter-egg! :)", 2000);
            } else {
                // zadny easter-egg
                mainWindow.getFlowchartOverlookManager().setStartDragGrab(me);
            }
        }
    }

    /**
     * Je-li dle platformy čas na ukázání dialogu, ukáže dialog.<br />
     * Dále deaktivuje případně spuštěný easter-egg.
     *
     * @param me
     */
    @Override
    public void mouseReleased(MouseEvent me)
    {
        maybeShowPopup(me);
        if (easterEgg) {
            easterEgg = false;
            animator.setBallToPos(easterPoint.getX(), easterPoint.getY());
            easterPoint = null;
            jPanelDiagram.setCursor(Cursor.getDefaultCursor());
        }
    }

    // **********************MouseMotionListener**********************
    /**
     * Obstarává případně spuštěný easter-egg.
     *
     * @param me
     */
    @Override
    public void mouseDragged(MouseEvent me)
    {
        if (easterEgg) {
            Point2D p = getTransformedPoint(me.getPoint(), new Point2D.Double());
            animator.setBallToPos(p.getX(), p.getY());
        }
    }

    /**
     * Obstarává případně spuštěný easter-egg.
     *
     * @param me
     */
    @Override
    public void mouseMoved(MouseEvent me)
    {
        if (easterEgg) {
            easterEgg = false;
            animator.setBallToPos(easterPoint.getX(), easterPoint.getY());
            easterPoint = null;
            jPanelDiagram.setCursor(Cursor.getDefaultCursor());
        }
    }

    // **********************KeyListener**********************
    /**
     * Zprostředkovává klávesové zkratky pro ovládání animace vývojového
     * diagramu.
     *
     * @param ke nová událost stisku klávesy
     */
    @Override
    public void keyPressed(KeyEvent ke)
    {
        if (ke.getKeyCode() == KeyEvent.VK_SPACE && jButtonToolPlayPause.isEnabled()) {
            if (jButtonToolPlayPause.getActionCommand().equals("animation/play")) {
                actionPerformed(new ActionEvent(this, this.hashCode(), "animation/play"));
            } else {
                actionPerformed(new ActionEvent(this, this.hashCode(), "animation/pause"));
            }
        } else if ((ke.getKeyCode() == KeyEvent.VK_LEFT || ke.getKeyCode() == KeyEvent.VK_UP) && ke.isControlDown() && jButtonToolPrevious.isEnabled()) {
            actionPerformed(new ActionEvent(this, this.hashCode(), "animation/previous"));
        } else if ((ke.getKeyCode() == KeyEvent.VK_RIGHT || ke.getKeyCode() == KeyEvent.VK_DOWN) && ke.isControlDown() && jButtonToolNext.isEnabled()) {
            actionPerformed(new ActionEvent(this, this.hashCode(), "animation/next"));
        }
    }

    /**
     * Metoda s prázdným tělem.
     */
    @Override
    public void keyReleased(KeyEvent ke)
    {
    }

    /**
     * Metoda s prázdným tělem.
     */
    @Override
    public void keyTyped(KeyEvent ke)
    {
    }

    // ***********************************************************
    // **********************PRIVATE METHODS**********************
    // ***********************************************************
    private Symbol getNoCommentSymbolContaining(Flowchart<LayoutSegment, LayoutElement> flowchart,
            Point2D p)
    {
        Symbol symbol;
        for (LayoutSegment segment : flowchart) {
            if (segment != null) {
                for (LayoutElement element : segment) {
                    symbol = element.getSymbol();
                    if (!(symbol instanceof Comment) && symbol.contains(p)) {
                        return symbol;
                    }
                }
            }
        }
        return null;
    }

    private boolean maybeShowPopup(MouseEvent me)
    {
        if (me.isPopupTrigger()) {
            breakpointSymbol = getNoCommentSymbolContaining(layout.getFlowchart(),
                    getTransformedPoint(me.getPoint(), new Point2D.Double()));
            if (breakpointPopup != null && breakpointSymbol != null) {
                breakpointPopup.show(me.getComponent(), me.getX(), me.getY());
            }
            return true;
        }
        return false;
    }

    private void exitAnimMode()
    {
        setPlayPauseButton(true);
        /*
         * jButtonToolPlayPause.setEnabled(false);
         * jButtonToolPrevious.setEnabled(false);
         * jButtonToolNext.setEnabled(false);
         * jButtonToolStop.setEnabled(false);
         * jButtonLaunch.setEnabled(false);
         */
    }

    private void play()
    {
        setPlayPauseButton(false);
        jButtonToolPrevious.setEnabled(false);
        jButtonLaunch.setEnabled(false);
    }

    private void pause()
    {
        jSliderSpeed.setEnabled(true);
        setPlayPauseButton(true);
        setForwardEnabled(true);
    }

    private void stop()
    {
        jSliderSpeed.setEnabled(true);
        setPlayPauseButton(true);
        setForwardEnabled(true);
        jButtonToolPrevious.setEnabled(false);
        jButtonToolStop.setEnabled(true);
    }

    private void launch()
    {
        jSliderSpeed.setEnabled(false);
        setForwardEnabled(false);
        setPlayPauseButton(false);
        jButtonToolPlayPause.setEnabled(true);
        jButtonToolStop.setEnabled(true);
        jButtonToolPrevious.setEnabled(false);
    }

    private void setPlayPauseButton(boolean play)
    {
        if (play) {
            jButtonToolPlayPause.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                    "/img/toolBar/24-Play.png")));
            jButtonToolPlayPause.setToolTipText("<html>Spustit animaci<br />(mezerník)</html>");
            jButtonToolPlayPause.setActionCommand("animation/play");
        } else {
            jButtonToolPlayPause.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                    "/img/toolBar/24-Pause.png")));
            jButtonToolPlayPause.setToolTipText("<html>Pozastavit animaci<br />(mezerník)</html>");
            jButtonToolPlayPause.setActionCommand("animation/pause");
        }
    }

    private void setForwardEnabled(boolean enabled)
    {
        jButtonToolPlayPause.setEnabled(enabled);
        jButtonToolNext.setEnabled(enabled);
        jButtonLaunch.setEnabled(enabled);
    }

    private Point2D getTransformedPoint(Point2D mousePoint, Point2D destPoint)
    {
        try {
            mainWindow.getAffineTransform().createInverse().transform(mousePoint, destPoint);
        } catch (NoninvertibleTransformException e) {
            throw new Error("Error while transforming coordnates!");
        } catch (NullPointerException e) {
        }
        return destPoint;
    }

}
