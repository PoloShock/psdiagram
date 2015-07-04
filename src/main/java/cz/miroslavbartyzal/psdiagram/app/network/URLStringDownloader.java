/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.network;

import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
//***null:
//HTTP/1.1 200 OK
//***Date:
//Thu, 12 Sep 2013 00:08:36 GMT
//***Content-Length:
//2146
//***Content-Disposition:
//inline
//***Expires:
//Fri, 12 Sep 2014 00:08:36 GMT
//***Last-Modified:
//Wed, 11 Sep 2013 16:24:38 GMT
//***Content-Type:
//application/xml;charset=utf-8
//***Server:
//Apache-Coyote/1.1
//***Pragma:
//cache
//***Cache-Control:
//max-age=31536000
//private
public class URLStringDownloader extends SwingWorker<String, Void>
{

    private String status;
    private String serverURL;
    private Map<String, String> parameters;
    private DownloadFinishListener downloadFinishedListener;
    private Charset charset;

    public void sendRequest(String serverURL, Map<String, String> parameters,
            DownloadFinishListener downloadFinishedListener)
    {
        this.serverURL = serverURL;
        this.parameters = parameters;
        this.downloadFinishedListener = downloadFinishedListener;
        super.execute();
    }

    @Override
    protected String doInBackground() throws Exception
    {
        URL url;
        try {
            url = URLParser.urlConcatenation(serverURL, parameters);
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

        charset = getCharset(conn, "UTF-8");
        BufferedReader rd;
        fireStatusChanged("otevírám vstupní stream...");
        try {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
        } catch (IOException ex) {
            if (SettingsHolder.IS_DEVELOPMENT_RUN_MODE && ex instanceof java.net.ConnectException) {
                System.err.println("Nepodařilo se navázat spojení s " + url + ".");
            } else {
                ex.printStackTrace(System.err);
            }
            super.firePropertyChange("error", null, "chyba při otevírání vstupního streamu");
            return null;
        }

        StringBuilder sb = new StringBuilder();
        String line;
        fireStatusChanged("stahuji informace...");
        try {
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            super.firePropertyChange("error", null, "chyba při stahování informací");
            return null;
        }
        try {
            rd.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            super.firePropertyChange("error", null, "chyba při uzavírání streamu");
            return null;
        }

        fireStatusChanged("stahování úspěšně dokončeno");
        return sb.toString();
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
                        downloadFinishedListener.onDownloadFinished(URLStringDownloader.super.get(),
                                charset);
                    } catch (InterruptedException | ExecutionException ex) {
                        ex.printStackTrace(System.err);
                        URLStringDownloader.super.firePropertyChange("error", null, "interní chyba");
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

    private Charset getCharset(URLConnection conn, String defaultCharset)
    {
        String chs = conn.getContentEncoding();
        String header = conn.getHeaderField("Content-Type");

        if (chs == null && header != null && header.contains("charset=")) {
            chs = header.substring(header.indexOf("charset=") + 8);
        }
        if (chs == null || chs.equals("") || !Charset.isSupported(chs)) {
            chs = defaultCharset;
        }
        return Charset.forName(chs);
    }

    public interface DownloadFinishListener
    {

        public void onDownloadFinished(String result, Charset charset);

    }

}
