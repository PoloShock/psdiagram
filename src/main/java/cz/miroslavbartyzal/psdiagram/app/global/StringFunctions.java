/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.global;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class StringFunctions
{

    public static String escapeHTML(String input)
    {
        return input.replaceAll("\\&", "&amp;").replaceAll("\\<", "&lt;").replaceAll("\\>", "&gt;").replaceAll(
                "\\\"", "&quot;").replaceAll("\\'", "&#x27;").replaceAll("\\/", "&#x2F;");
    }

}
