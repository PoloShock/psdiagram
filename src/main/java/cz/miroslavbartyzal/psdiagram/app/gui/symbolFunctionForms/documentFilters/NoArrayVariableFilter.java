/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters;

import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.ValidationListener;
import cz.miroslavbartyzal.psdiagram.app.parser.EnumRule;
import javax.swing.JTextField;

/**
 * Tato třída představuje filtr proměnné, která nepředstavuje pole.<br />
 * Takováto proměnná by tedy neměla obsahovat jiné znaky, než písmenné a
 * číselné, případně znak podtržítka či dolaru.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class NoArrayVariableFilter extends AbstractFilter
{

    private static final EnumRule RULE = EnumRule.NO_ARRAY_VARIABLE_TO_ASSIGN_TO;

    public NoArrayVariableFilter(JTextField parentJTextField, ValidationListener validationListener)
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
//        if (!input.matches("^[a-zA-Z\\_\\$][\\w\\$]{0,29}")) {
//            if (input.matches("^[a-zA-Z\\_\\$].*")) {
//                return ValidityCheckResult.createInvalidRes(
//                        "Proměnná může obsahovat jen písmena bez diakritiky, číslice, podtržítka nebo znaky dolaru.");
//            } else {
//                return ValidityCheckResult.createInvalidRes(
//                        "Proměnná může začínat jen písmenem bez diakritiky, podtržítkem nebo znakem dolaru.");
//            }
//        } else if (input.toLowerCase().matches(
//                "^true$|^false$|^arguments$|^this$|^break$|^case$|^catch$|^continue$|^debugger$|^default$|^delete$|^do$|^else$|^finally$|^for$|^function$|^if$|^in$|^instanceof$|^new$|^return$|^switch$|^this$|^throw$|^try$|^typeof$|^var$|^void$|^while$|^with$")) {
//            return ValidityCheckResult.createInvalidRes(
//                    "\"" + input.toLowerCase() + "\" je rezervované klíčové slovo a nemůže být použito jako název proměnné.");
//        } else {
//            return ValidityCheckResult.createValidRes(true);
//        }
//    }
}
