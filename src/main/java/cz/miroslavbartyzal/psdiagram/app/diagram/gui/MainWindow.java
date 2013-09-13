/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainWindow.java
 *
 * Created on 17.9.2011, 16:05:46
 */
package cz.miroslavbartyzal.psdiagram.app.diagram.gui;

import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.layouts.EnumLayout;
import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.layouts.Layout;
import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.layouts.LayoutSegment;
import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.symbols.*;
import cz.miroslavbartyzal.psdiagram.app.diagram.gui.managers.FlowchartAnimationManager;
import cz.miroslavbartyzal.psdiagram.app.diagram.gui.managers.FlowchartEditManager;
import cz.miroslavbartyzal.psdiagram.app.diagram.gui.managers.FlowchartEditUndoManager;
import cz.miroslavbartyzal.psdiagram.app.diagram.gui.managers.FlowchartOverlookManager;
import cz.miroslavbartyzal.psdiagram.app.global.GlobalFunctions;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import cz.miroslavbartyzal.psdiagram.app.update.Updater;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.pdf.PDFGraphics2D;

/**
 * Tato třída představuje hlavní okno aplikace.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class MainWindow extends javax.swing.JFrame
{
    // TODO kdyz se pomalinku posouva diagram doleva nahoru, posouvani se v miste objeveni scrollbaru seka (uprava ulozene pozice kurzoru v zavislosti na zmene velikosti platna pri zobrazenych scrollbrech? nebo podminka s elseif misto dvou?)
    // TODO az budu dynamicky menit layout, budu muset prepsat odkazy na layout ve vsech tridach jako by reference: AtomicReference<Object> ref = new AtomicReference<Object>("Hello");
    // TODO je-li v else vetvi jen podminka, je to pri exportu elseif!
    // TODO mozna pridat komentar do XML a exportu PDF, kodu.. o PS Diagramu
    // TODO do budoucna podprogram moznst expandovat/collapsovat :)

    private Layout layout;
    private boolean editMode = true;
    private boolean animationMode = false;
    private FlowchartEditManager flowchartEditManager;
    private FlowchartEditUndoManager flowchartEditUndoManager;
    private FlowchartOverlookManager flowchartOverlookManager;
    private FlowchartAnimationManager flowchartAnimationManager;
    private JPanelDiagram jPnlDiagram;
    private AffineTransform affineTransform;
    private boolean graphicsXTransformedByScrollbar = false;
    private boolean graphicsYTransformedByScrollbar = false;
    private JPanelVariables jPanelVariables = new JPanelVariables();
    private JFrameSettings jFrameSettings;
    private JFrameCodeImport jFrameCodeImport;
    private JFrameCodeExport jFrameCodeExport;
    private JFrameAbout jFrameAbout;
    private JFrameUpdate jFrameUpdate;
    private static JAXBContext jAXBcontext;
    private String windowTitle = "PS Diagram";
    private static Timer statusTimer = new Timer(0, new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            jLabelStatus.setText("");
            statusTimer.stop();
        }
    });

    ;

    /** Creates new form MainWindow */
    private MainWindow()
    {
        String buildProfile = ResourceBundle.getBundle("appliaction").getString("buildProfile");
        if (!buildProfile.equals("deployment") && !buildProfile.equals("development-run")) {
            if (System.getenv("COMPUTERNAME").equals("POLOSHOCK-NB")) {
                JOptionPane.showMessageDialog(null, buildProfile, "", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "<html>Tato verze PS Diagramu je určena pouze pro vývoj.<br />"
                        + "Pro obdržení správné verze navštivte www.psdiagram.cz nebo mne kontaktujte<br />"
                        + "na emailu miroslavbartyzal@gmail.com.</html>", "Chyba verze",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }

        System.setProperty("java.net.useSystemProxies", "true");
        initComponents();
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent we)
            {
                exit();
            }
        });

        //pridani tlacitek layoutu
        ButtonGroup layoutGroup = new ButtonGroup();
        for (EnumLayout enumLayout : EnumLayout.values()) {
            JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem(enumLayout.getName());
            if (enumLayout.equals(EnumLayout.valueOf(SettingsHolder.settings.getSelectedLayout()))) { //defaultni layout
                rbMenuItem.setSelected(true);
                layout = enumLayout.getInstance(jPanelDiagram, null);
                loadLayoutSettings();
            }
            rbMenuItem.setActionCommand("layout/" + enumLayout.name());
//            rbMenuItem.addActionListener(
//                    new ActionListener()
//                    {
//
//                        @Override
//                        public void actionPerformed(ActionEvent ae) {
//                            JRadioButtonMenuItem rbMenuItem = (JRadioButtonMenuItem) ae.getSource();
//                            String enumLayoutname = rbMenuItem.getActionCommand().split("/")[1];
//                            if (!enumLayoutname.equals(SettingsHolder.settings.getSelectedLayout())) {
//                                rbMenuItem.setSelected(true);
//                                layout = EnumLayout.valueOf(enumLayoutname).getInstance(jPanelDiagram, null);
//                                loadLayoutSettings();
//                                SettingsHolder.settings.setSelectedLayout(enumLayoutname);
//                                flowchartEditManager.refreshComments(); // TODO is this necessary?
//                                jPanelDiagram.repaint();
//                            }
//                        }
//
//                    });
            layoutGroup.add(rbMenuItem);
            jMenuLayouts.add(rbMenuItem, 0);
        }

        jFrameSettings = new JFrameSettings();
        jFrameCodeImport = new JFrameCodeImport(this);
        jFrameCodeExport = new JFrameCodeExport(this);
        jFrameAbout = new JFrameAbout();
        Updater updater = new Updater();
        jFrameUpdate = new JFrameUpdate(updater, new Updater.BeforeExitListener()
        {
            @Override
            public void onBeforeExit()
            {
                if (!checkIfSaved(false)) {
                    try {
                        Marshaller jAXBmarshaller = getJAXBcontext().createMarshaller();
                        jAXBmarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                        jAXBmarshaller.marshal(layout.getFlowchart(), new File(
                                SettingsHolder.WORKING_DIR, "tmp.xml"));
                    } catch (JAXBException ex) {
                        ex.printStackTrace(System.err);
                    }
                }
            }
        });

        jPanelVariables.setVisible(false);

        // nastaveni velikosti okna - mj. aby nebylo vetsi nez obrazovka
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (super.getWidth() + 20 > screenSize.width) {
            super.setSize(screenSize.width - 20, super.getHeight());
        }
        if (super.getHeight() + 20 > screenSize.height) {
            super.setSize(super.getWidth(), screenSize.height - 20);
        }

//        super.setLocationRelativeTo(null); // same as below
//        super.setLocation(screenSize.width / 2 - super.getWidth() / 2,
//                screenSize.height / 2 - super.getHeight() / 2);
//        jFrameSettings.setLocation(screenSize.width / 2 - jFrameSettings.getWidth() / 2,
//                screenSize.height / 2 - jFrameSettings.getHeight() / 2);
//        jFrameCodeImport.setLocation(screenSize.width / 2 - jFrameCodeImport.getWidth() / 2,
//                screenSize.height / 2 - jFrameCodeImport.getHeight() / 2);
//        jFrameCodeExport.setLocation(screenSize.width / 2 - jFrameCodeExport.getWidth() / 2,
//                screenSize.height / 2 - jFrameCodeExport.getHeight() / 2);
//        jFrameAbout.setLocation(screenSize.width / 2 - jFrameAbout.getWidth() / 2,
//                screenSize.height / 2 - jFrameAbout.getHeight() / 2);
//        jFrameUpdate.setLocation(screenSize.width / 2 - jFrameUpdate.getWidth() / 2,
//                screenSize.height / 2 - jFrameUpdate.getHeight() / 2);
        // (I'm using platform default location after all)

        jPnlDiagram = (JPanelDiagram) jPanelDiagram;

        /*
         * affineTransform = new AffineTransform();
         * affineTransform.setToTranslation(getTranslateX(), getTranslateY());
         * affineTransform.scale(getScale(), getScale());
         */

        jTextAreaTextSymbol.setFont(SettingsHolder.CODEFONT);
        jTextFieldTextSegment.setFont(SettingsHolder.SMALL_CODEFONT.deriveFont(13f));
        jPanelDiagram.setFocusable(true);
        jPanelDiagram.requestFocusInWindow();
        jPanelDiagram.setTransferHandler(createTransferHandler());
        jScrollPaneDiagram.getVerticalScrollBar().setUnitIncrement(10);
        jScrollPaneDiagram.getHorizontalScrollBar().setUnitIncrement(10);
        jScrollPaneFunction.getVerticalScrollBar().setUnitIncrement(10);
        jScrollPaneFunction.getHorizontalScrollBar().setUnitIncrement(10);
        jScrollPaneText.getVerticalScrollBar().setUnitIncrement(10);
        jScrollPaneText.getHorizontalScrollBar().setUnitIncrement(10);
        jScrollPane1.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE); // prevents glitches (http://andrewtill.blogspot.cz/2012/06/jscrollpane-repainting-problems.html)
        jScrollPaneFunction.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE); // prevents glitches (http://andrewtill.blogspot.cz/2012/06/jscrollpane-repainting-problems.html)
        jScrollPaneText.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE); // prevents glitches (http://andrewtill.blogspot.cz/2012/06/jscrollpane-repainting-problems.html)

        flowchartEditUndoManager = new FlowchartEditUndoManager(jMenuItemUndo, jMenuItemRedo,
                jButtonToolUndo, jButtonToolRedo);
        flowchartEditManager = new FlowchartEditManager(layout, this, flowchartEditUndoManager,
                jCheckBoxDefaultText, jComboBoxSegment, jTextFieldTextSegment, jTextAreaTextSymbol);
        flowchartOverlookManager = new FlowchartOverlookManager(this,
                jScrollPaneDiagram.getHorizontalScrollBar(),
                jScrollPaneDiagram.getVerticalScrollBar(), jSliderZoom);
        flowchartAnimationManager = new FlowchartAnimationManager(this,
                jPanelVariables.getVariableModel(), jPanelDiagram, jSliderSpeed,
                jButtonToolPlayPause, jButtonToolPrevious, jButtonToolNext, jButtonToolStop,
                jButtonLaunch);

        // vytvoreni Symbol popup menu.
        JPopupMenu symbolPopup = new JPopupMenu();
        for (Object menuObject : jMenuEdit.getMenuComponents()) {
            if (menuObject instanceof JMenuItem) {
                JMenuItem forItem = (JMenuItem) menuObject;
                JMenuItem jMenuItem = new JMenuItem(forItem.getText(), forItem.getIcon());
                jMenuItem.setMnemonic(forItem.getMnemonic());
                jMenuItem.setAccelerator(forItem.getAccelerator());
                jMenuItem.setActionCommand(forItem.getActionCommand());
                jMenuItem.addActionListener(flowchartEditManager);
                forItem.addActionListener(flowchartEditManager); // rovnou ted pridam i posluchac
                symbolPopup.add(jMenuItem);
            } else if (menuObject instanceof JSeparator) {
                symbolPopup.addSeparator();
            }
        }
        flowchartEditManager.setSymbolPopup(symbolPopup);

        //pridani tlacitek pro pridani symbolu
        for (EnumSymbol enumSymbol : EnumSymbol.values()) {
            try {
                JButton button = new JButton(new javax.swing.ImageIcon(getClass().getResource(
                        "/img/symbols/24-" + enumSymbol.name() + ".png")));
                button.setFocusable(false);
                button.setActionCommand("addSymbol/" + enumSymbol.name());
                button.setToolTipText(enumSymbol.getToolTipText());
                button.addActionListener(flowchartEditManager);
                jToolBarSymbols.add(button);
            } catch (NullPointerException e) {
                throw new Error("Error while loading symbol images!");
            }
        }

        // pridani posluchacu
        /*
         * jMenuItemDelete.addActionListener(flowchartEditManager);
         * jMenuItemCut.addActionListener(flowchartEditManager);
         * jMenuItemPaste.addActionListener(flowchartEditManager);
         */
        jButtonToolEdit.addActionListener(flowchartEditManager);
        jButtonToolAnimation.addActionListener(flowchartAnimationManager);
        jButtonToolZoomIn.addActionListener(flowchartOverlookManager);
        jButtonToolZoomOut.addActionListener(flowchartOverlookManager);
        jPanelDiagram.addMouseListener(flowchartOverlookManager);
        jPanelDiagram.addMouseMotionListener(flowchartOverlookManager);
        jPanelDiagram.addMouseWheelListener(flowchartOverlookManager);
        jPanelDiagram.addKeyListener(flowchartOverlookManager);
        jSliderZoom.addChangeListener(flowchartOverlookManager);
        jButtonToolPlayPause.addActionListener(flowchartAnimationManager);
        jButtonToolNext.addActionListener(flowchartAnimationManager);
        jButtonToolPrevious.addActionListener(flowchartAnimationManager);
        jButtonToolStop.addActionListener(flowchartAnimationManager);
        jButtonLaunch.addActionListener(flowchartAnimationManager);

        editMode = !editMode;
        setEditMode(!editMode);

        if (SettingsHolder.settings.getActualFlowchartFile() != null) {
            openDiagram(SettingsHolder.settings.getActualFlowchartFile());
        } else {
            File tmp = new File(SettingsHolder.WORKING_DIR, "tmp.xml");
            if (tmp.exists()) {
                openDiagram(tmp);
                SettingsHolder.settings.setActualFlowchartFile(null);
                tmp.delete();
            }
        }

        updater.loadInfo(null, new Updater.InfoLoadListener()
        {
            @Override
            public void onInfoLoaded(boolean newVersionAvailable)
            {
                if (newVersionAvailable) {
                    jMenuItemUpdateActionPerformed(null);
                }
            }
        });

        getJAXBcontext(); // TODO: did in hurry - I am not for lazy loading in the end
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

        jToolBarMenu = new javax.swing.JToolBar();
        jButtonToolNew = new javax.swing.JButton();
        jButtonToolOpen = new javax.swing.JButton();
        jButtonToolSave = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButtonToolUndo = new javax.swing.JButton();
        jButtonToolRedo = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        jButtonToolZoomIn = new javax.swing.JButton();
        jButtonToolZoomOut = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jButtonToolEdit = new javax.swing.JToggleButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jButtonToolAnimation = new javax.swing.JToggleButton();
        jButtonToolPlayPause = new javax.swing.JButton();
        jButtonToolPrevious = new javax.swing.JButton();
        jButtonToolNext = new javax.swing.JButton();
        jButtonToolStop = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        jButtonLaunch = new javax.swing.JButton();
        jSeparator13 = new javax.swing.JToolBar.Separator();
        jSliderSpeed = new javax.swing.JSlider();
        jPanelStatus = new javax.swing.JPanel();
        jSliderZoom = new javax.swing.JSlider();
        jLabelZoom = new javax.swing.JLabel();
        jLabelStatus = new javax.swing.JLabel();
        jSplitPane = new javax.swing.JSplitPane();
        jPanelLeftSplit = new javax.swing.JPanel();
        jPanelEdit = new javax.swing.JPanel();
        jPanelDetails = new javax.swing.JPanel();
        jTabbedPaneEdit = new javax.swing.JTabbedPane();
        jPanelOuterFunction = new javax.swing.JPanel();
        jScrollPaneFunction = new javax.swing.JScrollPane();
        jPanelInnerFunction = new javax.swing.JPanel();
        jLabelFunction = new javax.swing.JLabel();
        jPanelOuterText = new javax.swing.JPanel();
        jScrollPaneText = new javax.swing.JScrollPane();
        jPanelInnerText = new javax.swing.JPanel();
        jCheckBoxDefaultText = new javax.swing.JCheckBox();
        jPanelTextSymbol = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaTextSymbol = new javax.swing.JTextArea();
        jPanelTextSegment = new javax.swing.JPanel();
        jTextFieldTextSegment = new javax.swing.JTextField();
        jComboBoxSegment = new javax.swing.JComboBox<>();
        jToolBarSymbols = new javax.swing.JToolBar();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jScrollPaneDiagram = new javax.swing.JScrollPane();
        jPanelDiagram = new JPanelDiagram();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemNew = new javax.swing.JMenuItem();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenuItemSaveAs = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        jMenuItemExportImage = new javax.swing.JMenuItem();
        jMenuItemExportPDF = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        jMenuItemCodeImport = new javax.swing.JMenuItem();
        jMenuItemCodeExport = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JPopupMenu.Separator();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMenuItemUndo = new javax.swing.JMenuItem();
        jMenuItemRedo = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        jMenuItemCut = new javax.swing.JMenuItem();
        jMenuItemCopy = new javax.swing.JMenuItem();
        jMenuItemPaste = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenuItemDelete = new javax.swing.JMenuItem();
        jMenuLayouts = new javax.swing.JMenu();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuLayoutSetting = new javax.swing.JMenu();
        jMenuConfiguration = new javax.swing.JMenu();
        jMenuItemSettings = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuItemUpdate = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JPopupMenu.Separator();
        jMenuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(windowTitle);
        setIconImage(java.awt.Toolkit.getDefaultToolkit().createImage(getClass().getResource("/img/icon.png")));
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(712, 550));

        jToolBarMenu.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(147, 152, 161)));
        jToolBarMenu.setFloatable(false);
        jToolBarMenu.setRollover(true);
        jToolBarMenu.setName(""); // NOI18N

        jButtonToolNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolBar/24-New.png"))); // NOI18N
        jButtonToolNew.setToolTipText("<html>\nNový diagram<br />\n(Ctrl + N)\n</html>");
        jButtonToolNew.setFocusable(false);
        jButtonToolNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonToolNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonToolNew.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemNewActionPerformed(evt);
            }
        });
        jToolBarMenu.add(jButtonToolNew);

        jButtonToolOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolBar/24-Open.png"))); // NOI18N
        jButtonToolOpen.setToolTipText("<html>\nOtevřít diagram<br />\n(Ctrl + O)\n</html>");
        jButtonToolOpen.setFocusable(false);
        jButtonToolOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonToolOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonToolOpen.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemOpenActionPerformed(evt);
            }
        });
        jToolBarMenu.add(jButtonToolOpen);

        jButtonToolSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolBar/24-Save.png"))); // NOI18N
        jButtonToolSave.setToolTipText("<html>\nUložit diagram<br />\n(Ctrl + S)\n</html>");
        jButtonToolSave.setFocusable(false);
        jButtonToolSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonToolSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonToolSave.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jToolBarMenu.add(jButtonToolSave);
        jToolBarMenu.add(jSeparator1);

        jButtonToolUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolBar/24-Undo.png"))); // NOI18N
        jButtonToolUndo.setToolTipText("<html>Zpět<br />(Ctrl + Z)</html>");
        jButtonToolUndo.setEnabled(false);
        jButtonToolUndo.setFocusable(false);
        jButtonToolUndo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonToolUndo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonToolUndo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemUndoActionPerformed(evt);
            }
        });
        jToolBarMenu.add(jButtonToolUndo);

        jButtonToolRedo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolBar/24-Redo.png"))); // NOI18N
        jButtonToolRedo.setToolTipText("<html>Znovu<br />(Ctrl + Y)</html>");
        jButtonToolRedo.setEnabled(false);
        jButtonToolRedo.setFocusable(false);
        jButtonToolRedo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonToolRedo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonToolRedo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemRedoActionPerformed(evt);
            }
        });
        jToolBarMenu.add(jButtonToolRedo);

        jSeparator2.setEnabled(false);
        jSeparator2.setSeparatorSize(new java.awt.Dimension(14, 10));
        jToolBarMenu.add(jSeparator2);
        jToolBarMenu.add(jSeparator9);

        jButtonToolZoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolBar/24-ZoomIn.png"))); // NOI18N
        jButtonToolZoomIn.setToolTipText("<html>\nPřiblížit<br />\n(Ctrl + kolečko myši)\n</html>");
        jButtonToolZoomIn.setActionCommand("overlook/zoomIn");
        jButtonToolZoomIn.setFocusable(false);
        jButtonToolZoomIn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonToolZoomIn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarMenu.add(jButtonToolZoomIn);

        jButtonToolZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolBar/24-ZoomOut.png"))); // NOI18N
        jButtonToolZoomOut.setToolTipText("<html>\nOddálit<br />\n(Ctrl + kolečko myši)\n</html>");
        jButtonToolZoomOut.setActionCommand("overlook/zoomOut");
        jButtonToolZoomOut.setFocusable(false);
        jButtonToolZoomOut.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonToolZoomOut.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarMenu.add(jButtonToolZoomOut);
        jToolBarMenu.add(jSeparator4);

        jButtonToolEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolBar/24-Unlock.png"))); // NOI18N
        jButtonToolEdit.setSelected(true);
        jButtonToolEdit.setToolTipText("Editační režim");
        jButtonToolEdit.setActionCommand("mode/editMode");
        jButtonToolEdit.setFocusable(false);
        jButtonToolEdit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonToolEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarMenu.add(jButtonToolEdit);
        jToolBarMenu.add(jSeparator3);

        jButtonToolAnimation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolBar/24-MagicWand.png"))); // NOI18N
        jButtonToolAnimation.setToolTipText("Animační režim");
        jButtonToolAnimation.setActionCommand("mode/animationMode");
        jButtonToolAnimation.setFocusable(false);
        jButtonToolAnimation.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonToolAnimation.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarMenu.add(jButtonToolAnimation);

        jButtonToolPlayPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolBar/24-Play.png"))); // NOI18N
        jButtonToolPlayPause.setToolTipText("<html>\nSpustit animaci<br />\n(Mezerník)\n</html>");
        jButtonToolPlayPause.setActionCommand("animation/play");
        jButtonToolPlayPause.setEnabled(false);
        jButtonToolPlayPause.setFocusable(false);
        jButtonToolPlayPause.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonToolPlayPause.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarMenu.add(jButtonToolPlayPause);

        jButtonToolPrevious.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolBar/24-Previous.png"))); // NOI18N
        jButtonToolPrevious.setToolTipText("<html>\nKrok zpět<br />\n(Ctrl+šipka doleva, nebo nahoru)\n</html>");
        jButtonToolPrevious.setActionCommand("animation/previous");
        jButtonToolPrevious.setEnabled(false);
        jButtonToolPrevious.setFocusable(false);
        jButtonToolPrevious.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonToolPrevious.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarMenu.add(jButtonToolPrevious);

        jButtonToolNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolBar/24-Next.png"))); // NOI18N
        jButtonToolNext.setToolTipText("<html>\nKrok vpřed<br />\n(Ctrl+šipka doprava, nebo dolu)\n</html>");
        jButtonToolNext.setActionCommand("animation/next");
        jButtonToolNext.setEnabled(false);
        jButtonToolNext.setFocusable(false);
        jButtonToolNext.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonToolNext.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarMenu.add(jButtonToolNext);

        jButtonToolStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolBar/24-Stop.png"))); // NOI18N
        jButtonToolStop.setToolTipText("Reset");
        jButtonToolStop.setActionCommand("animation/stop");
        jButtonToolStop.setEnabled(false);
        jButtonToolStop.setFocusable(false);
        jButtonToolStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonToolStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarMenu.add(jButtonToolStop);
        jToolBarMenu.add(jSeparator8);

        jButtonLaunch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolBar/24-Launch.png"))); // NOI18N
        jButtonLaunch.setToolTipText("<html>\nSpustit rychle<br />\n(zastaví na umístěném breakpointu)\n</html>");
        jButtonLaunch.setActionCommand("animation/launch");
        jButtonLaunch.setEnabled(false);
        jButtonLaunch.setFocusable(false);
        jButtonLaunch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonLaunch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarMenu.add(jButtonLaunch);

        jSeparator13.setEnabled(false);
        jToolBarMenu.add(jSeparator13);

        jSliderSpeed.setMaximum(20);
        jSliderSpeed.setToolTipText("Rychlost animace");
        jSliderSpeed.setValue(10);
        jSliderSpeed.setEnabled(false);
        jSliderSpeed.setInverted(true);
        jSliderSpeed.setPreferredSize(new java.awt.Dimension(150, 21));
        jToolBarMenu.add(jSliderSpeed);

        jPanelStatus.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(147, 152, 161)));
        jPanelStatus.setPreferredSize(new java.awt.Dimension(200, 25));

        jSliderZoom.setMaximum(50);
        jSliderZoom.setMinimum(1);
        jSliderZoom.setToolTipText("Přiblížení");
        jSliderZoom.setValue(10);
        jSliderZoom.setPreferredSize(new java.awt.Dimension(150, 21));

        jLabelZoom.setText("100%");
        jLabelZoom.setPreferredSize(new java.awt.Dimension(32, 21));

        javax.swing.GroupLayout jPanelStatusLayout = new javax.swing.GroupLayout(jPanelStatus);
        jPanelStatus.setLayout(jPanelStatusLayout);
        jPanelStatusLayout.setHorizontalGroup(
            jPanelStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelStatusLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelZoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSliderZoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelStatusLayout.setVerticalGroup(
            jPanelStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelStatusLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanelStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanelStatusLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jSliderZoom, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelZoom, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13))
        );

        jSplitPane.setDividerLocation(306);

        jPanelLeftSplit.setMinimumSize(new java.awt.Dimension(231, 0));

        jPanelDetails.setBorder(null);
        jPanelDetails.setPreferredSize(new java.awt.Dimension(188, 517));

        jPanelInnerFunction.setPreferredSize(new java.awt.Dimension(0, 0));

        jLabelFunction.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelFunction.setText("<html>\n<p style=\"text-align: center;\">\nTento symbol nemá možnost nastavení funkce.\n</p>\n</html>");

        javax.swing.GroupLayout jPanelInnerFunctionLayout = new javax.swing.GroupLayout(jPanelInnerFunction);
        jPanelInnerFunction.setLayout(jPanelInnerFunctionLayout);
        jPanelInnerFunctionLayout.setHorizontalGroup(
            jPanelInnerFunctionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInnerFunctionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelFunction, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelInnerFunctionLayout.setVerticalGroup(
            jPanelInnerFunctionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInnerFunctionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelFunction, javax.swing.GroupLayout.DEFAULT_SIZE, 567, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPaneFunction.setViewportView(jPanelInnerFunction);

        javax.swing.GroupLayout jPanelOuterFunctionLayout = new javax.swing.GroupLayout(jPanelOuterFunction);
        jPanelOuterFunction.setLayout(jPanelOuterFunctionLayout);
        jPanelOuterFunctionLayout.setHorizontalGroup(
            jPanelOuterFunctionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneFunction, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
        );
        jPanelOuterFunctionLayout.setVerticalGroup(
            jPanelOuterFunctionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneFunction, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
        );

        jTabbedPaneEdit.addTab("Funkce", jPanelOuterFunction);

        jPanelInnerText.setPreferredSize(new java.awt.Dimension(0, 0));

        jCheckBoxDefaultText.setText("Výchozí hodnoty");
        jCheckBoxDefaultText.setToolTipText("Nastaví text symbolu, resp. jeho větví, na základě vyplněné funkce");
        jCheckBoxDefaultText.setActionCommand("edit/defaultText");

        jPanelTextSymbol.setBorder(javax.swing.BorderFactory.createTitledBorder("Text symbolu"));

        jScrollPane1.setViewportView(jTextAreaTextSymbol);

        javax.swing.GroupLayout jPanelTextSymbolLayout = new javax.swing.GroupLayout(jPanelTextSymbol);
        jPanelTextSymbol.setLayout(jPanelTextSymbolLayout);
        jPanelTextSymbolLayout.setHorizontalGroup(
            jPanelTextSymbolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanelTextSymbolLayout.setVerticalGroup(
            jPanelTextSymbolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTextSymbolLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelTextSegment.setBorder(javax.swing.BorderFactory.createTitledBorder("Text větví symbolu"));

        jComboBoxSegment.setActionCommand("edit/segmentText");

        javax.swing.GroupLayout jPanelTextSegmentLayout = new javax.swing.GroupLayout(jPanelTextSegment);
        jPanelTextSegment.setLayout(jPanelTextSegmentLayout);
        jPanelTextSegmentLayout.setHorizontalGroup(
            jPanelTextSegmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextFieldTextSegment)
            .addComponent(jComboBoxSegment, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelTextSegmentLayout.setVerticalGroup(
            jPanelTextSegmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTextSegmentLayout.createSequentialGroup()
                .addComponent(jTextFieldTextSegment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxSegment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelInnerTextLayout = new javax.swing.GroupLayout(jPanelInnerText);
        jPanelInnerText.setLayout(jPanelInnerTextLayout);
        jPanelInnerTextLayout.setHorizontalGroup(
            jPanelInnerTextLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelTextSymbol, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelTextSegment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelInnerTextLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxDefaultText, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelInnerTextLayout.setVerticalGroup(
            jPanelInnerTextLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInnerTextLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxDefaultText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelTextSymbol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelTextSegment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(217, Short.MAX_VALUE))
        );

        jScrollPaneText.setViewportView(jPanelInnerText);

        javax.swing.GroupLayout jPanelOuterTextLayout = new javax.swing.GroupLayout(jPanelOuterText);
        jPanelOuterText.setLayout(jPanelOuterTextLayout);
        jPanelOuterTextLayout.setHorizontalGroup(
            jPanelOuterTextLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneText, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
        );
        jPanelOuterTextLayout.setVerticalGroup(
            jPanelOuterTextLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneText, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
        );

        jTabbedPaneEdit.addTab("Text symbolu", jPanelOuterText);

        javax.swing.GroupLayout jPanelDetailsLayout = new javax.swing.GroupLayout(jPanelDetails);
        jPanelDetails.setLayout(jPanelDetailsLayout);
        jPanelDetailsLayout.setHorizontalGroup(
            jPanelDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPaneEdit)
        );
        jPanelDetailsLayout.setVerticalGroup(
            jPanelDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPaneEdit)
        );

        jToolBarSymbols.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 0, 1, new java.awt.Color(147, 152, 161)));
        jToolBarSymbols.setFloatable(false);
        jToolBarSymbols.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBarSymbols.setRollover(true);
        jToolBarSymbols.add(jSeparator5);

        javax.swing.GroupLayout jPanelEditLayout = new javax.swing.GroupLayout(jPanelEdit);
        jPanelEdit.setLayout(jPanelEditLayout);
        jPanelEditLayout.setHorizontalGroup(
            jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEditLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanelDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jToolBarSymbols, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanelEditLayout.setVerticalGroup(
            jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEditLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanelEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBarSymbols, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelDetails, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout jPanelLeftSplitLayout = new javax.swing.GroupLayout(jPanelLeftSplit);
        jPanelLeftSplit.setLayout(jPanelLeftSplitLayout);
        jPanelLeftSplitLayout.setHorizontalGroup(
            jPanelLeftSplitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelEdit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelLeftSplitLayout.setVerticalGroup(
            jPanelLeftSplitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelEdit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jSplitPane.setLeftComponent(jPanelLeftSplit);

        jScrollPaneDiagram.setHorizontalScrollBar(new JScrollBarDiagram(JScrollBar.HORIZONTAL));
        jScrollPaneDiagram.setMinimumSize(new java.awt.Dimension(101, 20));
        jScrollPaneDiagram.setPreferredSize(new java.awt.Dimension(690, 417));
        jScrollPaneDiagram.setVerticalScrollBar(new JScrollBarDiagram(JScrollBar.VERTICAL));

        jPanelDiagram.setBackground(new java.awt.Color(255, 255, 255));
        jPanelDiagram.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanelDiagram.setPreferredSize(new java.awt.Dimension(0, 0));

        javax.swing.GroupLayout jPanelDiagramLayout = new javax.swing.GroupLayout(jPanelDiagram);
        jPanelDiagram.setLayout(jPanelDiagramLayout);
        jPanelDiagramLayout.setHorizontalGroup(
            jPanelDiagramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 577, Short.MAX_VALUE)
        );
        jPanelDiagramLayout.setVerticalGroup(
            jPanelDiagramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 609, Short.MAX_VALUE)
        );

        jScrollPaneDiagram.setViewportView(jPanelDiagram);

        jSplitPane.setRightComponent(jScrollPaneDiagram);

        jMenuBar1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(147, 152, 161)));

        jMenuFile.setText("Soubor");

        jMenuItemNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/menuitems/16-New.png"))); // NOI18N
        jMenuItemNew.setMnemonic('n');
        jMenuItemNew.setText("Nový");
        jMenuItemNew.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemNewActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemNew);

        jMenuItemOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/menuitems/16-Open.png"))); // NOI18N
        jMenuItemOpen.setMnemonic('o');
        jMenuItemOpen.setText("Otevřít");
        jMenuItemOpen.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemOpenActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemOpen);

        jMenuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/menuitems/16-Save.png"))); // NOI18N
        jMenuItemSave.setMnemonic('u');
        jMenuItemSave.setText("Uložit");
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSave);

        jMenuItemSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSaveAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/menuitems/16-SaveAs.png"))); // NOI18N
        jMenuItemSaveAs.setMnemonic('j');
        jMenuItemSaveAs.setText("Uložit Jako");
        jMenuItemSaveAs.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemSaveAsActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSaveAs);
        jMenuFile.add(jSeparator10);

        jMenuItemExportImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/menuitems/16-Export_image.png"))); // NOI18N
        jMenuItemExportImage.setText("Export do obrázku");
        jMenuItemExportImage.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemExportImageActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExportImage);

        jMenuItemExportPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/menuitems/16-Export_pdf.png"))); // NOI18N
        jMenuItemExportPDF.setText("Export do PDF");
        jMenuItemExportPDF.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemExportPDFActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExportPDF);
        jMenuFile.add(jSeparator11);

        jMenuItemCodeImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/menuitems/16-Import_Code.png"))); // NOI18N
        jMenuItemCodeImport.setText("Import ze zdoj. kódu");
        jMenuItemCodeImport.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemCodeImportActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemCodeImport);

        jMenuItemCodeExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/menuitems/16-Export_Code.png"))); // NOI18N
        jMenuItemCodeExport.setText("Export do zdoj. kódu");
        jMenuItemCodeExport.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemCodeExportActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemCodeExport);
        jMenuFile.add(jSeparator14);

        jMenuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        jMenuItemExit.setText("Konec");
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBar1.add(jMenuFile);

        jMenuEdit.setText("Úpravy");
        jMenuEdit.setActionCommand("edit");

        jMenuItemUndo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/menuitems/16-Undo.png"))); // NOI18N
        jMenuItemUndo.setText("Zpět");
        jMenuItemUndo.setEnabled(false);
        jMenuItemUndo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemUndoActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemUndo);

        jMenuItemRedo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemRedo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/menuitems/16-Redo.png"))); // NOI18N
        jMenuItemRedo.setText("Znovu");
        jMenuItemRedo.setEnabled(false);
        jMenuItemRedo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemRedoActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemRedo);
        jMenuEdit.add(jSeparator12);

        jMenuItemCut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/menuitems/16-Cut.png"))); // NOI18N
        jMenuItemCut.setText("Vyjmout symbol");
        jMenuItemCut.setActionCommand("edit/cut");
        jMenuEdit.add(jMenuItemCut);

        jMenuItemCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/menuitems/16-Copy.png"))); // NOI18N
        jMenuItemCopy.setText("Kopírovat symbol");
        jMenuItemCopy.setActionCommand("edit/copy");
        jMenuEdit.add(jMenuItemCopy);

        jMenuItemPaste.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemPaste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/menuitems/16-Copy.png"))); // NOI18N
        jMenuItemPaste.setText("Vložit symbol");
        jMenuItemPaste.setActionCommand("edit/paste");
        jMenuEdit.add(jMenuItemPaste);
        jMenuEdit.add(jSeparator6);

        jMenuItemDelete.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        jMenuItemDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/menuitems/16-Delete.png"))); // NOI18N
        jMenuItemDelete.setMnemonic('d');
        jMenuItemDelete.setText("Smazat symbol");
        jMenuItemDelete.setActionCommand("edit/delete");
        jMenuEdit.add(jMenuItemDelete);

        jMenuBar1.add(jMenuEdit);

        jMenuLayouts.setText("Layouty");
        jMenuLayouts.setActionCommand("layout");
        jMenuLayouts.add(jSeparator7);

        jMenuLayoutSetting.setText("Nastavení zvoleného");
        jMenuLayoutSetting.setActionCommand("layout/setting");
        jMenuLayouts.add(jMenuLayoutSetting);

        jMenuBar1.add(jMenuLayouts);

        jMenuConfiguration.setText("Nastavení");
        jMenuConfiguration.setActionCommand("settings");

        jMenuItemSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/menuitems/16-Settings.png"))); // NOI18N
        jMenuItemSettings.setMnemonic('v');
        jMenuItemSettings.setText("  Volby");
        jMenuItemSettings.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemSettingsActionPerformed(evt);
            }
        });
        jMenuConfiguration.add(jMenuItemSettings);

        jMenuBar1.add(jMenuConfiguration);

        jMenuHelp.setText("Nápověda");
        jMenuHelp.setActionCommand("help");

        jMenuItemUpdate.setText("Kontrola aktualizací");
        jMenuItemUpdate.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemUpdateActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemUpdate);
        jMenuHelp.add(jSeparator15);

        jMenuItemAbout.setText("O aplikaci");
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemAbout);

        jMenuBar1.add(jMenuHelp);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBarMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
            .addComponent(jPanelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
            .addComponent(jSplitPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBarMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jSplitPane)
                .addGap(0, 0, 0)
                .addComponent(jPanelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSettingsActionPerformed
        jFrameSettings.setLocationRelativeTo(this);
        jFrameSettings.setVisible(true);
    }//GEN-LAST:event_jMenuItemSettingsActionPerformed

    private void jMenuItemExportPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportPDFActionPerformed
        File file = MyFileChooser.saveFileDialog(MyFileChooser.FilterType.PDF, "Export PDF", null);
        if (file != null) {
            int prevFlowchartPadding = layout.getFlowchartPadding();
            layout.setFlowchartPadding(SettingsHolder.settings.getExportFlowchartPadding());

//            Document document = new Document(new com.itextpdf.text.Rectangle((float) layout.getWidth(), (float) layout.getHeight()));
//            PdfWriter writer;
//            try {
//                writer = PdfWriter.getInstance(document, new FileOutputStream(file));
//            } catch (DocumentException | FileNotFoundException ex) {
//                ex.printStackTrace(System.err);
//                JOptionPane.showMessageDialog(this, "Při vytváření PDF souboru nastala chyba!", "Chyba", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//            document.open();
//            PdfContentByte cb = writer.getDirectContent();
//
//            Graphics2D g2d = new PdfGraphics2D(cb, (float) layout.getWidth(), (float) layout.getHeight());
//            layout.setEditMode(false);
//            layout.paintFlowchart(g2d, true);
//            layout.setEditMode(editMode);
//            layout.setFlowchartPadding(prevFlowchartPadding);
//            g2d.dispose();
//
//            //TODO add metadata?
//            document.close();

            VectorGraphics graphics;
            try {
                graphics = new PDFGraphics2D(file, new Dimension((int) layout.getWidth(),
                        (int) layout.getHeight()));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace(System.err);
                JOptionPane.showMessageDialog(this, "Při vytváření PDF souboru nastala chyba!",
                        "Chyba", JOptionPane.ERROR_MESSAGE);
                return;
            }
            graphics.startExport();

            layout.setEditMode(false);
            layout.paintFlowchart(graphics, true);
            layout.setEditMode(editMode);
            layout.setFlowchartPadding(prevFlowchartPadding);

            //TODO add metadata?
            graphics.endExport();
            graphics.dispose();

            setStatusText("Diagram byl úspěšně exportován do " + file.getPath(), 5000);
        }
    }//GEN-LAST:event_jMenuItemExportPDFActionPerformed

    private void jMenuItemExportImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportImageActionPerformed
        File file = MyFileChooser.saveFileDialog(MyFileChooser.FilterType.IMAGE, "Export obrázku",
                null);
        if (file != null) {
            int prevFlowchartPadding = layout.getFlowchartPadding();
            layout.setFlowchartPadding(SettingsHolder.settings.getExportFlowchartPadding());

            String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1,
                    file.getName().length());
            BufferedImage img;
            int width = (int) (layout.getWidth() * jPnlDiagram.getScale());
            int height = (int) (layout.getHeight() * jPnlDiagram.getScale());
            switch (extension) {
                case "wbmp":
                    img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
                    break;
                case "gif":
                case "png":
                    if (SettingsHolder.settings.isExportTransparency()) {
                        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                        break;
                    }
                default:
                    img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    break;
            }
            Graphics2D g2d = img.createGraphics();
            if (!SettingsHolder.settings.isExportTransparency() || (!extension.equals("gif") && !extension.equals(
                    "png"))) {
                g2d.setColor(Color.WHITE);
                g2d.fill(new Rectangle((int) layout.getWidth(), (int) layout.getHeight()));
            }
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.scale(jPnlDiagram.getScale(), jPnlDiagram.getScale());
            layout.setEditMode(false);
            layout.paintFlowchart(g2d, false);
            layout.setEditMode(editMode);
            layout.setFlowchartPadding(prevFlowchartPadding);
            g2d.dispose();

            try {
                if (!ImageIO.write(img, extension, file)) {
                    JOptionPane.showMessageDialog(this, "Obrázek se nepodařilo vytvořit.", "Chyba",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
                JOptionPane.showMessageDialog(this, "Při vytváření souboru obrázku nastala chyba!",
                        "Chyba", JOptionPane.ERROR_MESSAGE);
                return;
            }
            setStatusText("Diagram byl úspěšně exportován do " + file.getPath(), 5000);
        }
    }//GEN-LAST:event_jMenuItemExportImageActionPerformed

    private void jMenuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenActionPerformed
        File file = MyFileChooser.openFileDialog(MyFileChooser.FilterType.XML, "Otevřít diagram");
        if (file != null) {
            openDiagram(file);
        }
    }//GEN-LAST:event_jMenuItemOpenActionPerformed

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
        if (SettingsHolder.settings.getActualFlowchartFile() == null) {
            jMenuItemSaveAsActionPerformed(evt);
        } else {
            try {
                Marshaller jAXBmarshaller = getJAXBcontext().createMarshaller();
                jAXBmarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jAXBmarshaller.marshal(layout.getFlowchart(),
                        SettingsHolder.settings.getActualFlowchartFile());
                setStatusText(
                        "Diagram byl úspěšně uložen do " + SettingsHolder.settings.getActualFlowchartFile().getPath(),
                        3500);
            } catch (JAXBException ex) {
                ex.printStackTrace(System.err);
                JOptionPane.showMessageDialog(this, "Při ukládání diagramu nastala chyba!",
                        "Diagram se nepodařilo uložit", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    private void jMenuItemNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNewActionPerformed
        if (!checkIfSaved(true)) {
            return;
        }
        if (!editMode) {
            // prepnu do editacniho modu
            flowchartEditManager.actionPerformed(new ActionEvent(jButtonToolEdit,
                    jButtonToolEdit.hashCode(), "mode/editMode"));
        }
        layout.setFlowchart(null);
        flowchartEditUndoManager.discardAllEdits();
        flowchartEditManager.setEditMenuEnablers();
        flowchartEditManager.loadMarkedSymbolText();
        jPanelDiagram.repaint();
        SettingsHolder.settings.setActualFlowchartFile(null);
        super.setTitle(windowTitle);
    }//GEN-LAST:event_jMenuItemNewActionPerformed

    private void jMenuItemSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsActionPerformed
        String defname = null;
        if (SettingsHolder.settings.getActualFlowchartFile() != null) {
            defname = SettingsHolder.settings.getActualFlowchartFile().getName().substring(0,
                    SettingsHolder.settings.getActualFlowchartFile().getName().length() - 4);
        }
        File file = MyFileChooser.saveFileDialog(MyFileChooser.FilterType.XML, "Uložit diagram",
                defname);
        if (file != null) {
            try {
                Marshaller jAXBmarshaller = getJAXBcontext().createMarshaller();
                jAXBmarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jAXBmarshaller.marshal(layout.getFlowchart(), file);

                SettingsHolder.settings.setActualFlowchartFile(file);
                super.setTitle(SettingsHolder.settings.getActualFlowchartFile().getName().substring(
                        0, SettingsHolder.settings.getActualFlowchartFile().getName().length() - 4) + " - " + windowTitle);

                setStatusText(
                        "Diagram byl úspěšně uložen do " + SettingsHolder.settings.getActualFlowchartFile().getPath(),
                        3500);
            } catch (JAXBException ex) {
                ex.printStackTrace(System.err);
                JOptionPane.showMessageDialog(this, "Při ukládání diagramu nastala chyba!",
                        "Diagram se nepodařilo uložit", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jMenuItemSaveAsActionPerformed

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        exit();
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    private void jMenuItemUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUndoActionPerformed
        if (jMenuItemUndo.isEnabled()) {
            flowchartEditManager.doPendingUndoRedos();
            flowchartEditUndoManager.undo();
        }
    }//GEN-LAST:event_jMenuItemUndoActionPerformed

    private void jMenuItemRedoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRedoActionPerformed
        if (jMenuItemRedo.isEnabled()) {
            flowchartEditManager.doPendingUndoRedos();
            flowchartEditUndoManager.redo();
        }
    }//GEN-LAST:event_jMenuItemRedoActionPerformed

    private void jMenuItemCodeImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCodeImportActionPerformed
        jFrameCodeImport.setLocationRelativeTo(this);
        jFrameCodeImport.setVisible(true);
    }//GEN-LAST:event_jMenuItemCodeImportActionPerformed

    private void jMenuItemCodeExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCodeExportActionPerformed
        jFrameCodeExport.setLocationRelativeTo(this);
        jFrameCodeExport.setVisible(true);
    }//GEN-LAST:event_jMenuItemCodeExportActionPerformed

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
        jFrameAbout.setLocationRelativeTo(this);
        jFrameAbout.setVisible(true);
    }//GEN-LAST:event_jMenuItemAboutActionPerformed

    private void jMenuItemUpdateActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItemUpdateActionPerformed
    {//GEN-HEADEREND:event_jMenuItemUpdateActionPerformed
        jFrameUpdate.setLocationRelativeTo(this);
        jFrameUpdate.setVisible(true);
    }//GEN-LAST:event_jMenuItemUpdateActionPerformed

//    /**
//     * Positions component by given percentages relatively to middle of main window.
//     * If component's size would overlap screen, measures are taken against it.
//     * <p/>
//     * @param component component to be positioned
//     * @param widthPercent if set to 0 the component's middle is placed on left border of main window, 100 means right border
//     * @param heightPercent if set to 0 the component's middle is placed on top border of main window, 100 means bottom border
//     */
//    private void positionComponent(Component component, int widthPercent, int heightPercent)
//    {
//    }
    /**
     * Metoda pro spuštění hlavního okna aplikace. Nejsou přijímány žádné
     * paramtery.
     */
    public static void main(String args[])
    {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel.
         * For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new MainWindow().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonLaunch;
    private javax.swing.JToggleButton jButtonToolAnimation;
    private javax.swing.JToggleButton jButtonToolEdit;
    private javax.swing.JButton jButtonToolNew;
    private javax.swing.JButton jButtonToolNext;
    private javax.swing.JButton jButtonToolOpen;
    private javax.swing.JButton jButtonToolPlayPause;
    private javax.swing.JButton jButtonToolPrevious;
    private javax.swing.JButton jButtonToolRedo;
    private javax.swing.JButton jButtonToolSave;
    private javax.swing.JButton jButtonToolStop;
    private javax.swing.JButton jButtonToolUndo;
    private javax.swing.JButton jButtonToolZoomIn;
    private javax.swing.JButton jButtonToolZoomOut;
    private javax.swing.JCheckBox jCheckBoxDefaultText;
    private javax.swing.JComboBox<String> jComboBoxSegment;
    private javax.swing.JLabel jLabelFunction;
    private static javax.swing.JLabel jLabelStatus;
    private javax.swing.JLabel jLabelZoom;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuConfiguration;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemCodeExport;
    private javax.swing.JMenuItem jMenuItemCodeImport;
    private javax.swing.JMenuItem jMenuItemCopy;
    private javax.swing.JMenuItem jMenuItemCut;
    private javax.swing.JMenuItem jMenuItemDelete;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemExportImage;
    private javax.swing.JMenuItem jMenuItemExportPDF;
    private javax.swing.JMenuItem jMenuItemNew;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JMenuItem jMenuItemPaste;
    private javax.swing.JMenuItem jMenuItemRedo;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenuItem jMenuItemSaveAs;
    private javax.swing.JMenuItem jMenuItemSettings;
    private javax.swing.JMenuItem jMenuItemUndo;
    private javax.swing.JMenuItem jMenuItemUpdate;
    private javax.swing.JMenu jMenuLayoutSetting;
    private javax.swing.JMenu jMenuLayouts;
    private javax.swing.JPanel jPanelDetails;
    private javax.swing.JPanel jPanelDiagram;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPanel jPanelInnerFunction;
    private javax.swing.JPanel jPanelInnerText;
    private javax.swing.JPanel jPanelLeftSplit;
    private javax.swing.JPanel jPanelOuterFunction;
    private javax.swing.JPanel jPanelOuterText;
    private javax.swing.JPanel jPanelStatus;
    private javax.swing.JPanel jPanelTextSegment;
    private javax.swing.JPanel jPanelTextSymbol;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneDiagram;
    private javax.swing.JScrollPane jScrollPaneFunction;
    private javax.swing.JScrollPane jScrollPaneText;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JToolBar.Separator jSeparator13;
    private javax.swing.JPopupMenu.Separator jSeparator14;
    private javax.swing.JPopupMenu.Separator jSeparator15;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JSlider jSliderSpeed;
    private javax.swing.JSlider jSliderZoom;
    private javax.swing.JSplitPane jSplitPane;
    private javax.swing.JTabbedPane jTabbedPaneEdit;
    private javax.swing.JTextArea jTextAreaTextSymbol;
    private javax.swing.JTextField jTextFieldTextSegment;
    private javax.swing.JToolBar jToolBarMenu;
    private javax.swing.JToolBar jToolBarSymbols;
    // End of variables declaration//GEN-END:variables

    /**
     * Nastaví na daný počet milisekund informační text ve spodním levém rohu
     * aplikace.
     *
     * @param text informační text, který se má zobrazit
     * @param delay doba v milisekundách, po kterou má být informační text
     * zobrazen
     */
    public static void setStatusText(String text, int delay)
    {
        jLabelStatus.setText(text);
        statusTimer.setInitialDelay(delay);
        statusTimer.restart();
    }

    /**
     * Nastaví editační režim aplikace do požadovaného stavu.<br />
     * Tato metoda by měla být synchronizována s FlowchartEditManagerem.
     *
     * @param editMode požadovaný stav editačního režimu
     */
    public void setEditMode(boolean editMode)
    {
        if (this.editMode == editMode) {
            return;
        }
        if (editMode) {
            if (animationMode) {
                flowchartAnimationManager.actionPerformed(new ActionEvent(jButtonToolAnimation,
                        jButtonToolAnimation.hashCode(), "mode/animationMode"));
            }
            try {
                jButtonToolEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                        "/img/toolBar/24-Unlock.png")));
            } catch (NullPointerException e) {
                throw new Error("Error while loading toolbar image!");
            }
            jButtonToolEdit.setSelected(true);
            jPanelDiagram.addMouseListener(flowchartEditManager);
            jPanelDiagram.addMouseMotionListener(flowchartEditManager);
            jPanelDiagram.addKeyListener(flowchartEditManager);
            jTextAreaTextSymbol.addFocusListener(flowchartEditManager);
            jTextFieldTextSegment.addFocusListener(flowchartEditManager);

            if (jPanelVariables.isVisible()) {
                jPanelVariables.setVisible(false);
                ((GroupLayout) jPanelLeftSplit.getLayout()).replace(jPanelVariables, jPanelEdit);
                jPanelEdit.setVisible(true);
            }

            flowchartEditUndoManager.setButtons();
            for (Component component : getAllComponents(jPanelLeftSplit)) {
                component.setEnabled(true);
            }
            jTextAreaTextSymbol.getDocument().addDocumentListener(flowchartEditManager);
            jTextFieldTextSegment.getDocument().addDocumentListener(flowchartEditManager);
            //setEnableToSymbolButtons(true);
        } else {
            try {
                jButtonToolEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                        "/img/toolBar/24-Lock.png")));
            } catch (NullPointerException e) {
                throw new Error("Error while loading toolbar image!");
            }
            jButtonToolEdit.setSelected(false);
            jPanelDiagram.removeMouseListener(flowchartEditManager);
            jPanelDiagram.removeMouseMotionListener(flowchartEditManager);
            jPanelDiagram.removeKeyListener(flowchartEditManager);
            jTextAreaTextSymbol.removeFocusListener(flowchartEditManager);
            jTextFieldTextSegment.removeFocusListener(flowchartEditManager);
            jTextAreaTextSymbol.getDocument().removeDocumentListener(flowchartEditManager);
            jTextFieldTextSegment.getDocument().removeDocumentListener(flowchartEditManager);
            jTextAreaTextSymbol.setText("");
            jTextFieldTextSegment.setText("");
            jButtonToolUndo.setEnabled(false);
            jButtonToolRedo.setEnabled(false);
            jMenuItemUndo.setEnabled(false);
            jMenuItemRedo.setEnabled(false);
            for (Component component : getAllComponents(jPanelLeftSplit)) {
                component.setEnabled(false);
            }
            //setEnableToSymbolButtons(false);
        }
        layout.setEditMode(editMode);
        this.editMode = editMode;
    }

    /**
     * Nastaví animační režim aplikace do požadovaného stavu.<br />
     * Tato metoda by měla být synchronizována s FlowchartAnimationManagerem.
     *
     * @param animationMode požadovaný stav animačního režimu
     */
    public void setAnimationMode(boolean animationMode)
    {
        if (this.animationMode == animationMode) {
            return;
        }
        if (animationMode) {
            if (editMode) {
                flowchartEditManager.actionPerformed(new ActionEvent(jButtonToolEdit,
                        jButtonToolEdit.hashCode(), "mode/editMode"));
            }
            jMenuLayouts.setEnabled(false);
            jButtonToolAnimation.setSelected(true);
            jSliderZoom.addKeyListener(flowchartAnimationManager); // aby zkratky pro prehravani byli pristupny i kdyz je focus na slideru
            jSliderSpeed.addKeyListener(flowchartAnimationManager);
            jPanelDiagram.addKeyListener(flowchartAnimationManager);
            jPanelDiagram.addMouseListener(flowchartAnimationManager);
            jPanelDiagram.addMouseMotionListener(flowchartAnimationManager);
            jButtonToolPlayPause.setEnabled(true);
            jButtonToolNext.setEnabled(true);
            jButtonToolStop.setEnabled(true);
            jButtonLaunch.setEnabled(true);
            jSliderSpeed.setEnabled(true);

            if (jPanelEdit.isVisible()) {
                jPanelEdit.setVisible(false);
                ((GroupLayout) jPanelLeftSplit.getLayout()).replace(jPanelEdit, jPanelVariables);
                jPanelVariables.setVisible(true);
            }

            for (Component component : getAllComponents(jPanelLeftSplit)) {
                component.setEnabled(true);
            }
        } else {
            jMenuLayouts.setEnabled(true);
            jButtonToolAnimation.setSelected(false);
            jSliderZoom.removeKeyListener(flowchartAnimationManager);
            jSliderSpeed.removeKeyListener(flowchartAnimationManager);
            jPanelDiagram.removeKeyListener(flowchartAnimationManager);
            jPanelDiagram.removeMouseListener(flowchartAnimationManager);
            jPanelDiagram.removeMouseMotionListener(flowchartAnimationManager);
            jButtonToolPlayPause.setEnabled(false);
            jButtonToolPrevious.setEnabled(false);
            jButtonToolNext.setEnabled(false);
            jButtonToolStop.setEnabled(false);
            jButtonLaunch.setEnabled(false);
            jSliderSpeed.setEnabled(false);
            for (Component component : getAllComponents(jPanelLeftSplit)) {
                component.setEnabled(false);
            }
        }
        this.animationMode = animationMode;
    }

    /**
     * Vrátí stav editačního režimu aplikace.
     *
     * @return stav editačního režimu aplikace
     */
    public boolean getEditMode()
    {
        return editMode;
    }

    /**
     * Vrátí stav animačního režimu aplikace.
     *
     * @return stav animačního režimu aplikace
     */
    public boolean getAnimationMode()
    {
        return animationMode;
    }

    /**
     * Metoda invokuje překreslení plátna, na němž je vykreslen vývojový
     * diagram.
     */
    public void repaintJPanelDiagram()
    {
        jPanelDiagram.repaint();
    }

    /*
     * public void dispatchKeyEventToSymbolTextArea(KeyEvent ke) {
     * if (!jTextAreaTextSymbol.requestFocusInWindow()
     * && (jTabbedPaneEdit.getSelectedIndex() != 0
     * || (jScrollPaneFunction.getViewport().getView() instanceof
     * AbstractSymbolFunctionForm &&
     * ((AbstractSymbolFunctionForm)jScrollPaneFunction.getViewport().getView()).hasCommandsToSet())))
     * {
     * // nebylo mozne nastavit focus, zrejme je sepnuta zalozka funkce.. Text
     * symbolu vlozim jen jestli symbol nema funkce k nastavovani
     * return;
     * }
     * jTextAreaTextSymbol.setCaretPosition(jTextAreaTextSymbol.getDocument().getLength());
     * jTextAreaTextSymbol.dispatchEvent(ke);
     * }
     */
    /**
     * Nastaví parametr enabled tlačítka "Vymazat symbol" v nabídce "Úpravy" do
     * požadovaného stavu.
     *
     * @param enabled požadovaný stav tlačítka
     */
    public void setjMenuItemDeleteEnabled(boolean enabled)
    {
        jMenuItemDelete.setEnabled(enabled);
    }

    /**
     * Nastaví parametr enabled tlačítka "Vyjmout symbol" v nabídce "Úpravy" do
     * požadovaného stavu.
     *
     * @param enabled požadovaný stav tlačítka
     */
    public void setjMenuItemCutEnabled(boolean enabled)
    {
        jMenuItemCut.setEnabled(enabled);
    }

    /**
     * Nastaví parametr enabled tlačítka "Kopírovat symbol" v nabídce "Úpravy"
     * do
     * požadovaného stavu.
     *
     * @param enabled požadovaný stav tlačítka
     */
    public void setjMenuItemCopyEnabled(boolean enabled)
    {
        jMenuItemCopy.setEnabled(enabled);
    }

    /**
     * Nastaví parametr enabled tlačítka "Vložit symbol" v nabídce "Úpravy" do
     * požadovaného stavu.
     *
     * @param enabled požadovaný stav tlačítka
     */
    public void setjMenuItemPasteEnabled(boolean enabled)
    {
        jMenuItemPaste.setEnabled(enabled);
    }

    /**
     * Zažádá plátno s vývojovým diagramem o focus.
     */
    public void setJPanelDiagramFocus()
    {
        jPanelDiagram.requestFocusInWindow();
    }

    /**
     * Uvede paramter visible textového pole pro zadání textu segmentu do
     * požadovaného stavu.
     *
     * @param visible požadovaný stav textového pole
     */
    public void setJPanelTextSegmentVisible(boolean visible)
    {
        jPanelTextSegment.setVisible(visible);
    }

    /**
     * Vrací true, když byla Xová souřadnice tranformace diagramu změněna
     * scrollbarem.
     *
     * @return true, když byla Xová souřadnice tranformace diagramu změněna
     * scrollbarem
     */
    public boolean graphicsXTransformedByScrollbar()
    {
        return graphicsXTransformedByScrollbar;
    }

    /**
     * Vrací true, když byla Yová souřadnice tranformace diagramu změněna
     * scrollbarem.
     *
     * @return true, když byla Yová souřadnice tranformace diagramu změněna
     * scrollbarem
     */
    public boolean graphicsYTransformedByScrollbar()
    {
        return graphicsYTransformedByScrollbar;
    }

    /**
     * Vrátí instanci AffineTransform, používanou pro vyobrazení vývojového
     * diagramu.
     *
     * @return instanci AffineTransform, používanou pro vyobrazení vývojového
     * diagramu
     */
    public AffineTransform getAffineTransform()
    {
        return affineTransform;
    }

    /**
     * Vrátí Xovou hodnotu funkce Translate plátna diagramu.
     *
     * @return Xovou hodnotu funkce Translate plátna diagramu
     */
    public double getTranslateX()
    {
        return jPnlDiagram.getTranslateX();
    }

    /**
     * Vrátí Yovou hodnotu funkce Translate plátna diagramu.
     *
     * @return Yovou hodnotu funkce Translate plátna diagramu
     */
    public double getTranslateY()
    {
        return jPnlDiagram.getTranslateY();
    }

    /**
     * Nastaví požadované zvětšení vývojového diagramu.
     *
     * @param scale požadované zvětšení vývojového diagramu
     */
    public void setScale(double scale)
    {
        jPnlDiagram.setScale(scale);
    }

    /**
     * Vrátí aktuální zvětšení vývojového diagramu.
     *
     * @return aktuální zvětšení vývojového diagramu
     */
    public double getScale()
    {
        return jPnlDiagram.getScale();
    }

    /**
     * Nastaví požadovanou textovou hodnotu zoom labelu.
     *
     * @param text požadovaná textovou hodnotu zoom labelu
     */
    public void setJLabelZoomText(String text)
    {
        jLabelZoom.setText(text);
    }

    /*
     * public void setJTextAreaSymbolTextText(String text) {
     * jTextAreaTextSymbol.setText(text);
     * }
     */
    /**
     * Nastaví panelu s editací textu symbolu/segmentu optimální velikost.
     */
    public void packJPanelInnerText()
    {
        jPanelInnerText.setPreferredSize(jPanelInnerText.getLayout().minimumLayoutSize(
                jPanelInnerText));
        jScrollPaneText.revalidate();
    }

    /**
     * Vrací JTabbedPane, představujcící formulář pro editaci symbolu.
     *
     * @return JTabbedPane, představujcící formulář pro editaci symbolu
     */
    public JTabbedPane getJTabbedPaneEdit()
    {
        return jTabbedPaneEdit;
    }

    /**
     * Nastaví zobrazenému symbolu daný formulář pro nastavení jeho funkcí.
     * Je-li zadán parametr null, dosadí se na toto místo informace o
     * nepřítomnosti možnosti nastavení funkce tohoto symbolu.
     *
     * @param component formulář pro nastavení funkce aktuálně zvoleného symbolu
     */
    public void setJScrollPaneFunctionViewportView(Component component)
    {
        if (component == null) {
            jScrollPaneFunction.setViewportView(jPanelInnerFunction);
        } else {
            jScrollPaneFunction.setViewportView(component);
        }
    }

    /**
     * Vrací aktuálně nastevný formulář pro nastavení funkce symbolu.
     *
     * @return aktuálně nastevný formulář pro nastavení funkce symbolu
     */
    public Component getJScrollPaneFunctionViewportView()
    {
        return jScrollPaneFunction.getViewport().getView();
    }

    /**
     * Vrací aktuálně používaný layout vývojového diagramu.
     *
     * @return aktuálně používaný layout vývojového diagramu
     */
    public Layout getFlowchartLayout()
    {
        return layout;
    }

    /**
     * Vrací FlowchartOverlookManager, starající se o obecné uživatelské
     * manipulace s plátnem.
     *
     * @return používaná instance FlowchartOverlookManagera
     */
    public FlowchartOverlookManager getFlowchartOverlookManager()
    {
        return flowchartOverlookManager;
    }

    /**
     * Vrací platný kontext JAXB, použitelný pro marshalling/unmarshalling
     * vývojového diagramu.
     *
     * @return platný kontext JAXB, použitelný pro marshalling/unmarshalling
     * vývojového diagramu
     */
    public static JAXBContext getJAXBcontext()
    {
        if (jAXBcontext == null) {
            jAXBcontext = createJAXBContext();
        }
        return jAXBcontext;
    }

    /*
     * public void packJPanelInnerFunction() {
     * jPanelInnerFunction.setPreferredSize(jPanelInnerFunction.getLayout().minimumLayoutSize(jPanelInnerFunction));
     * jScrollPaneFunction.revalidate();
     * }
     */
    /*
     * public Graphics getJPanelDiagramGraphics() {
     * return jPanelDiagram.getGraphics();
     * }
     */
    private TransferHandler createTransferHandler()
    {
        // TODO: give it it's own class
        long currentTime = System.currentTimeMillis();

//        Proxy proxyHTTP = Proxy.NO_PROXY;
//        Proxy proxySOCKS = Proxy.NO_PROXY;
//        if (global.SettingsHolder.settings.getProxyHost() != null && !global.SettingsHolder.settings.getProxyHost().equals("") && global.SettingsHolder.settings.getProxyPort() > 0) {
//            try {
//                InetSocketAddress addr = new InetSocketAddress(InetAddress.getByName(global.SettingsHolder.settings.getProxyHost()), global.SettingsHolder.settings.getProxyPort());
//                proxyHTTP = new Proxy(Proxy.Type.HTTP, addr); //http://docs.oracle.com/javase/6/docs/technotes/guides/net/proxies.html
//                proxySOCKS = new Proxy(Proxy.Type.SOCKS, addr);
//            } catch (UnknownHostException ex) {
//                JOptionPane.showMessageDialog(null, "<html>Nepodařilo se přistoupit k zadané adrese proxy.<br />Zpráva:<br />" + ex.getMessage() + "</html>", "Špatná proxy adresa", JOptionPane.WARNING_MESSAGE);
//                System.err.println("0: " + ex);
//                ex.printStackTrace(System.err);
//            }
//        }
//        if (global.SettingsHolder.settings.getProxyHost() != null && !global.SettingsHolder.settings.getProxyHost().equals("") && global.SettingsHolder.settings.getProxyPort() > 0) {
//            System.setProperty("http.proxyHost", global.SettingsHolder.settings.getProxyHost());
//            System.setProperty("http.proxyPort", String.valueOf(global.SettingsHolder.settings.getProxyPort()));
//            System.setProperty("htttps.proxyHost", global.SettingsHolder.settings.getProxyHost());
//            System.setProperty("https.proxyPort", String.valueOf(global.SettingsHolder.settings.getProxyPort()));
//            System.setProperty("socksProxyHost", global.SettingsHolder.settings.getProxyHost());
//            System.setProperty("socksProxyPort", String.valueOf(global.SettingsHolder.settings.getProxyPort()));
//        }

        try {
            URL url = new URL(cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder.TIMESERVER);
            URLConnection urlConn = url.openConnection();
            urlConn.setConnectTimeout(6000);
            urlConn.setReadTimeout(6000);
            urlConn.setRequestProperty("User-agent", "spider");
            urlConn.connect();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",
                    Locale.ENGLISH);
            currentTime = sdf.parse(urlConn.getHeaderField("Date")).getTime();
        } catch (IOException ex) {
            System.err.println("1: " + ex);
            JOptionPane.showMessageDialog(null, new String(new char[]{'<', 'h', 't', 'm', 'l', '>',
                'T', 'a', 't', 'o', ' ', 'z', 'k', 'u', 'š', 'e', 'b', 'n', 'í', ' ', 'v', 'e', 'r',
                'z', 'e', ' ', 'a', 'p', 'l', 'i', 'k', 'a', 'c', 'e', ' ', 'p', 'o', 't', 'ř', 'e',
                'b', 'u', 'j', 'e', ' ', 'p', 'r', 'o', ' ', 's', 'v', 'ů', 'j', ' ', 'b', 'ě', 'h',
                ' ', 'p', 'ř', 'i', 'p', 'o', 'j', 'e', 'n', 'í', ' ', 'k', ' ', 'i', 'n', 't', 'e',
                'r', 'n', 'e', 't', 'u', '.', '<', 'b', 'r', ' ', '/', '>', 'M', 'á', 't', 'e', '-',
                'l', 'i', ' ', 'p', 'o', 't', 'í', 'ž', 'e', ',', ' ', 'k', 'o', 'n', 't', 'a', 'k',
                't', 'u', 'j', 't', 'e', ' ', 'm', 'n', 'e', ' ', 'n', 'a', ' ', 'm', 'i', 'r', 'o',
                's', 'l', 'a', 'v', 'b', 'a', 'r', 't', 'y', 'z', 'a', 'l', '@', 'g', 'm', 'a', 'i',
                'l', '.', 'c', 'o', 'm', '.', '<', 'b', 'r', ' ', '/', '>', 'N', 'y', 'n', 'í', ' ',
                's', 'e', ' ', 'P', 'S', ' ', 'D', 'i', 'a', 'g', 'r', 'a', 'm', ' ', 'u', 'k', 'o',
                'n', 'č', 'í', '.', '.', '.', '<', '/', 'h', 't', 'm', 'l', '>'}), new String(
                    new char[]{'N', 'e', 'p', 'o', 'd', 'a', 'ř', 'i', 'l', 'o', ' ', 's', 'e', ' ',
                'n', 'a', 'v', 'á', 'z', 'a', 't', ' ', 's', 'p', 'o', 'j', 'e', 'n', 'í', ' ', 's',
                'e', ' ', 's', 'e', 'r', 'v', 'e', 'r', 'e', 'm'}), JOptionPane.WARNING_MESSAGE); //<html>Nepodařilo se navázat spojení se serverem; Tato zkušební verze aplikace potřebuje pro svůj běh připojení k internetu.<br />Máte-li potíže, kontaktujte mne na miroslavbartyzal@gmail.com.<br /> Nyní se PS Diagram ukončí...</html>
            System.exit(0);
        } catch (ParseException ex) {
            System.err.println("2: " + ex);
        }

        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(InetAddress.getByName("92.62.226.175"), 33789),
                    2000);
        } catch (Exception ex) {
//            System.err.println("5: " + ex);
//            ex.printStackTrace(System.err);
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                System.err.println("6: " + ex);
                ex.printStackTrace(System.err);
            }
        }
//        JOptionPane.showMessageDialog(null, "Analýza probéhla, log chyb viz příkazový řádek. Nyní se program ukončí.", "Analýza proběhla", JOptionPane.INFORMATION_MESSAGE);
//        System.exit(0);

        if (currentTime > 1383260400000l || currentTime < SettingsHolder.settings.getLastTrialLaunchedTime()) { // 2013.11.1. 00:00:00 = 1383260400000 (new GregorianCalendar(2013, 10, 1).getTimeInMillis()) - month is zero-based
            // html content
            JEditorPane ep = new JEditorPane("text/html", new String(new char[]{'<', 'h', 't', 'm',
                'l', '>', 'P', 'l', 'a', 't', 'n', 'o', 's', 't', ' ', 't', 'é', 't', 'o', ' ', 'z',
                'k', 'u', 'š', 'e', 'b', 'n', 'í', ' ', 'v', 'e', 'r', 'z', 'e', ' ', 'a', 'p', 'l',
                'i', 'k', 'a', 'c', 'e', ' ', 'v', 'y', 'p', 'r', 'š', 'e', 'l', 'a', '.', '<', 'b',
                'r', ' ', '/', '>', 'P', 'r', 'o', ' ', 'z', 'í', 's', 'k', 'á', 'n', 'í', ' ', 'n',
                'o', 'v', 'é', ',', ' ', 'a', 'k', 't', 'u', 'á', 'l', 'n', 'í', ' ', 'v', 'e', 'r',
                'z', 'e', ' ', 'm', 'n', 'e', ' ', 'k', 'o', 'n', 't', 'a', 'k', 't', 'u', 'j', 't',
                'e', ' ', 'n', 'a', ' ', 'm', 'i', 'r', 'o', 's', 'l', 'a', 'v', 'b', 'a', 'r', 't',
                'y', 'z', 'a', 'l', '@', 'g', 'm', 'a', 'i', 'l', '.', 'c', 'o', 'm', ',', ' ', 'n',
                'e', 'b', 'o', ' ', 'n', 'a', 'v', 'š', 't', 'i', 'v', 't', 'e', ' ', 's', 't', 'r',
                'á', 'n', 'k', 'y', ' ', '<', 'a', ' ', 'h', 'r', 'e', 'f', '=', '\"', 'h', 't', 't',
                'p', ':', '/', '/', 'w', 'w', 'w', '.', 'p', 's', 'd', 'i', 'a', 'g', 'r', 'a', 'm',
                '.', 'c', 'z', '\"', '>', 'p', 's', 'd', 'i', 'a', 'g', 'r', 'a', 'm', '.', 'c', 'z',
                '<', '/', 'a', '>', '.', '<', '/', 'h', 't', 'm', 'l', '>'}));
            // handle link events
            ep.addHyperlinkListener(new HyperlinkListener()
            {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent e)
                {
                    if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                        try {
                            try {
                                Desktop.getDesktop().browse(e.getURL().toURI());
                            } catch (URISyntaxException ex) {
                            }
                        } catch (IOException ex) {
                        }
                    }
                }
            });
            ep.setEditable(false);
            ep.setBackground(new Color(0, 0, 0, 0));
            // show
            JOptionPane.showMessageDialog(null, ep, new String(new char[]{'K', 'o', 'n', 'e', 'c',
                ' ', 'p', 'l', 'a', 't', 'n', 'o', 's', 't', 'i', ' ', 'z', 'k', 'u', 'š', 'e', 'b',
                'n', 'í', ' ', 'v', 'e', 'r', 'z', 'e'}), JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        } else {
            SettingsHolder.settings.setLastTrialLaunchedTime(currentTime);
        }
        return new TransferHandler()
        {
            @Override
            public boolean importData(TransferSupport ts)
            {
                try {
                    if (canImport(ts)) {
                        java.util.List<File> fileList = GlobalFunctions.unsafeCast(
                                ts.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
                        openDiagram(fileList.get(0));
                    }
                } catch (UnsupportedFlavorException | IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }

            @Override
            public boolean canImport(TransferSupport ts)
            {
                if (ts.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    try {
                        java.util.List<File> fileList = GlobalFunctions.unsafeCast(
                                ts.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
                        if (fileList != null && fileList.size() == 1 && fileList.get(0).getName().endsWith(
                                ".xml")) {
                            return true;
                        }
                    } catch (InvalidDnDOperationException ex) {
                        return true; // z nejakeho duvodu to hazi pri dropnuti chybu, nevim proc, funguje s return true
                    } catch (UnsupportedFlavorException | IOException ex) {
                    }
                }

                return false;
            }
        };
    }

    private static JAXBContext createJAXBContext()
    {
        try {
            return JAXBContext.newInstance(Flowchart.class, LayoutSegment.class, LayoutElement.class,
                    Comment.class, Decision.class, Ellipsis.class, For.class, Goto.class,
                    GotoLabel.class, IO.class, LoopEnd.class, LoopStart.class,
                    cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.symbols.Process.class,
                    StartEnd.class, SubRoutine.class,
                    Switch.class);
        } catch (JAXBException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    private boolean checkIfSaved(boolean askAboutIt)
    {
        if (layout.getFlowchart().getMainSegment().size() > 2) { // diagram je prazdny, nema cenu ho ukladat.. even if it was not empty before
            if (SettingsHolder.settings.getActualFlowchartFile() == null) {
                if (askAboutIt) {
                    return askAboutSaving();
                } else {
                    return false;
                }
            } else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    Marshaller jAXBmarshaller = getJAXBcontext().createMarshaller();
                    jAXBmarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    jAXBmarshaller.marshal(layout.getFlowchart(), baos);
                    if (!Arrays.equals(baos.toByteArray(), Files.readAllBytes(
                            SettingsHolder.settings.getActualFlowchartFile().toPath()))) {
                        if (askAboutIt) {
                            return askAboutSaving();
                        } else {
                            return false;
                        }
                    }
                } catch (JAXBException | IOException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
        return true;
    }

    private boolean askAboutSaving()
    {
        int n = JOptionPane.showConfirmDialog(
                this,
                "<html>Diagram není uložen. Pokud jej neuložíte před jeho zavřením,<br />veškeré změny budou ztraceny.<br />Uložit nyní?</html>",
                "Diagram není uložen",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (n == JOptionPane.CANCEL_OPTION || n == JOptionPane.CLOSED_OPTION) {
            return false;
        } else if (n == JOptionPane.YES_OPTION) {
            jMenuItemSaveActionPerformed(null);
        }
        return true;
    }

    /**
     * Načte právě vygenerovaný diagram.
     * <p/>
     * @param flowchart diagram, který má být načten
     * @return true, když se načtení podařilo
     */
    public boolean openGeneratedDiagram(Flowchart<LayoutSegment, LayoutElement> flowchart)
    {
        if (!checkIfSaved(true)) {
            return false;
        }
        // prepnu do editacniho modu
        if (!editMode) {
            flowchartEditManager.actionPerformed(new ActionEvent(jButtonToolEdit,
                    jButtonToolEdit.hashCode(), "mode/editMode"));
        }

        layout.setFlowchart(flowchart);
        flowchartEditUndoManager.discardAllEdits();
        flowchartEditManager.setEditMenuEnablers();
        flowchartEditManager.loadMarkedSymbolText();
        flowchartEditManager.refreshComments();
        jPanelDiagram.repaint();

        SettingsHolder.settings.setActualFlowchartFile(null);
        super.setTitle(windowTitle);

        setStatusText("Diagram byl úspěšně vygenerován ze zdrojového kódu", 5000);
        return true;
    }

    private void openDiagram(File file)
    {
        if (!checkIfSaved(true)) {
            return;
        }
        try {
            Flowchart<LayoutSegment, LayoutElement> flowchart = GlobalFunctions.unsafeCast(
                    getJAXBcontext().createUnmarshaller().unmarshal(file));

            // prepnu do nahledoveho modu
            if (editMode) {
                flowchartEditManager.actionPerformed(new ActionEvent(jButtonToolEdit,
                        jButtonToolEdit.hashCode(), "mode/editMode"));
            } else if (animationMode) {
                flowchartAnimationManager.actionPerformed(new ActionEvent(jButtonToolAnimation,
                        jButtonToolAnimation.hashCode(), "mode/animationMode"));
            }

            layout.setFlowchart(flowchart);
            flowchartEditUndoManager.discardAllEdits();
            flowchartEditManager.setEditMenuEnablers();
            flowchartEditManager.refreshComments();
            jPanelDiagram.repaint();

            SettingsHolder.settings.setActualFlowchartFile(file);
            super.setTitle(SettingsHolder.settings.getActualFlowchartFile().getName().substring(0,
                    SettingsHolder.settings.getActualFlowchartFile().getName().length() - 4) + " - " + windowTitle);

            setStatusText(
                    "Diagram " + SettingsHolder.settings.getActualFlowchartFile().getPath() + " byl úspěšně otevřen.",
                    5000);
        } catch (JAXBException ex) {
            ex.printStackTrace(System.err);
            JOptionPane.showMessageDialog(this, "Při načítání diagramu nastala chyba!",
                    "Diagram se nepodařilo otevřít", JOptionPane.ERROR_MESSAGE);
            if (SettingsHolder.settings.getActualFlowchartFile() != null
                    && SettingsHolder.settings.getActualFlowchartFile().equals(file)) {
                SettingsHolder.settings.setActualFlowchartFile(null);
            }
        }
    }

    private ArrayList<Component> getAllComponents(Container c)
    {
        Component[] comps = c.getComponents();
        ArrayList<Component> compList = new ArrayList<>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container) {
                compList.addAll(getAllComponents((Container) comp));
            }
        }
        return compList;
    }

    /*
     * private void setEnableToSymbolButtons(boolean enabled) {
     * for (Component component: jToolBarSymbols.getComponents()) {
     * component.setEnabled(enabled);
     * }
     * }
     */
    private void loadLayoutSettings()
    {
        ArrayList<JMenuItem> menuItems = layout.getSettings();
        if (menuItems == null || menuItems.isEmpty()) {
            jMenuLayoutSetting.setEnabled(false);
        } else {
            jMenuLayoutSetting.setEnabled(true);
            for (JMenuItem menuItem : menuItems) {
                /*
                 * menuItem.addActionListener(new ActionListener() {
                 * @Override
                 * public void actionPerformed(ActionEvent ae) {
                 * if (animationMode) {
                 * layoutSettingsChanged = true;
                 * }
                 * }
                 * });
                 */
                jMenuLayoutSetting.add(menuItem);
            }
        }
    }

    private void exit()
    {
        if (!checkIfSaved(true)) {
            return;
        }
        System.exit(0);
    }

    private class JPanelDiagram extends JPanel
    {

        double translateX = 0;
        double translateY = 0;
        double scale = 1;

        @Override
        protected void paintComponent(Graphics grphcs)
        {
            super.paintComponent(grphcs);
            if (grphcs instanceof Graphics2D) {
                Graphics2D grphcs2D = (Graphics2D) grphcs;
                grphcs2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                //grphcs2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                //grphcs2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                double transX = translateX;
                double transY = translateY;
                if (transX < 0) { // TODO tohle asi budu muset odstranit, prizpusobit dragging
                    transX = 0;
                }
                if (transY < 0) {
                    transY = 0;
                }
                affineTransform = AffineTransform.getTranslateInstance(transX, transY);
                affineTransform.scale(scale, scale);
                grphcs2D.translate(transX, transY);
                grphcs2D.scale(scale, scale);
                if (animationMode) {
                    flowchartAnimationManager.paintFlowchart(grphcs2D);
                } else {
                    layout.paintFlowchart(grphcs2D, false);
                }
                Ellipse2D commentPathConnector = flowchartEditManager.getCommentPathConnector();
                if (commentPathConnector != null) {
                    float dash[] = {4, 4};
                    grphcs2D.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER, 10.0f, dash, 0));
                    grphcs2D.setColor(new Color(230, 230, 230));
                    grphcs2D.fill(commentPathConnector);
                    grphcs2D.setColor(Color.BLACK);
                    grphcs2D.draw(commentPathConnector);
                }
            } else {
                throw new Error("Parameter Graphics g is not instance of Graphics2D!");
            }
        }

        @Override
        public void setPreferredSize(Dimension dmnsn)
        {
            super.setPreferredSize(new Dimension((int) (dmnsn.width * scale + Math.abs(translateX)),
                    (int) (dmnsn.height * scale + Math.abs(translateY))));
            jPanelDiagram.revalidate();
            jScrollPaneDiagram.revalidate();
        }

        public void setTranslateX(double translateX)
        {
            if (this.translateX != translateX) {
                graphicsXTransformedByScrollbar = true;
            }
            this.translateX = translateX;
            this.setPreferredSize(new Dimension((int) layout.getWidth(), (int) layout.getHeight()));
        }

        public void setTranslateY(double translateY)
        {
            if (this.translateY != translateY) {
                graphicsYTransformedByScrollbar = true;
            }
            this.translateY = translateY;
            this.setPreferredSize(new Dimension((int) layout.getWidth(), (int) layout.getHeight()));
        }

        public double getTranslateX()
        {
            return translateX;
        }

        public double getTranslateY()
        {
            return translateY;
        }

        public void setScale(double scale)
        {
            // TODO zvetsovat k mysi, ke stredu
//            if (this.scale < scale) { // zvetsuji
//                translateX -= (layout.getWidth()*scale - layout.getWidth()*this.scale)/2;
//                translateY -= (layout.getHeight()*scale - layout.getHeight()*this.scale)/2;
//            } else if (this.scale > scale) { //zmensuji
//                translateX += (layout.getWidth()*this.scale - layout.getWidth()*scale)/2;
//                translateY += (layout.getHeight()*this.scale - layout.getHeight()*scale)/2;
//            }

            this.scale = scale;
            this.setPreferredSize(new Dimension((int) (layout.getWidth()),
                    (int) (layout.getHeight())));
        }

        public double getScale()
        {
            return scale;
        }

        /*
         * public AffineTransform getAffineTransform() {
         * return affineTransform;
         * }
         */
    }

    private class JScrollBarDiagram extends JScrollBar
    {

        private int jPanelDiagramSize;
        private int viewportSize;
        private int diagramSize;
        private int value;

        public JScrollBarDiagram(int orientation)
        {
            super(orientation);
        }

        @Override
        public void setValue(int value)
        {
            int viewportWidth = jScrollPaneDiagram.getViewport().getWidth();
            int viewportHeight = jScrollPaneDiagram.getViewport().getHeight();
            int hScrollMax = jPnlDiagram.getPreferredSize().width - viewportWidth;
            int vScrollMax = jPnlDiagram.getPreferredSize().height - viewportHeight;
            double translateX = jPnlDiagram.getTranslateX();
            double translateY = jPnlDiagram.getTranslateY();

            if (super.orientation == HORIZONTAL) {
                graphicsXTransformedByScrollbar = false;
            } else {
                graphicsYTransformedByScrollbar = false;
            }
            if (getValueIsAdjusting() || (value > 0 && ((super.orientation == HORIZONTAL && translateX == 0 && value <= hScrollMax) || (super.orientation == VERTICAL && translateY == 0 && value <= vScrollMax)))) {
                super.setValue(value);
                return;
            }

            this.value = value;
            int scrollMax;
            double translation;
            int flowchartPadding = (int) (layout.getFlowchartPadding() * getScale());

            if (super.orientation == HORIZONTAL) {
                viewportSize = viewportWidth;
                scrollMax = hScrollMax;
                translation = translateX;
                diagramSize = (int) (jPnlDiagram.getPreferredSize().width - Math.abs(translateX));
                jPanelDiagramSize = jPnlDiagram.getPreferredSize().width;
            } else {
                viewportSize = viewportHeight;
                scrollMax = vScrollMax;
                translation = translateY;
                diagramSize = (int) (jPnlDiagram.getPreferredSize().height - Math.abs(translateY));
                jPanelDiagramSize = jPnlDiagram.getPreferredSize().height;
            }

            if (value < 0 && translation < viewportSize - flowchartPadding) { // odkryvam levo/nahoru
                if (translation < 0) {
                    translation += getActualPosTranslation();
                } else {
                    translation = getActualPosTranslation();
                }
                if (translation > viewportSize - flowchartPadding) {
                    translation = viewportSize - flowchartPadding;
                }
            } else if (value > 0 && value <= scrollMax && translation != 0) { // kompenzuji
                if (translation > 0) {
                    double prevTranslation = translation;
                    translation = getActualPosTranslation();
                    if (translation < 0) {
                        translation = 0;
                    }
                    value -= prevTranslation;
                } else {
                    translation = getActualNegTranslation();
                    if (translation > 0) {
                        translation = 0;
                    }
                }
            } else if (value > 0 && translation > flowchartPadding - viewportSize) { // odkryvam pravo/dolu
                if (translation > 0 && diagramSize < viewportSize) {
                    translation = getActualPosTranslation();
                } else if (translation > 0) {
                    translation = getActualNegTranslation() + translation;
                } else {
                    translation = getActualNegTranslation();
                }

                if (translation < flowchartPadding - viewportSize) {
                    translation = flowchartPadding - viewportSize;
                }
            }

            if (super.orientation == HORIZONTAL) {
                jPnlDiagram.setTranslateX(translation);
            } else {
                jPnlDiagram.setTranslateY(translation);
            }
            jPnlDiagram.repaint();

            super.setValue(value);
        }

        private int getActualPosTranslation()
        {
            return jPanelDiagramSize - diagramSize - value;
        }

        private int getActualNegTranslation()
        {
            return diagramSize - value - viewportSize;
        }

    }

}
