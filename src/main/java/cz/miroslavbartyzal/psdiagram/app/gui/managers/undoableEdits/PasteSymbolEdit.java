/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.managers.undoableEdits;

import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Joint;
import cz.miroslavbartyzal.psdiagram.app.gui.managers.FlowchartEditManager;
import java.util.ArrayList;

/**
 * Tato třída je deprikována a její metody zakomentovány. Sloužila jako
 * Undo/Redo akce, byla ovšem nahrazena třídou UniversalEdit.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
// tento zpusob jsem se rozhodl nepouzivat, je pametove narocna, komplikovana a muze dochazet k desynchronizacim, kvuli javovskemu byValue
final class PasteSymbolEdit extends AddSymbolEdit
{
//    public PasteSymbolEdit(FlowchartEditManager flowchartEditManager, LayoutElement preFocusedElement, Joint preFocusedJoint, ArrayList<LayoutElement> addedElements) {
//        super(flowchartEditManager, preFocusedElement, preFocusedJoint, addedElements);
//    }
//
//    @Override
//    public String getPresentationName() {
//        return "Vložení symbolu";
//    }
}
