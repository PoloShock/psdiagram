/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.managers.undoableEdits;

import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Joint;
import cz.miroslavbartyzal.psdiagram.app.global.GlobalFunctions;
import cz.miroslavbartyzal.psdiagram.app.gui.MainWindow;
import cz.miroslavbartyzal.psdiagram.app.gui.managers.FlowchartEditManager;
import cz.miroslavbartyzal.psdiagram.app.gui.managers.FlowchartEditUndoManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.xml.bind.JAXBException;

/**
 * <p>
 * Tato třída představuje univerzální Undo/Redo akci. Je paměťově méně
 * náročná než ostatní metody implementace (třída AddSymbolEdit,
 * PasteSymbolEdit) a je méně komplikovaná, navíc univerzální. Nedochází zde ani
 * k desynchronizaci (kvůli javovske getByValue), která hrozila u předchozích
 * metod implementace.</p>
 *
 * <p>
 * Univerzálnosti je docíleno tak, že se otiskne XML podoba vývojového
 * diagramu před změnou, a po jeho změně. Tyto dva XML záznamy jsou poté
 * porovnány, jsou vyhledány rozdíly a ty jsou uloženy do paměti. Tímto způsobem
 * lze docílit i velmi nízké paměťové náročnosti, není totiž ukládán celý XML
 * export.<br />
 * XML export je navíc nativně zbaven veškerých nepotřebných informací. Nejsou
 * zde ukládány pozice symbolů, spojnice a další, je zde uložena pouze logická
 * struktura diagramu.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class UniversalEdit extends AbstractUndoableEdit
{

    private int firstIndex;
    private byte[] undoChange;
    private byte[] redoChange;
    private final FlowchartEditManager flowchartEditManager;
    private final FlowchartEditUndoManager flowchartEditUndoManager;
    private int[] beforeFocusedPath;
    private boolean beforeIsJoint;
    private int[] afterFocusedPath;
    private boolean afterIsJoint;
    private String presentationName;

    /**
     * Vytvoří univerzální Undo/Redo akci.
     *
     * @param flowchartEditManager instance FlowchartEditManagera, spravující
     * editační režim aplikace
     * @param flowchartEditUndoManager instance FlowchartEditUndoManager, spravující
     * undo/redo akce jako je tato
     * @param before předchozí XML podoba vývojového diagramu
     * @param after nynější XML podoba vývojového diagramu (po změně)
     * @param beforeFocusedPath cesta k předchozímu označenému symbolu (viz.
     * metoda getPathFromMainSegment() v LayoutElementu)
     * @param beforeIsJoint true, je-li označen joint
     * @param afterFocusedPath cesta k aktuálně označenému symbolu (po změně)
     * @param afterIsJoint true, je-li aktuálně označen joint (po změně)
     * @param presentationName jméno, pod kterým tato Undo/Redo akce má být
     * prezentována - např. v tooltipu tlačítek Undo/Redo
     */
    public UniversalEdit(FlowchartEditManager flowchartEditManager,
            FlowchartEditUndoManager flowchartEditUndoManager, ByteArrayOutputStream before,
            ByteArrayOutputStream after, int[] beforeFocusedPath, boolean beforeIsJoint,
            int[] afterFocusedPath, boolean afterIsJoint, String presentationName)
    {
        this.flowchartEditManager = flowchartEditManager;
        this.flowchartEditUndoManager = flowchartEditUndoManager;
        this.beforeFocusedPath = beforeFocusedPath;
        this.beforeIsJoint = beforeIsJoint;
        this.afterFocusedPath = afterFocusedPath;
        this.afterIsJoint = afterIsJoint;
        this.presentationName = presentationName;

        byte[] smallerArr;
        byte[] largerArr;
        if (before.size() < after.size()) {
            smallerArr = before.toByteArray();
            largerArr = after.toByteArray();
        } else {
            smallerArr = after.toByteArray();
            largerArr = before.toByteArray();
        }

        for (int i = 0; i < smallerArr.length; i++) {
            if (smallerArr[i] != largerArr[i]) {
                firstIndex = i;
                break;
            }
        }
        int remainLength = smallerArr.length - firstIndex; // vcetne prave oznaceneho
        for (int i = 1; i <= remainLength; i++) {
            if (smallerArr[smallerArr.length - i] != largerArr[largerArr.length - i] || i == remainLength) { // nechceme duplicitu ani volani metody..
                if (i == remainLength && smallerArr[smallerArr.length - i] == largerArr[largerArr.length - i]) {
                    i++;
                }
                int largerLastIndex = largerArr.length - i;
                int smallerLastIndex = smallerArr.length - i;

                byte[] largerChange = new byte[largerLastIndex - firstIndex + 1];
                for (int j = firstIndex; j <= largerLastIndex; j++) {
                    largerChange[j - firstIndex] = largerArr[j];
                }
                byte[] smallerChange = new byte[smallerLastIndex - firstIndex + 1];
                for (int j = firstIndex; j <= smallerLastIndex; j++) {
                    smallerChange[j - firstIndex] = smallerArr[j];
                }

                if (before.size() < after.size()) {
                    undoChange = smallerChange;
                    redoChange = largerChange;
                } else {
                    undoChange = largerChange;
                    redoChange = smallerChange;
                }

//                System.out.println(before);
//                System.out.println(after);
//                System.out.println(new String(undoChange));
//                System.out.println(new String(redoChange));
                break;
            }
        }

    }

    /**
     * Provede Redo akci.
     */
    @Override
    public synchronized void redo() throws CannotRedoException
    {
        super.redo();

        Flowchart<LayoutSegment, LayoutElement> futureFlowchart = getFutureFlowchart(undoChange,
                redoChange);
        if (futureFlowchart != null) {
            flowchartEditManager.getLayout().setFlowchart(futureFlowchart);
            setFocused(afterFocusedPath, afterIsJoint);

            flowchartEditManager.resetVariables();
            flowchartEditManager.loadMarkedSymbolText();
            flowchartEditManager.repaintJPanelDiagram();
        }
    }

    /**
     * Provede Undo akci.
     */
    @Override
    public synchronized void undo() throws CannotUndoException
    {
        super.undo();

        Flowchart<LayoutSegment, LayoutElement> futureFlowchart = getFutureFlowchart(redoChange,
                undoChange);
        if (futureFlowchart != null) {
            flowchartEditManager.getLayout().setFlowchart(futureFlowchart);
            setFocused(beforeFocusedPath, beforeIsJoint);

            flowchartEditManager.resetVariables();
            flowchartEditManager.loadMarkedSymbolText();
            flowchartEditManager.repaintJPanelDiagram();
        }
    }

    public synchronized ByteArrayOutputStream getUndoFlowchart()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] undoFlowchart = getFutureFlowchartAsArray(redoChange, undoChange);
        baos.write(undoFlowchart, 0, undoFlowchart.length);
        return baos;
    }

    /**
     * Vrátí prezentační název této univerzální Undo/Redo akce.
     * <p/>
     * @return prezentační název této Undo/Redo akce
     */
    @Override
    public String getPresentationName()
    {
        return presentationName;
    }

    private Flowchart<LayoutSegment, LayoutElement> getFutureFlowchart(byte[] currentChange,
            byte[] futureChange)
    {
        Flowchart<LayoutSegment, LayoutElement> futureFlowchart = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(getFutureFlowchartAsArray(
                    currentChange, futureChange));
            futureFlowchart = GlobalFunctions.unsafeCast(
                    MainWindow.unmarshal(bais));
        } catch (JAXBException | IndexOutOfBoundsException ex) {
            ex.printStackTrace(System.err);
            flowchartEditManager.resetUndoManager();
        }
        return futureFlowchart;
    }

    private byte[] getFutureFlowchartAsArray(byte[] currentChange, byte[] futureChange)
    {
        ByteArrayOutputStream baos = flowchartEditUndoManager.getLastSeenFlowchart();
        byte[] curFl = baos.toByteArray();
        byte[] futureFl = new byte[curFl.length - currentChange.length + futureChange.length];

        System.arraycopy(curFl, 0, futureFl, 0, firstIndex);
        System.arraycopy(futureChange, 0, futureFl, firstIndex, futureChange.length);
        System.arraycopy(curFl, firstIndex + currentChange.length, futureFl,
                firstIndex + futureChange.length,
                futureFl.length - firstIndex - futureChange.length);

//            System.out.println();
//            System.out.println(new String(curFl));
//            System.out.println(new String(futureFl));
//
//            System.out.println();
//            System.out.println();
//            System.out.println(new String(undoChange));
//            System.out.println(new String(redoChange));
//            System.out.println();
        return futureFl;
    }

    private void setFocused(int[] focusedPath, boolean focusedIsJoint)
    {
        LayoutSegment segment = flowchartEditManager.getLayout().getFlowchart().getMainSegment();
        LayoutElement element = segment.getElement(focusedPath[0]);

        int i = 1;
        while (i < focusedPath.length) {
            segment = element.getInnerSegment(focusedPath[i]);
            if (i + 1 < focusedPath.length) {
                element = segment.getElement(focusedPath[i + 1]);
            }
            i += 2;
        }

        if (focusedIsJoint) {
            Joint joint = new Joint(element, segment);
            flowchartEditManager.getLayout().setFocusedJoint(joint);
        } else {
            flowchartEditManager.getLayout().setFocusedElement(element);
        }
    }

    public FlowchartEditManager getFlowchartEditManager()
    {
        return flowchartEditManager;
    }

    public int[] getBeforeFocusedPath()
    {
        return beforeFocusedPath;
    }

    public boolean isBeforeIsJoint()
    {
        return beforeIsJoint;
    }

}
