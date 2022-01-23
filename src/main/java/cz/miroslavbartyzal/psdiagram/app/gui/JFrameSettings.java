/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import cz.miroslavbartyzal.psdiagram.app.global.GlobalFunctions;
import cz.miroslavbartyzal.psdiagram.app.global.MyExceptionHandler;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import cz.miroslavbartyzal.psdiagram.app.gui.managers.FlowchartEditManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import javax.swing.JSpinner;
import javax.swing.JViewport;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

/**
 * Tato třída představuje formulář s volitelným nastavením aplikace.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class JFrameSettings extends javax.swing.JFrame
{

    private boolean initializing = true;
    private final FlowchartEditManager flowchartEditManager;

    /**
     * Vytvoří nový formulář JFrameSettings.
     * <p>
     * @param flowchartEditManager
     */
    public JFrameSettings(FlowchartEditManager flowchartEditManager)
    {
        this.flowchartEditManager = flowchartEditManager;
        initComponents();
        jCheckBoxLoadLast.setSelected(SettingsHolder.settings.isLoadLastFlowchart());

        if (GlobalFunctions.isWindows()) {
            if (SettingsHolder.settings.getAssociateExtension() == null) {
                // application launched with settings not specifing association -> lets try to set it with care of possible other defaults to *.psdiagram
                boolean success = ensureAssociationState(true, true);
                jCheckBoxAssoc.setSelected(success);
                if (success) {
                    SettingsHolder.settings.setAssociateExtension(true); // association was successfuly assigned -> lets save it
                }
            } else {
                jCheckBoxAssoc.setSelected(ensureAssociationState(
                        SettingsHolder.settings.getAssociateExtension(), true));
            }
        } else {
            jCheckBoxAssoc.setEnabled(false);
        }

        jCheckBoxBallShine.setSelected(SettingsHolder.settings.isBallShine());
        jSliderRadius.setEnabled(jCheckBoxBallShine.isSelected());
        jSliderRadius.setValue(SettingsHolder.settings.getBallShineRadius());
        jSpinnerFPS.getModel().setValue(SettingsHolder.settings.getFps());
        ((JSpinner.DefaultEditor) jSpinnerFPS.getEditor()).getTextField().setEditable(false);
        if (SettingsHolder.settings.isBlockScopeVariables()) {
            jRadioButtonBlockScope.setSelected(true);
        } else {
            jRadioButtonGlobalScope.setSelected(true);
        }
        jCheckBoxExportTransparency.setSelected(SettingsHolder.settings.isExportTransparency());
        jTextFieldPadding.setText(Integer.toString(
                SettingsHolder.settings.getExportFlowchartPadding()));

        jScrollPane2.getVerticalScrollBar().setUnitIncrement(10);
        jScrollPane2.getHorizontalScrollBar().setUnitIncrement(10);
        setPreferedSizes();
        jScrollPane2.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE); // prevents glitches (http://andrewtill.blogspot.cz/2012/06/jscrollpane-repainting-problems.html)
        jScrollPane3.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE); // prevents glitches (http://andrewtill.blogspot.cz/2012/06/jscrollpane-repainting-problems.html)
        jScrollPane4.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE); // prevents glitches (http://andrewtill.blogspot.cz/2012/06/jscrollpane-repainting-problems.html)

        ((AbstractDocument) jTextFieldPadding.getDocument()).setDocumentFilter(new DocumentFilter()
        {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet as) throws BadLocationException
            {
                if (fb.getDocument().getLength() + text.length() > 3 || text.matches(".*[^0-9].*")) {
                    return;
                }
                super.insertString(fb, offset, text, as);
                SettingsHolder.settings.setExportFlowchartPadding(Integer.valueOf(
                        jTextFieldPadding.getText()));
            }

            @Override
            public void remove(FilterBypass fb, int i, int i1) throws BadLocationException
            {
                super.remove(fb, i, i1);
                if (fb.getDocument().getLength() == 0) {
                    insertString(fb, 0, "0", null);
                    jTextFieldPadding.selectAll();
                } else {
                    SettingsHolder.settings.setExportFlowchartPadding(Integer.valueOf(
                            jTextFieldPadding.getText()));
                }
            }

            @Override
            public void replace(FilterBypass fb, int i, int i1, String string, AttributeSet as) throws BadLocationException
            {
                if (fb.getDocument().getLength() + string.length() - i1 > 3 || string.matches(
                        ".*[^0-9].*")) {
                    return;
                }
                super.replace(fb, i, i1, string, as);
                if (fb.getDocument().getLength() == 0) {
                    insertString(fb, 0, "0", null);
                    jTextFieldPadding.selectAll();
                } else {
                    SettingsHolder.settings.setExportFlowchartPadding(Integer.valueOf(
                            jTextFieldPadding.getText()));
                }
            }
        });
        /*
         * jTextFieldPadding.getDocument().addDocumentListener(new DocumentListener() {
         * @Override
         * public void insertUpdate(DocumentEvent de) {
         * try {
         * Settings.exportFlowchartPadding = Integer.valueOf(de.getDocument().getText(0, de.getDocument().getLength()));
         * } catch (BadLocationException ex) {
         * Logger.getLogger(JFrameSettings.class.getName()).log(Level.SEVERE, null, ex);
         * }
         * }
         * @Override
         * public void removeUpdate(DocumentEvent de) {
         * try {
         * Settings.exportFlowchartPadding = Integer.valueOf(de.getDocument().getText(0, de.getDocument().getLength()));
         * } catch (BadLocationException ex) {
         * Logger.getLogger(JFrameSettings.class.getName()).log(Level.SEVERE, null, ex);
         * }
         * }
         * @Override
         * public void changedUpdate(DocumentEvent de) {
         * }
         * });
         */
        this.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent ce)
            {
                setPreferedSizes();
            }
        });
        initializing = false;
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

        buttonGroupScopes = new javax.swing.ButtonGroup();
        jTabbedPaneSettings = new javax.swing.JTabbedPane();
        jPanelGeneral = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jCheckBoxLoadLast = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        jCheckBoxAssoc = new javax.swing.JCheckBox();
        jLabel11 = new javax.swing.JLabel();
        jPanelAnimMode = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jRadioButtonGlobalScope = new javax.swing.JRadioButton();
        jRadioButtonBlockScope = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jCheckBoxBallShine = new javax.swing.JCheckBox();
        jSliderRadius = new javax.swing.JSlider();
        jLabelRadius = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jSpinnerFPS = new javax.swing.JSpinner();
        jPanelExport = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jCheckBoxExportTransparency = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldPadding = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setTitle("Volby nastavení");
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(150, 300));
        setType(java.awt.Window.Type.UTILITY);

        jPanelGeneral.setPreferredSize(new java.awt.Dimension(0, 0));

        jPanel9.setPreferredSize(new java.awt.Dimension(0, 0));

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("<html>Při spuštění</html>"));

        jCheckBoxLoadLast.setText("Načítat předchozí uložený diagram");
        jCheckBoxLoadLast.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jCheckBoxLoadLastActionPerformed(evt);
            }
        });

        jLabel8.setText("<html>\nJe-li volba povolena a pokud při minulém zavření aplikace byl otevřen uložený diagram, při opětovném spuštění aplikace se tento diagram znovu načte.\n</html>");

        jCheckBoxAssoc.setText("Asociovat s příponou *.psdiagram");
        jCheckBoxAssoc.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jCheckBoxAssocActionPerformed(evt);
            }
        });

        jLabel11.setText("<html>\nJe-li volba povolena a ve Windows je poklepáno na uložený soubor diagramu, otevře se automaticky v PS Diagramu.\n</html>");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8)
            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxLoadLast)
                    .addComponent(jCheckBoxAssoc))
                .addGap(0, 204, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jCheckBoxLoadLast)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxAssoc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 122, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPane4.setViewportView(jPanel9);

        javax.swing.GroupLayout jPanelGeneralLayout = new javax.swing.GroupLayout(jPanelGeneral);
        jPanelGeneral.setLayout(jPanelGeneralLayout);
        jPanelGeneralLayout.setHorizontalGroup(
            jPanelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
        );
        jPanelGeneralLayout.setVerticalGroup(
            jPanelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
        );

        jTabbedPaneSettings.addTab("Obecné", jPanelGeneral);

        jPanelAnimMode.setPreferredSize(new java.awt.Dimension(0, 0));

        jPanel3.setPreferredSize(new java.awt.Dimension(0, 0));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("<html>Nakládání s proměnnými</html>"));

        buttonGroupScopes.add(jRadioButtonGlobalScope);
        jRadioButtonGlobalScope.setText("Globální přístup");
        jRadioButtonGlobalScope.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jRadioButtonBlockScopeActionPerformed(evt);
            }
        });

        buttonGroupScopes.add(jRadioButtonBlockScope);
        jRadioButtonBlockScope.setText("Blokový přístup");
        jRadioButtonBlockScope.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jRadioButtonBlockScopeActionPerformed(evt);
            }
        });

        jLabel2.setText("<html>\nVytvořené proměnné existují globálně - nezanikají a po jejich vytvoření je s nimi možné pracovat kdykoliv a kdekoliv v rámci celého diagramu.\n</html>");
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel3.setText("<html>\nVytvořené proměnné existují jen v rámci svého bloku (větve symbolu) a ve větvích do něj vnořených. Jakmile se tok programu ocitne mimo tento blok (nebo jeho vnořené bloky), proměnná zaniká.\n</html>");
        jLabel3.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonGlobalScope)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jRadioButtonBlockScope)
                        .addGap(0, 91, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonGlobalScope)
                    .addComponent(jRadioButtonBlockScope))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("<html>Animace</html>"));

        jCheckBoxBallShine.setText("Záře průchozí kuličky");
        jCheckBoxBallShine.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jCheckBoxBallShineActionPerformed(evt);
            }
        });

        jSliderRadius.setMaximum(400);
        jSliderRadius.setMinimum(50);
        jSliderRadius.setValue(200);
        jSliderRadius.setEnabled(false);
        jSliderRadius.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                jSliderRadiusStateChanged(evt);
            }
        });

        jLabelRadius.setText("Radius záře kuličky: 200 pixelů");

        jLabel9.setText("Počet snímků za sekundu: ");

        jLabel10.setText("<html>\nUrčuje FPS (počet snímků za sekundu) animace. Snížením této hodnoty docílíte nižší náročnosti na výpočetní výkon.\n</html>");

        jSpinnerFPS.setModel(new javax.swing.SpinnerNumberModel(10, 10, 40, 1));
        jSpinnerFPS.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                jSpinnerFPSStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxBallShine)
                    .addComponent(jLabelRadius)
                    .addComponent(jSliderRadius, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerFPS, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jSpinnerFPS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jCheckBoxBallShine)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelRadius)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSliderRadius, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jScrollPane2.setViewportView(jPanel3);

        javax.swing.GroupLayout jPanelAnimModeLayout = new javax.swing.GroupLayout(jPanelAnimMode);
        jPanelAnimMode.setLayout(jPanelAnimModeLayout);
        jPanelAnimModeLayout.setHorizontalGroup(
            jPanelAnimModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
        );
        jPanelAnimModeLayout.setVerticalGroup(
            jPanelAnimModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
        );

        jTabbedPaneSettings.addTab("Animační režim", jPanelAnimMode);

        jPanelExport.setPreferredSize(new java.awt.Dimension(0, 0));

        jPanel6.setPreferredSize(new java.awt.Dimension(0, 0));

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("<html>Export do obrázku</html>"));

        jCheckBoxExportTransparency.setText("Používat transparentní pozadí");
        jCheckBoxExportTransparency.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jCheckBoxExportTransparencyActionPerformed(evt);
            }
        });

        jLabel5.setText("<html>\nJe-li volba povolena a dovoluje-li zvolený obrazový formát transparentnost, pro vykreslení pozadí vývojového diagramu se použije transparentní barva.<br />\nTransparenci podporuje formát <span style=\"font-style:italic;\">*.png</span> a částečně i starší <span style=\"font-style:italic;\">*.gif</span>.\n</html>");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jCheckBoxExportTransparency)
                .addGap(0, 231, Short.MAX_VALUE))
            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jCheckBoxExportTransparency)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(114, Short.MAX_VALUE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("<html>Export obecné</html>"));

        jLabel6.setText("Okraje exportovaného diagramu: ");

        jTextFieldPadding.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel7.setText("pixelů");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldPadding, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel6)
                .addComponent(jTextFieldPadding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel7))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPane3.setViewportView(jPanel6);

        javax.swing.GroupLayout jPanelExportLayout = new javax.swing.GroupLayout(jPanelExport);
        jPanelExport.setLayout(jPanelExportLayout);
        jPanelExportLayout.setHorizontalGroup(
            jPanelExportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
        );
        jPanelExportLayout.setVerticalGroup(
            jPanelExportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
        );

        jTabbedPaneSettings.addTab("Export", jPanelExport);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("<html>\nProvedené nastavení je ihned automaticky aplikováno a uloženo.\n</html>");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPaneSettings, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPaneSettings, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBoxExportTransparencyActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBoxExportTransparencyActionPerformed
    {//GEN-HEADEREND:event_jCheckBoxExportTransparencyActionPerformed
        if (!initializing)
        {
            SettingsHolder.settings.setExportTransparency(jCheckBoxExportTransparency.isSelected());
        }
    }//GEN-LAST:event_jCheckBoxExportTransparencyActionPerformed

    private void jSpinnerFPSStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_jSpinnerFPSStateChanged
    {//GEN-HEADEREND:event_jSpinnerFPSStateChanged
        if (!initializing)
        {
            SettingsHolder.settings.setFps((int) jSpinnerFPS.getValue());
        }
    }//GEN-LAST:event_jSpinnerFPSStateChanged

    private void jSliderRadiusStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_jSliderRadiusStateChanged
    {//GEN-HEADEREND:event_jSliderRadiusStateChanged
        if (!initializing)
        {
            SettingsHolder.settings.setBallShineRadius(jSliderRadius.getValue());
        }
        jLabelRadius.setText("Radius záře kuličky: " + jSliderRadius.getValue() + " pixelů");
    }//GEN-LAST:event_jSliderRadiusStateChanged

    private void jCheckBoxBallShineActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBoxBallShineActionPerformed
    {//GEN-HEADEREND:event_jCheckBoxBallShineActionPerformed
        if (!initializing)
        {
            SettingsHolder.settings.setBallShine(jCheckBoxBallShine.isSelected());
        }
        jSliderRadius.setEnabled(jCheckBoxBallShine.isSelected());
    }//GEN-LAST:event_jCheckBoxBallShineActionPerformed

    private void jRadioButtonBlockScopeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButtonBlockScopeActionPerformed
    {//GEN-HEADEREND:event_jRadioButtonBlockScopeActionPerformed
        if (!initializing)
        {
            SettingsHolder.settings.setBlockScopeVariables(jRadioButtonBlockScope.isSelected());
        }
    }//GEN-LAST:event_jRadioButtonBlockScopeActionPerformed

    private void jCheckBoxAssocActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBoxAssocActionPerformed
    {//GEN-HEADEREND:event_jCheckBoxAssocActionPerformed
        if (!initializing)
        {
            boolean realState = ensureAssociationState(jCheckBoxAssoc.isSelected(), false);
            if (jCheckBoxAssoc.isSelected() != realState)
            {
                initializing = true;
                jCheckBoxAssoc.setSelected(realState);
                initializing = false;
            } else
            {
                // state successfuly changed
                SettingsHolder.settings.setAssociateExtension(jCheckBoxAssoc.isSelected());
            }
        }
    }//GEN-LAST:event_jCheckBoxAssocActionPerformed

    private void jCheckBoxLoadLastActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBoxLoadLastActionPerformed
    {//GEN-HEADEREND:event_jCheckBoxLoadLastActionPerformed
        if (!initializing)
        {
            SettingsHolder.settings.setLoadLastFlowchart(jCheckBoxLoadLast.isSelected());
        }
    }//GEN-LAST:event_jCheckBoxLoadLastActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupScopes;
    private javax.swing.JCheckBox jCheckBoxAssoc;
    private javax.swing.JCheckBox jCheckBoxBallShine;
    private javax.swing.JCheckBox jCheckBoxExportTransparency;
    private javax.swing.JCheckBox jCheckBoxLoadLast;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelRadius;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelAnimMode;
    private javax.swing.JPanel jPanelExport;
    private javax.swing.JPanel jPanelGeneral;
    private javax.swing.JRadioButton jRadioButtonBlockScope;
    private javax.swing.JRadioButton jRadioButtonGlobalScope;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSlider jSliderRadius;
    private javax.swing.JSpinner jSpinnerFPS;
    private javax.swing.JTabbedPane jTabbedPaneSettings;
    private javax.swing.JTextField jTextFieldPadding;
    // End of variables declaration//GEN-END:variables

    private final File psdiagramExeFile = new File(SettingsHolder.MY_DIR, "PS Diagram.exe");
    private final String[] appKeysToRemove = new String[]{
        "Software\\Classes\\cz.miroslavbartyzal.psdiagram\\shell\\open\\command",
        "Software\\Classes\\cz.miroslavbartyzal.psdiagram\\shell\\open",
        "Software\\Classes\\cz.miroslavbartyzal.psdiagram\\shell",
        "Software\\Classes\\cz.miroslavbartyzal.psdiagram"};
    private final String appCallKey = "Software\\Classes\\cz.miroslavbartyzal.psdiagram\\shell\\open\\command";
    private final String appCallValue = "\"" + psdiagramExeFile.getAbsolutePath() + "\" \"%1\"";
    private final String extensionKey = "Software\\Classes\\.psdiagram";
    private final String extensionValue = "cz.miroslavbartyzal.psdiagram";

    /**
     * Assigns *.psdiagram file extension association through Windows registry with this
     * version and location of PS Diagram.
     * <p>
     * @param requestAssoc true if association should be enabled
     * @param preserveOtherValuesIfPresent if true and there is different application assigned with
     * .*psdiagram file extension, enabling / disabling operation will be canceled. Note that
     * different application does not mean different path to executable but shortcut key name in the
     * registry (PS Diagram has "cz.miroslavbartyzal.psdiagram").
     * @return true if extension is assigned
     */
    private boolean ensureAssociationState(boolean requestAssoc,
            boolean preserveOtherValuesIfPresent)
    {
        if (!GlobalFunctions.isWindows() || !psdiagramExeFile.exists()) {
            return false;
        }

        if (requestAssoc) {
            // ensure there is a record in registry so the extension is handled correctly
            if (preserveOtherValuesIfPresent && !isAirClean()) {
                return false;
            }

            Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, appCallKey);
            Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, appCallKey, "",
                    appCallValue);
            Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, extensionKey);
            Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, extensionKey, "",
                    extensionValue);
        } else {
            // ensure there is no record in registry
            if (preserveOtherValuesIfPresent && !isAirClean()) {
                return false;
            }

            if (Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, extensionKey)) {
                try {
                    Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, extensionKey);
                } catch (Win32Exception ex) {
                    MyExceptionHandler.handle(ex);
                }
            }
            // apparently keys with key inside can't be removed directly
            for (String appKeyToRemove : appKeysToRemove) {
                if (Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, appKeyToRemove)) {
                    try {
                        Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, appKeyToRemove);
                    } catch (Win32Exception ex) {
                        MyExceptionHandler.handle(ex);
                    }
                }
            }
        }

        return Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, extensionKey)
                && Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, extensionKey, "")
                && Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, extensionKey, "").equals(
                        extensionValue)
                && Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, appCallKey)
                && Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, appCallKey, "")
                && Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, appCallKey, "").equals(
                        appCallValue);
    }

    private boolean isAirClean()
    {
        if (Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, extensionKey) // if there is the extensionKey
                && Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, extensionKey,
                        "") // if there is a value in the extensionKey
                && !Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER,
                        extensionKey, "").equals(extensionValue)) { // if the value is not what is should be, that's it - extension is assign for something else
            return false;
        } else if (Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, appCallKey) // if there is the appCallKey
                && Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, appCallKey, "") // if there is a value in the appCallKey
                && !Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, appCallKey,
                        "").equals(appCallValue)) { // if the value is not matching with the supposed value
            String theValue = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER,
                    appCallKey, "");
            if (theValue.endsWith(psdiagramExeFile.getName() + "\" \"%1\"") // and the value is a path to another PS Diagram.exe
                    && new File(theValue.substring(1,
                                    theValue.length() - "\" \"%1\"".length())).exists()) { // and the path to another PS Diagram.exe leads to an existing file, that's it - we don't want to change other PSD's associations if preserveOtherValuesIfPresent is set
                return false;
            }
        }
        return true;
    }

    private void setPreferedSizes()
    {
        jPanel3.setPreferredSize(jPanel3.getLayout().minimumLayoutSize(jPanel3));
        jPanel6.setPreferredSize(jPanel6.getLayout().minimumLayoutSize(jPanel6));
        jPanel9.setPreferredSize(jPanel9.getLayout().minimumLayoutSize(jPanel9));
    }

}
