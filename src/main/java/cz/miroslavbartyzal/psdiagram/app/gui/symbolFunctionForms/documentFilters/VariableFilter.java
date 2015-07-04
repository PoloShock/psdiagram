/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters;

import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.ValidationListener;
import cz.miroslavbartyzal.psdiagram.app.parser.EnumRule;
import javax.swing.JTextField;

/**
 * <p>
 * Tato třída představuje filtr proměnné.</p>
 *
 * <p>
 * Proměnná může začínat pouze písmenem, podtržítkem, nebo znakem dolaru.
 * Další znaky již mohou obsahovat i číslice.<br />
 * Proměnná může představovat i pole, v tom případě bude obsahovat i hranaté
 * závorky, v nichž bude zapsána hodnota indexu pole.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class VariableFilter extends AbstractFilter
{

    private static final EnumRule RULE = EnumRule.VARIABLE_TO_ASSIGN_TO;

    public VariableFilter(JTextField parentJTextField, ValidationListener validationListener)
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

//    /**
//     * Pokusí se daný textový řeťezec porovnat vůči tomuto filtru.
//     *
//     * @param input text, který chceme prověřit
//     * @return
//     */
//    public static ValidityCheckResult isValid(String input)
//    {
//        if (input.length() == 0) {
//            return ValidityCheckResult.createValidRes(false);
//        }
//
//        String textToVerifyOn = input.replaceAll("\"([^\"\\\\]|\\\\.)*\"?", "\"\"");
//
//        boolean canBeLeftAsIs = (input.length() - input.replaceAll("\"", "").length()) % 2 == 0
//                && textToVerifyOn.replaceAll("\\[", "").length() == textToVerifyOn.replaceAll("\\]",
//                        "").length();  // odd count of quotes is not allowed as well as uneven square brackets (the other brackets will be checked at external filter)
//        String[] split = RegexFunctions.varBracketsInsides(textToVerifyOn);
//        for (int i = 0; i < split.length; i++) {
//            String splitPart = split[i];
//            if (i % 2 == 0) {
//                if (i == 0) {
//                    splitPart = splitPart.replaceFirst("\\[$", "");
//                } else {
//                    splitPart = splitPart.replaceFirst("\\[$", "").replaceFirst("^\\]", ""); // in case of '[' char at the end for editing convenience
//                }
//            }
//
//            ValidityCheckResult externalFilterRes = null;
//            if (i == 0) {
//                if (splitPart.replaceAll("\\s", "").isEmpty()) {
//                    return ValidityCheckResult.createInvalidRes(
//                            "Nejprve je nutné zadat název proměnné.");
//                }
//                externalFilterRes = NoArrayVariableFilter.isValid(splitPart);
//                if (!externalFilterRes.isValid) {
//                    externalFilterRes.command = input;
//                    return externalFilterRes;
//                }
//            } else if (i % 2 == 1) {
//                externalFilterRes = ValueFilter.isValid(splitPart, true, false); // let's allow commas so we can handle them ourselves
//                if (!externalFilterRes.isValid) {
//                    return externalFilterRes;
//                } else if (splitPart.startsWith("[")) {
//                    // in addition there should be no array
//                    return ValidityCheckResult.createInvalidRes(
//                            "Index pole může tvořit jen celé číslo.");
//                } else if (ConstantNumberFilter.isValid(splitPart).isValid) {
//                    if (splitPart.contains(".")) {
//                        // in addition there should be no plain decimal number or more than a single number
//                        return ValidityCheckResult.createInvalidRes(
//                                "Index pole může tvořit jen celé číslo.");
//                    } else if (splitPart.contains(",")) {
//                        // in addition there should be no more than a single number
//                        return ValidityCheckResult.createInvalidRes(
//                                "Index pole může tvořit jen jedno celé číslo.<br />"
//                                + "Pro indexaci vícerozměrného pole použijte syntaxi pole[x][y].");
//                    }
//                }
//
//                if (canBeLeftAsIs && splitPart.replaceAll("\\s", "").isEmpty()) {
//                    canBeLeftAsIs = false;
//                }
//            } else if (!splitPart.isEmpty()) {
//                // there should not be any clutter behind and between brackets closing (]...; ]...[)
//                return ValidityCheckResult.createInvalidRes("Zde tento znak není povolen.");
//            }
//
//            if (canBeLeftAsIs && externalFilterRes != null && !externalFilterRes.canBeLeftAsIs) {
//                canBeLeftAsIs = false;
//            }
//        }
//
//        if (checkBrackets(textToVerifyOn)) {
//            return ValidityCheckResult.createValidRes(canBeLeftAsIs);
//        } else {
//            return ValidityCheckResult.createInvalidRes("Detekováno nesprávné uzávorkování.");
//        }
//    }
//
//    private static boolean checkBrackets(String text)
//    {
//        int left = text.length() - text.replaceAll("\\[", "").length();
//        int right = text.length() - text.replaceAll("\\]", "").length();
//        return !((left != 0 || right != 0) && (left < right || (left == right && !text.matches(
//                ".*\\]$")))); // || (brackets > right && right > 0 && text.matches(".*\\[$"))) { nakonec toto nenechavam, pridavam tak podporu vicerozmernych poli
//    }
}
