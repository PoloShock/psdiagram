/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.network;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class URLParser
{

    public static URL urlConcatenation(String url, Map<String, String> parameters) throws UnsupportedEncodingException, MalformedURLException
    {
        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }
        if (parameters != null && !parameters.isEmpty()) {
            String separator = "?";
//            if (!url.endsWith("/")) {
//                url += "/";
//            }
            for (String var : parameters.keySet()) {
                url += separator + URLEncoder.encode(var, "UTF-8") + "=" + URLEncoder.encode(
                        parameters.get(var), "UTF-8");
                separator = "&"; // I know, I know, but.. common :)
            }
        }
        return new URL(url);
    }

}
