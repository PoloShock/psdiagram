/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms;

import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.EnumSymbol;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.gui.managers.FlowchartEditManager;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import cz.miroslavbartyzal.psdiagram.app.gui.balloonToolTip.MaxBalloonSizeCallback;
import java.util.LinkedHashMap;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

/**
 * Tato třída představuje formulář pro editaci funkce symbolu Spojky -
 * break/continue/goto.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class Goto extends AbstractSymbolFunctionForm
{

    private JLabel jLabelDescription;
    private Symbol mySymbol = EnumSymbol.GOTO.getInstance(null);

    /**
     * Konstruktor, inicializující tento formulář.
     *
     * @param element element, kterého se tento formulář týká
     * @param flowchartEditManager FlowchartEditManager, spravující editační
     * režim aplikace
     * @param maxBalloonSizeCallback
     */
    public Goto(LayoutElement element, FlowchartEditManager flowchartEditManager,
            MaxBalloonSizeCallback maxBalloonSizeCallback)
    {
        super(element, flowchartEditManager);
        /*
         * jLabelDescription = new JLabel("<html>"
         * + "- skok na místo označené<br />návěštím stejného identifi-<br
         * />kátoru<br />"
         * + "- identifikátor se automa-<br />ticky přejímá z textu sym-<br
         * />bolu"
         * + "</html>");
         */
        jLabelDescription = new JLabel("<html>"
                + "BREAK:<br />"
                + "- ukončí aktuálně probíhající cyklus<br />"
                + "- tok programu je přesměrován na následující příkaz za tímto cyklem<br />"
                + "CONTINUE:<br />"
                + "- přeruší aktuálně probíhající iteraci (smyčku) cyklu<br />"
                + "- tok programu je předán řídícímu symbolu cyklu<br />"
                + "GOTO:<br />"
                + "- skok na místo označené návěštím stejného identifikátoru<br />"
                + "- identifikátor se automaticky přejímá z textu symbolu"
                + "</html>");
        jLabelDescription.setFont(SettingsHolder.SMALLFONT_SYMBOLDESC);
        initComponents();

        if (element.getSymbol().getCommands() != null) {
            switch (element.getSymbol().getCommands().get("mode")) {
                case "break":
                    jRadioButtonBreak.setSelected(true);
                    break;
                case "continue":
                    jRadioButtonContinue.setSelected(true);
                    break;
                default:
                    jRadioButtonGoto.setSelected(true);
                    break;
            }
        } else {
            jRadioButtonBreak.setSelected(true);
            jRadioButtonBreakActionPerformed(null);
        }

        super.trimSize();
    }

    @Override
    void addDocumentListeners()
    {
    }

    @Override
    void generateValues()
    {
        if (jRadioButtonBreak.isSelected()) {
            generateBreakValues(super.getElement().getSymbol());
        } else if (jRadioButtonContinue.isSelected()) {
            generateContinueValues(super.getElement().getSymbol());
        } else if (jRadioButtonGoto.isSelected()) {
            generateGotoValues(super.getElement().getSymbol());
        }
    }

    /**
     * Vygeneruje danému symbolu funkci break a zároveň nastaví defaultní text
     * symbolu.
     *
     * @param symbol symbol, kterého se má generování hodnot týkat
     */
    public static void generateBreakValues(Symbol symbol)
    {
        symbol.setDefaultValue("__\nBR\n__");

        LinkedHashMap<String, String> commands = new LinkedHashMap<>();
        commands.put("mode", "break");
        symbol.setCommands(commands);
    }

    /**
     * Vygeneruje danému symbolu funkci continue a zároveň nastaví defaultní
     * text symbolu.
     *
     * @param symbol symbol, kterého se má generování hodnot týkat
     */
    public static void generateContinueValues(Symbol symbol)
    {
        symbol.setDefaultValue("__\nCT\n__");

        LinkedHashMap<String, String> commands = new LinkedHashMap<>();
        commands.put("mode", "continue");
        symbol.setCommands(commands);
    }

    /**
     * Vygeneruje danému symbolu goto a zároveň nastaví defaultní text symbolu.
     *
     * @param symbol symbol, kterého se má generování hodnot týkat
     */
    public static void generateGotoValues(Symbol symbol)
    {
        symbol.setDefaultValue(null);

        LinkedHashMap<String, String> commands = new LinkedHashMap<>();
        commands.put("mode", "goto");
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

        buttonGroup = new javax.swing.ButtonGroup();
        jPanel1 = new cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.JPanelSymbol(mySymbol, jLabelDescription);
        jRadioButtonBreak = new javax.swing.JRadioButton();
        jRadioButtonContinue = new javax.swing.JRadioButton();
        jRadioButtonGoto = new javax.swing.JRadioButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder("<html>\nSpojka - goto, break, nebo continue\n</html>"));
        setPreferredSize(new java.awt.Dimension(187, 493));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 159, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 6, Short.MAX_VALUE)
        );

        buttonGroup.add(jRadioButtonBreak);
        jRadioButtonBreak.setText("Break");
        jRadioButtonBreak.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jRadioButtonBreakActionPerformed(evt);
            }
        });

        buttonGroup.add(jRadioButtonContinue);
        jRadioButtonContinue.setText("Continue");
        jRadioButtonContinue.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jRadioButtonContinueActionPerformed(evt);
            }
        });

        buttonGroup.add(jRadioButtonGoto);
        jRadioButtonGoto.setText("Goto");
        jRadioButtonGoto.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jRadioButtonGotoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonBreak)
                    .addComponent(jRadioButtonContinue)
                    .addComponent(jRadioButtonGoto))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonBreak)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonContinue)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonGoto)
                .addContainerGap(373, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButtonBreakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonBreakActionPerformed
        super.setHasCommandsToSet(true);
        generateValues();
        super.fireChangeEventToEditManager();
    }//GEN-LAST:event_jRadioButtonBreakActionPerformed

    private void jRadioButtonContinueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonContinueActionPerformed
        super.setHasCommandsToSet(true);
        generateValues();
        super.fireChangeEventToEditManager();
    }//GEN-LAST:event_jRadioButtonContinueActionPerformed

    private void jRadioButtonGotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonGotoActionPerformed
        super.setHasCommandsToSet(false);
        generateValues();
        super.fireChangeEventToEditManager();
    }//GEN-LAST:event_jRadioButtonGotoActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButtonBreak;
    private javax.swing.JRadioButton jRadioButtonContinue;
    private javax.swing.JRadioButton jRadioButtonGoto;
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
     * Metoda s prázdným tělem.
     * <p>
     * @param de
     */
    @Override
    public void insertUpdate(DocumentEvent de)
    {
    }

    /**
     * Metoda s prázdným tělem.
     * <p>
     * @param de
     */
    @Override
    public void removeUpdate(DocumentEvent de)
    {
    }

    @Override
    public JTextField getJTextFieldToDispatchKeyEventsAt()
    {
        return null;
    }

}
