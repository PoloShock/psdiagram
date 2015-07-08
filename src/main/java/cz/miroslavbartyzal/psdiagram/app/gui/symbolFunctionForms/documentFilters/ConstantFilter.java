/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters;

import cz.miroslavbartyzal.psdiagram.app.gui.balloonToolTip.MaxBalloonSizeCallback;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.ValidationListener;
import cz.miroslavbartyzal.psdiagram.app.parser.EnumRule;
import javax.swing.JTextField;

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

    private static final EnumRule RULE = EnumRule.LIST_OF_CONSTANTS;

    public ConstantFilter(JTextField parentJTextField, ValidationListener validationListener,
            MaxBalloonSizeCallback maxBalloonSizeCallback)
    {
        super(parentJTextField, validationListener, maxBalloonSizeCallback);

        if (!parentJTextField.getText().isEmpty()) {
            super.parseInputAndUpdateGUI(parentJTextField.getText());
        }
    }

    @Override
    EnumRule getRule()
    {
        return RULE;
    }

    /**
     * Pokusí se daný textový řeťezec porovnat vůči tomuto filtru.
     *
     * @param input text, který chceme prověřit
     * @return
     */
    public static boolean isValid(String input)
    {
        return AbstractFilter.parseInput(input, RULE);
    }

//    /**
//     * Pokusí se daný textový řeťezec porovnat vůči tomuto filtru.
//     * <p/>
//     * @param input text, který chceme prověřit
//     * @return
//     */
//    public static ValidityCheckResult isValid(String input)
//    {
//        if (input.contains("'")) { // retezec nesmi obsahovat znak '
//            return ValidityCheckResult.createInvalidRes("Znak ' není povolen.");
//        }
//        String textToVerifyOn = input.replaceAll("\"([^\"\\\\]|\\\\.)*\"?", "\"\"").replaceFirst(
//                "^\\,|\\,$|\\,(?=\\,)", ""); // let's tolerate single empty slot -> example: ,1 |  1, | 1,,1
//        if (textToVerifyOn.length() == 0) {
//            return ValidityCheckResult.createValidRes(false);
//        }
//        if (textToVerifyOn.matches("^\\,.*|.*\\,\\,.*")) {
//            return ValidityCheckResult.createInvalidRes(
//                    "Detekováno příliš mnoho prázdných míst ve výčtu kontant.");
//        }
//
//        // I'm allowing mixed strings and numbers after all since JavaScipt is tolerant too
//        String allowedMatch = "("
//                + "((\\+(\\-\\+)*\\-?)|(\\-(\\+\\-)*\\+?))|"
//                + "(((\\+(\\-\\+)*\\-?)|(\\-(\\+\\-)*\\+?))?([1-9]|0(?=[\\.\\,]|$))[0-9]*(\\.[0-9]*)?)"
//                + ")"; // with editing convenience taken into account
//        allowedMatch = allowedMatch + "|(\"\")";
//        allowedMatch = allowedMatch + "|(t(r(ue?)?)?|f(a(l(se?)?)?)?)";
//        allowedMatch = "(" + allowedMatch + ")";
//
//        boolean result = textToVerifyOn.matches(allowedMatch + "(\\," + allowedMatch + ")*\\,?"); // with editing convenience taken into account
//        if (result) {
//            return ValidityCheckResult.createValidRes(canBeLeftAsIs(input));
//        } else {
//            if (textToVerifyOn.contains("--") || textToVerifyOn.contains("++")) {
//                return ValidityCheckResult.createInvalidRes(
//                        "Zdvojené znaménko není povoleno.");
//            } else {
//                return ValidityCheckResult.createInvalidRes(
//                        "Neplatná konstantní hodnota.");
//            }
//        }
//    }
//
//    private static boolean canBeLeftAsIs(String input)
//    {
////        // create new textToVerifyOn in order to reveal any quote related issues
////        textToVerifyOn = input.replaceAll("\"[^\"]*\"", "\"\""); // replace only enclosed quotes
////        textToVerifyOn = textToVerifyOn.replaceAll("(?<!\")\"(?![^\"]+\")[^\"]+", "\""); // replace only unenclosed quotes
//
//        int quotesCount = input.length() - input.replaceAll("\"", "").length();
//        if (quotesCount % 2 == 1) {
//            return false;
//        }
//
//        String textToVerifyOn = input.replaceAll("\"([^\"\\\\]|\\\\.)*\"?", "\"\"");
//        String allowedMatch = "(((\\+(\\-\\+)*\\-?)|(\\-(\\+\\-)*\\+?))?([1-9]|0(?=[\\.\\,]|$))[0-9]*(\\.[0-9]+)?)"; // without editing convenience
//        allowedMatch = allowedMatch + "|(\"\")";
//        allowedMatch = allowedMatch + "|(true|false)";
//        allowedMatch = "(" + allowedMatch + ")";
//
//        return textToVerifyOn.matches(allowedMatch + "(\\," + allowedMatch + ")*"); // without editing convenience
//    }
}
