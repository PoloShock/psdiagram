/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms;

import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import cz.miroslavbartyzal.psdiagram.app.gui.balloonToolTip.MaxBalloonSizeCallback;
import cz.miroslavbartyzal.psdiagram.app.gui.managers.FlowchartEditManager;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.BooleanValueFilter;
import java.util.LinkedHashMap;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;

/**
 * Tato třída představuje formulář pro editaci funkce symbolu cyklu s podmínkou
 * na konci.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class LoopEnd extends AbstractSymbolFunctionForm
{

    private final JLabel jLabelDescription;
    private final Symbol mySymbol = new cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.LoopEnd();
    private final LoopEndValidationListener validationListener = new LoopEndValidationListener();

    /**
     * Konstruktor, inicializující tento formulář.
     *
     * @param element element, kterého se tento formulář týká
     * @param flowchartEditManager FlowchartEditManager, spravující editační
     * režim aplikace
     * @param maxBalloonSizeCallback
     */
    public LoopEnd(LayoutElement element, FlowchartEditManager flowchartEditManager,
            MaxBalloonSizeCallback maxBalloonSizeCallback)
    {
        super(element, flowchartEditManager);
        /*
         * jLabelDescription = new JLabel("<html>"
         * + "- tělo cyklu se vykonává<br />minimálně jednou, dále<br />jen
         * pokud hodnota rozho-<br />dovacího výrazu je logická<br />pravda<br
         * />"
         * + "- dostupné relační operá-<br />tory: =,!=,&gt;,&lt;,&gt;=,&lt;=<br
         * />"
         * + "- dostupné logické operá-<br />tory: &(and),|(or),!(negace)"
         * + "</html>");
         */
        String operators;
        operators = "- dostupné relační operátory: =,!=,&gt;,&lt;,&gt;=,&lt;=<br />"
                + "- dostupné logické operátory: &&(and),||(or),!(negace)";
        operators += "</p></html>";
        jLabelDescription = new JLabel("<html><p>"
                + "- tělo cyklu se vykonává minimálně jednou, dále jen pokud hodnota rozhodovacího výrazu je logická pravda<br />"
                + operators);
        jLabelDescription.setFont(SettingsHolder.SMALLFONT_SYMBOLDESC);
        initComponents();

        jTextFieldCondition.setFont(SettingsHolder.CODEFONT);
        jLabelExamples.setFont(SettingsHolder.SMALL_CODEFONT);

        if (element.getSymbol().getCommands() != null) {
            jTextFieldCondition.setText(element.getSymbol().getCommands().get("condition"));
        }

        ((AbstractDocument) jTextFieldCondition.getDocument()).setDocumentFilter(new BooleanValueFilter(jTextFieldCondition, validationListener,
                        maxBalloonSizeCallback));
        AbstractSymbolFunctionForm.enhanceWithUndoRedoCapability(jTextFieldCondition);
        addDocumentListeners();
        super.trimSize();
    }

    @Override
    void addDocumentListeners()
    {
        jTextFieldCondition.getDocument().addDocumentListener(this);
    }

    @Override
    void generateValues()
    {
        String condition = "";
        try {
            condition = jTextFieldCondition.getDocument().getText(0,
                    jTextFieldCondition.getDocument().getLength());
        } catch (BadLocationException ex) {
        }

        generateValues(super.getElement().getSymbol(), condition.trim());
    }

    /**
     * Vygeneruje danému symbolu dané funkční příkazy a zároveň nastaví
     * defaultní text symbolu. Případné znaky převeditelné do čitelnější podoby,
     * jsou v defaultním textu konvertovány.
     * <p/>
     * @param symbol symbol, kterého se má generování hodnot týkat
     * @param condition podmínkový výraz jako funkce symbolu
     */
    public static void generateValues(Symbol symbol, String condition)
    {
        if (!condition.isEmpty()) {
            symbol.setDefaultValue(AbstractSymbolFunctionForm.convertToPSDDisplayCommands(
                    condition));
        } else {
            symbol.setDefaultValue(null);
        }

        LinkedHashMap<String, String> commands = new LinkedHashMap<>();
        commands.put("condition", condition);
        symbol.setCommands(commands);
    }
    
    public static boolean isConditionValid(String condition) {
        return BooleanValueFilter.isValid(condition);
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
        jTextFieldCondition = new WatermarkJTextField("proveď cyklus, když...")
        ;
        jPanel1 = new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.JPanelSymbol(mySymbol, jLabelDescription);
        jLabel3 = new javax.swing.JLabel();
        jLabelExamples = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("<html>Cyklus s podmínkou na konci</html>"));
        setPreferredSize(new java.awt.Dimension(187, 493));

        jLabel1.setText("Rozhodovací výraz:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 159, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jLabel3.setText("Příklady:");

        jLabelExamples.setText("<html>\n- A > 0<br />\n- A >= B<br />\n- (A != B) && (A > 0)<br />\n- bool<br />\n- !bool<br />\n- !((A+B=C) || (B-A=C))\n</html>");
        jLabelExamples.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabelExamples, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTextFieldCondition)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldCondition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelExamples, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(255, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelExamples;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextFieldCondition;
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

    @Override
    public JTextField getJTextFieldToDispatchKeyEventsAt()
    {
        return jTextFieldCondition;
    }

    private class LoopEndValidationListener implements ValidationListener
    {

        @Override
        public void validationStateChanged()
        {
            LoopEnd.super.getElement().getSymbol().setCommandsValid(
                    (boolean) jTextFieldCondition.getDocument().getProperty("commandValid"));

            LoopEnd.super.getFlowchartEditManager().repaintJPanelDiagram();
        }

    }

}
