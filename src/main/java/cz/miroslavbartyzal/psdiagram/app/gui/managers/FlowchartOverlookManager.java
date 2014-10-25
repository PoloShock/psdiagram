/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.managers;

import cz.miroslavbartyzal.psdiagram.app.gui.MainWindow;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <p>
 * Tato třída je vrchním správcem náhledového režimu aplikace. Je zároveň
 * posluchačem událostí přicházejících z uživatelského rozhraní a obstarává tedy
 * předání příkazů hlavnímu oknu aplikace.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class FlowchartOverlookManager implements MouseListener, MouseMotionListener,
        MouseWheelListener, KeyListener, ChangeListener, ActionListener
{

    private MainWindow mainWindow;
    private JScrollBar horizontalScrollbar;
    private JScrollBar verticalScrollbar;
    private JSlider sliderZoom;
    private Cursor grab = null;
    private Integer pressedCursorX = null;
    private Integer pressedCursorY = null;
    private boolean horizontalScrolled = false;
    private Point2D zoomAnchorPoint = null;

    /**
     * Konstruktor, zajišťující základní spojení s klíčovými prvky uživatelského
     * rozhraní.
     *
     * @param mainWindow hlavní okno aplikace
     * @param horizontalScrollbar horizontální scrollbar plátna vývojového
     * diagramu
     * @param verticalScrollbar vertikální scrollbar plátna vývojového diagramu
     * @param sliderZoom slider používaný pro zvětšování vývojového diagramu
     */
    public FlowchartOverlookManager(MainWindow mainWindow, JScrollBar horizontalScrollbar,
            JScrollBar verticalScrollbar, JSlider sliderZoom)
    {
        this.mainWindow = mainWindow;
        this.horizontalScrollbar = horizontalScrollbar;
        this.verticalScrollbar = verticalScrollbar;
        this.sliderZoom = sliderZoom;

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        try {
            Image image = ImageIO.read(getClass().getResource(
                    "/img/cursors/cursor_grab.png").openStream());
            grab = toolkit.createCustomCursor(image, new Point(0, 0), "grab");
        } catch (IOException e) {
            throw new Error("Error while loading cursor image!");
        }
    }

    // **********************ActionListener**********************
    /**
     * Metoda pro příjem událostí, týkající se náhledu vývojového diagramu.
     *
     * @param ae nová událost
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        String[] action = ae.getActionCommand().split("/");
        switch (action[0]) {
            case "overlook":
                switch (action[1]) {
                    case "zoomIn": {
                        int zoomValue = sliderZoom.getValue();
                        if (zoomValue == sliderZoom.getMaximum()) {
                            return;
                        }
                        sliderZoom.setValue(zoomValue + 1);
                        break;
                    }
                    case "zoomOut": {
                        int zoomValue = sliderZoom.getValue();
                        if (zoomValue == sliderZoom.getMinimum()) {
                            return;
                        }
                        sliderZoom.setValue(zoomValue - 1);
                        break;
                    }
                }
                break;
        }
    }

    // **********************MouseListener**********************
    /**
     * Metoda s prázdným tělem.
     * <p>
     * @param me
     */
    @Override
    public void mouseClicked(MouseEvent me)
    {
    }

    /**
     * Metoda s prázdným tělem.
     * <p>
     * @param me
     */
    @Override
    public void mouseEntered(MouseEvent me)
    {
    }

    /**
     * Metoda s prázdným tělem.
     * <p>
     * @param me
     */
    @Override
    public void mouseExited(MouseEvent me)
    {
    }

    /**
     * Je-li klávesa control stisknuta, započne akce dragování (posouvání)
     * diagramu myší. Je také zažádáno o nabytí focusu pro plátno vývojového
     * diagramu.
     *
     * @param me nová událost
     */
    @Override
    public void mousePressed(MouseEvent me)
    {
        mainWindow.setJPanelDiagramFocus();
        if (!mainWindow.getEditMode() && !mainWindow.getAnimationMode()) {
            setStartDragGrab(me);
        }
    }

    /**
     * Resetuje dragování.
     *
     * @param me nová událost
     */
    @Override
    public void mouseReleased(MouseEvent me)
    {
        if (pressedCursorX != null || pressedCursorY != null) {
            resetVariables(me);
        }
    }

    // **********************MouseMotionListener**********************
    /**
     * Je-li nastaveno dragování, posouvá diagram na základě uživatelova
     * kurzoru.
     *
     * @param me nová událost
     */
    @Override
    public void mouseDragged(MouseEvent me)
    {
        if (pressedCursorX != null || pressedCursorY != null) {
            if (!me.getComponent().getCursor().equals(grab)) {
                me.getComponent().setCursor(grab);
            }
            //component.scrollRectToVisible(new Rectangle(me.getX(), me.getY(), 1, 1));

            int moveX = pressedCursorX - me.getXOnScreen();
            int moveY = pressedCursorY - me.getYOnScreen();

            horizontalScrollbar.setValue(horizontalScrollbar.getValue() + moveX);
            verticalScrollbar.setValue(verticalScrollbar.getValue() + moveY);

            if (mainWindow.graphicsXTransformedByScrollbar()) {
                pressedCursorX = me.getXOnScreen();
            }
            if (mainWindow.graphicsYTransformedByScrollbar()) {
                pressedCursorY = me.getYOnScreen();
            }
        }
    }

    /**
     * Resetuje dragování.
     *
     * @param me nová událost
     */
    @Override
    public void mouseMoved(MouseEvent me)
    {
        if (pressedCursorX != null || pressedCursorY != null) {
            resetVariables(me);
        }
    }

    // **********************MouseWheelListener**********************
    /**
     * Je-li stisknuta klávesa ctrl, přiblíží/oddálí vývojový diagram.<br />
     * Není-li stisknuta, provede scrollování plátna diagramu vertikálně, je-li
     * stisknuta klávesa alt, horizontálně.
     *
     * @param mwe nová událost
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe)
    {
        if (mwe.isControlDown()) { // zoomovani
            if (mwe.getWheelRotation() < 0) { // zoom-in
                int zoomValue = sliderZoom.getValue();
                if (zoomValue == sliderZoom.getMaximum()) {
                    return;
                }
                zoomAnchorPoint = mwe.getPoint();
                sliderZoom.setValue(zoomValue + 1);
            } else { // zoom-out
                int zoomValue = sliderZoom.getValue();
                if (zoomValue == sliderZoom.getMinimum()) {
                    return;
                }
                zoomAnchorPoint = mwe.getPoint();
                sliderZoom.setValue(zoomValue - 1);
            }
            return;
        }

        // scrollovani
        int totalScrollAmount = mwe.getWheelRotation() * Math.abs(mwe.getUnitsToScroll());
        if (mwe.isAltDown() || mwe.isShiftDown()) {
            horizontalScrolled = true;
            totalScrollAmount *= horizontalScrollbar.getUnitIncrement();
            horizontalScrollbar.setValue(horizontalScrollbar.getValue() + totalScrollAmount);
        } else {
            totalScrollAmount *= verticalScrollbar.getUnitIncrement();
            verticalScrollbar.setValue(verticalScrollbar.getValue() + totalScrollAmount);
        }
    }

    // **********************KeyListener**********************
    /**
     * Metoda s prázdným tělem.
     * <p>
     * @param ke
     */
    @Override
    public void keyPressed(KeyEvent ke)
    {
    }

    /**
     * Je-li puštěná klávesa alt a bylo horizontálně scrollováno, tato událost
     * bude zkonzumována pro zabránění nativní funkce označení menu aplikace.
     *
     * @param ke nová událost
     */
    @Override
    public void keyReleased(KeyEvent ke)
    {
        if (ke.getKeyCode() == KeyEvent.VK_ALT && horizontalScrolled) { // je treba zamezit pri ALT-scrollovani ztratu focusu
            horizontalScrolled = false;
            ke.consume();
        }
    }

    /**
     * Metoda s prázdným tělem.
     * <p>
     * @param ke
     */
    @Override
    public void keyTyped(KeyEvent ke)
    {
    }

    // **********************ChangeListener**********************
    /**
     * Provede oddálení/přiblížení diagramu na základě posunutí slideru pro
     * zoomování.
     *
     * @param ce nová událost
     */
    @Override
    public void stateChanged(ChangeEvent ce)
    {
        int value = sliderZoom.getValue();
        mainWindow.setJLabelZoomText((value * 10) + "%");
        mainWindow.setScale(value * 0.1, zoomAnchorPoint);
        zoomAnchorPoint = null;
    }

    protected void setStartDragGrab(MouseEvent me)
    {
        pressedCursorX = me.getXOnScreen();
        pressedCursorY = me.getYOnScreen();
    }

    // ***********************************************************
    // **********************PRIVATE METHODS**********************
    // ***********************************************************
    private void resetVariables(MouseEvent me)
    {
        pressedCursorX = null;
        pressedCursorY = null;
        if (me.getComponent().getCursor().equals(grab)) {
            me.getComponent().setCursor(Cursor.getDefaultCursor());
        }
    }

}
