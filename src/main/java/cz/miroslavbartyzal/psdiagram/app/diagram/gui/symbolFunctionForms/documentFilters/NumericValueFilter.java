/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.diagram.gui.symbolFunctionForms.documentFilters;

/**
 * Tato třída představuje filtr numerické hodnoty.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class NumericValueFilter extends AbstractFilter
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

        if (text.matches(".*\"+.*")) { //|| text.matches("^\\[+.*")) {
            return false;
        } else {
            return ValueFilter.isValid(text);
        }
    }

}
