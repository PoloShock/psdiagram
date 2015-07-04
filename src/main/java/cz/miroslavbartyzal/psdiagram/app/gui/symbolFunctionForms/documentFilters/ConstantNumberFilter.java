/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters;

import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.ValidationListener;
import cz.miroslavbartyzal.psdiagram.app.parser.EnumRule;
import javax.swing.JTextField;

/**
 * Tato třída představuje filtr numerické konstanty.<br />
 * Numerická konstanta může obsahovat pouze číselné hodnoty. Je možné vepsat i více
 * konstant, v takovém případě je nutné je oddělit čárkami.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class ConstantNumberFilter extends AbstractFilter
{

    private static final EnumRule RULE = EnumRule.LIST_OF_NUMERIC_CONSTANTS;

    public ConstantNumberFilter(JTextField parentJTextField, ValidationListener validationListener)
    {
        super(parentJTextField, validationListener);

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

//    public static ValidityCheckResult isValid(String input)
//    {
//        String textToVerifyOn = input.replaceFirst("^\\,|\\,$|\\,(?=\\,)", ""); // let's tolerate single empty slot (with inclusion of ",") -> example: ,1 |  1, | 1,,1
//        if (textToVerifyOn.length() == 0) {
//            return ValidityCheckResult.createValidRes(false);
//        }
//        if (textToVerifyOn.matches("^\\,.*|.*\\,\\,.*")) {
//            return ValidityCheckResult.createInvalidRes(
//                    "Detekováno příliš mnoho prázdných míst ve výčtu kontant.");
//        }
//
//        String allowedMatch = "("
//                + "((\\+(\\-\\+)*\\-?)|(\\-(\\+\\-)*\\+?))|"
//                + "(((\\+(\\-\\+)*\\-?)|(\\-(\\+\\-)*\\+?))?([1-9]|0(?=[\\.\\,]|$))[0-9]*(\\.[0-9]*)?)"
//                + ")"; // with editing convenience taken into account
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
//        String allowedMatch = "(((\\+(\\-\\+)*\\-?)|(\\-(\\+\\-)*\\+?))?([1-9]|0(?=[\\.\\,]|$))[0-9]*(\\.[0-9]+)?)"; // without editing convenience
//        return input.matches(allowedMatch + "(\\," + allowedMatch + ")*"); // without editing convenience
//    }
}
