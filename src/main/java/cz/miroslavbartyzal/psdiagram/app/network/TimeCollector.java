/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.network;

import cz.miroslavbartyzal.psdiagram.app.global.MyExceptionHandler;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class TimeCollector
{

    public static Date getTimeAndDate(String url)
    {
        try {
            return getTimeAndDate(new URL(url));
        } catch (MalformedURLException ex) {
            MyExceptionHandler.handle(ex);
            return null;
        }
    }

    public static Date getTimeAndDate(URL url)
    {
        try {
            URLConnection urlConn = url.openConnection();
            urlConn.setConnectTimeout(15000);
            urlConn.setReadTimeout(15000);
            urlConn.setRequestProperty("accept", "Date");
            urlConn.setRequestProperty("user-agent",
                    "psdiagram-" + cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder.PSDIAGRAM_VERSION + "(" + cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder.PSDIAGRAM_BUILD_NUMBER + ")");
            urlConn.setRequestProperty("psdiagram-version",
                    cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder.PSDIAGRAM_VERSION + "(" + cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder.PSDIAGRAM_BUILD_NUMBER + ")");
            urlConn.connect();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",
                    Locale.ENGLISH);
            return sdf.parse(urlConn.getHeaderField("Date"));
        } catch (NullPointerException | IOException | ParseException ex) {
            MyExceptionHandler.handle(ex);
        }
        return null;
    }

}
