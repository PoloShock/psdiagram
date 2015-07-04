/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms;

import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.EnumSymbol;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.gui.managers.FlowchartEditManager;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.ConstantFilter;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.VariableFilter;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.LinkedHashMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;

/**
 * Tato třída představuje formulář pro editaci funkce symbolu vícecestného
 * rozhodování.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class Switch extends AbstractSymbolFunctionForm
{

    private final JLabel jLabelDescription;
    private final Symbol mySymbol = EnumSymbol.SWITCH.getInstance(null);
    private JTextField[] jTextFieldSegments;
    private final SwitchValidationListener validationListener = new SwitchValidationListener();

    /**
     * Konstruktor, inicializující tento formulář.
     *
     * @param element element, kterého se tento formulář týká
     * @param flowchartEditManager FlowchartEditManager, spravující editační
     * režim aplikace
     */
    public Switch(LayoutElement element, FlowchartEditManager flowchartEditManager)
    {
        super(element, flowchartEditManager);
        /*
         * jLabelDescription = new JLabel("<html>"
         * + "- řízení toku programu na<br />základě rovnosti vstupního<br
         * />číselného(nebo řetězcové-<br />ho) výrazu vůči konstan-<br />tám<br
         * />"
         * + "- pro oddělení více kons-<br />tatnt pro jednu větev pou-<br
         * />žijte znak \",\" (čárka)"
         * + "</html>");
         */
        jLabelDescription = new JLabel("<html><p>"
                + "- řízení toku programu na základě rovnosti vstupního číselného(nebo řetězcového) výrazu vůči konstantám<br />"
                + "- pro oddělení více konstatnt pro jednu větev použijte znak \",\" (čárka)"
                + "</p></html>");
        jLabelDescription.setFont(SettingsHolder.SMALLFONT_SYMBOLDESC);
        initComponents();

        jTextFieldConditionVar.setFont(SettingsHolder.CODEFONT);
        jLabelExamples.setFont(SettingsHolder.SMALL_CODEFONT);

        if (element.getSymbol().getCommands() != null) {
            if (SettingsHolder.settings.isFunctionFilters()) {
                jTextFieldConditionVar.setText(
                        AbstractSymbolFunctionForm.convertFromJSToPSDCommands(
                                element.getSymbol().getCommands().get("conditionVar")));
                for (int i = 0; i < jTextFieldSegments.length; i++) {
                    jTextFieldSegments[i].setText(
                            AbstractSymbolFunctionForm.convertFromJSToPSDCommands(
                                    element.getSymbol().getCommands().get("" + (i + 1))));
                }
            } else {
                jTextFieldConditionVar.setText(element.getSymbol().getCommands().get("conditionVar"));
                for (int i = 0; i < jTextFieldSegments.length; i++) {
                    jTextFieldSegments[i].setText(
                            element.getSymbol().getCommands().get("" + (i + 1)));
                }
            }
        }

        ((AbstractDocument) jTextFieldConditionVar.getDocument()).setDocumentFilter(
                new VariableFilter(jTextFieldConditionVar, validationListener));
        AbstractSymbolFunctionForm.enhanceWithUndoRedoCapability(jTextFieldConditionVar);
        addDocumentListeners();
        super.trimSize();
    }

    @Override
    void addDocumentListeners()
    {
        jTextFieldConditionVar.getDocument().addDocumentListener(this);
        for (JTextField jTextField : jTextFieldSegments) {
            jTextField.getDocument().addDocumentListener(this);
        }
    }

    @Override
    void generateValues()
    {
        String conditionVar = "";
        String[] segmentConstants = new String[jTextFieldSegments.length];
        try {
            conditionVar = jTextFieldConditionVar.getDocument().getText(0,
                    jTextFieldConditionVar.getDocument().getLength());
            for (int i = 0; i < jTextFieldSegments.length; i++) {
                segmentConstants[i] = jTextFieldSegments[i].getDocument().getText(0,
                        jTextFieldSegments[i].getDocument().getLength()).trim();
            }
        } catch (BadLocationException ex) {
        }

        generateValues(super.getElement(), conditionVar.trim(), segmentConstants);
    }

    /**
     * Vygeneruje danému symbolu elementu dané funkční příkazy a zároveň nastaví
     * defaultní text symbolu.
     * <p/>
     * @param element element, kterého se má generování hodnot týkat
     * @param conditionVar proměnná, které se tento Switch příkaz týká
     * @param segmentConstants konstanty jednotlivých větví tohoto symbolu
     */
    public static void generateValues(LayoutElement element, String conditionVar,
            String[] segmentConstants)
    {
        boolean setDefaultDescripton = !conditionVar.isEmpty() && segmentConstants.length == element.getInnerSegmentsCount() - 1;
        if (setDefaultDescripton) {
            for (String segmentConstant : segmentConstants) {
                if (segmentConstant.isEmpty()) {
                    setDefaultDescripton = false;
                    break;
                }
            }
        }

        LinkedHashMap<String, String> commands = new LinkedHashMap<>();
        if (setDefaultDescripton) {
            element.getSymbol().setDefaultValue(
                    AbstractSymbolFunctionForm.convertToPSDDisplayCommands(conditionVar) + "?");
        } else {
            element.getSymbol().setDefaultValue("");
        }
        if (SettingsHolder.settings.isFunctionFilters()) {
            commands.put("conditionVar", AbstractSymbolFunctionForm.convertFromPSDToJSCommands(
                    conditionVar));
            for (int i = 0; i < segmentConstants.length; i++) {
                if (setDefaultDescripton) {
                    element.getInnerSegment(i + 1).setDefaultDescripton(segmentConstants[i]);
                } else {
                    element.getInnerSegment(i + 1).setDefaultDescripton(null);
                }
                commands.put("" + (i + 1),
                        AbstractSymbolFunctionForm.convertFromPSDToJSCommands(
                                segmentConstants[i]));
            }
        } else {
            commands.put("conditionVar", conditionVar);
            for (int i = 0; i < segmentConstants.length; i++) {
                if (setDefaultDescripton) {
                    element.getInnerSegment(i + 1).setDefaultDescripton(segmentConstants[i]);
                } else {
                    element.getInnerSegment(i + 1).setDefaultDescripton(null);
                }
                commands.put("" + (i + 1), segmentConstants[i]);
            }
        }
        element.getSymbol().setCommands(commands);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jLabel1 = new javax.swing.JLabel();
        jTextFieldConditionVar = new javax.swing.JTextField();
        jPanel1 = new JPanelSymbol(mySymbol, jLabelDescription);
        jLabel3 = new javax.swing.JLabel();
        jLabelExamples = new javax.swing.JLabel();
        jPanelSegments = new JPanelSegments(super.getElement());

        setBorder(javax.swing.BorderFactory.createTitledBorder("<html>\nRozhodování - vícecestné (switch)\n</html>"));
        setPreferredSize(new java.awt.Dimension(187, 493));

        jLabel1.setText("Vstupní proměnná:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 186, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jLabel3.setText("Příklady:");

        jLabelExamples.setText("<html>\n- A<br />\n1<br />\n2,3,4<br />\n<br />\n- B<br />\n\"text1\"<br />\n\"text2\",\"text3\"\n</html>");
        jLabelExamples.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jPanelSegments.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout jPanelSegmentsLayout = new javax.swing.GroupLayout(jPanelSegments);
        jPanelSegments.setLayout(jPanelSegmentsLayout);
        jPanelSegmentsLayout.setHorizontalGroup(
            jPanelSegmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelSegmentsLayout.setVerticalGroup(
            jPanelSegmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabelExamples, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTextFieldConditionVar)
            .addComponent(jPanelSegments, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldConditionVar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelSegments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelExamples, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(213, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelExamples;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelSegments;
    private javax.swing.JTextField jTextFieldConditionVar;
    // End of variables declaration//GEN-END:variables

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
     * Automaticky vygeneruje hodnoty na základě vyplněné funkce symbolu a
     * spraví o této události FlowchartEditManager.
     *
     * @param de nová událost úpravy funkce
     */
    @Override
    public void insertUpdate(DocumentEvent de)
    {
        generateValues();
        super.fireChangeEventToEditManager();
    }

    /**
     * Automaticky vygeneruje hodnoty na základě vyplněné funkce symbolu a
     * spraví o této události FlowchartEditManager.
     *
     * @param de nová událost úpravy funkce
     */
    @Override
    public void removeUpdate(DocumentEvent de)
    {
        generateValues();
        super.fireChangeEventToEditManager();
    }

    private class JPanelSegments extends javax.swing.JPanel
    {

        private int minWidth = 0;
        private int minHeight = 0;
        private final int componentPadding = 5;

        public JPanelSegments(LayoutElement element)
        {
            super.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            jTextFieldSegments = new JTextField[element.getInnerSegmentsCount() - 1];

            for (int i = 0; i < jTextFieldSegments.length; i++) {
                JLabel jLabel = new JLabel("Konstanta pro " + (i + 1) + ". větev");
                jLabel.setSize(jLabel.getPreferredSize());
                jLabel.setAlignmentX(0);
                if (jLabel.getSize().width > minWidth) {
                    minWidth = jLabel.getSize().width;
                }
                //jLabel.setBounds(0, minHeight, jLabel.getSize().width, jLabel.getSize().height);
                super.add(jLabel);
                super.add(Box.createRigidArea(new Dimension(0, componentPadding)));
                minHeight += jLabel.getSize().height + componentPadding;

                jTextFieldSegments[i] = new JTextField();
                jTextFieldSegments[i].setSize(jTextFieldSegments[i].getPreferredSize());
                jTextFieldSegments[i].setAlignmentX(0);
                jTextFieldSegments[i].setFont(SettingsHolder.CODEFONT);
                ((AbstractDocument) jTextFieldSegments[i].getDocument()).setDocumentFilter(
                        new ConstantFilter(jTextFieldSegments[i], validationListener));
                AbstractSymbolFunctionForm.enhanceWithUndoRedoCapability(jTextFieldSegments[i]);
                //jTextFieldSegments[i].setBounds(0, minHeight, minWidth, jTextFieldSegments[i].getSize().height);
                super.add(jTextFieldSegments[i]);
                minHeight += jTextFieldSegments[i].getSize().height;
                if (i + 1 < jTextFieldSegments.length) { //jeste se bude opakovat
                    super.add(Box.createRigidArea(new Dimension(0, componentPadding)));
                    minHeight += componentPadding;
                }
            }

            Dimension dim = new Dimension(minWidth, minHeight);
            super.setMinimumSize(dim);
        }

        @Override
        public void setMinimumSize(Dimension dmnsn)
        { // je treba zajistit, aby komponent nebyl mensi nez minimalni hodnoty
            super.setMinimumSize(new Dimension(minWidth, minHeight));
        }

        @Override
        public void setPreferredSize(Dimension dmnsn)
        { // je treba zajistit, aby se vzdy inicializoval symbol a preferedsize nebyl mensi nez minimalni hodnoty
            if (dmnsn.width < minWidth) {
                dmnsn.width = minWidth;
            }
            if (dmnsn.height < minHeight) {
                dmnsn.height = minHeight;
            }
            /*
             * for (JTextField jTextField: jTextFieldSegments) {
             * jTextField.setPreferredSize(new Dimension(dmnsn.width,
             * jTextField.getSize().height));
             * }
             */
            super.setPreferredSize(dmnsn);
        }

        @Override
        public void setLayout(LayoutManager lm)
        {
        }

    }

    private class SwitchValidationListener implements ValidationListener
    {

        @Override
        public void validationStateChanged()
        {
            Boolean varValid = (Boolean) jTextFieldConditionVar.getDocument().getProperty(
                    "commandValid");
            if (varValid == null) {
                return;
            } else if (!varValid) {
                Switch.super.getElement().getSymbol().setCommandsValid(false);
                return;
            }
            // varValid is true
            for (JTextField jTextFieldSegment : jTextFieldSegments) {
                Boolean segmentValid = (Boolean) jTextFieldSegment.getDocument().getProperty(
                        "commandValid");
                if (segmentValid == null) {
                    return;
                } else if (!segmentValid) {
                    Switch.super.getElement().getSymbol().setCommandsValid(false);
                }
            }
            Switch.super.getElement().getSymbol().setCommandsValid(true);

            if (SettingsHolder.settings.isFunctionFilters()) {
                Switch.super.getFlowchartEditManager().repaintJPanelDiagram();
            }
        }

    }

}
