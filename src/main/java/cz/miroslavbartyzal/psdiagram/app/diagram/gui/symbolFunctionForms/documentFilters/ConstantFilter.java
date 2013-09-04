/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.diagram.gui.symbolFunctionForms.documentFilters;

import java.util.regex.Pattern;

/**
 * Tato třída představuje filtr konstanty.<br />
 * Konstanta může obsahovat pouze číselné, nebo řetězcové údaje ohraničené
 * uvozovkami. Je možné vepsat i více konstant, v takovém případě je nutné je
 * oddělit čárkami.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class ConstantFilter extends AbstractFilter
{

    @Override
    boolean validMe(String text)
    {
        return isValid(text);
    }

    /**
     * Pokusí se daný textový řeťezec porovnat vůči tomuto filtru.
     * <p/>
     * @param text text, který chceme prověřit
     * @return true, prošel-li text tímto filtrem
     */
    public static boolean isValid(String text)
    {
        if (text.length() == 0) {
            return true;
        }

        if (!text.matches("(^[0-9\\-]+[^\'\"]*)|(^[\"]+[^\']*)")) { // prvni musi byt num, minus, nebo uvozovky;   retezec nesmi obsahovat znak '
            return false;
        }

        String withoutQuotes = text.replaceAll("\"[^\"]*\"?", "\"");
        if (withoutQuotes.length() == 0) {
            return true;
        }

        boolean quotes = false;
        Pattern allowedChars;
        Pattern doubles;
        if (text.matches(".*\"+.*")) { // jestlize jsou obsazeny "
            allowedChars = Pattern.compile("[^\\,\"]");
            quotes = true;
        } else {
            allowedChars = Pattern.compile("[^0-9\\-\\,\\.]");
        }
        doubles = Pattern.compile("\\,{2,}"
                + "|\\-{2,}"
                + "|\\.{2,}"
                + "|[0-9]\\-"
                + "|[^0-9]\\.");

        if (quotes && allowedChars.matcher(withoutQuotes).find() || doubles.matcher(withoutQuotes).find()) {
            return false;
        }
        if (!quotes && allowedChars.matcher(withoutQuotes).find() || doubles.matcher(withoutQuotes).find()) {
            return false;
        }
        return true;
    }

}
