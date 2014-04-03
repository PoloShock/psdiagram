/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms;

import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.EnumSymbol;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.gui.managers.FlowchartEditManager;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.ValueFilter;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.VariableFilter;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import java.util.LinkedHashMap;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;

/**
 * Tato třída představuje formulář pro editaci funkce symbolu Vstup/Výstup.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class IO extends AbstractSymbolFunctionForm
{

    private JLabel jLabelDescription;
    private Symbol mySymbol = EnumSymbol.IO.getInstance(null);

    /**
     * Konstruktor, inicializující tento formulář.
     *
     * @param element element, kterého se tento formulář týká
     * @param flowchartEditManager FlowchartEditManager, spravující editační
     * režim aplikace
     */
    public IO(LayoutElement element, FlowchartEditManager flowchartEditManager)
    {
        super(element, flowchartEditManager);
        /*
         * jLabelDescription = new JLabel("<html>"
         * + "- vstup/výstup pomocí<br />dialogu<br />"
         * + "- v případě, že při vstupní<br />operaci cílová proměnná<br />neexistuje, bude vytvoře-<br />na"
         * + "</html>");
         */
        jLabelDescription = new JLabel("<html><p>"
                + "- vstup/výstup pomocí dialogu<br />"
                + "- v případě, že při vstupní operaci cílová proměnná neexistuje, bude vytvořena"
                + "</p></html>");
        jLabelDescription.setFont(SettingsHolder.SMALLFONT_SYMBOLDESC);

        initComponents();

        jTextFieldVar.setFont(SettingsHolder.CODEFONT);
        jTextFieldValue.setFont(SettingsHolder.CODEFONT);
        jLabelExamples.setFont(SettingsHolder.SMALL_CODEFONT);

        if (element.getSymbol().getCommands() != null) {
            if (element.getSymbol().getCommands().containsKey("var")) {
                jTextFieldVar.setText(element.getSymbol().getCommands().get("var"));
                jRadioButtonInput.setSelected(true);
                setInputVisible();
            } else {
                jTextFieldValue.setText(element.getSymbol().getCommands().get("value"));
                jRadioButtonOutput.setSelected(true);
                setOutputVisible();
            }
        } else {
            jRadioButtonInput.setSelected(true);
            setInputVisible();
        }

        if (SettingsHolder.settings.isFunctionFilters()) {
            ((AbstractDocument) jTextFieldVar.getDocument()).setDocumentFilter(new VariableFilter());
            ((AbstractDocument) jTextFieldValue.getDocument()).setDocumentFilter(new ValueFilter());
        }
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

        if (jRadioButtonInput.isSelected()) {
            generateIValues(super.getElement().getSymbol(), var);
        } else if (jRadioButtonOutput.isSelected()) {
            generateOValues(super.getElement().getSymbol(), value);
        }
    }

    /**
     * Vygeneruje danému symbolu funkční příkaz vstupu a zároveň nastaví
     * defaultní text symbolu.
     * <p/>
     * @param symbol symbol, kterého se má generování hodnot týkat
     * @param var proměnná, do které se má vstup uložit
     */
    public static void generateIValues(Symbol symbol, String var)
    {
        if (!var.equals("")) {
            symbol.setDefaultValue(var + " ←");

            LinkedHashMap<String, String> commands = new LinkedHashMap<>();
            commands.put("var", var);
            symbol.setCommands(commands);
        } else {
            symbol.setDefaultValue(null);
            symbol.setCommands(null);
        }
    }

    /**
     * Vygeneruje danému symbolu funkční příkaz výstupu a zároveň nastaví
     * defaultní text symbolu
     * <p/>
     * @param symbol symbol, kterého se má generování hodnot týkat
     * @param value hodnota, která má vést na výstup
     */
    public static void generateOValues(Symbol symbol, String value)
    {
        if (!value.equals("")) {
            symbol.setDefaultValue(value + " →");

            LinkedHashMap<String, String> commands = new LinkedHashMap<>();
            commands.put("value", value);
            symbol.setCommands(commands);
        } else {
            symbol.setDefaultValue(null);
            symbol.setCommands(null);
        }
    }

    private void setInputVisible()
    {
        jLabelValue.setVisible(false);
        jTextFieldValue.setVisible(false);
        jLabelExampleLabel.setVisible(false);
        jLabelExamples.setVisible(false);
        jLabelVar.setVisible(true);
        jTextFieldVar.setVisible(true);
    }

    private void setOutputVisible()
    {
        jLabelValue.setVisible(true);
        jTextFieldValue.setVisible(true);
        jLabelExampleLabel.setVisible(true);
        jLabelExamples.setVisible(true);
        jLabelVar.setVisible(false);
        jTextFieldVar.setVisible(false);
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

        buttonGroup = new javax.swing.ButtonGroup();
        jLabelVar = new javax.swing.JLabel();
        jTextFieldVar = new javax.swing.JTextField();
        jLabelValue = new javax.swing.JLabel();
        jTextFieldValue = new javax.swing.JTextField();
        jPanel1 = new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.JPanelSymbol(mySymbol, jLabelDescription);
        jLabelExampleLabel = new javax.swing.JLabel();
        jLabelExamples = new javax.swing.JLabel();
        jRadioButtonInput = new javax.swing.JRadioButton();
        jRadioButtonOutput = new javax.swing.JRadioButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Vstup/Výstup"));
        setPreferredSize(new java.awt.Dimension(187, 493));

        jLabelVar.setText("Do proměnné:");

        jTextFieldVar.setToolTipText("Proměnná, do které bude vstupní hodnota uložena.");

        jLabelValue.setText("Hodnota pro výstup:");

        jTextFieldValue.setToolTipText("<html>\nHodnota, která se má zobrazit na výstupu.<br />\nŘetězcovou hodnotu ohraničte uvozovkami, matematické operace závorkami.\n</html>");

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

        jLabelExampleLabel.setText("Příklady:");

        jLabelExamples.setText("<html>\n- A<br />\n- \"nějaký text\"<br />\n- \"hodnota A: \" + A<br />\n- \"A+B = \" + (A+B)<br />\n</html>");
        jLabelExamples.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        buttonGroup.add(jRadioButtonInput);
        jRadioButtonInput.setText("Vstup");
        jRadioButtonInput.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jRadioButtonInputActionPerformed(evt);
            }
        });

        buttonGroup.add(jRadioButtonOutput);
        jRadioButtonOutput.setText("Výstup");
        jRadioButtonOutput.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jRadioButtonOutputActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelVar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabelValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTextFieldValue)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabelExamples, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jLabelExampleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTextFieldVar)
            .addComponent(jRadioButtonInput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jRadioButtonOutput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonInput)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonOutput)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelVar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldVar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelValue)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelExampleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelExamples, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(177, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButtonInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonInputActionPerformed
        super.getFlowchartEditManager().prepareUndoManager();
        setInputVisible();
        super.trimSize();
        generateValues();
        super.fireChangeEventToEditManager();
    }//GEN-LAST:event_jRadioButtonInputActionPerformed

    private void jRadioButtonOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonOutputActionPerformed
        super.getFlowchartEditManager().prepareUndoManager();
        setOutputVisible();
        super.trimSize();
        generateValues();
        super.fireChangeEventToEditManager();
    }//GEN-LAST:event_jRadioButtonOutputActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JLabel jLabelExampleLabel;
    private javax.swing.JLabel jLabelExamples;
    private javax.swing.JLabel jLabelValue;
    private javax.swing.JLabel jLabelVar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButtonInput;
    private javax.swing.JRadioButton jRadioButtonOutput;
    private javax.swing.JTextField jTextFieldValue;
    private javax.swing.JTextField jTextFieldVar;
    // End of variables declaration//GEN-END:variables

    /**
     * Metoda s prázdným tělem.
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
        super.getFlowchartEditManager().prepareUndoManager();
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
        super.getFlowchartEditManager().prepareUndoManager();
        generateValues();
        super.fireChangeEventToEditManager();
    }

}
