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
public class HTTPParser
{

    public static URL urlConcatenation(String url, Map<String, String> parameters) throws UnsupportedEncodingException, MalformedURLException
    {
        String result = url;
        if (!result.startsWith("http://")) {
            result = "http://" + result;
        }
        if (parameters != null && !parameters.isEmpty()) {
            result += "?" + parameterConcatenation(parameters);
        }
        return new URL(result);
    }

    public static String parameterConcatenation(Map<String, String> parameters) throws UnsupportedEncodingException
    {
        String result = "";
        if (parameters != null && !parameters.isEmpty()) {
            String separator = "&";
            for (Map.Entry<String, String> param : parameters.entrySet()) {
                result += separator + URLEncoder.encode(param.getKey(), "UTF-8") + "=" + URLEncoder.encode(
                        param.getValue(), "UTF-8");
            }
        }
        return result.substring(1); // cutting the first separator out
    }

}
