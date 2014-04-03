/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.managers.undoableEdits;

import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Comment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Joint;
import cz.miroslavbartyzal.psdiagram.app.gui.managers.FlowchartEditManager;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * Tato třída je deprikována a její metody zakomentovány. Sloužila jako
 * Undo/Redo akce, byla ovšem nahrazena třídou UniversalEdit.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
// tento zpusob jsem se rozhodl nepouzivat, je pametove narocna, komplikovana a muze dochazet k desynchronizacim, kvuli javovskemu byValue
class AddSymbolEdit extends AbstractUndoableEdit
{
//    private FlowchartEditManager flowchartEditManager;
//    private LayoutElement preFocusedElement;
//    private Joint preFocusedJoint;
//
//    private ArrayList<LayoutElement> addedElements;
//
//    public AddSymbolEdit(FlowchartEditManager flowchartEditManager, LayoutElement preFocusedElement, Joint preFocusedJoint, ArrayList<LayoutElement> addedElements) {
//        this.flowchartEditManager = flowchartEditManager;
//        this.preFocusedElement = preFocusedElement;
//        this.preFocusedJoint = preFocusedJoint;
//        this.addedElements = addedElements;
//    }
//
//    @Override
//    public synchronized void redo() throws CannotRedoException {
//        super.redo();
//        if (preFocusedElement != null) {
//            flowchartEditManager.getLayout().setFocusedElement(preFocusedElement);
//        } else {
//            flowchartEditManager.getLayout().setFocusedJoint(preFocusedJoint);
//        }
//        flowchartEditManager.getLayout().addElements(addedElements);
//
//        // je treba ulozit pozice komentaru muze dojit k jejich desynchronizaci...
//        HashMap<LayoutElement, Integer> commentIndexs = new HashMap<>();
//        for (LayoutElement element: addedElements) {
//            if (element.getSymbol() instanceof Comment) {
//                commentIndexs.put(element, element.getParentSegment().indexOfElement(element));
//            }
//        }
//
//        flowchartEditManager.refreshComments();
//
//        for (LayoutElement element: commentIndexs.keySet()) { // zde zabranuji desynchronizaci komentaru
//            addedElements.set(addedElements.indexOf(element), element.getParentSegment().getElement(commentIndexs.get(element)));
//        }
//
//        flowchartEditManager.loadMarkedSymbol();
//        flowchartEditManager.repaintJPanelDiagram();
//    }
//
//    @Override
//    public synchronized void undo() throws CannotUndoException {
//        super.undo();
//        for (LayoutElement element: addedElements) {
//            if (element.getParentSegment().containsElement(element)) { // otestovat, pri vlozeni nekolika symbolu by se to melo jinak zacyklit?
//                flowchartEditManager.getLayout().setFocusedElement(element);
//                flowchartEditManager.removeFocusedElement();
//            }
//        }
//        if (preFocusedElement != null) {
//            flowchartEditManager.getLayout().setFocusedElement(preFocusedElement);
//        } else {
//            flowchartEditManager.getLayout().setFocusedJoint(preFocusedJoint);
//        }
//        flowchartEditManager.loadMarkedSymbol();
//        flowchartEditManager.repaintJPanelDiagram();
//    }
//
//    @Override
//    public String getPresentationName() {
//        return "Přidání symbolu";
//    }
}
