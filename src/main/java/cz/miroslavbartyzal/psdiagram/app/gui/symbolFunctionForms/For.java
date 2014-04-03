/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms;

import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.EnumSymbol;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.gui.managers.FlowchartEditManager;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.NoArrayVariableFilter;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.NumericValueFilter;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import java.util.LinkedHashMap;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;

/**
 * Tato třída představuje formulář pro editaci funkce symbolu cyklu for.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class For extends AbstractSymbolFunctionForm
{

    private JLabel jLabelDescription;
    private Symbol mySymbol = EnumSymbol.FOR.getInstance(null);

    /**
     * Konstruktor, inicializující tento formulář.
     *
     * @param element element, kterého se tento formulář týká
     * @param flowchartEditManager FlowchartEditManager, spravující editační
     * režim aplikace
     */
    public For(LayoutElement element, FlowchartEditManager flowchartEditManager)
    {
        super(element, flowchartEditManager);
        /*
         * jLabelDescription = new JLabel("<html>"
         * + "- hodnota proměnné cyklu<br />se postupně mění a je k<br />dispozici pro výpočty v tě-<br />le cyklu<br />"
         * + "- proměnné, vytvořené<br />uvnitř těla cyklu, existují<br />jen po dobu aktuální, pro-<br />bíhající smyčky"
         * + "</html>");
         */
        jLabelDescription = new JLabel("<html><p>"
                + "- hodnota proměnné cyklu se postupně mění a je k dispozici pro výpočty v těle cyklu");
        if (SettingsHolder.settings.isBlockScopeVariables()) {
            jLabelDescription.setText(
                    jLabelDescription.getText() + "<br />- proměnné, vytvořené uvnitř těla cyklu, existují jen po dobu aktuální, probíhající smyčky");
        }
        jLabelDescription.setText(jLabelDescription.getText() + "</p></html>");
        jLabelDescription.setFont(SettingsHolder.SMALLFONT_SYMBOLDESC);

        initComponents();
        jTextFieldVar.setFont(SettingsHolder.CODEFONT);
        jTextFieldFrom.setFont(SettingsHolder.CODEFONT);
        jTextFieldForTo.setFont(SettingsHolder.CODEFONT);
        jTextFieldIncrement.setFont(SettingsHolder.CODEFONT);
        jTextFieldForeach.setFont(SettingsHolder.CODEFONT);
        jLabelExamples1.setFont(SettingsHolder.SMALL_CODEFONT);
        jLabelExamples2.setFont(SettingsHolder.SMALL_CODEFONT);

        if (element.getSymbol().getCommands() != null) {
            jTextFieldVar.setText(element.getSymbol().getCommands().get("var"));
            if (element.getSymbol().getCommands().containsKey("from")) {
                jTextFieldFrom.setText(element.getSymbol().getCommands().get("from"));
                jTextFieldForTo.setText(element.getSymbol().getCommands().get("to"));
                jTextFieldIncrement.setText(element.getSymbol().getCommands().get("inc"));
                jRadioButtonFor.setSelected(true);
                setForVisible();
            } else {
                jTextFieldForeach.setText(element.getSymbol().getCommands().get("array"));
                jRadioButtonForeach.setSelected(true);
                setForeachVisible();
            }
        } else {
            jRadioButtonFor.setSelected(true);
            setForVisible();
        }

        if (SettingsHolder.settings.isFunctionFilters()) {
            ((AbstractDocument) jTextFieldVar.getDocument()).setDocumentFilter(
                    new NoArrayVariableFilter());
            ((AbstractDocument) jTextFieldFrom.getDocument()).setDocumentFilter(
                    new NumericValueFilter());
            ((AbstractDocument) jTextFieldForTo.getDocument()).setDocumentFilter(
                    new NumericValueFilter());
            ((AbstractDocument) jTextFieldIncrement.getDocument()).setDocumentFilter(
                    new NumericValueFilter());
            ((AbstractDocument) jTextFieldForeach.getDocument()).setDocumentFilter(
                    new NoArrayVariableFilter());
        }
        addDocumentListeners();
        super.trimSize();
    }

    @Override
    void addDocumentListeners()
    {
        jTextFieldVar.getDocument().addDocumentListener(this);
        jTextFieldFrom.getDocument().addDocumentListener(this);
        jTextFieldForTo.getDocument().addDocumentListener(this);
        jTextFieldIncrement.getDocument().addDocumentListener(this);
        jTextFieldForeach.getDocument().addDocumentListener(this);
    }

    @Override
    void generateValues()
    {
        String var = "";
        String from = "";
        String to = "";
        String inc = "";
        String array = "";
        try {
            var = jTextFieldVar.getDocument().getText(0, jTextFieldVar.getDocument().getLength());
            from = jTextFieldFrom.getDocument().getText(0, jTextFieldFrom.getDocument().getLength());
            to = jTextFieldForTo.getDocument().getText(0, jTextFieldForTo.getDocument().getLength());
            inc = jTextFieldIncrement.getDocument().getText(0,
                    jTextFieldIncrement.getDocument().getLength());
            array = jTextFieldForeach.getDocument().getText(0,
                    jTextFieldForeach.getDocument().getLength());
        } catch (BadLocationException ex) {
        }

        if (jRadioButtonFor.isSelected()) {
            generateForValues(super.getElement().getSymbol(), var, from, to, inc);
        } else if (jRadioButtonForeach.isSelected()) {
            generateForeachValues(super.getElement().getSymbol(), var, array);
        }
    }

    /**
     * Vygeneruje danému symbolu dané funkční příkazy cyklu for a zároveň
     * nastaví defaultní text symbolu.
     * <p/>
     * @param symbol symbol, kterého se má generování hodnot týkat
     * @param var proměnná pole
     * @param from začít od
     * @param to počítat do
     * @param inc inkrement
     */
    public static void generateForValues(Symbol symbol, String var, String from, String to,
            String inc)
    {
        if (!var.equals("") && !from.equals("") && !to.equals("") && !inc.equals("")) {
            boolean setIncToDef = false;
            if (!inc.equals("1")) {
                setIncToDef = true;
            }

            if (from.matches("^\\-?[0-9]+(\\.[0-9])?") && to.matches("^\\-?[0-9]+(\\.[0-9])?") && inc.matches(
                    "^\\-?[0-9]+(\\.[0-9])?")) { // je treba zjistit, jestli increment neni nesmyslny - pak zobrazit increment
                double dfrom = Double.parseDouble(from);
                double dto = Double.parseDouble(to);
                double dinc = Double.parseDouble(inc);

                if (dfrom > dto) {
                    if (dinc == -1) {
                        setIncToDef = false;
                    } else if (dinc == 1) {
                        setIncToDef = true;
                    }
                }
            }

            symbol.setDefaultValue(var + " ← " + from + ".." + to);
            if (setIncToDef) {
                symbol.setDefaultValue(symbol.getDefaultValue() + "(" + inc + ")");
            }

            LinkedHashMap<String, String> commands = new LinkedHashMap<>();
            commands.put("var", var);
            commands.put("from", from);
            commands.put("to", to);
            commands.put("inc", inc);
            symbol.setCommands(commands);
        } else {
            symbol.setDefaultValue(null);
            symbol.setCommands(null);
        }
    }

    /**
     * Vygeneruje danému symbolu dané funkční příkazy cyklu for-each a zároveň
     * nastaví defaultní text symbolu.
     * <p/>
     * @param symbol symbol, kterého se má generování hodnot týkat
     * @param var proměnná cyklu
     * @param array proměnná pole
     */
    public static void generateForeachValues(Symbol symbol, String var, String array)
    {
        if (!var.equals("") && !array.equals("")) {
            symbol.setDefaultValue(var + " ← " + array + "[]");

            LinkedHashMap<String, String> commands = new LinkedHashMap<>();
            commands.put("var", var);
            commands.put("array", array);
            symbol.setCommands(commands);
        } else {
            symbol.setDefaultValue(null);
            symbol.setCommands(null);
        }
    }

    private void setForVisible()
    {
        jPanelForeach.setVisible(false);
        jPanelFor.setVisible(true);
    }

    private void setForeachVisible()
    {
        jPanelFor.setVisible(false);
        jPanelForeach.setVisible(true);
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
        jPanel1 = new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.JPanelSymbol(mySymbol, jLabelDescription);
        jRadioButtonFor = new javax.swing.JRadioButton();
        jRadioButtonForeach = new javax.swing.JRadioButton();
        jPanelFor = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldFrom = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldForTo = new javax.swing.JTextField();
        jLabelExampleLabel1 = new javax.swing.JLabel();
        jLabelExamples1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldIncrement = new javax.swing.JTextField();
        jPanelForeach = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldForeach = new javax.swing.JTextField();
        jLabelExampleLabel2 = new javax.swing.JLabel();
        jLabelExamples2 = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("<html>\nCyklus s pevným počtem opakování\n</html>"));
        setPreferredSize(new java.awt.Dimension(187, 493));

        jLabelVar.setText("Proměnná cyklu:");

        jTextFieldVar.setToolTipText("<html>\nProměnná, do které bude ukládána aktuální hodnota probíhajícího cyklu.<br />\nV případě for cyklu se hodnota rovná aktuální hodnotě počítadla.<br />\nV případě for-each cyklu se hodnota rovná aktuálnímu prvku z daného pole.<br />\n</html>");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 206, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        buttonGroup.add(jRadioButtonFor);
        jRadioButtonFor.setText("<html>\nZadaný počet opakování<br />\n(cyklus for)\n</html>");
        jRadioButtonFor.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jRadioButtonForActionPerformed(evt);
            }
        });

        buttonGroup.add(jRadioButtonForeach);
        jRadioButtonForeach.setText("<html>\nProjít všechny prvky pole<br />\n(cyklus for-each)\n</html>");
        jRadioButtonForeach.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jRadioButtonForeachActionPerformed(evt);
            }
        });

        jLabel1.setText("Začít od:");

        jTextFieldFrom.setToolTipText("<html>\nCyklus inkrementuje počáteční číselnou hodnotu vždy o hodnotu inkrementu,<br />\ndokud není dosažena maximální stanovená hodnota - tím cyklus končí.\n</html>");

        jLabel2.setText("Počítat do (včetně):");

        jTextFieldForTo.setToolTipText("<html>\nCyklus inkrementuje počáteční číselnou hodnotu vždy o hodnotu inkrementu,<br />\ndokud není dosažena maximální stanovená hodnota - tím cyklus končí.\n</html>");

        jLabelExampleLabel1.setText("Příklady:");

        jLabelExamples1.setText("<html>\n- I; 1; 10;1<br />\n- I; 10; 1;-1<br />\n- I; 0; pole.length-1;1\n</html>");
        jLabelExamples1.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel4.setText("Inkrement:");

        jTextFieldIncrement.setText("1");
        jTextFieldIncrement.setToolTipText("<html>\nNejčastěji se používá inkrement 1. Pro dekrementaci použijte zápornou hodnotu.\n</html>");

        javax.swing.GroupLayout jPanelForLayout = new javax.swing.GroupLayout(jPanelFor);
        jPanelFor.setLayout(jPanelForLayout);
        jPanelForLayout.setHorizontalGroup(
            jPanelForLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTextFieldFrom)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
            .addComponent(jTextFieldForTo)
            .addComponent(jLabelExampleLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabelExamples1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTextFieldIncrement)
        );
        jPanelForLayout.setVerticalGroup(
            jPanelForLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelForLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldForTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldIncrement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelExampleLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelExamples1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setText("Proměnná pole:");

        jLabelExampleLabel2.setText("Příklady:");

        jLabelExamples2.setText("<html>\n- prvek; pole<br />\n</html>");
        jLabelExamples2.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanelForeachLayout = new javax.swing.GroupLayout(jPanelForeach);
        jPanelForeach.setLayout(jPanelForeachLayout);
        jPanelForeachLayout.setHorizontalGroup(
            jPanelForeachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTextFieldForeach)
            .addComponent(jLabelExampleLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabelExamples2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanelForeachLayout.setVerticalGroup(
            jPanelForeachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelForeachLayout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldForeach, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelExampleLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelExamples2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelVar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTextFieldVar)
            .addComponent(jRadioButtonFor, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
            .addComponent(jRadioButtonForeach)
            .addComponent(jPanelFor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelForeach, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonFor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonForeach, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelVar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldVar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelFor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelForeach, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButtonForActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonForActionPerformed
        super.getFlowchartEditManager().prepareUndoManager();
        setForVisible();
        super.trimSize();
        generateValues();
        super.fireChangeEventToEditManager();
    }//GEN-LAST:event_jRadioButtonForActionPerformed

    private void jRadioButtonForeachActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonForeachActionPerformed
        super.getFlowchartEditManager().prepareUndoManager();
        setForeachVisible();
        super.trimSize();
        generateValues();
        super.fireChangeEventToEditManager();
    }//GEN-LAST:event_jRadioButtonForeachActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelExampleLabel1;
    private javax.swing.JLabel jLabelExampleLabel2;
    private javax.swing.JLabel jLabelExamples1;
    private javax.swing.JLabel jLabelExamples2;
    private javax.swing.JLabel jLabelVar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelFor;
    private javax.swing.JPanel jPanelForeach;
    private javax.swing.JRadioButton jRadioButtonFor;
    private javax.swing.JRadioButton jRadioButtonForeach;
    private javax.swing.JTextField jTextFieldForTo;
    private javax.swing.JTextField jTextFieldForeach;
    private javax.swing.JTextField jTextFieldFrom;
    private javax.swing.JTextField jTextFieldIncrement;
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
