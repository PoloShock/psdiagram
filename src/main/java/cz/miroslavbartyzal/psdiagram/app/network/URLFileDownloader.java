/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.network;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
//***null:
//HTTP/1.1 200 OK
//***Date:
//Thu, 12 Sep 2013 14:31:22 GMT
//***Content-Length:
//2654459
//***Content-Disposition:
//attachment; filename="PS_Diagram_1.0.5.1.zip"
//***Expires:
//Fri, 12 Sep 2014 14:31:22 GMT
//***Last-Modified:
//Sat, 31 Aug 2013 21:42:58 GMT
//***Content-Type:
//application/zip
//***Server:
//Apache-Coyote/1.1
//***Pragma:
//cache
//***Cache-Control:
//max-age=31536000
//private
public class URLFileDownloader extends SwingWorker<File, Void>
{

    private static final int BUFFER_SIZE = 1024;
    private String status;
    private String serverURL;
    private Map<String, String> parameters;
    private File downloadFolder;
    private String fileName;
    private DownloadFinishListener downloadFinishedListener;
    private final Timer tmrSpeed;
    private long timerData = 0;
    private long downloadedDataSize;

    public URLFileDownloader()
    {
        tmrSpeed = new Timer(1000, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                riseBandwithInfo();
            }
        });
    }

    private void riseBandwithInfo()
    {
        long currentData = downloadedDataSize;
        firePropertyChange("speed", null, (int) (currentData - timerData));
        timerData = currentData;
        firePropertyChange("currentsize", null, currentData);
    }

    public void sendRequest(String serverURL, Map<String, String> parameters, File downloadFolder,
            String fileName, DownloadFinishListener downloadFinishedListener)
    {
        this.serverURL = serverURL;
        this.parameters = parameters;
        this.downloadFolder = downloadFolder;
        this.fileName = fileName;
        this.downloadFinishedListener = downloadFinishedListener;
        super.execute();
    }

    @Override
    protected File doInBackground() throws Exception
    {
        URL url;
        try {
            url = HTTPParser.urlConcatenation(serverURL, parameters);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace(System.err);
            super.firePropertyChange("error", null, "chyba: špatné kódování URL");
            return null;
        } catch (MalformedURLException ex) {
            ex.printStackTrace(System.err);
            super.firePropertyChange("error", null, "chyba: špatný formát URL");
            return null;
        }

        URLConnection conn;
        fireStatusChanged("navazuji spojení...");
        try {
            conn = url.openConnection();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            super.firePropertyChange("error", null, "chyba při navazování spojení");
            return null;
        }
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);

        long filesize = conn.getContentLengthLong();
        if (filesize > 0) {
            super.firePropertyChange("filesize", null, filesize);
        }

        if (!downloadFolder.exists()) {
            downloadFolder.mkdirs();
        }

        if (fileName == null) {
            fileName = getFilename(conn, "downloadedfile");
        }
        File file = new File(downloadFolder, fileName);
        if (file.exists()) {
            if (!file.delete()) {
                super.firePropertyChange("error", null,
                        "chyba: soubor \"" + file.getName() + "\" již existuje a nelze smazat");
                return null;
            }
        }

        FileOutputStream fos = null;
        BufferedOutputStream bout = null;
        BufferedInputStream rd = null;
        try {
            fos = new java.io.FileOutputStream(file);
            bout = new BufferedOutputStream(fos, BUFFER_SIZE);
            byte data[] = new byte[BUFFER_SIZE];
            int chunkSize = 0;
            downloadedDataSize = 0;

            fireStatusChanged("otevírám vstupní stream...");
            try {
                rd = new BufferedInputStream(conn.getInputStream());
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
                super.firePropertyChange("error", null, "chyba při otevírání vstupního streamu");
                return null;
            }

            tmrSpeed.start();
            fireStatusChanged("stahuji...");

            while (!super.isCancelled() && (chunkSize = rd.read(data, 0, BUFFER_SIZE)) != -1) {
                try {
                    bout.write(data, 0, chunkSize);
                } catch (IOException ex) {
                    ex.printStackTrace(System.err);
                    tmrSpeed.stop();
                    riseBandwithInfo();
                    super.firePropertyChange("error", null, "chyba při stahování souboru");
                    return null;
                }

                downloadedDataSize += chunkSize;
                int progress = (int) (downloadedDataSize * 100 / filesize);
                if (super.getProgress() != progress) {
                    super.setProgress(progress);
                }
            }
        } finally {
            try {
                if (bout != null) {
                    bout.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (rd != null) {
                    rd.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
                tmrSpeed.stop();
                riseBandwithInfo();
                super.firePropertyChange("error", null, "chyba při uzavírání streamu");
                return null;
            }
        }

        tmrSpeed.stop();
        riseBandwithInfo();
        if (super.isCancelled()) {
            file.delete();
            fireStatusChanged("stahování zrušeno");
            return null;
        }
        fireStatusChanged("stahování úspěšně dokončeno");
        return file;
    }

    @Override
    protected void done()
    {
        if (downloadFinishedListener != null && !super.isCancelled()) {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    try {
                        downloadFinishedListener.onDownloadFinished(URLFileDownloader.super.get());
                    } catch (InterruptedException | ExecutionException ex) {
                        ex.printStackTrace(System.err);
                        URLFileDownloader.super.firePropertyChange("error", null, "interní chyba");
                        downloadFinishedListener.onDownloadFinished(null);
                    }
                }
            });

        }
    }

    private void fireStatusChanged(String newStatus)
    {
        super.firePropertyChange("status", status, newStatus);
        status = newStatus;
    }

    private String getFilename(URLConnection conn, String defaultname)
    {
        String filename = null;
        String header = conn.getHeaderField("Content-Disposition");

        if (header != null && header.contains("filename=")) {
            filename = header.substring(header.indexOf("filename=") + 9);
        }
        if (filename == null || filename.equals("")) {
            filename = defaultname;
        }
        return filename;
    }

    public interface DownloadFinishListener
    {

        public void onDownloadFinished(File downloadedFile);

    }

}
