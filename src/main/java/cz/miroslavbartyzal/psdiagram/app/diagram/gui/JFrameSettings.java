/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.diagram.gui;

import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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

    /**
     * Vytvoří nový formulář JFrameSettings.
     */
    public JFrameSettings()
    {
        initComponents();
        jCheckBoxLoadLast.setSelected(SettingsHolder.settings.isLoadLastFlowchart());
        jCheckBoxFunctionFilters.setSelected(SettingsHolder.settings.isFunctionFilters());
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

        jScrollPane1.getVerticalScrollBar().setUnitIncrement(10);
        jScrollPane1.getHorizontalScrollBar().setUnitIncrement(10);
        jScrollPane2.getVerticalScrollBar().setUnitIncrement(10);
        jScrollPane2.getHorizontalScrollBar().setUnitIncrement(10);
        setPreferedSizes();
        jScrollPane1.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE); // prevents glitches (http://andrewtill.blogspot.cz/2012/06/jscrollpane-repainting-problems.html)
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
        jPanelEditMode = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jCheckBoxFunctionFilters = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
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

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Při spuštění"));

        jCheckBoxLoadLast.setText("Načítat předchozí uložený diagram");
        jCheckBoxLoadLast.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jCheckBoxLoadLastActionPerformed(evt);
            }
        });

        jLabel8.setText("<html>\nJe-li volba povolena a pokud při minulém zavření aplikace byl otevřen uložený diagram, při opětovném spuštění aplikace se tento diagram otevře spolu s aplikací.\n</html>");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jCheckBoxLoadLast)
                .addGap(0, 204, Short.MAX_VALUE))
            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jCheckBoxLoadLast)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 190, Short.MAX_VALUE))
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

        jPanelEditMode.setPreferredSize(new java.awt.Dimension(0, 0));

        jPanel1.setPreferredSize(new java.awt.Dimension(0, 0));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Funkce symbolu"));

        jCheckBoxFunctionFilters.setText("Používat syntaktické filtry");
        jCheckBoxFunctionFilters.setToolTipText("<html>\nSyntaktické filtry jsou aplikovány v textových polích pro vkládání funkcí symbolu.<br />\nZabraňují nechtěnným syntaktickým chybám, jako například čárka místo desetinné tečky,<br />\nnevalidním názvům proměnných apod.\n</html>");
        jCheckBoxFunctionFilters.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jCheckBoxFunctionFiltersActionPerformed(evt);
            }
        });

        jLabel1.setText("<html>\n<p style=\"font-weight:bold;\">\nPozor! Tato volba je jen pro pokročilé uživatele, měla by být vždy povolena.<br />\nJste-li nuceni tuto funkci vypnout z důvodu chyby některého z filtrů, obraťte se prosím s popisem chyby na autora aplikace.\n</p>\n</html>");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jCheckBoxFunctionFilters)
                .addGap(0, 259, Short.MAX_VALUE))
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jCheckBoxFunctionFilters)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(174, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPane1.setViewportView(jPanel1);

        javax.swing.GroupLayout jPanelEditModeLayout = new javax.swing.GroupLayout(jPanelEditMode);
        jPanelEditMode.setLayout(jPanelEditModeLayout);
        jPanelEditModeLayout.setHorizontalGroup(
            jPanelEditModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
        );
        jPanelEditModeLayout.setVerticalGroup(
            jPanelEditModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
        );

        jTabbedPaneSettings.addTab("Editační režim", jPanelEditMode);

        jPanelAnimMode.setPreferredSize(new java.awt.Dimension(0, 0));

        jPanel3.setPreferredSize(new java.awt.Dimension(0, 0));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Nakládání s proměnnými"));

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
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, Short.MAX_VALUE)))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Animace"));

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
                .addContainerGap(92, Short.MAX_VALUE))
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
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Export do obrázku"));

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

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Export obecné"));

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

    private void jCheckBoxFunctionFiltersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxFunctionFiltersActionPerformed
        if (!initializing) {
            SettingsHolder.settings.setFunctionFilters(jCheckBoxFunctionFilters.isSelected());
        }
    }//GEN-LAST:event_jCheckBoxFunctionFiltersActionPerformed

    private void jCheckBoxBallShineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxBallShineActionPerformed
        if (!initializing) {
            SettingsHolder.settings.setBallShine(jCheckBoxBallShine.isSelected());
        }
        jSliderRadius.setEnabled(jCheckBoxBallShine.isSelected());
    }//GEN-LAST:event_jCheckBoxBallShineActionPerformed

    private void jRadioButtonBlockScopeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonBlockScopeActionPerformed
        if (!initializing) {
            SettingsHolder.settings.setBlockScopeVariables(jRadioButtonBlockScope.isSelected());
        }
    }//GEN-LAST:event_jRadioButtonBlockScopeActionPerformed

    private void jCheckBoxExportTransparencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxExportTransparencyActionPerformed
        if (!initializing) {
            SettingsHolder.settings.setExportTransparency(jCheckBoxExportTransparency.isSelected());
        }
    }//GEN-LAST:event_jCheckBoxExportTransparencyActionPerformed

    private void jCheckBoxLoadLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxLoadLastActionPerformed
        if (!initializing) {
            SettingsHolder.settings.setLoadLastFlowchart(jCheckBoxLoadLast.isSelected());
        }
    }//GEN-LAST:event_jCheckBoxLoadLastActionPerformed

    private void jSliderRadiusStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderRadiusStateChanged
        if (!initializing) {
            SettingsHolder.settings.setBallShineRadius(jSliderRadius.getValue());
        }
        jLabelRadius.setText("Radius záře kuličky: " + jSliderRadius.getValue() + " pixelů");
    }//GEN-LAST:event_jSliderRadiusStateChanged

    private void jSpinnerFPSStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerFPSStateChanged
        if (!initializing) {
            SettingsHolder.settings.setFps((int) jSpinnerFPS.getValue());
        }
    }//GEN-LAST:event_jSpinnerFPSStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupScopes;
    private javax.swing.JCheckBox jCheckBoxBallShine;
    private javax.swing.JCheckBox jCheckBoxExportTransparency;
    private javax.swing.JCheckBox jCheckBoxFunctionFilters;
    private javax.swing.JCheckBox jCheckBoxLoadLast;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelRadius;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelAnimMode;
    private javax.swing.JPanel jPanelEditMode;
    private javax.swing.JPanel jPanelExport;
    private javax.swing.JPanel jPanelGeneral;
    private javax.swing.JRadioButton jRadioButtonBlockScope;
    private javax.swing.JRadioButton jRadioButtonGlobalScope;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSlider jSliderRadius;
    private javax.swing.JSpinner jSpinnerFPS;
    private javax.swing.JTabbedPane jTabbedPaneSettings;
    private javax.swing.JTextField jTextFieldPadding;
    // End of variables declaration//GEN-END:variables

    private void setPreferedSizes()
    {
        jPanel1.setPreferredSize(jPanel1.getLayout().minimumLayoutSize(jPanel1));
        jPanel3.setPreferredSize(jPanel3.getLayout().minimumLayoutSize(jPanel3));
        jPanel6.setPreferredSize(jPanel6.getLayout().minimumLayoutSize(jPanel6));
        jPanel9.setPreferredSize(jPanel9.getLayout().minimumLayoutSize(jPanel9));
    }

}
