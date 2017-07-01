/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class URLPostUploader extends SwingWorker<Integer, Void>
{

    private String status;
    private String serverURL;
    private Map<String, String> parameters;
    private UploadFinishListener uploadFinishedListener;

    public void sendRequest(String serverURL, Map<String, String> parameters,
            UploadFinishListener uploadFinishedListener)
    {
        this.serverURL = serverURL;
        this.parameters = parameters;
        this.uploadFinishedListener = uploadFinishedListener;
        super.execute();
    }

    @Override
    protected Integer doInBackground() throws Exception
    {
        URL url;
        try {
            url = HTTPParser.urlConcatenation(serverURL, null);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace(System.err);
            super.firePropertyChange("error", null, "chyba: špatné kódování URL");
            return null;
        } catch (MalformedURLException ex) {
            ex.printStackTrace(System.err);
            super.firePropertyChange("error", null, "chyba: špatný formát URL");
            return null;
        }

        HttpURLConnection conn;
        fireStatusChanged("navazuji spojení...");
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            super.firePropertyChange("error", null, "chyba při navazování spojení");
            return null;
        }
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);

        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;
        conn.setFixedLengthStreamingMode(length);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        try {
            conn.connect();
            fireStatusChanged("otevírám výstupní stream...");
            try (OutputStream os = conn.getOutputStream()) {
                fireStatusChanged("odesílám informace...");
                os.write(out);
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            super.firePropertyChange("error", null, "chyba při odesílání informací");
            return null;
        }

        int httpResponseCode = conn.getResponseCode();

        fireStatusChanged("odeslání úspěšně dokončeno");
        return httpResponseCode;
    }

    private void sendFile(OutputStream out, String name, InputStream in, String fileName) throws UnsupportedEncodingException, IOException
    {
        String o = "Content-Disposition: form-data; name=\"" + URLEncoder.encode(name, "UTF-8")
                + "\"; filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"\r\n\r\n";
        out.write(o.getBytes(StandardCharsets.UTF_8));
        byte[] buffer = new byte[2048];
        for (int n = 0; n >= 0; n = in.read(buffer)) {
            out.write(buffer, 0, n);
        }
        out.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    private void sendField(OutputStream out, String name, String field) throws UnsupportedEncodingException, IOException
    {
        String o = "Content-Disposition: form-data; name=\""
                + URLEncoder.encode(name, "UTF-8") + "\"\r\n\r\n";
        out.write(o.getBytes(StandardCharsets.UTF_8));
        out.write(URLEncoder.encode(field, "UTF-8").getBytes(StandardCharsets.UTF_8));
        out.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    private void fireStatusChanged(String newStatus)
    {
        super.firePropertyChange("status", status, newStatus);
        status = newStatus;
    }

    @Override
    protected void done()
    {
        if (uploadFinishedListener != null && !super.isCancelled()) {
            SwingUtilities.invokeLater(() -> {
                try {
                    uploadFinishedListener.onUploadFinished(URLPostUploader.super.get());
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace(System.err);
                    URLPostUploader.super.firePropertyChange("error", null, "interní chyba");
                    uploadFinishedListener.onUploadFinished(null);
                }
            });

        }
    }

    public interface UploadFinishListener
    {

        public void onUploadFinished(Integer httpResponseCode);

    }

}
