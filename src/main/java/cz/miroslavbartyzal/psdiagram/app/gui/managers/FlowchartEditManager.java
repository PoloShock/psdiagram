/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.managers;

import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Comment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.StartEnd;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.LoopEnd;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Decision;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Joint;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.EnumSymbol;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.Layout;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;
import cz.miroslavbartyzal.psdiagram.app.gui.MainWindow;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.AbstractSymbolFunctionForm;
import cz.miroslavbartyzal.psdiagram.app.global.GlobalFunctions;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;
import javax.xml.bind.JAXBException;

/**
 * <p>
 * Tato třída je vrchním správcem editačního režimu aplikace. Je zároveň
 * posluchačem událostí přicházejících z uživatelského rozhraní a obstarává tedy
 * předání příkazů layoutu vývojového diagramu.</p>
 * <p>
 * Třída také úzce spolupracuje s třídou FlowchartEditUndoManager. Některé
 * události jsou třídě pro zpracování Undo/Redo předány úmyslně opožděně, pro
 * větší komfort při editaci. Konkrétně se jedná o editaci textu
 * symbolu/segmentu, kdy není vhodné zaznamenávat změny po jednom znaku, nýbrž
 * až po provedené editaci.<p/>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class FlowchartEditManager implements ActionListener, MouseListener,
        MouseMotionListener, DocumentListener, KeyListener, FocusListener
{

    // TODO pridat do nastaveni skalu barev cest pri animaci - nejmene 3
    // TODO pridat do nastaveni reset do defaultniho nastaveni
    private final Layout layout;
    private final MainWindow mainWindow;
    private JPopupMenu symbolPopup;
    private JMenuItem jSymbolPopupDelete;
    private JMenuItem jSymbolPopupCut;
    private JMenuItem jSymbolPopupCopy;
    private JMenuItem jSymbolPopupPaste;
    private final JCheckBox jCheckBoxDefaultText;
    private final JComboBox<String> jComboBoxSegment;
    private final JTextField jTextFieldTextSegment;
    private final JTextArea jTextAreaTextSymbol;
    private final FlowchartEditUndoManager flowchartEditUndoManager;
    private final FlowchartCommentSymbolManager commentsManager;
    private final FlowchartSymbolDragManager symbolDragManager;
    private LinkedHashMap<ByteArrayOutputStream, Boolean> elementsToPaste;
    private LayoutElement lastMarkedElement = null; // pro identifikaci posledniho elementu, pro ktery bylo zobrazeno editovani deskripce segmentů apod. - aby se index comboboxu nevracel kdyz nema apod.
    private boolean puttingText = false; // slouzi pro signalizaci DocumentListeneru, ze prave probiha aplikacni vkladani textu
    private boolean segmentTextBuffered = false; // pro bufferovani editcniho pole textu segmentu a jeho zalozohavni do undomanagera
    private boolean symbolTextBuffered = false; // pro bufferovani editcniho pole textu symbolu a jeho zalozohavni do undomanagera
    private boolean defaultTextBeingEdited = false;

    /**
     * Konstruktor, zajišťující základní spojení s klíčovými prvky uživatelského
     * rozhraní.
     *
     * @param layout layout vývojového diagramu
     * @param mainWindow hlavní okno aplikace
     * @param flowchartEditUndoManager správce funkce undo/redo
     * @param jCheckBoxDefaultText checkbox pro přepínání
     * defaultního/uživateského textu symbolu
     * @param jComboBoxSegment combobox pro výběr větve symbolu k editaci textu
     * @param jTextFieldTextSegment textové pole textu segmentu
     * @param jTextAreaTextSymbol textová oblast textu symbolu
     */
    public FlowchartEditManager(Layout layout, MainWindow mainWindow,
            FlowchartEditUndoManager flowchartEditUndoManager, JCheckBox jCheckBoxDefaultText,
            JComboBox<String> jComboBoxSegment, JTextField jTextFieldTextSegment,
            JTextArea jTextAreaTextSymbol)
    {
        this.layout = layout;
        this.mainWindow = mainWindow;
        this.flowchartEditUndoManager = flowchartEditUndoManager;
        this.jCheckBoxDefaultText = jCheckBoxDefaultText;
        this.jComboBoxSegment = jComboBoxSegment;
        this.jTextFieldTextSegment = jTextFieldTextSegment;
        this.jTextAreaTextSymbol = jTextAreaTextSymbol;

        commentsManager = new FlowchartCommentSymbolManager(layout);
        symbolDragManager = new FlowchartSymbolDragManager(layout);
        loadMarkedSymbol();
        flowchartEditUndoManager.init(layout);
    }

//    /**
//     * Vrátí aktuálně označenou spojnici komentářové cesty. Není-li žádná
//     * vybrána, vrací null.
//     *
//     * @return Aktuálně označenou spojnici komentářové cesty. Není-li žádná
//     * vybrána, vrací null.
//     */
//    public Ellipse2D getCommentPathConnector()
//    {
//        return commentsManager.getCommentPathConnector();
//    }
    /**
     * Vymaže veškeré záznamy UndoManagera.
     */
    public void resetUndoManager()
    {
        flowchartEditUndoManager.discardAllEdits();
        flowchartEditUndoManager.init(layout);
    }

    public void undo()
    {
        doPendingUndoRedos();
        flowchartEditUndoManager.undo();
    }

    public void redo()
    {
        doPendingUndoRedos();
        flowchartEditUndoManager.redo();
    }

    /**
     * Provede reset všech proměnných této třídy do defaultních hodnot.
     */
    public void resetVariables()
    {
        commentsManager.resetVariables();
        defaultTextBeingEdited = false;
    }

    /**
     * Znovu načte všechny komentáře obsažené v diagramu. Tato metoda by měla
     * být volána vždy po jakékoliv modifikaci komentářových symbolů, aby je
     * bylo možné modifikovat krz tuto třídu.
     */
    public void refreshComments()
    {
        commentsManager.refreshComments();
    }

    public void paint(Graphics2D grphcs2D)
    {
        commentsManager.paint(grphcs2D);
        symbolDragManager.paint(grphcs2D);
    }

    /**
     * Překreslí plátno a tím i vývojový diagram.
     */
    public void repaintJPanelDiagram()
    {
        mainWindow.repaintJPanelDiagram();
    }

    /**
     * Vrátí aktuálně používaný layout pro vykreslení vývojového diagramu.
     *
     * @return aktuálně používaný layout vývojového diagramu
     */
    public Layout getLayout()
    {
        return layout;
    }

    /**
     * Načte text právě označeného symbolu, případně i jeho segmentů do
     * odpovídajících textových polí.
     */
    public void loadMarkedSymbolText()
    {
        puttingText = true;
        loadMarkedSymbol();
        puttingText = false;
    }

    /**
     * Uloží popup dialog s možnostmi úprav symbolu.
     *
     * @param symbolPopup popup dialog s možnostmi úprav symbolu
     */
    public void setSymbolPopup(JPopupMenu symbolPopup)
    {
        this.symbolPopup = symbolPopup;
        for (MenuElement menuElement : symbolPopup.getSubElements()) {
            if (menuElement instanceof JMenuItem) {
                JMenuItem jMenuItem = (JMenuItem) menuElement;
                switch (jMenuItem.getActionCommand()) {
                    case "edit/delete":
                        jSymbolPopupDelete = jMenuItem;
                        break;
                    case "edit/cut":
                        jSymbolPopupCut = jMenuItem;
                        break;
                    case "edit/copy":
                        jSymbolPopupCopy = jMenuItem;
                        break;
                    case "edit/paste":
                        jSymbolPopupPaste = jMenuItem;
                        break;
                    default:
                        this.symbolPopup.remove(jMenuItem);
                        break;
                }
            }
        }

        // odstraneni pripadneho prvniho/posledniho separatoru
        for (Component c : this.symbolPopup.getComponents()) {
            if (c instanceof JSeparator) {
                this.symbolPopup.remove(c);
            } else {
                break;
            }
        }
        Component[] cps = this.symbolPopup.getComponents();
        for (int i = cps.length - 1; i > 0; i--) {
            if (cps[i] instanceof JSeparator) {
                this.symbolPopup.remove(cps[i]);
            } else {
                break;
            }
        }

        this.symbolPopup.addPopupMenuListener(new PopupMenuListener()
        {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e)
            {
                updateEditMenuEnablers();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
            {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e)
            {
            }
        });
    }

    /**
     * Metoda zpracuje všechny čekající operace k zápisu do UndoManagera.
     */
    private void doPendingUndoRedos()
    {
        if (segmentTextBuffered) {
            segmentTextBuffered = false;
            flowchartEditUndoManager.addEdit(layout, this, "Editace textu větve symbolu");
        } else if (symbolTextBuffered) {
            symbolTextBuffered = false;
            flowchartEditUndoManager.addEdit(layout, this, "Editace textu symbolu");
        }

        if (commentsManager.isCommentOrJointBeingDragged() || symbolDragManager.isAbleToDrag()) {
            if (commentsManager.isCommentOrJointBeingDragged()) {
                layout.prepareFlowchart(); // pro pripad ze bod ci komentar je umisten za platnem, nebo byl a byl posunut do platna
                flowchartEditUndoManager.addEdit(layout, this, commentsManager.getCommentAction());
            } else if (symbolDragManager.isDragging()) {
                if (symbolDragManager.mouseReleased()) {
                    flowchartEditUndoManager.addEdit(layout, this, "Přesunutí symbolu");
                }
            }
            repaintJPanelDiagram();
        }

        resetVariables();

    }

    // **********************ActionListener**********************
    /**
     * Metoda pro příjem událostí, týkající se editace vývojového diagramu.
     *
     * @param ae nová událost
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        doPendingUndoRedos();
        String[] action = ae.getActionCommand().split("/");
        switch (action[0]) {
            case "mode": {
                if (action[1].equals("editMode")) {
                    if (!mainWindow.getEditMode()) {
                        lastMarkedElement = null;
                        loadMarkedSymbol();
                    }
                    mainWindow.setEditMode(!mainWindow.getEditMode());
                    repaintJPanelDiagram();
                }
                break;
            }
            case "addSymbol": {
                switch (action[1]) {
                    case "COMMENT": {
                        Symbol symbol = EnumSymbol.valueOf(action[1]).getInstance("");
                        if (layout.getFocusedElement() != null) {
                            symbol.setHasPairSymbol(true);
                        }
                        layout.addNewSymbol(symbol);
                        refreshComments();
                        break;
                    }
                    case "LOOPCONDITIONUP":
                    case "LOOPCONDITIONDOWN": {
                        layout.addNewSymbol(new LoopEnd());
                        layout.addNewSymbol(EnumSymbol.valueOf(action[1]).getInstance(""));
                        break;
                    }
                    case "STARTEND": {
                        layout.addNewSymbol(EnumSymbol.valueOf(action[1]).getInstance("Konec"));
                        break;
                    }
                    default: {
                        Symbol symbol = EnumSymbol.valueOf(action[1]).getInstance("");
                        int innerOutCount = 0;

                        if (symbol.getInnerOutsCount() == -1) {
                            int max = 50;
                            int min = 0;
                            int def = min + 1;
                            if (symbol.hasElseSegment()) {
                                def++;
                            }
                            String defaultString = String.valueOf(def);
                            do {
                                String str = askForNumber(min, max, defaultString);
                                try {
                                    innerOutCount = Integer.parseInt(str);
                                } catch (NumberFormatException e) {
                                    if (str == null) {
                                        return;
                                    }
                                }
                                defaultString = str;
                            } while (innerOutCount < min || innerOutCount > max);
                            if (symbol.hasElseSegment()) {
                                innerOutCount++;
                            }
                        }

                        layout.addNewSymbol(symbol, innerOutCount);

                        /*
                         * // prirazeni deskripce segmentum
                         * LayoutElement element = layout.addNewSymbol(symbol,
                         * innerOutCount);
                         * for (int i = 0; i <
                         * symbol.getDefaultSegmentDescriptions().length; i++) {
                         * element.getInnerSegment(i).setDescription(symbol.getDefaultSegmentDescriptions()[i]);
                         * if (i ==
                         * symbol.getDefaultSegmentDescriptions().length - 1) {
                         * layout.prepareFlowchart();
                         * }
                         * }
                         * break;
                         */
                    }
                }

                repaintJPanelDiagram();
                puttingText = true;
                loadMarkedSymbol();
                puttingText = false;

                flowchartEditUndoManager.addEdit(layout, this, "Přidání symbolu");
                break;
            }
            case "edit": {
                switch (action[1]) {
                    case "delete": {
                        if (commentsManager.isCommentPathConnectorVisible()) { // jestli je zobrazena kulicka zlomu komentarove cesty
                            if (!commentsManager.isCommentPathConnectorDeletable()) {
                                break;
                            }

                            commentsManager.deleteCommentPathConnector();
                            resetVariables();
                            layout.prepareFlowchart();
                            repaintJPanelDiagram();

                            flowchartEditUndoManager.addEdit(layout, this, "Smazání bodu komentáře");
                        } else {
                            removeFocusedElement();
                            repaintJPanelDiagram();
                            flowchartEditUndoManager.addEdit(layout, this, "Smazání symbolu");
                        }
                        break;
                    }
                    case "cut":
                    case "copy": {
                        if (commentsManager.isCommentPathConnectorVisible()) {
                            break;
                        }
                        LayoutElement focusedElement = layout.getFocusedElement();
                        if (focusedElement != null) {
                            elementsToPaste = new LinkedHashMap<>();
                            try {
                                for (LayoutElement element : layout.getMeAndMyDependants(
                                        focusedElement)) {
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    MainWindow.getJAXBcontext().createMarshaller().marshal(element,
                                            baos);
                                    elementsToPaste.put(baos,
                                            element.getSymbol().hasPairSymbol() && element.getSymbol() instanceof Comment);
                                }
                            } catch (JAXBException ex) {
                                ex.printStackTrace(System.err);
                                elementsToPaste = null;
                                break;
                            }
                            if (action[1].equals("cut")) {
                                removeFocusedElement();
                                repaintJPanelDiagram();
                                flowchartEditUndoManager.addEdit(layout, this, "Vyjmutí symbolu");
                            }
                        }
                        break;
                    }
                    case "paste": {
                        if (commentsManager.isCommentPathConnectorVisible() || elementsToPaste == null) {
                            break;
                        }
                        ArrayList<LayoutElement> elementsToAdd = new ArrayList<>();
                        try {
                            for (ByteArrayOutputStream baos : elementsToPaste.keySet()) {
                                LayoutElement newElement = GlobalFunctions.unsafeCast(
                                        MainWindow.getJAXBcontext().createUnmarshaller().unmarshal(
                                                new ByteArrayInputStream(baos.toByteArray())));
                                elementsToAdd.add(newElement);
                            }
                        } catch (JAXBException ex) {
                            ex.printStackTrace(System.err);
                            break;
                        }

                        layout.addElements(elementsToAdd);
                        refreshComments();
                        //elementsToMove = null;
                        repaintJPanelDiagram();

                        flowchartEditUndoManager.addEdit(layout, this, "Vložení symbolu");
                        break;
                    }
                    case "segmentText": {
                        puttingText = true;
                        jTextFieldTextSegment.setText(getMarkedElement().getInnerSegment(
                                jComboBoxSegment.getSelectedIndex()).getDescription());
                        puttingText = false;
                        return;
                    }
                    case "defaultText": {
                        if (jCheckBoxDefaultText.isSelected()) {
                            loadDefaults(getMarkedElement());
                        } else {
                            defaultTextBeingEdited = true;
                            loadCustom(getMarkedElement());
                        }
                        layout.prepareFlowchart();
                        repaintJPanelDiagram();

                        flowchartEditUndoManager.addEdit(layout, this, "Výchozí text symbolu");
                        break;
                    }
                    case "defaultsChanged": { // udalost vystrelena kdyz se zmeni defaultni textove hodnoty aktuálně zobrazeného symbolu
                        defaultTextBeingEdited = true;
                        LayoutElement element = getMarkedElement();
                        if (!defaultsEqualsValues(element)
                                && ((jCheckBoxDefaultText.isVisible() && jCheckBoxDefaultText.isSelected())
                                || (!jCheckBoxDefaultText.isVisible() && !jCheckBoxDefaultText.isSelected() && !consistsCustomValues(
                                        element)))) {
                            //nacteme defaulty i v pripade ze byli prave vymazany
                            loadDefaults(element);
                        } else {
                            break;
                        }
                        layout.prepareFlowchart();
                        repaintJPanelDiagram();

                        flowchartEditUndoManager.addEdit(layout, this, "Funkce symbolu");
                        break;
                    }
                }
                puttingText = true;
                loadMarkedSymbol();
                puttingText = false;
                break;
            }
        }
        if (action.length < 2 || !action[1].equals("defaultsChanged")) {
            mainWindow.setJPanelDiagramFocus();
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
     * Není-li klávesa control stisknuta, metoda hledá na základě pozice kurzoru
     * jakýkoliv označitelný prvek. Před tím ještě volá metodu
     * doPendingUndoRedos().
     *
     * @param me nová událost
     */
    @Override
    public void mousePressed(MouseEvent me)
    {
        if (me.isControlDown()) {
            return;
        }
        Point2D p = getTransformedPoint(me.getPoint(), new Point2D.Double());
        doPendingUndoRedos();

        if (commentsManager.wasMousePressedEventRelevantForConnector(p)) {
            maybeShowPopup(me);
            return;
        }

        Joint joint = getJointContaining(p);
        if (joint != null) {
            layout.setFocusedJoint(joint);
            puttingText = true;
            loadMarkedSymbol();
            puttingText = false;
            repaintJPanelDiagram();
            maybeShowPopup(me);
            return;
        }

        LayoutElement element = getElementContaining(layout.getFlowchart(), p);
        if (element != null) {
            layout.setFocusedElement(element);
            puttingText = true;
            loadMarkedSymbol();
            puttingText = false;
            repaintJPanelDiagram();
            if (element.getSymbol() instanceof Comment) {
                commentsManager.mousePressedOnComment(element, p);
            } else {
                updateEditMenuEnablers();
                if (jSymbolPopupCut.isEnabled()) {
                    symbolDragManager.mousePressedOnSymbol(element, p);
                }
            }
            maybeShowPopup(me);
            return;
        }

        if (layout.getBoldPathComment() == null) {
            // nebyla provedena zadna akce, mohu povolit dragGrab
            mainWindow.getFlowchartOverlookManager().setStartDragGrab(me);
        }
    }

    /**
     * V případě, že byl dragován komentář, invokuje přepočtení vývojového
     * diagramu layoutem. Může také (závislé na platformě), vyvolat popup dialog
     * pro editaci symbolu.
     *
     * @param me nová událost
     */
    @Override
    public void mouseReleased(MouseEvent me)
    {
        //if (commentPathConnector.getCenterX() > mainWindow.setJPanelDiagramFocus().getWidth() - layout.getFlowchartPadding() || commentPathConnector.getCenterX() < 0 + layout.getFlowchartPadding() || commentPathConnector.getCenterY() > mainWindow.setJPanelDiagramFocus().getHeight() - layout.getFlowchartPadding() || commentPathConnector.getCenterY() < 0 + layout.getFlowchartPadding()) {
        //if (procesComment.getX() + procesComment.getWidth() > mainWindow.setJPanelDiagramFocus().getWidth() - layout.getFlowchartPadding() || procesComment.getX() < 0 + layout.getFlowchartPadding() || procesComment.getY() + procesComment.getHeight() > mainWindow.setJPanelDiagramFocus().getHeight() - layout.getFlowchartPadding() || procesComment.getY() < 0 + layout.getFlowchartPadding()) {

        if (commentsManager.isCommentOrJointBeingDragged()) {
            layout.prepareFlowchart(); // pro pripad ze bod ci komentar je umisten za platnem, nebo byl a byl posunut do platna
        } else if (symbolDragManager.isDragging()) {
            if (symbolDragManager.mouseReleased()) {
                flowchartEditUndoManager.addEdit(layout, this, "Přesunutí symbolu");
            }
            repaintJPanelDiagram();
        }
        Point2D p = getTransformedPoint(me.getPoint(), new Point2D.Double());
        if (commentsManager.isCommentOrJointBeingDragged()) {
            if (!symbolPopup.isVisible()) {
                commentsManager.analyzeMouseToCommentPathAndConnector(p);
            }
            repaintJPanelDiagram();
            flowchartEditUndoManager.addEdit(layout, this, commentsManager.getCommentAction());
        }
        if (commentsManager.isCommentPathConnectorVisible()) {
            if (commentsManager.wereMouseReleasedCoordsInsideConnector(p)) {
                if (maybeShowPopup(me)) {
                    return;
                }
            }
        } else if ((layout.getFocusedElement() != null && layout.getFocusedElement().getSymbol().contains(
                p)) || (layout.getFocusedJoint() != null && layout.getFocusedJoint().contains(p))) {
            maybeShowPopup(me);
        }
        resetVariables();
    }

    // **********************MouseMotionListener**********************
    /**
     * Je-li kurzor nad komentářem nebo jeho cestou/bodem cesty, proběhne
     * jeho/její přesun.
     *
     * @param me nová událost
     */
    @Override
    public void mouseDragged(MouseEvent me)
    {
        if (me.isControlDown()) {
            return;
        }
        Point2D p = getTransformedPoint(me.getPoint(), new Point2D.Double());

        if (commentsManager.isAbleToDrag()) {
            commentsManager.performDrag(p);
            repaintJPanelDiagram();
        } else if (symbolDragManager.isAbleToDrag()) {
            symbolDragManager.performDrag(p);
            repaintJPanelDiagram();
        }
    }

    /**
     * Není-li stisknuta klávesa ctrl, je zanalyzována, zda se kurzor
     * nevyskytuje nad bodem zlomu komentářové cesty. V tom případě bude
     * zobrazen bod pro jeho editaci.
     *
     * @param me nová událost
     */
    @Override
    public void mouseMoved(MouseEvent me)
    {
        if (me.isControlDown()) {
            return;
        }
        Point2D p = getTransformedPoint(me.getPoint(), new Point2D.Double());
        if (!symbolPopup.isVisible()) {
            commentsManager.analyzeMouseToCommentPathAndConnector(p);
        }
        repaintJPanelDiagram();
    }

    // **********************DocumentListener**********************
    /**
     * Metoda s prázdným tělem.
     * <p>
     * @param de
     */
    @Override
    public void changedUpdate(DocumentEvent de)
    {
    }

    /**
     * Pokud není text vkládán programově, nastaví tato metoda aktuálně
     * editovaný text symbolu/segmentu.
     *
     * @param de nová událost
     */
    @Override
    public void insertUpdate(DocumentEvent de)
    {
        if (puttingText) {
            return;
        }
        try {
            if (de.getDocument().equals(jTextFieldTextSegment.getDocument())) {
                setSegmentDescription(getMarkedElement().getInnerSegment(
                        jComboBoxSegment.getSelectedIndex()), de.getDocument().getText(0,
                                de.getDocument().getLength()));
            } else {
                setSymbolText(getMarkedElement(), de.getDocument().getText(0,
                        de.getDocument().getLength()));
            }
        } catch (BadLocationException ex) {
        }
    }

    /**
     * Pokud není text vkládán programově, nastaví tato metoda aktuálně
     * editovaný text symbolu/segmentu.
     *
     * @param de nová událost
     */
    @Override
    public void removeUpdate(DocumentEvent de)
    {
        if (puttingText) {
            return;
        }
        try {
            if (de.getDocument().equals(jTextFieldTextSegment.getDocument())) {
                setSegmentDescription(getMarkedElement().getInnerSegment(
                        jComboBoxSegment.getSelectedIndex()), de.getDocument().getText(0,
                                de.getDocument().getLength()));
            } else {
                setSymbolText(getMarkedElement(), de.getDocument().getText(0,
                        de.getDocument().getLength()));
            }
        } catch (BadLocationException ex) {
        }
    }

    // **********************KeyListener**********************
    /**
     * Posluchač stiknutých kláves nad plátnem. Je-li stisknuta klávesa
     * back space, je tato událost přesměrována textovému poli pro editaci textu
     * symbolu.
     *
     * @param ke nová událost
     */
    @Override
    public void keyPressed(KeyEvent ke)
    {
        updateEditMenuEnablers(); // update it so the shortcuts (delete, ctrl+c, ...) are handled correctly
        if (ke.getKeyCode() == KeyEvent.VK_BACK_SPACE) {// || ke.getKeyCode() == KeyEvent.VK_ENTER) {
            dispatchKeyEventToSymbolTextArea(ke); // predani KeyEventu editaci textu symbolu
        }
    }

    /**
     * Posluchač stiknutých kláves nad plátnem. Je-li stisknuta klávesa
     * back space, je tato událost přesměrována textovému poli pro editaci textu
     * symbolu.
     *
     * @param ke nová událost
     */
    @Override
    public void keyReleased(KeyEvent ke)
    {
        if (ke.getKeyCode() == KeyEvent.VK_BACK_SPACE) {// || ke.getKeyCode() == KeyEvent.VK_ENTER) {
            dispatchKeyEventToSymbolTextArea(ke); // predani KeyEventu editaci textu symbolu
        }
    }

    /**
     * Posluchač stiknutých kláves nad plátnem. Není-li stisknuta Delete, nebo
     * Enter, je tato událost přesměrována textovému poli pro editaci textu
     * symbolu.
     *
     * @param ke nová událost
     */
    @Override
    public void keyTyped(KeyEvent ke)
    {
        if (!ke.isControlDown() && !ke.isAltDown() && (int) ke.getKeyChar() != KeyEvent.VK_DELETE && (int) ke.getKeyChar() != KeyEvent.VK_ENTER) {
            dispatchKeyEventToSymbolTextArea(ke); // predani KeyEventu editaci textu symbolu
        }
    }

    // **********************FocusListener**********************
    /**
     * Metoda s prázdným tělem.
     * <p>
     * @param fe
     */
    @Override
    public void focusGained(FocusEvent fe)
    {
    }

    /**
     * Čeká-li některý příkaz pro uložení změny textu symbolu/segmentu do
     * UndoManagera, je vykonán právě teď.
     *
     * @param fe nová událost
     */
    @Override
    public void focusLost(FocusEvent fe)
    {
        if (fe.getComponent().equals(jTextFieldTextSegment)) {
            if (segmentTextBuffered) {
                segmentTextBuffered = false;
                flowchartEditUndoManager.addEdit(layout, this, "Editace textu větve symbolu");
            }
        } else { // jTextAreaTextSymbol
            if (symbolTextBuffered) {
                symbolTextBuffered = false;
                flowchartEditUndoManager.addEdit(layout, this, "Editace textu symbolu");
            }
        }
    }

    // ***********************************************************
    // **********************PRIVATE METHODS**********************
    // ***********************************************************
    private void dispatchKeyEventToSymbolTextArea(final KeyEvent ke)
    {
        boolean focusSuccess = jTextAreaTextSymbol.requestFocusInWindow();
        if (!focusSuccess
                && (mainWindow.getJTabbedPaneEdit().getSelectedIndex() != 0
                || (mainWindow.getJScrollPaneFunctionViewportView() instanceof AbstractSymbolFunctionForm && ((AbstractSymbolFunctionForm) mainWindow.getJScrollPaneFunctionViewportView()).hasCommandsToSet()))) {
            // nebylo mozne nastavit focus, zrejme je sepnuta zalozka funkce.. Text symbolu vlozim jen jestli symbol nema funkce k nastavovani
            return;
        }
        jTextAreaTextSymbol.setCaretPosition(jTextAreaTextSymbol.getDocument().getLength());
        if (focusSuccess) {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    jTextAreaTextSymbol.dispatchEvent(ke); // pockam na focusgain
                }
            });
        } else {
            jTextAreaTextSymbol.dispatchEvent(ke);
        }
    }

    private void loadMarkedSymbol()
    {
        LayoutElement markedElement = getMarkedElement();
        if (!markedElement.equals(lastMarkedElement)) {
            defaultTextBeingEdited = false; // nacitam novy symbol, takze nyni uzivatel jiz defaultni texty needituje
        }
        loadMarkedSymbolText(markedElement);
        loadMarkedSymbolFunction(markedElement);
        lastMarkedElement = markedElement;
    }

    private LayoutElement getMarkedElement()
    {
        LayoutElement element = layout.getFocusedElement();
        if (element == null) {
            element = layout.getFocusedJoint().getParentElement();
        }
        return element;
    }

    private void loadDefaults(LayoutElement element)
    {
        element.getSymbol().setValueAndSize(element.getSymbol().getDefaultValue());
        if (element.getSymbol().getInnerOutsCount() == -1) {
            for (int i = element.getSymbol().getDefaultSegmentDescriptions().length; i < element.getInnerSegments().size(); i++) {
                //for(LayoutSegment segment: element.getInnerSegments()) {
                element.getInnerSegment(i).setDescription(
                        element.getInnerSegment(i).getDefaultDescription());
            }
        }
    }

    private void loadCustom(LayoutElement element)
    {
        element.getSymbol().setValueAndSize(element.getSymbol().getCustomValue());
        if (element.getSymbol().getInnerOutsCount() == -1) {
            for (int i = element.getSymbol().getDefaultSegmentDescriptions().length; i < element.getInnerSegments().size(); i++) {
                element.getInnerSegment(i).setDescription(
                        element.getInnerSegment(i).getCustomDescription());
            }
        }
    }

    private void loadMarkedSymbolText(LayoutElement markedElement)
    {
        jTextAreaTextSymbol.setText(markedElement.getSymbol().getValue());
        if (markedElement.getSymbol() instanceof Decision) {
            if (lastMarkedElement == null || !lastMarkedElement.equals(markedElement)) { // neni-li symbol jiz zobrazen
                mainWindow.setJPanelTextSegmentVisible(true);

                /*
                 * DefaultComboBoxModel<String> comboBoxModel = new
                 * DefaultComboBoxModel<>();
                 * for (int i = 0; i < markedElement.getInnerSegmentsCount();
                 * i++) {
                 * if (i == 0) {
                 * comboBoxModel.addElement(i + ". - Else větev");
                 * } else {
                 * comboBoxModel.addElement(i + ".");
                 * }
                 * }
                 * comboBoxModel.addListDataListener(this);
                 * jComboBoxSegment.setModel(comboBoxModel);
                 */
                //if (lastMarkedElement != null) { // neboli neni-li odebrany action-listener
                jComboBoxSegment.removeActionListener(this);
                //}
                jComboBoxSegment.removeAllItems();
                for (int i = 0; i < markedElement.getInnerSegmentsCount(); i++) {
                    if (i == 0) {
                        jComboBoxSegment.addItem(i + ". - Else větev");
                    } else {
                        jComboBoxSegment.addItem(i + ".");
                    }
                }
                jComboBoxSegment.setSelectedIndex(0);
                jComboBoxSegment.addActionListener(this);
                jTextFieldTextSegment.setText(markedElement.getInnerSegment(0).getDescription());
            }
        } else {
            jComboBoxSegment.removeActionListener(this);
            mainWindow.setJPanelTextSegmentVisible(false);
        }
        if (consistsDefaultValues(markedElement) || (consistsCustomValues(markedElement) && defaultsEqualsValues(
                markedElement))) {
            // jestli symbol obsahuje defaultni texty, nebo jestli je neobsahuje ale obsahuje custom a je aktualne nastaveny na prazdy text (= defaultni)
            if (jCheckBoxDefaultText.isVisible()) {
                jCheckBoxDefaultText.removeActionListener(this);
            }
            if (defaultsEqualsValues(markedElement)) {
                jCheckBoxDefaultText.setSelected(true); // shodují-li se texty s defaultními, zatrhneme default checkbox
            } else {
                jCheckBoxDefaultText.setSelected(false);
            }
            jCheckBoxDefaultText.setVisible(true); // jsou-li definované defaultní texty, zviditelníme checkbox
            jCheckBoxDefaultText.addActionListener(this);
        } else if (jCheckBoxDefaultText.isVisible() && !defaultTextBeingEdited) {
            jCheckBoxDefaultText.removeActionListener(this);
            jCheckBoxDefaultText.setSelected(false);
            jCheckBoxDefaultText.setVisible(false);
        }
        mainWindow.packJPanelInnerText();
    }

    private void loadMarkedSymbolFunction(LayoutElement markedElement)
    {
        if (lastMarkedElement != null && lastMarkedElement.equals(markedElement)) { // je-li symbol jiz zobrazen
            return;
        }
        mainWindow.setJScrollPaneFunctionViewportView(EnumSymbol.getEnumSymbol(
                markedElement.getSymbol().getClass()).getFunctionFormInstance(markedElement, this));
    }

    private boolean defaultsEqualsValues(LayoutElement element)
    {
        if (element.getSymbol().getValue().equals(element.getSymbol().getDefaultValue()) || element.getSymbol().getValue().equals(
                "") && element.getSymbol().getDefaultValue() == null) {
            if (element.getSymbol().getInnerOutsCount() == -1) { // je-li symbol instanci podminky nebo switch, je treba otestovat i deskripci segmentů
                for (int i = element.getSymbol().getDefaultSegmentDescriptions().length; i < element.getInnerSegments().size(); i++) {
                    String desc = element.getInnerSegment(i).getDescription();
                    String defDesc = element.getInnerSegment(i).getDefaultDescription();

                    if ((desc != null || defDesc != null)
                            && ((desc != null || defDesc == null) || !defDesc.equals(""))
                            && ((defDesc != null || desc == null) || !desc.equals(""))
                            && (desc == null || !desc.equals(defDesc))) {
                        // jestli se defaultni a aktualni deskripce segmentu nerovnaji (beru v potaz i null)
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean consistsDefaultValues(LayoutElement element)
    {
        if (element.getSymbol().getDefaultValue() != null && !element.getSymbol().getDefaultValue().equals(
                "")) {
            if (element.getSymbol().getInnerOutsCount() == -1) { // je-li symbol instanci podminky nebo switch, je treba otestovat i deskripci segmentů
                for (int i = element.getSymbol().getDefaultSegmentDescriptions().length; i < element.getInnerSegments().size(); i++) {
                    if (element.getInnerSegment(i).getDefaultDescription() == null || element.getInnerSegment(
                            i).getDefaultDescription().equals("")) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean consistsCustomValues(LayoutElement element)
    {
        if (element.getSymbol().getCustomValue() != null && !element.getSymbol().getCustomValue().equals(
                "")) {
            if (element.getSymbol().getInnerOutsCount() == -1) { // je-li symbol instanci podminky nebo switch, je treba otestovat i deskripci segmentů
                for (int i = element.getSymbol().getDefaultSegmentDescriptions().length; i < element.getInnerSegments().size(); i++) {
                    if (element.getInnerSegment(i).getCustomDescription() == null || element.getInnerSegment(
                            i).getCustomDescription().equals("")) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    // POZOR! tuto metodu muze pouzivat jen documment listener - pozor na undoManagera
    private void setSegmentDescription(LayoutSegment segment, String text)
    {
        if (segment.getDescription() == null || !segment.getDescription().equals(text)) {
            if (!segmentTextBuffered) {
                segmentTextBuffered = true;
            }
            segment.setDescription(text);
            if (segment.getDefaultDescription() != null) {
                if (!segment.getDefaultDescription().equals(text)) {
                    segment.setCustomDescription(text);
                    if (jCheckBoxDefaultText.isVisible() && jCheckBoxDefaultText.isSelected()) {
                        jCheckBoxDefaultText.removeActionListener(this);
                        jCheckBoxDefaultText.setSelected(false);
                        jCheckBoxDefaultText.addActionListener(this);
                    }
                } else if (jCheckBoxDefaultText.isVisible() && !jCheckBoxDefaultText.isSelected() && defaultsEqualsValues(
                        getMarkedElement())) {
                    jCheckBoxDefaultText.removeActionListener(this);
                    jCheckBoxDefaultText.setSelected(true);
                    jCheckBoxDefaultText.addActionListener(this);
                }
            } else {
                segment.setCustomDescription(text);
            }

            layout.prepareFlowchart(); // nemohu zde otestovat velikost jako v setSymbolText(..), protoze zustava stejna - roztahovani se deje jen pri definovani mrizky
            repaintJPanelDiagram();
        }
    }

    // POZOR! tuto metodu muze pouzivat jen documment listener - pozor na undoManagera
    private void setSymbolText(LayoutElement element, String text)
    {
        if (element.getSymbol().getValue() == null || !element.getSymbol().getValue().equals(text)) {
            if (jTextAreaTextSymbol.isFocusOwner()) {
                if (!symbolTextBuffered) {
                    symbolTextBuffered = true;
                }
            } else {
                symbolTextBuffered = false;
            }
            double prevSymbolWidth = element.getSymbol().getWidth();
            double prevSymbolHeight = element.getSymbol().getHeight();

            element.getSymbol().setValueAndSize(text);
            if (element.getSymbol().getDefaultValue() != null) {
                if (!element.getSymbol().getDefaultValue().equals(text)) {
                    element.getSymbol().setCustomValue(text);
                    if (jCheckBoxDefaultText.isVisible() && jCheckBoxDefaultText.isSelected()) {
                        jCheckBoxDefaultText.removeActionListener(this);
                        jCheckBoxDefaultText.setSelected(false);
                        jCheckBoxDefaultText.addActionListener(this);
                    }
                } else if (jCheckBoxDefaultText.isVisible() && !jCheckBoxDefaultText.isSelected() && defaultsEqualsValues(
                        element)) {
                    jCheckBoxDefaultText.removeActionListener(this);
                    jCheckBoxDefaultText.setSelected(true);
                    jCheckBoxDefaultText.addActionListener(this);
                }
            } else {
                element.getSymbol().setCustomValue(text);
            }

            if (element.getSymbol().getWidth() != prevSymbolWidth || element.getSymbol().getHeight() != prevSymbolHeight) {
                layout.prepareFlowchart(); // pro znovuumisteni v pripade zmeny velikosti symbolu
            }
            if (!symbolTextBuffered) {
                flowchartEditUndoManager.addEdit(layout, this, "Editace textu symbolu");
            }
            repaintJPanelDiagram();
        }
    }

    private void removeFocusedElement()
    {
        LayoutElement focusedElement = layout.getFocusedElement();
        if (focusedElement != null) {
            layout.removeElement(focusedElement);
            refreshComments();
        }
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

    private String askForNumber(int min, int max, String defaultString)
    {
        return (String) JOptionPane.showInputDialog(mainWindow,
                "Zadej, kolik má tento symbol mít větví (else větev se nepočítá):\n" + "(minimálně " + min + ", maximálně " + max + ")",
                "Počet větví", JOptionPane.QUESTION_MESSAGE, null, null, defaultString);
    }

    public void updateUndoRedoEnablers()
    {
        flowchartEditUndoManager.setButtons();
    }

    /**
     * Nastaví korespondující hodnoty enabled položkám v menu pro úpravu
     * symbolu.
     */
    public void updateEditMenuEnablers()
    {
        if (!mainWindow.getEditMode()) {
            setEditMenuEnablers(false);
        } else if (commentsManager.isCommentPathConnectorVisible()) {
            setEditMenuEnablers(true);
        } else {
            LayoutElement element = layout.getFocusedElement();
            if (element != null && !(element.getParentSegment().getParentElement() == null && (element.getParentSegment().indexOfElement(
                    element) + 1 == element.getParentSegment().size() || (element.getParentSegment().indexOfElement(
                            element) == 0 && element.getSymbol() instanceof StartEnd) || (element.getParentSegment().indexOfElement(
                            element) == 1 && element.getParentSegment().getElement(0).getSymbol() instanceof Comment)))) {
                mainWindow.setjMenuItemDeleteEnabled(true);
                mainWindow.setjMenuItemCutEnabled(true);
                mainWindow.setjMenuItemCopyEnabled(true);
                if (jSymbolPopupDelete != null) {
                    jSymbolPopupDelete.setEnabled(true);
                }
                if (jSymbolPopupCut != null) {
                    jSymbolPopupCut.setEnabled(true);
                }
                if (jSymbolPopupCopy != null) {
                    jSymbolPopupCopy.setEnabled(true);
                }
            } else {
                mainWindow.setjMenuItemDeleteEnabled(false);
                mainWindow.setjMenuItemCutEnabled(false);
                mainWindow.setjMenuItemCopyEnabled(false);
                if (jSymbolPopupDelete != null) {
                    jSymbolPopupDelete.setEnabled(false);
                }
                if (jSymbolPopupCut != null) {
                    jSymbolPopupCut.setEnabled(false);
                }
                if (jSymbolPopupCopy != null) {
                    jSymbolPopupCopy.setEnabled(false);
                }
            }
            setPasteEnabled();
        }
    }

    private void setEditMenuEnablers(boolean enabled)
    {
        if (jSymbolPopupDelete != null) {
            jSymbolPopupDelete.setEnabled(enabled);
        }
        mainWindow.setjMenuItemDeleteEnabled(enabled);

        if (enabled) {
            setPasteEnabled();
        } else {
            if (jSymbolPopupPaste != null) {
                jSymbolPopupPaste.setEnabled(false);
            }
            mainWindow.setjMenuItemPasteEnabled(false);
        }

        if (commentsManager.isCommentPathConnectorVisible()) {
            enabled = false;
        }
        if (jSymbolPopupCut != null) {
            jSymbolPopupCut.setEnabled(enabled);
        }
        if (jSymbolPopupCopy != null) {
            jSymbolPopupCopy.setEnabled(enabled);
        }
        mainWindow.setjMenuItemCutEnabled(enabled);
        mainWindow.setjMenuItemCopyEnabled(enabled);
    }

    private void setPasteEnabled()
    {
        if (elementsToPaste != null && !elementsToPaste.isEmpty() && !commentsManager.isCommentPathConnectorVisible()) {
            if (jSymbolPopupPaste != null) {
                jSymbolPopupPaste.setEnabled(true);
            }
            mainWindow.setjMenuItemPasteEnabled(true);
        } else {
            if (jSymbolPopupPaste != null) {
                jSymbolPopupPaste.setEnabled(false);
            }
            mainWindow.setjMenuItemPasteEnabled(false);
        }
    }

    private boolean maybeShowPopup(MouseEvent me)
    {
        if (me.isPopupTrigger()) {
            if (symbolPopup != null) {
                //Point2D p = getTransformedPoint(me.getPoint(), new Point2D.Double());
                symbolPopup.show(me.getComponent(), me.getX(), me.getY());
            }
            return true;
        }
        return false;
    }

    private Joint getJointContaining(Point2D p)
    {
        for (Joint joint : layout.getlJoints()) {
            if (joint.getShape().contains(p)) {
                return joint;
            }
        }
        return null;
    }

    private LayoutElement getElementContaining(Flowchart<LayoutSegment, LayoutElement> flowchart,
            Point2D p)
    {
        for (LayoutSegment segment : flowchart) {
            if (segment != null) {
                for (LayoutElement element : segment) {
                    if (element.getSymbol().contains(p)) {
                        return element;
                    }
                }
            }
        }
        return null;
    }

}
