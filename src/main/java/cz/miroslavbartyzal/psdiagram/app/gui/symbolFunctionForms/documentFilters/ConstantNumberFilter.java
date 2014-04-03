/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters;

import java.util.regex.Pattern;

/**
 * Tato třída představuje filtr numerické konstanty.<br />
 * Numerická konstanta může obsahovat pouze číselné. Je možné vepsat i více
 * konstant, v takovém případě je nutné je oddělit čárkami.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class ConstantNumberFilter extends AbstractFilter
{

    @Override
    boolean validMe(String text)
    {
        return isValid(text);
    }

    protected static boolean isValid(String text)
    {
        if (text.length() == 0) {
            return true;
        }

        if (!text.matches("^[0-9\\-]+[^\'\"]*")) { // prvni musi byt num, nebo minus;   retezec nesmi obsahovat znak ',"
            return false;
        }

        Pattern allowedChars = Pattern.compile("[^0-9\\-\\.]");
        Pattern doubles = Pattern.compile("\\-{2,}"
                + "|\\.{2,}"
                + "|[0-9]\\-"
                + "|[^0-9]\\.");

        if (allowedChars.matcher(text).find() || doubles.matcher(text).find()) {
            return false;
        }
        return true;
    }

}
