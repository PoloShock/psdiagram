/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters;

import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.ValidationListener;
import cz.miroslavbartyzal.psdiagram.app.parser.EnumRule;
import javax.swing.JTextField;

/**
 * Tato třída představuje filtr numerické hodnoty.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class NumericValueFilter extends AbstractFilter
{

    private static final EnumRule RULE = EnumRule.NUMERIC_EXPRESSION;

    public NumericValueFilter(JTextField parentJTextField, ValidationListener validationListener)
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
//     * <p/>
//     * @param input text, který chceme prověřit
//     * @return
//     */
//    public static ValidityCheckResult isValid(String input)
//    {
//        if (input.length() == 0) {
//            return ValidityCheckResult.createValidRes(false);
//        }
//
//        if (input.contains("\"")) { //|| text.matches("^\\[+.*")) {
//            return ValidityCheckResult.createInvalidRes("Řetězcové hodnoty nejsou povoleny.");
//        } else {
//            return ValueFilter.isValid(input);
//        }
//    }
}
