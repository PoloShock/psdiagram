/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters;

import cz.miroslavbartyzal.psdiagram.app.global.RegexFunctions;

/**
 * <p>Tato třída představuje filtr proměnné.</p>
 *
 * <p>Proměnná může začínat pouze písmenem, podtržítkem, nebo znakem dolaru.
 * Další znaky již mohou obsahovat i číslice.<br />
 * Proměnná může představovat i pole, v tom případě bude obsahovat i hranaté
 * závorky, v nichž bude zapsána hodnota indexu pole.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class VariableFilter extends AbstractFilter
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

        String[] onlyVariable = text.split("\\[.*");//Pattern.compile("\\[.*").split(text);
        if (onlyVariable.length == 0) {
            return false; // neni zde promenna
        }
        if (!NoArrayVariableFilter.isValid(onlyVariable[0])) { // promenna musi odpovidat promenne (cast za pripadnym [ se nepocita)
            return false;
        }
        String[] onlyBrackets = text.split("^[a-zA-Z\\_\\$][\\w\\$]{0,29}");//Pattern.compile("^[a-zA-Z\\_\\$][\\w\\$]{0,29}").split(text); // cast za [
        if (onlyBrackets.length == 0) {
            return true; // nejedna se o promennou pole
        }

        for (int i = 1; i < onlyBrackets.length; i++) {
            onlyBrackets[0] += onlyBrackets[i];
        }

        String[] insides = RegexFunctions.varBracketsInsides(onlyBrackets[0]);//onlyBrackets[0].replaceFirst("^\\[", "").replaceFirst("\\]$", ""); //[uvnitr plati numeric hodnoty]
        for (int i = 1; i < insides.length; i += 2) {
            if (!NumericValueFilter.isValid(insides[i])) {
                return false;
            }
        }

        return checkBrackets(onlyBrackets[0]);
    }

    private static boolean checkBrackets(String text)
    {
        int left = text.length() - text.replaceAll("\\[", "").length();
        int right = text.length() - text.replaceAll("\\]", "").length();
        if (left < right || (left == right && !text.matches(".*\\]$"))) {// || (brackets > right && right > 0 && text.matches(".*\\[$"))) { nakonec toto nenechavam, pridavam tak podporu vicerozmernych poli
            return false;
        } else {
            return true;
        }
    }

}
