/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.diagram.gui;

import cz.miroslavbartyzal.psdiagram.app.update.ChangesCondenser;
import cz.miroslavbartyzal.psdiagram.app.update.Updater;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class JFrameUpdate extends javax.swing.JFrame implements PropertyChangeListener
{

    private final Updater updater;
    private String fileSize;
    private boolean downloadInProgress;
    private final Updater.BeforeExitListener beforeExitListener;

    public JFrameUpdate(Updater updater, Updater.BeforeExitListener beforeExitListener)
    {
        this.updater = updater;
        this.beforeExitListener = beforeExitListener;
        initComponents();
        jScrollPane2.getVerticalScrollBar().setUnitIncrement(10);
        jScrollPane2.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE); // prevents glitches (http://andrewtill.blogspot.cz/2012/06/jscrollpane-repainting-problems.html)
    }

    @Override
    public void setVisible(boolean visibility)
    {
        if (visibility && !super.isVisible()) {
            if (!updater.hasNewerVersion()) {
                final JFrameUpdateChild child = new JFrameUpdateChild();
                child.setLocationRelativeTo(this);
                child.setVisible(true);
                updater.loadInfo(child, new Updater.InfoLoadListener()
                {
                    @Override
                    public void onInfoLoaded(boolean newVersionAvailable)
                    {
                        if (child.isVisible()) { // = if process wasn't canceled
                            if (newVersionAvailable) {
                                child.dispose();
                                setVisible(true); // call to 'self'
                            } else {
                                child.setNoUpdateState();
                            }
                        }

                    }
                });

                return;
            } else {
                setInfo(updater.getChangesCondenser()); // all is prepared -> load it
            }
        }

        super.setVisible(visibility);
    }

    private void setInfo(ChangesCondenser condenser)
    {
        String newVersion = condenser.getTopVersion();
        String ov = cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder.PSDIAGRAM_VERSION;
        String nv = newVersion;
        if (condenser.getReleaseDates() != null) {
            if (condenser.getReleaseDates().get(ov) != null) {
                ov += " (" + condenser.getReleaseDates().get(ov).get(Calendar.DAY_OF_MONTH)
                        + "." + condenser.getReleaseDates().get(ov).get(Calendar.MONTH)
                        + "." + condenser.getReleaseDates().get(ov).get(Calendar.YEAR)
                        + ")";
            }
            if (condenser.getReleaseDates().get(nv) != null) {
                nv += " (" + condenser.getReleaseDates().get(nv).get(Calendar.DAY_OF_MONTH)
                        + "." + condenser.getReleaseDates().get(nv).get(Calendar.MONTH)
                        + "." + condenser.getReleaseDates().get(nv).get(Calendar.YEAR)
                        + ")";
            }
        }
        jLabelOldVersion.setText(ov);
        jLabelNewVersion.setText(nv);

        resetProgreess();

        if (condenser.getReleaseURLs() != null && condenser.getReleaseURLs().get(newVersion) != null) {
            final String url = condenser.getReleaseURLs().get(newVersion);
            jLabelIntro.setText("Podrobné představení nové verze:");
            jButtonWebIntro.setText(
                    "<html><font color=\"#000099\"><u>na www.psdiagram.cz</u></font></html>");
            jButtonWebIntro.setToolTipText(url);
            jButtonWebIntro.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(URI.create(url));
                        } catch (IOException ex) {
                            ex.printStackTrace(System.err);
                        }
                    } else {
                        // TODO: error handling
                    }
                }
            });
        } else {
            jLabelIntro.setText(null);
            jButtonWebIntro.setText(null);
            jButtonWebIntro.setToolTipText(null);
        }
        jButtonWebIntro.requestFocus();
        if (condenser.getHeadlines() != null && condenser.getDescriptions() != null) {
            String headline = condenser.getHeadlines().get(newVersion);
            String description = condenser.getDescriptions().get(newVersion);
            if (headline == null && description != null) {
                jLabelHeading.setText(newVersion); // set default heading if description follows
            } else if (description != null) {
                jLabelHeading.setText(headline);
            }
            jLabelDescription.setText(description);
        } else {
            jLabelHeading.setText(null);
            jLabelDescription.setText(null);
        }
        jTextPaneNews.setText(updater.getChangesHTML());

        // jPanel2 needs to be resized acording to its content (it wont resize automatically)
        jPanel2.revalidate();
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                jScrollPane2.getVerticalScrollBar().setValue(0);
            }
        });
    }

    private void resetProgreess()
    {
        jLabelSpeed.setText(" "); // don't fold layout
        jLabelSizes.setText(" "); // don't fold layout
        jProgressBar.setIndeterminate(false);
        jProgressBar.setValue(0);
        jProgressBar.setStringPainted(false);
        jProgressBar.setEnabled(true);
        jButtonDoIn.setText("Stáhnout a nainstalovat");
        jLabelStatus.setText(null);
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

        jPanel1 = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabelOldVersion = new javax.swing.JLabel();
        jLabelNewVersion = new javax.swing.JLabel();
        jButtonDoIn = new javax.swing.JButton();
        jProgressBar = new javax.swing.JProgressBar();
        jLabelStatus = new javax.swing.JLabel();
        jLabelSizes = new javax.swing.JLabel();
        jLabelSpeed = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jLabelIntro = new javax.swing.JLabel();
        jButtonWebIntro = new javax.swing.JButton();
        jLabelHeading = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jTextPaneNews = new javax.swing.JTextPane();
        jLabelDescription = new javax.swing.JLabel();

        setTitle("O apikaci PS Diagram");
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(500, 500));
        setResizable(false);
        setType(java.awt.Window.Type.UTILITY);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabelTitle.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
        jLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle.setText("Vyšla nová verze PS Diagramu! :)");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Stávající verze:");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Verze k dispozici:");

        jLabelOldVersion.setFont(new java.awt.Font("sansserif", 2, 12)); // NOI18N
        jLabelOldVersion.setText("1.0.2.5 (25.6.2013)");

        jLabelNewVersion.setText("1.1.0.3 (10.9.2013)");

        jButtonDoIn.setText("Stáhnout a nainstalovat");
        jButtonDoIn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonDoInActionPerformed(evt);
            }
        });

        jProgressBar.setValue(20);
        jProgressBar.setStringPainted(true);

        jLabelStatus.setText("chyba při stahování souboru");

        jLabelSizes.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelSizes.setText("5 KB / 2.1 MB");
        jLabelSizes.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jLabelSpeed.setText("30 KB/s");

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jLabelIntro.setText("Podrobné představení nové verze:");

        jButtonWebIntro.setText("<html>\n<FONT color=\\\"#000099\\\"><U>na www.psdiagram.cz</U></FONT>\n</html>");
        jButtonWebIntro.setAlignmentY(0.0F);
        jButtonWebIntro.setBorder(null);
        jButtonWebIntro.setBorderPainted(false);
        jButtonWebIntro.setContentAreaFilled(false);
        jButtonWebIntro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonWebIntro.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        jLabelHeading.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        jLabelHeading.setText("Ať žijí aktualizace!");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Další informace"));

        jTextPaneNews.setEditable(false);
        jTextPaneNews.setBorder(null);
        jTextPaneNews.setContentType("text/html"); // NOI18N
        jTextPaneNews.setText("<html>\n<style type=\"text/css\">\nbody { background-color:D6D9DF; }\nh2 { font-size: 19;\nmargin: 0;\nmargin-left: 2; \nmargin-top: 5; \npadding: 0;  }\nul { margin-left: 30;\nmargin-bottom: 10; }\nli { margin-bottom: 2; }\n</style>\n<h2>Nové funkce</h2>\n<ul>\n<li>automatické aktualizace</li>\n<li>další položka</li>\n</ul>\n<h2>Vylepšení</h2>\n<lu>\n<li>dlouhá položka dlouhá položka dlouhá položka dlouhá položka dlouhá položka dlouhá položka dlouhá položka dlouhá položka dlouhá položka</li>\n<li>položka</li>\n</lu>\n</html>");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextPaneNews, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextPaneNews, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jLabelDescription.setText("<html> Konečně jsme se dočkali. Nová verze PS Diagramu přináší funkci automatických aktualizací. Nejen že je aplikace schopna sama detekovat přítomnost nových verzí, umí je dokonce stahnout a sama nainstalovat.\n<br /><br />\nPS: užijte si to :)\n</html>");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabelIntro, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonWebIntro))
                    .addComponent(jLabelHeading, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(175, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelIntro)
                    .addComponent(jButtonWebIntro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelHeading)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jScrollPane2.setViewportView(jPanel3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelNewVersion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelOldVersion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonDoIn))
                            .addComponent(jProgressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabelSpeed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelSizes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTitle)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabelOldVersion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabelNewVersion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSizes)
                    .addComponent(jLabelSpeed))
                .addGap(0, 0, 0)
                .addComponent(jProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonDoIn)
                    .addComponent(jLabelStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonDoInActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonDoInActionPerformed
    {//GEN-HEADEREND:event_jButtonDoInActionPerformed
        if (!downloadInProgress) {
            resetProgreess();
            downloadInProgress = true;
            jButtonDoIn.setText("Zrušit");
            fileSize = null;
            updater.downloadAndInstallUdate(this, beforeExitListener);
            jProgressBar.setIndeterminate(true);
        } else {
            updater.cancelDownloadAndInstallUdate();
            jProgressBar.setEnabled(false);
            jButtonDoIn.setText("Stáhnout a nainstalovat");
//            resetProgreess(); (ponecham stav pro zkoumani uzivatelem)
            downloadInProgress = false;
        }

    }//GEN-LAST:event_jButtonDoInActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDoIn;
    private javax.swing.JButton jButtonWebIntro;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelDescription;
    private javax.swing.JLabel jLabelHeading;
    private javax.swing.JLabel jLabelIntro;
    private javax.swing.JLabel jLabelNewVersion;
    private javax.swing.JLabel jLabelOldVersion;
    private javax.swing.JLabel jLabelSizes;
    private javax.swing.JLabel jLabelSpeed;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JProgressBar jProgressBar;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane jTextPaneNews;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        String propertyName = evt.getPropertyName();
        String message;

        if (downloadInProgress) {
            switch (propertyName) {
                case "status":
                    message = (String) evt.getNewValue();
                    switch (message) {
                        case "stahuji...":
                            jProgressBar.setIndeterminate(false);
                            jProgressBar.setStringPainted(true);
                            break;
                        case "ověřuji kontrolní součet":
                            jLabelSpeed.setText(" "); // don't fold layout
                            jProgressBar.setStringPainted(false);
                            jProgressBar.setValue(100);
                            jProgressBar.setIndeterminate(true);
                            break;
                        default:

                            break;
                    }
                    jLabelStatus.setText(message);
                    break;
                case "speed":
                    float speed = (int) evt.getNewValue();
                    DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
                    symbols.setGroupingSeparator(' ');
                    DecimalFormat df = new DecimalFormat("###,##0.#", symbols);
                    df.setRoundingMode(RoundingMode.HALF_UP);
                    jLabelSpeed.setText(df.format(speed / 1000) + " KB/s");
                    break;
                case "currentsize":
                    long currentSize = (long) evt.getNewValue();
                    jLabelSizes.setText(getProperFileSize(currentSize));
                    if (fileSize != null && !fileSize.equals("")) {
                        jLabelSizes.setText(jLabelSizes.getText() + " / " + fileSize);
                    }
                    break;
                case "progress":
                    jProgressBar.setValue((int) evt.getNewValue());
                    break;
                case "filesize":
                    fileSize = getProperFileSize((long) evt.getNewValue());
                    jLabelStatus.setText(fileSize);
                    break;
                case "error":
                    message = (String) evt.getNewValue();
                    jLabelStatus.setText(message);
                    jProgressBar.setIndeterminate(false);
                    jProgressBar.setEnabled(false);
                    jButtonDoIn.setText("Zkusit to znovu");
                    downloadInProgress = false;
                    break;
            }
        } else if (propertyName.equals("status") && evt.getNewValue().equals("stahování zrušeno")) {
            jLabelStatus.setText((String) evt.getNewValue());
        }
    }

    private String getProperFileSize(long sizeInBytes)
    {
        if (sizeInBytes < 1000) {
            return sizeInBytes + "B";
        }
        int exp = (int) (Math.log(sizeInBytes) / Math.log(1000));
        DecimalFormat df = new DecimalFormat("0.#");
        df.setRoundingMode(RoundingMode.HALF_UP);

        return df.format(sizeInBytes / Math.pow(1000, exp)) + "KMGTPE".charAt(exp - 1) + "B";
    }

}
