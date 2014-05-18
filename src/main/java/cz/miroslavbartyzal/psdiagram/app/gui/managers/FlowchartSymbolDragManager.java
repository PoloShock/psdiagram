/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.managers;

import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.Layout;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Joint;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.global.GlobalFunctions;
import cz.miroslavbartyzal.psdiagram.app.gui.MainWindow;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.dnd.DragSource;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.xml.bind.JAXBException;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class FlowchartSymbolDragManager
{

    private final Layout layout;
    private final JPanel canvasPanel;
    private LayoutElement processedElement = null;
    private ArrayList<LayoutElement> elementsToMove = null;
    private ArrayList<LayoutElement> elementsToAdd = null;
    private Symbol processedSymbol = null;
    private Joint defaultJoint = null;
    private Joint dockedJoint = null;
    private boolean dragging = false;
    private boolean copying = false;
    private final double SCALE = 0.5;
    private String dragAction = null;
    private Point2D lastP = new Point2D.Double();
    private Point2D lastPScaled = new Point2D.Double();

    public FlowchartSymbolDragManager(Layout layout, JPanel canvasPanel)
    {
        this.layout = layout;
        this.canvasPanel = canvasPanel;
    }

    protected void mousePressedOnSymbol(LayoutElement element, Point2D mouseCoords)
    {
        processedElement = element;
        try {
            processedSymbol = cloneElement(element).getSymbol();
        } catch (JAXBException ex) {
            ex.printStackTrace(System.err);
        }
    }

    /**
     *
     * @return true kdyz byla provedena zmena
     */
    protected boolean mouseReleased()
    {
        boolean retVal = false;
        processedElement = null;
        processedSymbol = null;

        if (dragging) {
            if (!copying) {
                if (!isFocusedJointDefaultJoint()) {
                    retVal = true;
                    dragAction = "Přesunutí symbolu";
                }
                layout.addElements(elementsToMove);
            } else {
                canvasPanel.setCursor(Cursor.getDefaultCursor());
                if (dockedJoint != null) {
                    layout.addElements(elementsToAdd);
                    retVal = true;
                    dragAction = "Kopírování symbolu";
                }
            }

            dragging = false;
            defaultJoint = null;
            dockedJoint = null;
            elementsToMove = null;
            elementsToAdd = null;
            copying = false;
            layout.setFocusPaintToDefault(); // just in case... (needed when copied but actually no symbols were added)
        }
        return retVal;
    }

    protected boolean isDragging()
    {
        return dragging;
    }

    protected boolean isAbleToDrag()
    {
        return processedElement != null;
    }

    protected boolean isSymbolBeingDragged()
    {
        return dragging;
    }

    /**
     *
     * @param isControlDown
     * @return true if repaint of flowchart is needed
     */
    protected boolean controlKeyUpdate(boolean isControlDown)
    {
        if (dragging) {
            if (isControlDown && !copying) {
                copying = true;

                if (elementsToAdd == null) {
                    elementsToAdd = new ArrayList<>();
                    try {
                        for (LayoutElement element : elementsToMove) {
                            elementsToAdd.add(cloneElement(element));
                        }
                    } catch (JAXBException ex) {
                        ex.printStackTrace(System.err);
                        copying = false;
                        elementsToAdd = null;
                        return false;
                    }
                }

                canvasPanel.setCursor(DragSource.DefaultCopyDrop);
                if (dockedJoint != null) {
                    layout.setFocusedJoint(defaultJoint);
                    layout.addElements(elementsToMove);
                    layout.setFocusedJoint(dockedJoint);
                    dockedJoint = layout.getFocusedJoint(); // refresh its position etc.
                    processedSymbol.setCenterX(dockedJoint.getCenterX() / SCALE);
                    processedSymbol.setCenterY(dockedJoint.getCenterY() / SCALE);
                    layout.setFocusJointOnly();
                    performMouseUpdate(); // symbol has potentionaly moved
                } else {
                    layout.addElements(elementsToMove);
                    layout.setNoFocusPaint();
                }
                return true;
            }
            if (!isControlDown && copying) {
                copying = false;
                canvasPanel.setCursor(Cursor.getDefaultCursor());
                layout.setFocusedElement(processedElement);
                layout.removeElementFocusItsJoint(processedElement);
                layout.setFocusJointOnly();
                if (dockedJoint != null) {
                    layout.setFocusedJoint(dockedJoint);
                    dockedJoint = layout.getFocusedJoint(); // refresh its position etc.
                    processedSymbol.setCenterX(dockedJoint.getCenterX() / SCALE);
                    processedSymbol.setCenterY(dockedJoint.getCenterY() / SCALE);
                    performMouseUpdate(); // symbol has potentionaly moved
                }
                return true;
            }
        }
        return false;
    }

    private LayoutElement cloneElement(LayoutElement element) throws JAXBException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MainWindow.getJAXBcontext().createMarshaller().marshal(element, baos);
        return GlobalFunctions.unsafeCast(
                MainWindow.getJAXBcontext().createUnmarshaller().unmarshal(new ByteArrayInputStream(
                                baos.toByteArray())));
    }

    private boolean isFocusedJointDefaultJoint()
    {
        return layout.getFocusedJoint().getParentElement().equals(defaultJoint.getParentElement())
                && layout.getFocusedJoint().getParentSegment().equals(
                        defaultJoint.getParentSegment());
    }

    protected void performDrag(Point2D p, boolean isControlDown)
    {
        lastP = p;
        if (!dragging && !processedElement.getSymbol().contains(p)) { // cloned processedSymbol might not have proper coordenates
            dragging = true;
            dragAction = null;
            elementsToMove = layout.getMeAndMyDependants(processedElement);
            layout.removeElementFocusItsJoint(processedElement);
            layout.setFocusJointOnly();
            defaultJoint = layout.getFocusedJoint();
        }
        if (dragging) {
            controlKeyUpdate(isControlDown);
            lastPScaled = new Point2D.Double(p.getX() / SCALE, p.getY() / SCALE);
            performMouseUpdate();
        }
    }

    private void performMouseUpdate()
    {
        if (dockedJoint != null && (processedSymbol.contains(lastPScaled) || dockedJoint.contains(
                lastP))) { // dockedJoint.contains je tu pro symbol GotoLabel
            return; // mouse is still inside the docked symbol
        }
        for (Joint joint : layout.getlJoints()) {
            processedSymbol.setCenterX(joint.getCenterX() / SCALE);
            processedSymbol.setCenterY(joint.getCenterY() / SCALE);
            if (processedSymbol.contains(lastPScaled) || joint.contains(lastP)) { // joint.contains je tu pro symbol GotoLabel
                layout.setFocusedJoint(joint);
                dockedJoint = joint;
                if (copying) {
                    layout.setFocusJointOnly();
                }
                return;
            }
        }
        if (copying) {
            layout.setNoFocusPaint();
        }
        dockedJoint = null;
        processedSymbol.setCenterX(lastPScaled.getX());
        processedSymbol.setCenterY(lastPScaled.getY());
        layout.setFocusedJoint(defaultJoint);
    }

    public String getDragAction()
    {
        return dragAction;
    }

    public void paint(Graphics2D grphcs2D)
    {
        if (dragging && processedSymbol != null) {
            grphcs2D.scale(SCALE, SCALE);
            grphcs2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

//            Color shapeUpColor = processedSymbol.getShapeUpColor();
//            Color shapeDownColor = processedSymbol.getShapeDownColor();
//            Color borderColor = processedSymbol.getBorderColor();
            Color shapeUpColor = new Color(processedSymbol.getShapeUpColor().getRed(),
                    processedSymbol.getShapeUpColor().getGreen(),
                    processedSymbol.getShapeUpColor().getBlue(), 220);
            Color shapeDownColor = new Color(processedSymbol.getShapeDownColor().getRed(),
                    processedSymbol.getShapeDownColor().getGreen(),
                    processedSymbol.getShapeDownColor().getBlue(), 220);
            Color borderColor = new Color(processedSymbol.getBorderColor().getRed(),
                    processedSymbol.getBorderColor().getGreen(),
                    processedSymbol.getBorderColor().getBlue(), 220);

            // inicializace barev (gradient ci nikoliv)
            if (processedSymbol.getShapeUpColor().equals(processedSymbol.getShapeDownColor())) {
                grphcs2D.setColor(shapeUpColor);
            } else {
                grphcs2D.setPaint(new GradientPaint(
                        (float) (processedSymbol.getX() + processedSymbol.getWidth() * 0.25),
                        (float) processedSymbol.getY(), shapeUpColor,
                        (float) (processedSymbol.getX() + processedSymbol.getWidth() * 0.75),
                        (float) (processedSymbol.getY() + processedSymbol.getHeight()),
                        shapeDownColor));
            }
            // vykreslení symbolu
            grphcs2D.fill(processedSymbol.getShape());
            // vykreslení okraje symbolu
            grphcs2D.setColor(borderColor);
            grphcs2D.setStroke(new BasicStroke(1));
            grphcs2D.draw(processedSymbol.getShape());

            // vykresleni textu symbolu
            for (int i = 0; i < processedSymbol.getTextLayoutLines().size(); i++) {
                Point2D p = processedSymbol.getTextLayoutOrigins().get(i);
                processedSymbol.getTextLayoutLines().get(i).draw(grphcs2D, (float) p.getX(),
                        (float) p.getY());
            }
        }
    }

}
