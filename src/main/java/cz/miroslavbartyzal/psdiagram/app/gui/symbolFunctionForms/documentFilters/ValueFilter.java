/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters;

import java.util.regex.Pattern;

/**
 * <p>Tato třída představuje filtr obecné hodnoty.</p>
 *
 * <p>Může se jednat o hodnotu dosazovanou do proměnné, či o hodnotu použitou
 * jako vyhodnocovací podmíněný výraz. Tento filtr je nejkomplexnější ze všech
 * ostatních.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class ValueFilter extends AbstractFilter
{

    @Override
    boolean validMe(String text)
    {
        return isValid(text);
    }

    /**
     * Pokusí se daný textový řeťezec porovnat vůči tomuto filtru.
     *
     * @param text text, který chceme prověřit
     * @return true, prošel-li text tímto filtrem
     */
    public static boolean isValid(String text)
    {
        if (text.length() == 0) {
            return true;
        }

        if (!text.matches("^[\\w\\$\"\\-\\[\\(\\!]+[^\']*")) { // prvni musi byt alfanum, nebo uvozovky, nebo minus, nebo _, nebo $, nebo ( nebo [; retezec nesmi obsahovat znak '
            return false;
        }
        String withoutQuotes = text.replaceAll("\"[^\"]*\"?", "\"");
        if (withoutQuotes.length() == 0) {
            return true;
        }

        if (withoutQuotes.contains(",")) {
            String withoutBrackets = withoutQuotes;
            while (withoutBrackets.matches(".*\\[[^\\[\\]]*\\].*")) {
                withoutBrackets = withoutBrackets.replaceAll("\\[[^\\[\\]]*\\]", "");
            }

            if (withoutBrackets.contains(",")) {
                while (withoutBrackets.matches(
                        "[^0-9]*([a-zA-Z\\_\\$][\\w\\$]*\\.?)+\\([^\\(\\)]*\\).*")) {
                    withoutBrackets = withoutBrackets.replaceAll(
                            "([a-zA-Z\\_\\$][\\w\\$]*\\.?)+\\([^\\(\\)]*\\)", "");
                }

                if (withoutBrackets.indexOf(",") != -1
                        && ((withoutBrackets.indexOf("[") == -1 || withoutBrackets.indexOf("[") > withoutBrackets.indexOf(
                        ","))
                        && (!withoutBrackets.matches(
                        "[^0-9]*([a-zA-Z\\_\\$][\\w\\$]*\\.?)+\\([^\\(\\)]*\\)?.*") || withoutBrackets.indexOf(
                        "(") > withoutBrackets.indexOf(",")))) {
                    return false; // carka se muze vyskytovat jen uvnitr hranatych zavorek - pri inicializaci pole a ve volani fce
                }
            }
        }

        String op = "\\s\\(\\)\\+\\-\\/\\*\\%\\=\\>\\<\\!\\&\\|";
        Pattern allowedChars = Pattern.compile(
                "[^\\w\\$\\s\\(\\)\\[\\]\\.\\+\\-\\/\\*\\%\\=\\>\\<\\!\\&\\|\"\\,]");
        Pattern doubles = Pattern.compile("([\\.\\+\\/\\*\\%\\=\\&\\|\\,][\\s]*){2,}"
                + "|([\\.\\+\\-\\/\\*\\%\\&\\|\\,][\\s]*){2,}"
                + "|\\-\\s*\\="
                + "|([\\.\\+\\-\\/\\*\\%\\&\\|\\>\\<\\,][\\s]*){2,}"
                + "|([\\!\\.\\%\\>\\<\\,][\\s]*){2,}"
                + "|\\!\\s"
                + "|\\=[\\>\\<]"
                + "|[^\\w\\$\\[\\]\"][^a-zA-Z\\_\\$" + op + "][\\w\\$]*\\["
                + "|^[^\\-a-zA-Z\\_\\$\\[\\]][\\w\\$]*\\["
                + "|[^\\w\\$\\[\\]\\," + op + "]\\[" //pole
                + "|\\s{2,}");

        if (allowedChars.matcher(withoutQuotes).find() || doubles.matcher(withoutQuotes).find()) {
            return false;
        }
        int left = withoutQuotes.length() - withoutQuotes.replaceAll("\\[", "").length();
        int right = withoutQuotes.length() - withoutQuotes.replaceAll("\\]", "").length();
        if (left < right) {
            return false;
        } else {
            return true;
        }

        /*
         * Pattern pattern =
         * Pattern.compile("([\\w\\s\\(\\)\\[\\]\\.\\+\\-\\/\\*\\%]"
         * + "|"
         * + "(\"[^\"]*\"?))+");
         * Matcher matcher = pattern.matcher(text);
         * if (matcher.matches()) {
         * return matcher.replaceAll(text).equals(text);
         * } else {
         * return false;
         * }
         */

        /*
         * String group = "";
         * if (matcher.matches()) {
         * group = matcher.group();
         * } else {
         *
         * }
         * String replaceAll = matcher.replaceAll(text);
         * System.out.println("ReplaceAll: " + replaceAll + ".");
         * System.out.println("Group: " + group + ".");
         * System.out.println("Text: " + text + ".");
         * return replaceAll.equals(text);
         */
    }

}
