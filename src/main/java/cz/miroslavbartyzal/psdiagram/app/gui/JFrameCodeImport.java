/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui;

import cz.miroslavbartyzal.psdiagram.app.codeImportExport.EnumSourceCode;
import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;
import java.awt.event.*;
import javax.swing.JRadioButton;
import javax.swing.JViewport;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Tato třída představuje formulář pro import zdrojového kódu do vývojového
 * diagramu.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class JFrameCodeImport extends javax.swing.JFrame
{

    private final MainWindow mainwindow;

    /**
     * Vytvoří nový formulář JFrameCodeImport
     * <p/>
     * @param mainwindow
     */
    public JFrameCodeImport(MainWindow mainwindow)
    {
        this.mainwindow = mainwindow;

        initComponents();

        for (EnumSourceCode enumSourceCode : EnumSourceCode.values()) {
            JRadioButton jRadioButton = new JRadioButton(enumSourceCode.getUniqueTextValue());
            jRadioButton.setActionCommand(enumSourceCode.name());
            jRadioButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent ae)
                {
                    jTextAreaCode.setText(EnumSourceCode.valueOf(
                            buttonGroup.getSelection().getActionCommand()).getGuideText());
                    jTextAreaCode.setEnabled(true);
                    jTextAreaCode.requestFocusInWindow();
                }
            });
            buttonGroup.add(jRadioButton);
            jPanelCodes.add(jRadioButton);
        }
        jScrollPane1.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE); // prevents glitches (http://andrewtill.blogspot.cz/2012/06/jscrollpane-repainting-problems.html)

        super.setMinimumSize(super.getPreferredSize());
        jTextAreaCode.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent de)
            {
                setGenerateEnabled();
            }

            @Override
            public void removeUpdate(DocumentEvent de)
            {
                setGenerateEnabled();
            }

            @Override
            public void changedUpdate(DocumentEvent de)
            {
                setGenerateEnabled();
            }
        });
        jTextAreaCode.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent me)
            {
                if (jTextAreaCode.getText().equals(EnumSourceCode.valueOf(
                        buttonGroup.getSelection().getActionCommand()).getGuideText())) {
                    jTextAreaCode.selectAll();
                }
            }

            @Override
            public void mouseReleased(MouseEvent me)
            {
                if (jTextAreaCode.getText().equals(EnumSourceCode.valueOf(
                        buttonGroup.getSelection().getActionCommand()).getGuideText())) {
                    jTextAreaCode.selectAll();
                }
            }
        });
        jTextAreaCode.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent fe)
            {
                if (jTextAreaCode.getText().equals(EnumSourceCode.valueOf(
                        buttonGroup.getSelection().getActionCommand()).getGuideText())) {
                    jTextAreaCode.selectAll();
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        buttonGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaCode = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jButtonGenerate = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanelDescription = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanelCodes = new javax.swing.JPanel();

        setTitle("Import ze zdrojového kódu");
        setAlwaysOnTop(true);
        setType(java.awt.Window.Type.UTILITY);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("<html>Vstupní zdrojový kód</html>"));

        jTextAreaCode.setColumns(20);
        jTextAreaCode.setRows(5);
        jTextAreaCode.setTabSize(3);
        jTextAreaCode.setEnabled(false);
        jScrollPane1.setViewportView(jTextAreaCode);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
        );

        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jButtonGenerate.setText("Generuj diagram");
        jButtonGenerate.setEnabled(false);
        jButtonGenerate.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonGenerateActionPerformed(evt);
            }
        });
        jPanel5.add(jButtonGenerate);

        jPanelDescription.setBorder(javax.swing.BorderFactory.createTitledBorder("<html>Popis funkce</html>"));

        jLabel1.setText("<html>\nTato funkce slouží k rychlému vygenerování vývojového diagramu z vloženého zdrojového kódu některého z podporovaných programovacích jazyků.<br /><br />\nSprávnost vygenerovaného diagramu není vždy stoprocentní, proto je doporučeno výsledný diagram ještě překontrolovat.\n</html>");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanelDescriptionLayout = new javax.swing.GroupLayout(jPanelDescription);
        jPanelDescription.setLayout(jPanelDescriptionLayout);
        jPanelDescriptionLayout.setHorizontalGroup(
            jPanelDescriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanelDescriptionLayout.setVerticalGroup(
            jPanelDescriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanelCodes.setBorder(javax.swing.BorderFactory.createTitledBorder("<html>Volba programovacího jazyka</html>"));
        jPanelCodes.setMinimumSize(new java.awt.Dimension(150, 0));
        jPanelCodes.setPreferredSize(new java.awt.Dimension(150, 42));
        jPanelCodes.setLayout(new javax.swing.BoxLayout(jPanelCodes, javax.swing.BoxLayout.PAGE_AXIS));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanelCodes, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanelDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelCodes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGenerateActionPerformed
        super.setAlwaysOnTop(false); // kdyby generovani diagramu hodilo errormessage
        Flowchart<LayoutSegment, LayoutElement> flowchart = EnumSourceCode.valueOf(
                buttonGroup.getSelection().getActionCommand()).getFlowchart(jTextAreaCode.getText());
        super.setAlwaysOnTop(true);

        if (flowchart != null) {
            super.setAlwaysOnTop(false);
            if (mainwindow.openGeneratedDiagram(flowchart)) {
                buttonGroup.clearSelection();
                jTextAreaCode.setEnabled(false);
                jTextAreaCode.setText("");
                super.setVisible(false);
            }
            super.setAlwaysOnTop(true);
        }
    }//GEN-LAST:event_jButtonGenerateActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JButton jButtonGenerate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelCodes;
    private javax.swing.JPanel jPanelDescription;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaCode;
    // End of variables declaration//GEN-END:variables

    private void setGenerateEnabled()
    {
        if (jTextAreaCode.getText().equals("") || jTextAreaCode.getText().equals(
                EnumSourceCode.valueOf(buttonGroup.getSelection().getActionCommand()).getGuideText())) {
            jButtonGenerate.setEnabled(false);
        } else {
            jButtonGenerate.setEnabled(true);
        }
    }

//    private class SourceCodesPanel extends javax.swing.JPanel {
//
//        @Override
//        public Dimension getPreferredSize() {
//            trimSize();
//            return super.getPreferredSize();
//        }
//
//        @Override
//        public void setPreferredSize(Dimension dmnsn) {
//            if (dmnsn.width < super.getMinimumSize().width) {
//                dmnsn = new Dimension(super.getMinimumSize().width, dmnsn.height);
//            }
//            super.setPreferredSize(dmnsn);
//        }
//
//        private void trimSize() {
//            this.setPreferredSize(this.getLayout().minimumLayoutSize(this));
//            this.repaint();
//        }
//
//    }
}
