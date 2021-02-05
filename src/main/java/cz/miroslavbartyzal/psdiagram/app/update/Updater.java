/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.update;

import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import cz.miroslavbartyzal.psdiagram.app.network.URLFileDownloader;
import cz.miroslavbartyzal.psdiagram.app.network.URLStringDownloader;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import javax.swing.SwingUtilities;
import jakarta.xml.bind.JAXBException;

/**
 *
 * @author Miroslav Bartyzal
 */
public final class Updater
{

    private static final String ARCHIVE_NAME = "update.zip";
    private static final String UPDATER_FILENAME = "updater.jar";
    private static final String[] UPDATER_FILES_TO_REMOVE = new String[]{"updater.bat",
        "dirfootprint.xml"};
    private String changesHTML;
    private ChangesCondenser changesCondenser;
    private URLFileDownloader uRLFileDownloader;

    public void loadInfo(final PropertyChangeListener statusListener,
            final InfoLoadListener infoLoadListener)
    {
        new Thread(() -> {
            cleanAfterSelf();
            HashMap<String, String> vars = new HashMap<>();
            vars.put("since",
                    cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder.PSDIAGRAM_VERSION);
            URLStringDownloader uRLStringDownloader = new URLStringDownloader();
            uRLStringDownloader.addPropertyChangeListener(statusListener);
            uRLStringDownloader.sendRequest(SettingsHolder.PSDIAGRAM_SERVER + "/versioninfo",
                    vars, new URLStringDownloader.DownloadFinishListener()
            {
                @Override
                public void onDownloadFinished(final String result,
                        final Charset charset)
                {
                    new Thread(() -> {
                        final Boolean newVersionAvailable;
                        if (result == null) {
                            newVersionAvailable = null; // means we did not reach the server
                        } else {
                            if (!result.isEmpty()) {
                                try {
                                    changesCondenser = (ChangesCondenser) JAXBUpdateContext.getUnmarshaller().unmarshal(
                                            new ByteArrayInputStream(result.getBytes(charset)));
                                } catch (JAXBException ex) {
                                    ex.printStackTrace(System.err);
                                    statusListener.propertyChange(
                                            new PropertyChangeEvent(this, "error", null, "chyba při konverzi xml"));
                                }
                                if (changesCondenser != null) {
                                    changesHTML = CondenserToHTMLConverter.convertToHTML(changesCondenser, charset);
                                }
                            }
                            newVersionAvailable = hasNewerVersion();
                        }
                        SwingUtilities.invokeLater(() -> {
                            infoLoadListener.onInfoLoaded(newVersionAvailable);
                        });
                    }).start();
                }
            });
        }).start();
    }

    public void downloadAndInstallUdate(final PropertyChangeListener statusListener,
            final BeforeExitListener beforeExitListener) throws IllegalStateException
    {
        if (!hasNewerVersion()) {
            throw new IllegalStateException(
                    "Can't init download without any version info. Please invoke loadInfo method first.");
        }
        HashMap<String, String> vars = new HashMap<>();
        vars.put("version", getVersionAvailable());
        uRLFileDownloader = new URLFileDownloader();
        uRLFileDownloader.addPropertyChangeListener(statusListener);
        uRLFileDownloader.sendRequest(SettingsHolder.PSDIAGRAM_SERVER + "/download", vars,
                SettingsHolder.WORKING_DIR, ARCHIVE_NAME,
                new URLFileDownloader.DownloadFinishListener()
        {
            @Override
            public void onDownloadFinished(File downloadedFile)
            {
                if (downloadedFile != null) {
                    statusListener.propertyChange(new PropertyChangeEvent(this, "status",
                            null,
                            "ověřuji kontrolní součet"));
                    HashMap<String, String> vars = new HashMap<>();
                    vars.put("checksum", getVersionAvailable());
                    vars.put("alg", "MD5");
                    URLStringDownloader uRLStringDownloader = new URLStringDownloader();
                    uRLStringDownloader.sendRequest(
                            SettingsHolder.PSDIAGRAM_SERVER + "/download",
                            vars, new ChecksumCheck(downloadedFile, statusListener,
                                    beforeExitListener)); // let the rest of it on ChecksumCheck
                }
            }
        });
    }

    public void cancelDownloadAndInstallUdate()
    {
        uRLFileDownloader.cancel(false);
    }

    public String getVersionAvailable()
    {
        if (changesCondenser != null) {
            return changesCondenser.getTopVersion();
        }
        return null;
    }

    public String getChangesHTML()
    {
        return changesHTML;
    }

    public boolean hasNewerVersion()
    {
        // changesCondenser is downloaded only if there is a new version, but let's not trust the server... :)
        return changesCondenser != null && ChangesCondenser.parseVersion(getVersionAvailable()) > ChangesCondenser.parseVersion(
                cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder.PSDIAGRAM_VERSION);
    }

    public ChangesCondenser getChangesCondenser()
    {
        return changesCondenser;
    }

    private void launchUpdate(File downloadedFile, final PropertyChangeListener statusListener,
            final BeforeExitListener beforeExitListener)
    {
        statusListener.propertyChange(new PropertyChangeEvent(this, "status", null,
                "extrahuji..."));
        File extractedDir;
        try {
            extractedDir = ArchiveUtil.extractZIP(downloadedFile, true,
                    Charset.defaultCharset());
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            statusListener.propertyChange(new PropertyChangeEvent(this, "error", null,
                    "chyba při extrahování instalátoru"));
            return;
        } catch (IllegalArgumentException ex) { // let's try it once more with UTF-8
            try {
                extractedDir = ArchiveUtil.extractZIP(downloadedFile, true,
                        StandardCharsets.UTF_8);
            } catch (IOException | IllegalArgumentException ex2) {
                ex.printStackTrace(System.err);
                statusListener.propertyChange(new PropertyChangeEvent(this, "error", null,
                        "chyba při extrahování instalátoru"));
                return;
            }
        }
        downloadedFile.delete();

        statusListener.propertyChange(new PropertyChangeEvent(this, "status", null,
                "připravuji spuštění instalace..."));
        final File updaterFile = new File(extractedDir, UPDATER_FILENAME);
        if (!updaterFile.exists()) {
            statusListener.propertyChange(new PropertyChangeEvent(this, "error", null,
                    "chyba: nenalezen instalátor"));
            return;
        }
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                beforeExitListener.onBeforeExit();

                // launch it
                try {
                    if (SettingsHolder.JAVAW == null) {
                        String command = "start \"updater\" /d \"" + updaterFile.getParentFile().getAbsolutePath() + "\""
                                + " \"" + updaterFile.getAbsolutePath() + "\""
                                + " -psdir \"" + SettingsHolder.MY_DIR.getAbsolutePath() + "\"";

                        Runtime.getRuntime().exec(new String[]{"cmd", "/s", "/c",
                            "\"" + command + "\""});
                    } else {
                        String command = "start \"updater\" /d \"" + updaterFile.getParentFile().getAbsolutePath() + "\""
                                + " \"" + SettingsHolder.JAVAW.getAbsolutePath() + "\""
                                + " -jar \"" + updaterFile.getAbsolutePath() + "\""
                                + " -psdir \"" + SettingsHolder.MY_DIR.getAbsolutePath() + "\"";

                        Runtime.getRuntime().exec(new String[]{"cmd", "/s", "/c",
                            "\"" + command + "\""}, null, updaterFile.getParentFile());
                    }

                } catch (IOException ex) {
                    ex.printStackTrace(System.err);
                    statusListener.propertyChange(new PropertyChangeEvent(this, "error", null,
                            "chyba při spouštění instalátoru"));
                    return;
                }

                System.exit(0);
            }
        });
    }

    private void cleanAfterSelf()
    {
        try {
            new File(SettingsHolder.WORKING_DIR, ARCHIVE_NAME).delete();
            new File(SettingsHolder.WORKING_DIR, UPDATER_FILENAME).delete();
            for (String str : UPDATER_FILES_TO_REMOVE) {
                new File(SettingsHolder.WORKING_DIR, str).delete();
            }
            ArchiveUtil.delete(new File(SettingsHolder.WORKING_DIR, ARCHIVE_NAME.substring(
                    0, ARCHIVE_NAME.length() - 4)));
            ArchiveUtil.delete(new File(SettingsHolder.WORKING_DIR, "oldjre"));
        } catch (IOException ex) {
        }
    }

    /**
     * I don't want the checksum HTTP request to freeze our EDT, so the rest of the update process is handled here.
     */
    private class ChecksumCheck implements URLStringDownloader.DownloadFinishListener
    {

        private final File downloadedFile;
        private final PropertyChangeListener statusListener;
        private final BeforeExitListener beforeExitListener;

        public ChecksumCheck(File downloadedFile, PropertyChangeListener statusListener,
                BeforeExitListener beforeExitListener)
        {
            this.downloadedFile = downloadedFile;
            this.statusListener = statusListener;
            this.beforeExitListener = beforeExitListener;
        }

        @Override
        public void onDownloadFinished(final String result, final Charset charset)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    String checksum = FileChecksum.getMD5Checksum(downloadedFile);
                    if (result == null || checksum == null) {
                        statusListener.propertyChange(new PropertyChangeEvent(this, "error", null,
                                "chyba: nepodařilo se ověřit kontrolní součet"));
                        return;
                    }
                    if (!result.equals(checksum)) {
                        statusListener.propertyChange(new PropertyChangeEvent(this, "error", null,
                                "kontrolní součet nesouhlasí"));
                        return;
                    }

                    statusListener.propertyChange(new PropertyChangeEvent(this, "status", null,
                            "kontrolní součet ověřen"));
                    launchUpdate(downloadedFile, statusListener, beforeExitListener);
                }
            }).start();
        }

    }

    public interface InfoLoadListener
    {

        public void onInfoLoaded(Boolean newVersionAvailable);

    }

    public interface BeforeExitListener
    {

        public void onBeforeExit();

    }

}
