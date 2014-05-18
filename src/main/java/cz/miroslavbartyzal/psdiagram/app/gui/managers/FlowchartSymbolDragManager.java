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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class FlowchartSymbolDragManager
{

    private final Layout layout;
    private LayoutElement processedElement = null;
    private ArrayList<LayoutElement> elementsToMove = null;
    private Symbol processedSymbol = null;
    private Joint defaultJoint = null;
    private Joint dockedJoint = null;
    private boolean dragging = false;
    private final double SCALE = 0.5;

    public FlowchartSymbolDragManager(Layout layout)
    {
        this.layout = layout;
    }

    protected void mousePressedOnSymbol(LayoutElement element, Point2D mouseCoords)
    {
        processedElement = element;
        processedSymbol = element.getSymbol();
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
            if (!layout.getFocusedJoint().getParentElement().equals(defaultJoint.getParentElement())
                    || !layout.getFocusedJoint().getParentSegment().equals(
                            defaultJoint.getParentSegment())) {
                retVal = true;
            }
            dragging = false;
            defaultJoint = null;
            dockedJoint = null;

            layout.addElements(elementsToMove);
            elementsToMove = null;
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

    protected void performDrag(Point2D p)
    {
        if (!dragging && !processedSymbol.contains(p)) {
            dragging = true;
            elementsToMove = layout.getMeAndMyDependants(processedElement);
            layout.removeElementTemporarily(processedElement);
            defaultJoint = layout.getFocusedJoint();
        }
        if (dragging) {
            Point2D pScaled = new Point2D.Double(p.getX() / SCALE, p.getY() / SCALE);
            if (dockedJoint != null && (processedSymbol.contains(pScaled) || dockedJoint.contains(p))) { // dockedJoint.contains je tu pro symbol GotoLabel
                return; // mouse is still inside the docked symbol
            }
            for (Joint joint : layout.getlJoints()) {
                processedSymbol.setCenterX(joint.getCenterX() / SCALE);
                processedSymbol.setCenterY(joint.getCenterY() / SCALE);
                if (processedSymbol.contains(pScaled) || joint.contains(p)) { // joint.contains je tu pro symbol GotoLabel
                    layout.setFocusedJoint(joint);
                    dockedJoint = joint;
                    return;
                }
            }
            dockedJoint = null;
            processedSymbol.setCenterX(pScaled.getX());
            processedSymbol.setCenterY(pScaled.getY());
            layout.setFocusedJoint(defaultJoint);
        }
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
