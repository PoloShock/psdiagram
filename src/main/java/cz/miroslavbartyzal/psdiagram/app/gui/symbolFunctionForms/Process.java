/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms;

import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.EnumSymbol;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import cz.miroslavbartyzal.psdiagram.app.gui.balloonToolTip.MaxBalloonSizeCallback;
import cz.miroslavbartyzal.psdiagram.app.gui.managers.FlowchartEditManager;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.ValueFilter;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.VariableFilter;
import java.util.LinkedHashMap;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;

/**
 * Tato třída představuje formulář pro editaci funkce symbolu zpracování.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class Process extends AbstractSymbolFunctionForm
{

    private final JLabel jLabelDescription;
    private final Symbol mySymbol = EnumSymbol.PROCESS.getInstance(null);
    private final ProcessValidationListener validationListener = new ProcessValidationListener();

    /**
     * Konstruktor, inicializující tento formulář.
     *
     * @param element element, kterého se tento formulář týká
     * @param flowchartEditManager FlowchartEditManager, spravující editační
     * režim aplikace
     * @param maxBalloonSizeCallback
     */
    public Process(LayoutElement element, FlowchartEditManager flowchartEditManager,
            MaxBalloonSizeCallback maxBalloonSizeCallback)
    {
        super(element, flowchartEditManager);
        /*
         * jLabelDescription = new JLabel("<html>"
         * + "- přiřazení hodnoty do pro-<br />měnné<br />"
         * + "- v případě, že proměnná<br />neexistuje, bude vytvoře-<br />na"
         * + "</html>");
         */
        jLabelDescription = new JLabel("<html><p>"
                + "- přiřazení hodnoty do proměnné<br />"
                + "- v případě, že proměnná neexistuje, bude vytvořena"
                + "</p></html>");
        jLabelDescription.setFont(SettingsHolder.SMALLFONT_SYMBOLDESC);
        initComponents();

        jTextFieldVar.setFont(SettingsHolder.CODEFONT);
        jTextFieldValue.setFont(SettingsHolder.CODEFONT);
        jLabelExamples.setFont(SettingsHolder.SMALL_CODEFONT);

        if (element.getSymbol().getCommands() != null) {
            jTextFieldVar.setText( element.getSymbol().getCommands().get("var"));
            jTextFieldValue.setText(element.getSymbol().getCommands().get("value"));
        }

        ((AbstractDocument) jTextFieldVar.getDocument()).setDocumentFilter(new VariableFilter(
                jTextFieldVar, validationListener, maxBalloonSizeCallback));
        ((AbstractDocument) jTextFieldValue.getDocument()).setDocumentFilter(new ValueFilter(
                jTextFieldValue, validationListener, maxBalloonSizeCallback));
        AbstractSymbolFunctionForm.enhanceWithUndoRedoCapability(jTextFieldVar);
        AbstractSymbolFunctionForm.enhanceWithUndoRedoCapability(jTextFieldValue);
        addDocumentListeners();
        super.trimSize();
    }

    @Override
    void addDocumentListeners()
    {
        jTextFieldVar.getDocument().addDocumentListener(this);
        jTextFieldValue.getDocument().addDocumentListener(this);
    }

    @Override
    void generateValues()
    {
        String var = "";
        String value = "";
        try {
            var = jTextFieldVar.getDocument().getText(0, jTextFieldVar.getDocument().getLength());
            value = jTextFieldValue.getDocument().getText(0,
                    jTextFieldValue.getDocument().getLength());
        } catch (BadLocationException ex) {
        }

        generateValues(super.getElement().getSymbol(), var.trim(), value.trim());
    }

    /**
     * Vygeneruje danému symbolu dané funkční příkazy a zároveň nastaví
     * defaultní text symbolu.
     * <p/>
     * @param symbol symbol, kterého se má generování hodnot týkat
     * @param var proměnná, se kterou se má operovat
     * @param value hodnota, která má bý do proměnné uložena
     */
    public static void generateValues(Symbol symbol, String var, String value)
    {
        if (!var.isEmpty() && !value.isEmpty()) {
            symbol.setDefaultValue(
                    AbstractSymbolFunctionForm.convertToPSDDisplayCommands(var) + " ← "
                    + AbstractSymbolFunctionForm.convertToPSDDisplayCommands(value));
        } else {
            symbol.setDefaultValue(null);
        }

        LinkedHashMap<String, String> commands = new LinkedHashMap<>();
        commands.put("var", var);
        commands.put("value", value);
        symbol.setCommands(commands);
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
        jTextFieldVar = new WatermarkJTextField("do jaké proměnné ukládat");
        jLabel2 = new javax.swing.JLabel();
        jTextFieldValue = new WatermarkJTextField("co uložit");
        jPanel1 = new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.JPanelSymbol(mySymbol, jLabelDescription);
        jLabel3 = new javax.swing.JLabel();
        jLabelExamples = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Zpracování"));
        setPreferredSize(new java.awt.Dimension(187, 493));

        jLabel1.setText("Proměnná:");

        jTextFieldVar.setToolTipText("Proměnná, do které se uloží hodnota níže.");

        jLabel2.setText("Hodnota:");

        jTextFieldValue.setToolTipText("<html>\nHodnota, která se uloží do proměnné výše.<br />\nŘetězcovou hodnotu ohraničte uvozovkami.\n</html>");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel3.setText("Příklady:");

        jLabelExamples.setText("<html>\n- A; 0<br />\n- B; B + A<br />\n- D; A // 5<br />\n- A; Math.sqrt(A*2)<br />\n- A; Math.floor(<br />Math.random()*10)+1<br />\n- A; B % C<br />\n- C; \"text\"<br />\n- C; \"hodnota A: \" + A<br />\n- A; \"B na druhou: \" + Math.pow(B,2)<br />\n- A; \"retezec\"[2]<br />\n- D; false<br />\n- pole; [1, 2, 3]<br />\n- pole[3]; 4<br />\n- pole[pole.length]; 5<br />\n- E; pole[2][0]<br />\n</html>");
        jLabelExamples.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTextFieldValue)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabelExamples)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTextFieldVar)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldVar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelExamples, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelExamples;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextFieldValue;
    private javax.swing.JTextField jTextFieldVar;
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
        return jTextFieldVar;
    }

    private class ProcessValidationListener implements ValidationListener
    {

        @Override
        public void validationStateChanged()
        {
            Boolean varValid = (Boolean) jTextFieldVar.getDocument().getProperty("commandValid");
            Boolean valueValid = (Boolean) jTextFieldValue.getDocument().getProperty("commandValid");
            if (varValid == null || valueValid == null) {
                // validation not completed on every command yet
                return;
            }

            if (varValid && valueValid) {
                Process.super.getElement().getSymbol().setCommandsValid(true);
            } else {
                Process.super.getElement().getSymbol().setCommandsValid(false);
            }

            Process.super.getFlowchartEditManager().repaintJPanelDiagram();
        }

    }

}
