/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.diagram.gui.symbolFunctionForms;

import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.symbols.EnumSymbol;
import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.diagram.gui.managers.FlowchartEditManager;
import cz.miroslavbartyzal.psdiagram.app.diagram.gui.symbolFunctionForms.documentFilters.ValueFilter;
import cz.miroslavbartyzal.psdiagram.app.global.RegexFunctions;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import java.util.LinkedHashMap;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;

/**
 * Tato třída představuje formulář pro editaci funkce symbolu cyklu s podmínkou
 * na začátku.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class LoopStart extends AbstractSymbolFunctionForm
{

    private JLabel jLabelDescription;
    private Symbol mySymbol = EnumSymbol.LOOPCONDITIONUP.getInstance(null);

    /**
     * Konstruktor, inicializující tento formulář.
     *
     * @param element element, kterého se tento formulář týká
     * @param flowchartEditManager FlowchartEditManager, spravující editační
     * režim aplikace
     */
    public LoopStart(LayoutElement element, FlowchartEditManager flowchartEditManager)
    {
        super(element, flowchartEditManager);
        /*
         * jLabelDescription = new JLabel("<html>"
         * + "- tělo cyklu se vykonává<br />jen pokud hodnota rozho-<br
         * />dovacího výrazu je logická<br />pravda<br />"
         * + "- dostupné relační operá-<br />tory: =,!=,&gt;,&lt;,&gt;=,&lt;=<br
         * />"
         * + "- dostupné logické operá-<br />tory: &(and),|(or),!(negace)"
         * + "</html>");
         */
        String operators;
        if (SettingsHolder.settings.isFunctionFilters()) {
            operators = "- dostupné relační operátory: =,!=,&gt;,&lt;,&gt;=,&lt;=<br />"
                    + "- dostupné logické operátory: &(and),|(or),!(negace)";
        } else {
            operators = "- dostupné relační operátory: ==, !=, &gt;, &lt;, &gt;=, &lt;=<br />"
                    + "- dostupné logické operátory: &&, &, ||, |, !(negace)";
        }
        operators += "</p></html>";
        jLabelDescription = new JLabel("<html><p>"
                + "- tělo cyklu se vykonává jen pokud hodnota rozhodovacího výrazu je logická pravda<br />"
                + operators);
        jLabelDescription.setFont(SettingsHolder.SMALLFONT_SYMBOLDESC);
        initComponents();

        jTextFieldCondition.setFont(SettingsHolder.CODEFONT);
        jLabelExamples.setFont(SettingsHolder.SMALL_CODEFONT);

        if (element.getSymbol().getCommands() != null) {
            if (SettingsHolder.settings.isFunctionFilters()) {
                jTextFieldCondition.setText(
                        element.getSymbol().getCommands().get("condition").replace("==", "=").replace(
                        "&&", "&").replace("||", "|")); // ulozeny jsou dvojite hodnoty
            } else {
                jTextFieldCondition.setText(element.getSymbol().getCommands().get("condition"));
            }
        }

        if (SettingsHolder.settings.isFunctionFilters()) {
            ((AbstractDocument) jTextFieldCondition.getDocument()).setDocumentFilter(
                    new ValueFilter());
        }
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

        generateValues(super.getElement().getSymbol(), condition);
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
        if (!condition.equals("")) {
            // v uvozovkach si nesmim vsimat niceho
            String[] conditionWithoutQ = RegexFunctions.splitString(condition, "\"[^\"]*\"?");
            for (int i = 0; i < conditionWithoutQ.length; i += 2) {
                conditionWithoutQ[i] = conditionWithoutQ[i].replace("!=", "≠").replace("!", "¬").replace(
                        ">=", "≥").replace("<=", "≤");
            }
            String defVal = "";
            for (String commandPart : conditionWithoutQ) {
                defVal += commandPart;
            }
            symbol.setDefaultValue(defVal);

            LinkedHashMap<String, String> commands = new LinkedHashMap<>();
            if (SettingsHolder.settings.isFunctionFilters()) {
                // v uvozovkach si nesmim vsimat niceho
                conditionWithoutQ = RegexFunctions.splitString(condition, "\"[^\"]*\"?");
                for (int i = 0; i < conditionWithoutQ.length; i += 2) {
                    conditionWithoutQ[i] = conditionWithoutQ[i].replace("!=", "≠").replace(">=", "≥").replace(
                            "<=", "≤").replace("=", "==").replace("&", "&&").replace("|", "||").replace(
                            "≠", "!=").replace("≥", ">=").replace("≤", "<=");
                }
                String cndtn = "";
                for (String commandPart : conditionWithoutQ) {
                    cndtn += commandPart;
                }
                commands.put("condition", cndtn); //ulozit dvojite hodnoty
            } else {
                commands.put("condition", condition);
            }
            symbol.setCommands(commands);
        } else {
            symbol.setDefaultValue(null);
            symbol.setCommands(null);
        }
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
        jTextFieldCondition = new javax.swing.JTextField();
        jPanel1 = new cz.miroslavbartyzal.psdiagram.app.diagram.gui.symbolFunctionForms.JPanelSymbol(mySymbol, jLabelDescription);
        jLabel3 = new javax.swing.JLabel();
        jLabelExamples = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("<html>\nCyklus s podmínkou na začátku\n</html>"));
        setPreferredSize(new java.awt.Dimension(187, 493));

        jLabel1.setText("Rozhodovací výraz:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );

        jLabel3.setText("Příklady:");

        jLabelExamples.setText("<html>\n- A > 0<br />\n- A >= B<br />\n- (A != B) & (A > 0)<br />\n- bool<br />\n- !bool<br />\n- !((A+B=C) | (B-A=C))\n</html>");
        jLabelExamples.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabelExamples, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
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
                .addContainerGap(263, Short.MAX_VALUE))
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
