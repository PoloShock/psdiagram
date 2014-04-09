/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.managers;

import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.Layout;
import cz.miroslavbartyzal.psdiagram.app.gui.MainWindow;
import cz.miroslavbartyzal.psdiagram.app.gui.managers.undoableEdits.UniversalEdit;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import javax.xml.bind.JAXBException;

/**
 * Tato třída je vrchním správcem Undo/Redo funkcionality aplikace.<br />
 * Třída se také automaticky stará o nastavení tlačítek undo/redo v aplikaci.
 * Jmenovitě jejich enabled stavu a tooltiptextů.<br />
 * Maximální počet undo/redo akcí je stanoven na 1000.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class FlowchartEditUndoManager extends UndoManager
{

    private final JMenuItem jMenuItemUndo;
    private final JMenuItem jMenuItemRedo;
    private final JButton jButtonToolUndo;
    private final JButton jButtonToolRedo;
    private int[] beforeFocusedPath;
    private boolean beforeIsJoint;
    private ByteArrayOutputStream before;

    /**
     * Konstruktor, zajišťující základní spojení s klíčovými prvky uživatelského
     * rozhraní.
     *
     * @param jMenuItemUndo tlačítko pro undo akci v menu aplikace
     * @param jMenuItemRedo tlačítko pro redo akci v menu aplikace
     * @param jButtonToolUndo tlačítko pro undo akci na hlavní liště aplikace
     * @param jButtonToolRedo tlačítko pro redo akci na hlavní liště aplikace
     */
    public FlowchartEditUndoManager(JMenuItem jMenuItemUndo, JMenuItem jMenuItemRedo,
            JButton jButtonToolUndo, JButton jButtonToolRedo)
    {
        this.jMenuItemUndo = jMenuItemUndo;
        this.jMenuItemRedo = jMenuItemRedo;
        this.jButtonToolUndo = jButtonToolUndo;
        this.jButtonToolRedo = jButtonToolRedo;

        super.setLimit(1000);
    }

    protected void init(Layout layout)
    {
        beforeFocusedPath = getFocusedPath(layout);
        beforeIsJoint = layout.getFocusedJoint() != null;
        before = new ByteArrayOutputStream();
        try {
            MainWindow.getJAXBcontext().createMarshaller().marshal(layout.getFlowchart(), before);
        } catch (JAXBException ex) {
            ex.printStackTrace(System.err);
        }
    }

    protected void addEdit(Layout layout, FlowchartEditManager flowchartEditManager,
            String presentationName)
    {
        if (before != null) {
            ByteArrayOutputStream current = new ByteArrayOutputStream();
            try {
                MainWindow.getJAXBcontext().createMarshaller().marshal(layout.getFlowchart(),
                        current);
            } catch (JAXBException ex) {
                ex.printStackTrace(System.err);
            }
            if (!Arrays.equals(before.toByteArray(), current.toByteArray())) {
                int[] currentFocusedPath = getFocusedPath(layout);
                boolean currentIsJoint = layout.getFocusedJoint() != null;

                addEdit(new UniversalEdit(flowchartEditManager, before, current, beforeFocusedPath,
                        beforeIsJoint, currentFocusedPath, currentIsJoint, presentationName));

                beforeFocusedPath = currentFocusedPath;
                beforeIsJoint = currentIsJoint;
                before = current;
            }
        } else {
            discardAllEdits();
        }
    }

    /**
     * Nastaví takové hodnoty tlačítkům undo/redo, aby odpovídali stavu této
     * třídy.
     */
    public synchronized void setButtons()
    {
        if (super.canUndo()) {
            String tooltip = super.editToBeUndone().getPresentationName();
            jMenuItemUndo.setEnabled(true);
            jMenuItemUndo.setToolTipText("<html>Zpět " + tooltip + "<br />(Ctrl + Z)</html>");
            jButtonToolUndo.setEnabled(true);
            jButtonToolUndo.setToolTipText("<html>Zpět " + tooltip + "<br />(Ctrl + Z)</html>");

        } else {
            jMenuItemUndo.setEnabled(false);
            jMenuItemUndo.setToolTipText("<html>Zpět<br />(Ctrl + Z)</html>");
            jButtonToolUndo.setEnabled(false);
            jButtonToolUndo.setToolTipText("<html>Zpět<br />(Ctrl + Z)</html>");
        }
        if (super.canRedo()) {
            String tooltip = super.editToBeRedone().getPresentationName();
            jMenuItemRedo.setEnabled(true);
            jMenuItemRedo.setToolTipText("<html>Znovu " + tooltip + "<br />(Ctrl + Y)</html>");
            jButtonToolRedo.setEnabled(true);
            jButtonToolRedo.setToolTipText("<html>Znovu " + tooltip + "<br />(Ctrl + Y)</html>");
        } else {
            jMenuItemRedo.setEnabled(false);
            jMenuItemRedo.setToolTipText("<html>Znovu<br />(Ctrl + Y)</html>");
            jButtonToolRedo.setEnabled(false);
            jButtonToolRedo.setToolTipText("<html>Znovu<br />(Ctrl + Y)</html>");
        }
    }

    private int[] getFocusedPath(Layout layout)
    {
        if (layout.getFocusedElement() != null) {
            return layout.getFocusedElement().getPathFromMainSegment();
        } else if (layout.getFocusedJoint().getParentElement().getInnerSegmentsCount() == 0) {
            return layout.getFocusedJoint().getParentElement().getPathFromMainSegment();
        } else {
            return layout.getFocusedJoint().getParentSegment().getPathFromMainSegment();
        }
    }

    /**
     * Metoda volá metodu rodičovskou, navíc nastaví odpovídající stav tlačítkům
     * pro undo/redo akci v uživatelském prostředí.
     */
    @Override
    public synchronized boolean addEdit(UndoableEdit ue)
    {
        boolean ret = super.addEdit(ue);
        setButtons();
        return ret;

    }

    /**
     * Metoda volá metodu rodičovskou, navíc nastaví odpovídající stav tlačítkům
     * pro undo/redo akci v uživatelském prostředí.
     */
    @Override
    public synchronized void discardAllEdits()
    {
        super.discardAllEdits();
        setButtons();
    }

    /**
     * Metoda volá metodu rodičovskou, navíc nastaví odpovídající stav tlačítkům
     * pro undo/redo akci v uživatelském prostředí.
     */
    @Override
    public synchronized void end()
    {
        super.end();
        setButtons();
    }

    /**
     * Metoda volá metodu rodičovskou, navíc nastaví odpovídající stav tlačítkům
     * pro undo/redo akci v uživatelském prostředí.
     */
    @Override
    public synchronized void redo() throws CannotRedoException
    {
        UniversalEdit UEdit = (UniversalEdit) super.editToBeRedone();
        super.redo();
        init(UEdit.getFlowchartEditManager().getLayout());
        setButtons();
    }

    /**
     * Metoda volá metodu rodičovskou, navíc nastaví odpovídající stav tlačítkům
     * pro undo/redo akci v uživatelském prostředí.
     */
    @Override
    protected void redoTo(UndoableEdit ue) throws CannotRedoException
    {
        super.redoTo(ue);
        setButtons();
    }

    /**
     * Metoda volá metodu rodičovskou, navíc nastaví odpovídající stav tlačítkům
     * pro undo/redo akci v uživatelském prostředí.
     */
    @Override
    protected void trimEdits(int i, int i1)
    {
        super.trimEdits(i, i1);
        setButtons();
    }

    /**
     * Metoda volá metodu rodičovskou, navíc nastaví odpovídající stav tlačítkům
     * pro undo/redo akci v uživatelském prostředí.
     */
    @Override
    public synchronized void undo() throws CannotUndoException
    {
        UniversalEdit UEdit = (UniversalEdit) super.editToBeUndone();
        super.undo();
        init(UEdit.getFlowchartEditManager().getLayout());
        setButtons();
    }

    /**
     * Metoda volá metodu rodičovskou, navíc nastaví odpovídající stav tlačítkům
     * pro undo/redo akci v uživatelském prostředí.
     */
    @Override
    protected void undoTo(UndoableEdit ue) throws CannotUndoException
    {
        super.undoTo(ue);
        setButtons();
    }

}
