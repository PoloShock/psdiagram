/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters;

/**
 * Tato třída představuje filtr proměnné, která nepředstavuje pole.<br />
 * Takováto proměnná by tedy neměla obsahovat jiné znaky, než písmenné a
 * číselné, případně znak podtržítka či dolaru.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class NoArrayVariableFilter extends AbstractFilter
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

        return text.matches("^[a-zA-Z\\_\\$][\\w\\$]{0,29}") && !text.toLowerCase().matches(
                "^true$|^false$|^arguments$|^this$|^break$|^case$|^catch$|^continue$|^debugger$|^default$|^delete$|^do$|^else$|^finally$|^for$|^function$|^if$|^in$|^instanceof$|^new$|^return$|^switch$|^this$|^throw$|^try$|^typeof$|^var$|^void$|^while$|^with$");

        /*
         * if (!Character.isLetter(text.charAt(0)) || text.length() > 30) {
         * return false;
         * }
         * for (int i = 1; i < text.length(); i++) {
         * if (!Character.isLetterOrDigit(text.charAt(i))) {
         * return false;
         * }
         * }
         */
    }

}
